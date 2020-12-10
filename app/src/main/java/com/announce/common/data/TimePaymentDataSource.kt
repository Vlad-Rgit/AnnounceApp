package com.announce.common.data

import com.announce.common.domain.TimePayment

interface TimePaymentDataSource {
    suspend fun getCurrentTimePeriodOrNull(): TimePayment?
    suspend fun addNewPayment(payedMillis: Long): TimePayment
    suspend fun addMillisToCurrentPayment(timePayment: TimePayment, millis: Long)
}