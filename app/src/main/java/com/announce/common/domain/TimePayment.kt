package com.announce.common.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp


data class TimePayment @JvmOverloads constructor(
        @DocumentId
        var documentId: String? = null,
        val userId: String = "",
        @ServerTimestamp
        var dateStart: Timestamp? = null,
        var dateEnd: Timestamp? = null,
        var isFinish: Boolean = false
)
