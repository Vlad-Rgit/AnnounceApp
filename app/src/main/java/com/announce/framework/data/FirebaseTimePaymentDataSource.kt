package com.announce.framework.data

import com.announce.common.data.TimeDataSource
import com.announce.common.data.TimePaymentDataSource
import com.announce.common.domain.TimePayment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTimePaymentDataSource
    @Inject constructor(
        private val timeDataSource: TimeDataSource
    ): TimePaymentDataSource {

    companion object {
        private const val collectionName = "timePayments"
    }

    private val database = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    override suspend fun getCurrentTimePeriodOrNull(): TimePayment? {
        return withContext(Dispatchers.IO) {

            val currentTime = timeDataSource.getCurrentDateTime()
            val currentTimestamp = Timestamp(currentTime.epochSecond, currentTime.nano)

            val result = database.collection(collectionName)
                    .whereEqualTo("userId", userId)
                    .whereGreaterThan("dateEnd", currentTimestamp)
                    .get()
                    .await()

            if(result.isEmpty)
                null
            else
                result.documents[0].toObject<TimePayment>()
        }
    }

    override suspend fun addNewPayment(payedMillis: Long): TimePayment {
       return withContext(Dispatchers.IO) {

           val timePayment = TimePayment(userId = userId)

           val addedDocument = database.collection(collectionName)
                   .add(timePayment)
                   .await()
                   .get()
                   .await()

           timePayment.documentId = addedDocument.id
           timePayment.dateStart = addedDocument.getTimestamp("dateStart")

           addMillisToCurrentPayment(timePayment, payedMillis)

           timePayment
       }
    }

    override suspend fun addMillisToCurrentPayment(timePayment: TimePayment, millis: Long) {
        withContext(Dispatchers.IO) {

            val date = if(timePayment.dateEnd == null)
                timePayment.dateStart
            else
                timePayment.dateEnd

            val newSeconds = millis / 1000 + date!!.seconds

            timePayment.dateEnd = Timestamp(newSeconds, 0)

            database.collection(collectionName)
                    .document(timePayment.documentId!!)
                    .set(timePayment)
                    .await()
        }
    }

}