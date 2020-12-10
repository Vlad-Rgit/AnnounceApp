package com.announce.common.data

import com.announce.common.domain.Message
import kotlinx.coroutines.flow.Flow


interface MessagesDataSource {
    val messagesFlow: Flow<List<Message>>
    suspend fun refreshItems()
}