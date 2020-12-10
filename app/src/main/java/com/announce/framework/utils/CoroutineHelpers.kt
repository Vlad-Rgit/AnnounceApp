package com.announce.framework.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> onIo(block: suspend () -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}