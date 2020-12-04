package com.announce.presenter.login.viewmodel

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.*
import com.announce.AnnounceApp
import com.announce.R
import com.announce.utils.emailMatches
import com.announce.data.UserDataSource
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class LoginFragmentViewModel(application: Application)
    : AndroidViewModel(application) {


    private val context = application.applicationContext

    @Inject
    lateinit var userDataSource: UserDataSource

    private val _state = MutableLiveData<State>(InitUserState())
    val state: LiveData<State>
        get() = _state

    init {
        AnnounceApp.dataComponent.inject(this)
    }

    fun login() {
        viewModelScope.launch {
            loginImpl()
        }
    }

    suspend fun loginImpl() {

        val user = state.value!!.user

        _state.postValue(LoadingUserState(user))

        try {
            if(userDataSource.login(user).isSuccesfull) {
                _state.postValue(LoggedUserState(user))
            }
        }
        catch (ex: Exception) {
            _state.postValue(InvalidCredentialsState(user))
        }
    }
}