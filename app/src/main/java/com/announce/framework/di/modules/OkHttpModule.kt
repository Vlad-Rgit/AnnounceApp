package com.announce.framework.di.modules

import com.squareup.okhttp.OkHttpClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class OkHttpModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }
}