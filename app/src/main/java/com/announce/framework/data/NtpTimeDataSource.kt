package com.announce.framework.data

import com.announce.common.data.TimeDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NtpTimeDataSource
    @Inject constructor()
    : TimeDataSource {

    companion object {
        private const val serverName = "ntp3.stratum2.ru"
    }

    override suspend fun getCurrentDateTime(): Instant {
        return withContext(Dispatchers.IO) {

            val client = NTPUDPClient()

            val inetAddress = InetAddress.getByName(serverName)
            val timeInfo = client.getTime(inetAddress)
            timeInfo.computeDetails()

            val millis =
                timeInfo.message.transmitTimeStamp.time

            Instant.ofEpochMilli(millis)
        }
    }

}