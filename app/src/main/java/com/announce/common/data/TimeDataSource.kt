package com.announce.common.data

import java.time.Instant

interface TimeDataSource {
    suspend fun getCurrentDateTime(): Instant
}