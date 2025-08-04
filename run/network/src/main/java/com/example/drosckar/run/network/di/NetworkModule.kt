package com.example.drosckar.run.network.di

import com.example.drosckar.core.domain.run.RemoteRunDataSource
import com.example.drosckar.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


val networkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}