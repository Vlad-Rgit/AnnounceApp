package com.announce.framework.utils

import com.announce.framework.AnnounceApp
import com.squareup.okhttp.Request
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.text.StringBuilder

suspend fun httpGet(url: String): String = onIo {

    val client = AnnounceApp.dataComponent.getOkHttpClient()

    val request = Request.Builder()
        .url(url)
        .build()

    val body = client.newCall(request).await().body()
    val json = body.string()
    body.close()
    json
}


suspend fun httpGetBytes(url: String): ByteArray = onIo {
    val client = AnnounceApp.dataComponent.getOkHttpClient()
    val request = Request.Builder()
            .url(url)
            .get()
            .build()
    val body = client.newCall(request).await().body()
    val bytes = body.bytes()
    body.close()
    bytes
}

fun httpGetBytesLegacy(url: String): ByteArray {
    val httpConn = makeHttpsRequestLegacy(url)
    httpConn.requestMethod = "GET"
    return readBodyAsBytesLegacy(httpConn)
}

fun httpGetLegacy(url: String): String {
    val httpConn = makeHttpsRequestLegacy(url)
    httpConn.requestMethod = "GET"
    return readBodyAsStringLegacy(httpConn)
}

fun makeHttpsRequestLegacy(url: String): HttpsURLConnection {
    return URL(url).openConnection()
        as HttpsURLConnection
}

fun readBodyAsStringLegacy(httpsURLConnection: HttpsURLConnection): String {

    val reader = InputStreamReader(httpsURLConnection.inputStream).buffered()

    val sb = StringBuilder()
    var line: String? = reader.readLine()

    while (line != null) {
        sb.append(line)
        line = reader.readLine()
    }

    reader.close()

    return sb.toString()
}

fun readBodyAsBytesLegacy(httpsURLConnection: HttpsURLConnection): ByteArray {
    val bufferedStream = httpsURLConnection.inputStream.buffered()
    val byteOutputStream = ByteArrayOutputStream()
    bufferedStream.copyTo(byteOutputStream)
    bufferedStream.close()
    return byteOutputStream.toByteArray()
}