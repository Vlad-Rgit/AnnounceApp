package com.announce.framework.data

import androidx.lifecycle.MutableLiveData
import com.announce.utils.onIo
import com.announce.data.AuthState
import com.announce.data.UserDataSource
import com.announce.domain.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Inject

class FirebaseUserDataSource
     @Inject constructor(): UserDataSource {

    private var _loggedUser: User? = null
    val loggedUser: User
        get() {
            val tmp = _loggedUser
                ?: throw IllegalStateException(
                    "User is not logged"
                )
            return tmp
        }
    
    private lateinit var userReference: DatabaseReference
    
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore

    override suspend fun login(user: User): AuthState {
        return onIo {

            val user = auth.signInWithEmailAndPassword(
                user.email,
                user.password!!
            ).await().user!!
            
            val map = database.collection("users")
                .document(user.uid)
                .get()
                .await()
                .data!!

            _loggedUser = User.createFromMapWithoutPassword(map)

            AuthState(true)
        }
    }

    override suspend fun registrate(user: User): AuthState {
        return onIo {

            val firebaseUser = auth.createUserWithEmailAndPassword(
                user.email,
                user.password!!
            ).await().user!!

            database.collection("users")
                .document(firebaseUser.uid)
                .set(user.toMapWithoutPassword())
                .await()

            _loggedUser = user

            AuthState(true)
        }
    }

    override suspend fun isLogged(): AuthState {
        TODO("Not yet implemented")
    }

}