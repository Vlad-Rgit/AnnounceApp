package com.announce.presenter.login.viewmodel

import com.announce.domain.User


sealed class State(val user: User)

class InitUserState(user: User = User.EMPTY): State(user)
class InvalidCredentialsState(user: User): State(user)
class LoggedUserState(user: User): State(user)
class LoadingUserState(user: User): State(user)