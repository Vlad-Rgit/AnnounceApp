package com.announce.framework.utils

import com.squareup.okhttp.Call
import com.squareup.okhttp.Callback
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Call.await() = suspendCoroutine<Response> {

    enqueue(object : Callback {
        override fun onFailure(request: Request?, e: IOException) {
            it.resumeWithException(e)
        }

        override fun onResponse(response: Response) {
            it.resume(response)
        }
    })

}