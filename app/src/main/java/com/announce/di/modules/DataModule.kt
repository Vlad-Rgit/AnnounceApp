package com.announce.di.modules

import com.announce.data.GreenApiConfigSource
import com.announce.framework.data.FirebaseUserDataSource
import com.announce.data.UserDataSource
import com.announce.framework.data.FirebaseGreenApiConfigDataSource
import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {

    @Binds
    abstract fun bindUserDataSource(impl: FirebaseUserDataSource)
        : UserDataSource

    @Binds
    abstract fun bindGreenApiConfigSource(impl: FirebaseGreenApiConfigDataSource)
        : GreenApiConfigSource
}