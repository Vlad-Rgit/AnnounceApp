package com.announce.framework.database.mappers

import com.announce.common.domain.Message
import com.announce.framework.database.models.MessageDatabase

interface MessageMapper {
    fun toDomain(message: MessageDatabase): Message
    fun toDatabase(message: Message): MessageDatabase
}