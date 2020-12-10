package com.announce.common.data

import com.announce.common.domain.GreenApiConfig

interface GreenApiConfigSource {
    suspend fun getGreenApiConfig(): GreenApiConfig
}