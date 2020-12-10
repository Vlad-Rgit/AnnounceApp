package com.announce.framework.di.components

import android.content.Context
import com.announce.common.data.MessagesDataSource
import com.announce.presenter.mainmenu.viewmodel.MainMenuViewModel
import com.announce.framework.di.modules.DataModule
import com.announce.framework.di.modules.MapperModule
import com.announce.framework.di.modules.OkHttpModule
import com.announce.framework.di.modules.RoomModule
import com.announce.presenter.login.viewmodel.LoginFragmentViewModel
import com.announce.presenter.registration.viewmodel.RegistrationFragmentViewModel
import com.announce.presenter.user_announces.viewmodel.UserAnnounceViewModel
import com.squareup.okhttp.OkHttpClient
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DataModule::class,
    OkHttpModule::class,
    MapperModule::class,
    RoomModule::class])
@Singleton
interface DataComponent {
    fun inject(item: LoginFragmentViewModel)
    fun inject(item: RegistrationFragmentViewModel)
    fun inject(item: MainMenuViewModel)
    fun inject(item: UserAnnounceViewModel)

    fun getOkHttpClient(): OkHttpClient
    fun getMessageDataSource(): MessagesDataSource


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun appContext(context: Context): Builder

        fun build(): DataComponent
    }
}