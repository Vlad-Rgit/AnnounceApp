package com.announce.presenter.mainmenu.viewmodel

import com.announce.common.domain.Message

sealed class State
class LoadingState(): State()
class ListState(val list: List<Message>): State()

class FilteredListState(
    val originalList: List<Message>,
    val filteredList: List<Message>
): State()

class TimePaymentState(
        val remainingSeconds: Long,
        val hasTime: Boolean
): State()