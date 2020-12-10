package com.announce.common.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Announcment @JvmOverloads constructor(
    @DocumentId
    var documentId: String? = null,
    val userId: String = "",
    var text: String = "",
    var phoneNumber: String = "",
    @ServerTimestamp
    var dateCreated: Timestamp? = null
): DiffListItem<Announcment> {

    override fun areContentsTheSame(other: Announcment): Boolean {
        return text == other.text && phoneNumber == other.phoneNumber
    }

    override fun areItemsTheSame(other: Announcment): Boolean {
        return documentId == other.documentId
    }

}
