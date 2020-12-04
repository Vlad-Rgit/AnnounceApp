package com.announce.domain

import com.google.firebase.database.Exclude

data class User(
    var email: String,
    @set:Exclude
    @get:Exclude
    var password: String,
    var firstName: String = "",
    var lastName: String = ""
) {
    companion object {
        val EMPTY
            get() = User("", "", "", "")

        fun createFromMapWithoutPassword(map: Map<String, Any>): User {
            return User(
                email = map["email"] as String,
                firstName = map["firstName"] as String,
                lastName = map["lastName"] as String,
                password = ""
            )
        }
    }

    fun toMapWithoutPassword(): Map<String, Any> {
        return mapOf(
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName
        )
    }


}