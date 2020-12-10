package com.announce.framework.di.modules

import android.content.Context
import com.announce.framework.database.AnnounceDatabase
import dagger.Module
import dagger.Provides

@Module
class RoomModule() {
    @Provides
    fun provideAnnounceDatabase(context: Context) =
        AnnounceDatabase.getInstance(context)
}