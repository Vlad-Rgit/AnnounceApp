package com.announce.framework.data

import com.announce.common.data.AnnounceDataSource
import com.announce.common.domain.Announcment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirebaseUserAnnounceDataSource
    @Inject constructor(): AnnounceDataSource {

    companion object {
        private const val collectionName = "announcments"
    }
    

    private val database = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    @ExperimentalCoroutinesApi
    override val announcmentsChannel: Flow<List<Announcment>> = callbackFlow {
        database.collection(collectionName)
                .whereEqualTo("userId", userId)
                .addSnapshotListener { value, error ->

                    if (value == null || error != null) {
                        close(error)
                    }

                    offer(value!!.toObjects<Announcment>())
                }
        awaitClose { cancel() }
    }



    override suspend fun addAnnouncment(text: String, phoneNumber: String): Announcment {
        return withContext(Dispatchers.IO) {

            val announcment = Announcment(
                    userId = userId,
                    text = text,
                    phoneNumber = phoneNumber
            )

            val addedDocument = database.collection(collectionName)
                    .add(announcment)
                    .await()
                    .get()
                    .await()

            announcment.documentId = addedDocument.id

            announcment.dateCreated = addedDocument
                    .getTimestamp("dateCreated")!!

            announcment
        }
    }

    override suspend fun updateAnnouncment(announcment: Announcment) {
        withContext(Dispatchers.IO) {
            database.collection(collectionName)
                    .document(announcment.documentId!!)
                    .set(announcment)
                    .await()
        }
    }

}