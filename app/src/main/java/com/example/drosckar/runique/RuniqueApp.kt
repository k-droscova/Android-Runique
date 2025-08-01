package com.example.drosckar.runique

import android.app.Application
import com.example.drosckar.auth.data.di.authDataModule
import com.example.drosckar.auth.presentation.di.authViewModelModule
import com.example.drosckar.core.data.di.coreDataModule
import com.example.drosckar.run.presentation.run_overview.di.runViewModelModule
import com.example.drosckar.runique.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import timber.log.Timber
import org.koin.core.context.startKoin

class RuniqueApp: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runViewModelModule
            )
        }
    }
}