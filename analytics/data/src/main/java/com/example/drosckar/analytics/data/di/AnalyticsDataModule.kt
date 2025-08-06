package com.example.drosckar.analytics.data.di

import com.example.drosckar.analytics.data.RoomAnalyticsRepository
import com.example.drosckar.analytics.domain.AnalyticsRepository
import com.example.drosckar.core.database.RunDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsDataModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
    single {
        get<RunDatabase>().analyticsDao
    }
}