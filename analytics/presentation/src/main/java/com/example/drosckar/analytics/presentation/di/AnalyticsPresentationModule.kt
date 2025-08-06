package com.example.drosckar.analytics.presentation.di

import com.example.drosckar.analytics.presentation.AnalyticsDashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val analyticsPresentationModule = module {
    viewModelOf(::AnalyticsDashboardViewModel)
}