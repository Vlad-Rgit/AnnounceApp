package com.announce.data

import com.announce.domain.GreenApiConfig

interface GreenApiConfigSource {
    suspend fun getGreenApiConfig(): GreenApiConfig
}