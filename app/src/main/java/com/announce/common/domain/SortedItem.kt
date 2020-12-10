package com.announce.common.domain

interface DiffListItem<T>{
    fun areContentsTheSame(other: T): Boolean
    fun areItemsTheSame(other: T): Boolean
}