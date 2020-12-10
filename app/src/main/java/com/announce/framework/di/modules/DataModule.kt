package com.announce.framework.di.modules

import com.announce.common.data.*
import com.announce.framework.data.*
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

    @Binds
    abstract fun bindMessageDataSource(impl: GreenApiMessageDataSource)
        : MessagesDataSource

    @Binds
    abstract fun bindTimePaymentDataSource(impl: FirebaseTimePaymentDataSource)
        : TimePaymentDataSource


    @Binds
    abstract fun bindTimeDataSource(impl: NtpTimeDataSource)
            : TimeDataSource

    @Binds
    abstract fun bindAnnounceDataSource(impl: FirebaseUserAnnounceDataSource)
        : AnnounceDataSource
}