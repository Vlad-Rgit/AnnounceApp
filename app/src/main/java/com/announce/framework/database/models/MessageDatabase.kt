package com.announce.framework.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class MessageDatabase(
        val type: String,
        @PrimaryKey
        val idMessage: String,
        val timestamp: Int,
        val typeMessage: String,
        val chatId: String,
        val senderId: String,
        val senderName: String,
        val textMessage: String?,
        val downloadUrl: String?,
        val caption: String?,
        val text: String?,
        val description: String?,
        val title: String?,
        val previewType: String?,
        val jpegThumbnail: String?
)
