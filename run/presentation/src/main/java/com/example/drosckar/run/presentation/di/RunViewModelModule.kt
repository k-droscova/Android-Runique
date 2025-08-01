package com.example.drosckar.run.presentation.di

import com.example.drosckar.run.presentation.active_run.ActiveRunViewModel
import com.example.drosckar.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}