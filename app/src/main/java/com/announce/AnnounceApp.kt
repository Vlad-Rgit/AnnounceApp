package com.announce

import android.app.Application
import android.provider.ContactsContract
import com.announce.di.components.DaggerDataComponent
import com.announce.di.components.DataComponent

class AnnounceApp: Application() {

    companion object {
        private lateinit var _dataComponent: DataComponent
        val dataComponent: DataComponent
            get() = _dataComponent
    }

    override fun onCreate() {
        super.onCreate()
        _dataComponent = DaggerDataComponent.create()
    }
}