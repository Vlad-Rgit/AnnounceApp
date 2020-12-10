package com.announce.common.data

import com.announce.common.domain.User

interface UserDataSource {
    suspend fun login(user: User): AuthState
    suspend fun registrate(user: User): AuthState
    suspend fun isLogged(): AuthState
}