package com.example.drosckar.core.data.di

import com.example.drosckar.core.data.auth.EncryptedSessionStorage
import com.example.drosckar.core.data.networking.HttpClientFactory
import com.example.drosckar.core.data.run.OfflineFirstRunRepository
import com.example.drosckar.core.domain.run.RunRepository
import com.example.drosckar.core.domain.util.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}