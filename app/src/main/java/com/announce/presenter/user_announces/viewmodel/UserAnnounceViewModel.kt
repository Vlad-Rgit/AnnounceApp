package com.announce.presenter.user_announces.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.announce.common.data.AnnounceDataSource
import com.announce.common.domain.Announcment
import com.announce.framework.AnnounceApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserAnnounceViewModel: ViewModel() {

    @Inject
    lateinit var announceDataSource: AnnounceDataSource

    private val _announces = MutableLiveData<List<Announcment>>()
    val announces: LiveData<List<Announcment>>
        get() = _announces

    init {
        AnnounceApp.dataComponent.inject(this)
    }

    @ExperimentalCoroutinesApi
    fun startCollectAnnounces() {
        viewModelScope.launch(Dispatchers.IO) {
            announceDataSource.announcmentsChannel
                    .collectLatest { _announces.postValue(it) }
        }
    }

}