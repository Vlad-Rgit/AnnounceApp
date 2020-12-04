package com.announce.presenter.registration.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.announce.AnnounceApp
import com.announce.utils.emailMatches
import com.announce.utils.passwordMatches
import com.announce.data.UserDataSource
import com.announce.domain.User
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class RegistrationFragmentViewModel: ViewModel() {

    @Inject
    lateinit var userDataSource: UserDataSource

    private val _state = MutableLiveData<State>(InitUserState())
    val state: LiveData<State>
        get() = _state


    init {
        AnnounceApp.dataComponent.inject(this)
    }


    fun register() {
        viewModelScope.launch {
            registerImpl()
        }
    }

    private fun validate(user: User): ValidationErrorsState {

        val errorState = ValidationErrorsState(user)

        if(user.firstName.isBlank()) {
            errorState.addErrorType(
                ValidationErrorsState.ErrorType.emptyFirstName)
        }

        if(user.lastName.isBlank()) {
            errorState.addErrorType(
                ValidationErrorsState.ErrorType.emptyLastName)
        }

        if(!emailMatches(user.email)) {
            errorState.addErrorType(
                ValidationErrorsState.ErrorType.emailFormat
            )
        }

        if(!passwordMatches(user.email)) {
            errorState.addErrorType(
                ValidationErrorsState.ErrorType.passwordLength
            )
        }

        return errorState
    }

    suspend fun registerImpl() {

        val user = state.value!!.user

        _state.postValue(LoadingState(user))

        val errorState = validate(user)

        if(errorState.hasErrors) {
            _state.postValue(errorState)
            return;
        }

        try {
            if(userDataSource.registrate(user).isSuccesfull)
                _state.postValue(RegisteredUserState(user))
        }
        catch (ex: Exception) {
            Log.i("FirebaseException", ex.javaClass.name
                    + " " + ex.message.toString())
        }
    }

}