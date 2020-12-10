package com.announce.presenter.mainmenu.announce_list.adapters

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.announce.R
import com.announce.databinding.AnnounceItemBinding
import com.announce.common.domain.Message
import com.announce.framework.utils.AnnounceDiffUtilCallback
import com.bumptech.glide.Glide
import java.lang.IllegalArgumentException


typealias MenuClickedCallback = (message: Message) -> Unit


class AnnounceAdapter: RecyclerView.Adapter<AnnounceAdapter.ViewHolder>() {

    class ViewHolder(private val binding: AnnounceItemBinding)
        : RecyclerView.ViewHolder(binding.root) {


        companion object {
            private const val menuCallId = 1
            private const val menuDownloadImageId = 2
            private const val menuCopyTextId = 3
        }

        private lateinit var currentMessage: Message

        private var menuCallClicked: MenuClickedCallback? = null
        private var menuCopyTextClicked: MenuClickedCallback? = null
        private var menuDownloadImageClicked: MenuClickedCallback? = null

        private val popupMenu = PopupMenu(binding.root.context, binding.btnMenu).apply {
            menu.add(0,
                menuCopyTextId,
                0,
                R.string.copy_text)

            setOnMenuItemClickListener {
                when(it.itemId) {
                    menuCallId -> menuCallClicked?.invoke(currentMessage)
                    menuDownloadImageId ->
                        menuDownloadImageClicked?.invoke(currentMessage)
                    menuCopyTextId -> menuCopyTextClicked?.invoke(currentMessage)
                }
                true
            }
        }

        init {
            binding.btnMenu.setOnClickListener {
                popupMenu.show()
            }
        }

        private fun removeMenuItem(id: Int) {
            popupMenu.menu.removeItem(id)
        }

        private fun addMenuItemIfNotExists(id: Int, titleRes: Int) {
            if(popupMenu.menu.findItem(id) == null) {
                popupMenu.menu.add(
                    0,
                    id,
                    0,
                    titleRes
                )
            }
        }

        private fun buildCircularProgress() =
            CircularProgressDrawable(binding.root.context).apply {
                strokeWidth = 5f
                centerRadius = 30f
                start()
            }

        fun bind(message: Message, mode: Mode) {

            currentMessage = message

            if(mode == Mode.Blocked) {
                removeMenuItem(menuCallId)
            }
            else {
                addMenuItemIfNotExists(menuCallId, R.string.call)
            }

            when(message.messageType) {
                Message.MessageType.TextMessage ->
                    renderTextMessage(message, mode)
                Message.MessageType.ExtendedTextMessage ->
                    renderExtendedTextMessage(message, mode)
                Message.MessageType.ImageMessage ->
                    renderImageMessage(message, mode)
                else -> throw IllegalArgumentException(
                    "Can not render message of type ${message.messageType.name}")
            }
        }

        private fun renderTextMessage(message: Message, mode: Mode) {

            removeMenuItem(menuDownloadImageId)

            binding.run {
                Glide.with(root).clear(imgAnnounce)
                imgAnnounce.visibility = View.GONE
                tvMessageText.text = message.textMessage
                tvPhoneNumber.text = if(mode == Mode.Unblocked)
                    message.phoneNumber
                else
                    message.lockedPhoneNumber
            }
        }

        private fun renderExtendedTextMessage(message: Message, mode: Mode) {

            addMenuItemIfNotExists(menuDownloadImageId, R.string.download_image)

            val extendedData = message.extendedTextMessage!!

            binding.run {

                imgAnnounce.visibility = View.VISIBLE

                val bytes = Base64.decode(extendedData.jpegThumbnail, Base64.DEFAULT)

                Glide.with(root)
                    .load(bytes)
                    .placeholder(buildCircularProgress())
                    .into(imgAnnounce)

                val messageBuilder = StringBuilder().apply {
                    message.caption?.let {
                        appendLine(it)
                    }
                    appendLine(extendedData.title)
                    appendLine(extendedData.description)
                }

                tvMessageText.text = messageBuilder.toString()
                tvPhoneNumber.text = if(mode == Mode.Unblocked)
                    message.phoneNumber
                else
                    message.lockedPhoneNumber
            }
        }

        private fun renderImageMessage(message: Message, mode: Mode) {

            addMenuItemIfNotExists(menuDownloadImageId, R.string.download_image)

            binding.run {

                imgAnnounce.visibility = View.VISIBLE

                Glide.with(root)
                    .load(message.downloadUrl!!)
                    .placeholder(buildCircularProgress())
                    .into(imgAnnounce)

                tvPhoneNumber.text = if(mode == Mode.Unblocked)
                    message.phoneNumber
                else
                    message.lockedPhoneNumber

                tvMessageText.text = ""
            }
        }

        fun setOnMenuCallClicked(callback: MenuClickedCallback) {
            menuCallClicked = callback
        }

        fun setOnMenuDownloadImageClicked(callback: MenuClickedCallback) {
            menuDownloadImageClicked = callback
        }
        fun setOnMenuCopyTextClicked(callback: MenuClickedCallback) {
            menuCopyTextClicked = callback
        }
    }

    enum class Mode {
        Blocked,
        Unblocked
    }

    private var menuCallClicked: MenuClickedCallback? = null
    private var menuCopyTextClicked: MenuClickedCallback? = null
    private var menuDownloadImageClicked: MenuClickedCallback? = null

    var mode = Mode.Blocked
        set(value) {
            if(value != field) {
                field = value
                notifyDataSetChanged()
            }
        }

    private var items = emptyList<Message>()


    fun setItems(newItems: List<Message>) {
        val diffCallback = AnnounceDiffUtilCallback(newItems, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AnnounceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )

        return ViewHolder(binding).apply {
            setOnMenuCallClicked {
                menuCallClicked?.invoke(it)
            }
            setOnMenuCopyTextClicked {
                menuCopyTextClicked?.invoke(it)
            }
            setOnMenuDownloadImageClicked {
                menuDownloadImageClicked?.invoke(it)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], mode)
    }

    override fun getItemCount() = items.size


    fun setOnMenuCallClicked(callback: MenuClickedCallback) {
        menuCallClicked = callback
    }

    fun setOnMenuDownloadImageClicked(callback: MenuClickedCallback) {
        menuDownloadImageClicked = callback
    }
    fun setOnMenuCopyTextClicked(callback: MenuClickedCallback) {
        menuCopyTextClicked = callback
    }


}

