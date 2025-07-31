package com.example.drosckar.auth.presentation.di

import com.example.drosckar.auth.presentation.login.LoginViewModel
import com.example.drosckar.auth.presentation.register.RegisterViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}