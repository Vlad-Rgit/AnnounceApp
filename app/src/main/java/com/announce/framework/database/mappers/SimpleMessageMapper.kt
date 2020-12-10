package com.announce.framework.database.mappers

import com.announce.common.domain.ExtendedTextMessage
import com.announce.common.domain.Message
import com.announce.framework.database.models.MessageDatabase
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SimpleMessageMapper @Inject constructor()
    : MessageMapper{

    override fun toDomain(message: MessageDatabase): Message {

        var extendedData: ExtendedTextMessage? = null

        if (message.text != null) {
            extendedData = ExtendedTextMessage(
                text = message.text,
                description = message.description!!,
                title = message.title!!,
                previewType = message.previewType!!,
                jpegThumbnail = message.jpegThumbnail!!
            )
        }

        return Message(
            type = message.type,
            idMessage = message.idMessage,
            timestamp = message.timestamp,
            typeMessage = message.typeMessage,
            chatId = message.chatId,
            senderId = message.senderId,
            senderName = message.senderName,
            textMessage = message.textMessage,
            downloadUrl = message.downloadUrl,
            caption = message.caption,
            extendedTextMessage = extendedData
        )

    }

    override fun toDatabase(message: Message): MessageDatabase {
        return MessageDatabase(
            type = message.type,
            idMessage = message.idMessage,
            timestamp = message.timestamp,
            typeMessage = message.typeMessage,
            chatId = message.chatId,
            senderId = message.senderId,
            senderName = message.senderName,
            textMessage = message.textMessage,
            downloadUrl = message.downloadUrl,
            caption = message.caption,
            text = message.extendedTextMessage?.text,
            description = message.extendedTextMessage?.text,
            previewType = message.extendedTextMessage?.previewType,
            title = message.extendedTextMessage?.title,
            jpegThumbnail = message.extendedTextMessage?.jpegThumbnail
        )
    }
}