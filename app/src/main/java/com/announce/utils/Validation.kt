package com.announce.utils

import android.util.Patterns

fun emailMatches(email: String)
    = Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun passwordMatches(password: String)
    = password.length >= 6