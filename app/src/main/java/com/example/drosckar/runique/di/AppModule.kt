package com.example.drosckar.runique.di

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.drosckar.runique.MainViewModel
import com.example.drosckar.runique.RuniqueApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * App-wide Koin module for shared dependencies.
 */
val appModule = module {
    /**
     * Provides encrypted SharedPreferences using Jetpack Security.
     */
    single<SharedPreferences> {
        EncryptedSharedPreferences(
            androidApplication(),
            "auth_pref",
            MasterKey(androidApplication()),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    /**
     * Provides access to application-wide CoroutineScope from RuniqueApp.
     */
    single<CoroutineScope> {
        (androidApplication() as RuniqueApp).applicationScope
    }

    viewModelOf(::MainViewModel)
}