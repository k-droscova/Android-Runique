package com.example.drosckar.analytics.data.di

import com.example.drosckar.analytics.data.RoomAnalyticsRepository
import com.example.drosckar.analytics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
}