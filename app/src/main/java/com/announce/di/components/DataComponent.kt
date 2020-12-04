package com.announce.di.components

import com.announce.announce_list.viewmodel.AnnounceListViewModel
import com.announce.di.modules.DataModule
import com.announce.presenter.login.viewmodel.LoginFragmentViewModel
import com.announce.presenter.registration.viewmodel.RegistrationFragmentViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DataModule::class])
@Singleton
interface DataComponent {
    fun inject(item: LoginFragmentViewModel)
    fun inject(item: RegistrationFragmentViewModel)
    fun inject(item: AnnounceListViewModel)
}