package com.announce.framework.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.announce.common.data.MessagesDataSource

class SyncMessagesWorker(
    appContext: Context,
    params: WorkerParameters,
    private val messageDataSource: MessagesDataSource
): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {

        return try {
            messageDataSource.refreshItems()
            Result.success()
        } catch (ex: Exception) {
            Result.retry()
        }

    }

}