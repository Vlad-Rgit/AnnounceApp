package com.announce.data

import com.announce.domain.User

interface UserDataSource {
    suspend fun login(user: User): AuthState
    suspend fun registrate(user: User): AuthState
    suspend fun isLogged(): AuthState
}