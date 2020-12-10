package com.announce.framework.di.modules

import com.announce.framework.database.mappers.MessageMapper
import com.announce.framework.database.mappers.SimpleMessageMapper
import dagger.Binds
import dagger.Module

@Module
abstract class MapperModule {
    @Binds
    abstract fun bindMessageMapper(impl: SimpleMessageMapper): MessageMapper
}