package com.announce.presenter.registration.viewmodel

import com.announce.common.domain.User


sealed class State(val user: User)

class InitUserState(user: User = User.EMPTY): State(user)
class ValidationErrorsState(user: User): State(user) {

    enum class ErrorType {
        passwordLength,
        emailFormat,
        emptyFirstName,
        emptyLastName
    }

    private val errorKeys = hashSetOf<String>()

    val hasErrors
        get() = errorKeys.isNotEmpty()

    fun hasError(type: ErrorType) = errorKeys.contains(type.name)

    fun addErrorType(type: ErrorType) = errorKeys.add(type.name)

}
class RegisteredUserState(user: User): State(user)
class LoadingState(user: User): State(user)
