package com.announce.common.data

import com.announce.common.domain.Announcment
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow

interface AnnounceDataSource {

    val announcmentsChannel: Flow<List<Announcment>>

    suspend fun addAnnouncment(text: String, phoneNumber: String): Announcment
    suspend fun updateAnnouncment(announcment: Announcment)
}