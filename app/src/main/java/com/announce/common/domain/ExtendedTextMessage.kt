package com.announce.common.domain

import kotlinx.serialization.Serializable


@Serializable
data class ExtendedTextMessage(
        val text: String,
        val description: String,
        val title: String,
        val previewType: String,
        val jpegThumbnail: String
)
