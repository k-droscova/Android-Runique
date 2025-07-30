package com.example.drosckar.auth.data.di

import com.example.drosckar.auth.data.EmailPatternValidator
import com.example.drosckar.auth.domain.PatternValidator
import com.example.drosckar.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
}