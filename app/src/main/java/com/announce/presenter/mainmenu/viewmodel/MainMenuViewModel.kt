package com.announce.presenter.mainmenu.viewmodel


import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.*
import com.announce.framework.AnnounceApp
import com.announce.R
import com.announce.common.data.MessagesDataSource
import com.announce.common.data.TimeDataSource
import com.announce.common.data.TimePaymentDataSource
import com.announce.common.domain.Message
import com.announce.common.domain.TimePayment
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

class MainMenuViewModel(application: Application)
    : AndroidViewModel(application) {

    private val context = application.applicationContext

    private var payedTimeMillis: Long = 0L

    private var currentTimePayment: TimePayment? = null
        set(value) {
            field = value
            timePaymentCounter?.let {
                it.currentTimePayment = value
            }
        }



    @Inject
    lateinit var messagesDataSource: MessagesDataSource

    @Inject
    lateinit var timePaymentDataSource: TimePaymentDataSource

    @Inject
    lateinit var timeDataSource: TimeDataSource

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _filter = MutableLiveData(Filter())

    private val _filterState = MediatorLiveData<State>().apply {

        addSource(_state) {

            if(it is LoadingState) {
                this.postValue(it)
                return@addSource
            }

            if(it is ListState) {
                val originalList = it.list
                val filteredList = filter(originalList)
                this.postValue(FilteredListState(originalList, filteredList))
            }
        }

        addSource(_filter) {
            value?.let {
                if(it is FilteredListState) {
                    val originalList = it.originalList
                    val filteredList = filter(originalList)
                    this.postValue(FilteredListState(originalList, filteredList))
                }
            }
        }
    }

    val filterState: LiveData<State>
        get() = _filterState

    val filterEditor = FilterEditor(_filter)

    val filterReader = FilterReader(_filter)

    private val _paymentState = MutableLiveData(TimePaymentState(0L, false))
    val paymentState: LiveData<TimePaymentState>
        get() = _paymentState

    private var timePaymentTimer: Timer? = null
    private var timePaymentCounter: TimePeriodCounter? = null

    init {

        _state.value = LoadingState()

        AnnounceApp.dataComponent.inject(this)

        viewModelScope.launch(Dispatchers.IO) {

            val currentTime = timeDataSource.getCurrentDateTime()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

            timePaymentCounter = TimePeriodCounter(_paymentState, currentTime)

            timePaymentTimer = Timer(false)
            timePaymentTimer!!.schedule(timePaymentCounter, 0L, 1000L)

            currentTimePayment = timePaymentDataSource.getCurrentTimePeriodOrNull()



            messagesDataSource.messagesFlow.collect {
                _state.postValue(ListState(it))
            }
        }


    }


    private fun filter(originalList: List<Message>): List<Message> {

        val filteredList = mutableListOf<Message>()
        val filter = _filter.value!!

        for(message in originalList) {

            if((message.messageType == Message.MessageType.ImageMessage ||
                message.messageType == Message.MessageType.ExtendedTextMessage) &&
                    !filter.isPhotoEnabled)  {
                continue
            }

            if(message.messageType == Message.MessageType.AudioMessage &&
                    !filter.isAudioEnabled) {
                continue
            }

            var containsKeyword = filter.keywords.isEmpty()

            for (keyword in filter.keywords) {
                if(message.content.contains(keyword)) {
                    containsKeyword = true
                    break
                }
            }

            if(containsKeyword) {
                filteredList.add(message)
            }
        }

        return filteredList
    }

    fun downloadMessageImage(message: Message) {
        when(message.messageType) {
            Message.MessageType.ImageMessage ->
                downloadMessageImageImpl(message)
            Message.MessageType.ExtendedTextMessage ->
                saveJpegThumbnail(message)
            else -> throw IllegalArgumentException(
                "Message type ${message.messageType.name} " +
                        "does not have image"
            )
        }
    }

    private fun downloadMessageImageImpl(message: Message) {

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val cached = Glide.with(context)
                    .asFile()
                    .load(message.downloadUrl!!)
                    .submit()
                    .get()

                val bytes = cached.readBytes()
                saveToGallery(bytes)

                showToastOnMain(
                    context.getString(R.string.image_saved_in_gallery))

            } catch (ex: Exception) {
                showToastOnMain(
                    context.getString(R.string.download_error) + ex.message
                )
            }
        }
    }

    fun addPayedTime(payedTimeMillis: Long) {
        viewModelScope.launch {

            var currentPeriod = currentTimePayment

            if(currentPeriod == null) {
                currentPeriod = timePaymentDataSource.addNewPayment(payedTimeMillis)
            }
            else {
                timePaymentDataSource
                        .addMillisToCurrentPayment(currentPeriod, payedTimeMillis)
            }

            currentTimePayment = currentPeriod
        }
    }

    private suspend fun showToastOnMain(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Suppress("Deprecation")
    private fun saveToGallery(bytes: ByteArray) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        }
        else {
            val imagesDir =
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            it.write(bytes)
        }
    }

    private fun saveJpegThumbnail(message: Message) {

        viewModelScope.launch(Dispatchers.IO) {

            val bytes = Base64.decode(
                message.extendedTextMessage!!.jpegThumbnail,
                Base64.DEFAULT
            )

            try {

                saveToGallery(bytes)

                showToastOnMain(
                    context.getString(R.string.image_saved_in_gallery))

            } catch (ex: Exception) {
                showToastOnMain(
                    context.getString(R.string.download_error) + ex.message
                )
            }
        }
    }

    fun loadMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            loadMessagesImpl()
        }
    }

    suspend fun loadMessagesImpl() {
        messagesDataSource.refreshItems()
    }


    class FilterEditor(private val filter: MutableLiveData<Filter>) {

        private val actions = LinkedList<(filterState: Filter) -> Unit>()

        fun setKeywords(keywords: List<String>): FilterEditor {
            actions.add {
                it.keywords = keywords
            }
            return this
        }


        fun setIsAudioEnabled(isAudioEnabled: Boolean): FilterEditor {
            actions.add {
                it.isAudioEnabled = isAudioEnabled
            }
            return this
        }


        fun setIsPhotoEnabled(isPhotoEnabled: Boolean): FilterEditor {
            actions.add {
                it.isPhotoEnabled = isPhotoEnabled
            }
            return this
        }

        fun commit() {
            val fs = filter.value!!
            for (action in actions)
                action(fs)
            actions.clear()
            filter.postValue(fs)
        }
    }

    class FilterReader(private val filter: LiveData<Filter>) {
        val isAudioEnabled
            get() = filter.value!!.isAudioEnabled
        val isPhotoEnabled
            get() = filter.value!!.isPhotoEnabled
        val keywords: List<String>
            get() = filter.value!!.keywords.toList()
    }

    data class Filter(
        var isAudioEnabled: Boolean = true,
        var isPhotoEnabled: Boolean = true,
        var keywords: List<String> = emptyList()
    )


    class TimePeriodCounter(
            private val paymentState: MutableLiveData<TimePaymentState>,
            private var currentTime: LocalDateTime
    )
        : TimerTask() {

        private var dateEndLocal: LocalDateTime? = null

        var currentTimePayment: TimePayment? = null
            set(value) {
                field = value
                value?.let {
                    val dateEnd = it.dateEnd!!
                    dateEndLocal = Instant.ofEpochSecond(dateEnd.seconds,
                                dateEnd.nanoseconds.toLong())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                }
            }


        override fun run() {

            currentTime = currentTime.plusSeconds(1)

            val lDateEndLocal = dateEndLocal ?: return

            val remainingSeconds = ChronoUnit.SECONDS.between(currentTime, lDateEndLocal)

            if(remainingSeconds < 0) {
                dateEndLocal = null
                currentTimePayment = null
                postTimeState(0L)
                return
            }

            postTimeState(remainingSeconds)
        }

        private fun postTimeState(remainingSeconds: Long) {
            paymentState.postValue(TimePaymentState(remainingSeconds,
                    remainingSeconds > 0))
        }
    }

}