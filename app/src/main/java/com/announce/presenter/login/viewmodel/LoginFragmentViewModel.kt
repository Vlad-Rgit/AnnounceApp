package com.announce.presenter.login.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.announce.framework.AnnounceApp
import com.announce.common.data.UserDataSource
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
        val user = state.value!!.user
        user.email = "vlyachmenev16@gmail.com"
        user.password = "123456"
        login()
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