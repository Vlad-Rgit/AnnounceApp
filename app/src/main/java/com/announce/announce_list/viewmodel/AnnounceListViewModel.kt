package com.announce.announce_list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.announce.AnnounceApp
import com.announce.data.GreenApiConfigSource
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.json.JSONObject
import javax.inject.Inject

class AnnounceListViewModel: ViewModel() {

    @Inject
    lateinit var greenApiConfigSource: GreenApiConfigSource


    init {
        AnnounceApp.dataComponent.inject(this)
    }


    fun loadMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            loadMessagesImpl()
        }
    }

    suspend fun loadMessagesImpl() {

        val client = OkHttpClient()
        val apiConfig = greenApiConfigSource.getGreenApiConfig()

        val url = "https://api.green-api.com/waInstance" +
                "${apiConfig.idInstance}/LastIncomingMessages/${apiConfig.apiTokenInstance}"


        val request = Request.Builder()
            .url(url)
            .build()

        val answer = client.newCall(request).execute().body().string()
        Log.i("messages", answer)

    }

}