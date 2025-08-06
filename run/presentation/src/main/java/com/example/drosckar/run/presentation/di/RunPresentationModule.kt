package com.example.drosckar.run.presentation.di

import com.example.drosckar.run.domain.RunningTracker
import com.example.drosckar.run.presentation.active_run.ActiveRunViewModel
import com.example.drosckar.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val runPresentationModule = module {
    singleOf(::RunningTracker)

    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}