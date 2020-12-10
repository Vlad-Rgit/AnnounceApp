package com.announce.framework

import android.app.Application
import androidx.work.*
import com.announce.framework.di.components.DaggerDataComponent
import com.announce.framework.di.components.DataComponent
import com.announce.framework.workers.SyncMessagesWorker
import java.util.concurrent.TimeUnit

class AnnounceApp: Application() {

    companion object {
        private lateinit var _dataComponent: DataComponent
        val dataComponent: DataComponent
            get() = _dataComponent
    }

    override fun onCreate() {
        super.onCreate()

        _dataComponent = DaggerDataComponent
            .builder()
            .appContext(applicationContext)
            .build()

        scheduleSyncMessagesWorker()
    }

    private fun scheduleSyncMessagesWorker() {

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<SyncMessagesWorker>(
            23, TimeUnit.HOURS, // repeat interval
            15, TimeUnit.MINUTES) // flex interval
            .setConstraints(workConstraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                SyncMessagesWorker::class.java.name,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )

    }
}