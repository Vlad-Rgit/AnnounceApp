package com.announce.framework.data

import android.os.Build
import com.announce.config.Config
import com.announce.common.data.GreenApiConfigSource
import com.announce.common.data.MessagesDataSource
import com.announce.common.domain.GreenApiConfig
import com.announce.common.domain.Message
import com.announce.framework.database.AnnounceDatabase
import com.announce.framework.database.mappers.MessageMapper
import com.announce.framework.utils.httpGet
import com.announce.framework.utils.httpGetLegacy
import com.announce.framework.utils.onIo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GreenApiMessageDataSource
    @Inject constructor(
        private val greenApiConfigSource: GreenApiConfigSource,
        database: AnnounceDatabase,
        private val messageMapper: MessageMapper
    ): MessagesDataSource {

    private lateinit var greenApiConfig: GreenApiConfig

    private val messageDao = database.messageDao

    override val messagesFlow: Flow<List<Message>>
        get() = messageDao.getAllFlow()
            .map { list ->
                list.map {
                    messageMapper.toDomain(it)
                }
            }

    private suspend fun initGreenApiConfig() {
        if(!::greenApiConfig.isInitialized) {
            greenApiConfig = greenApiConfigSource.getGreenApiConfig()
        }
    }


    override suspend fun refreshItems() {
        return onIo {

            initGreenApiConfig()

            val json = if (Build.VERSION.SDK_INT < 21) {
                httpGetLegacy(buildUrl())
            } else {
                httpGet(buildUrl())
            }

            val messages = Json { ignoreUnknownKeys = true }
                    .decodeFromString<List<Message>>(json)
                    .filter {
                        it.messageType != Message.MessageType.UnsupportedType
                    }
                .map {
                    messageMapper.toDatabase(it)
                }

            messageDao.insertAll(messages)

            val count = messageDao.count()

            if(count > Config.MAX_MESSAGES_ROWS) {
                val amount = count - Config.MAX_MESSAGES_ROWS
                messageDao.deleteOldest(amount)
            }
        }
    }




    private fun buildUrl(): String {
        return "https://api.green-api.com/" +
                "waInstance${greenApiConfig.idInstance}/" +
                "LastIncomingMessages/${greenApiConfig.apiTokenInstance}"
    }



}