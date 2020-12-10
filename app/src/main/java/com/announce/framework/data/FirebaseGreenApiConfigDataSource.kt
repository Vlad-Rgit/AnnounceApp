package com.announce.framework.data

import com.announce.common.data.GreenApiConfigSource
import com.announce.common.domain.GreenApiConfig
import com.announce.framework.utils.onIo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseGreenApiConfigDataSource
 @Inject constructor(): GreenApiConfigSource {

    private val database = Firebase.firestore

    private lateinit var greenApiConfig: GreenApiConfig

    override suspend fun getGreenApiConfig(): GreenApiConfig {
        return onIo {

            if (::greenApiConfig.isInitialized) {
                greenApiConfig
            } else {
                val documentRef = database.collection("config")
                    .document("greenApiConfig")

                documentRef.addSnapshotListener { value, error ->
                    value!!.toObject<GreenApiConfig>()?.let {
                        greenApiConfig = it
                    }
                }

                greenApiConfig = documentRef
                    .get()
                    .await()
                    .toObject()!!

                greenApiConfig
            }
        }
    }

}