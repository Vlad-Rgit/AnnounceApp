package com.announce.common.domain

import kotlinx.serialization.Serializable
import kotlin.text.StringBuilder

@Serializable
data class Message(
        val type: String,
        val idMessage: String,
        val timestamp: Int,
        val typeMessage: String,
        val chatId: String,
        val senderId: String,
        val senderName: String,
        val textMessage: String? = null,
        val downloadUrl: String? = null,
        val caption: String? = null,
        val extendedTextMessage: ExtendedTextMessage? = null
): DiffListItem<Message> {

    enum class MessageType {
        TextMessage,
        ExtendedTextMessage,
        ImageMessage,
        AudioMessage,
        UnsupportedType
    }

    val messageType: MessageType = when(typeMessage) {
        "textMessage" -> MessageType.TextMessage
        "imageMessage" -> MessageType.ImageMessage
        "extendedTextMessage" -> MessageType.ExtendedTextMessage
        else -> MessageType.UnsupportedType
    }

    val phoneNumber = senderId.substringBefore('@')
    val lockedPhoneNumber = phoneNumber.run {
        val sb = StringBuilder(this)
        for(i in sb.length - 1 downTo sb.length - 4)
            sb[i] = '*'
        sb.toString()
    }

    val content: String by lazy {
        when (messageType) {
            MessageType.TextMessage -> textMessage!!
            MessageType.ExtendedTextMessage -> {
                "$caption\n${extendedTextMessage!!.description}"
            }
            MessageType.ImageMessage -> {
                "$caption"
            }
            else -> ""
        }
    }


    override fun areContentsTheSame(other: Message): Boolean {
        return this == other
    }

    override fun areItemsTheSame(other: Message): Boolean {
        return other.idMessage == other.idMessage
    }
}
