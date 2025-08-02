package com.example.drosckar.runique

import android.app.Application
import com.example.drosckar.auth.data.di.authDataModule
import com.example.drosckar.auth.presentation.di.authViewModelModule
import com.example.drosckar.core.data.di.coreDataModule
import com.example.drosckar.run.location.di.locationModule
import com.example.drosckar.run.presentation.di.runPresentationModule
import com.example.drosckar.runique.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import timber.log.Timber
import org.koin.core.context.startKoin

/**
 * Custom Application class for initializing app-wide dependencies.
 */
class RuniqueApp: Application() {

    /**
     * Application-wide coroutine scope used for long-running background tasks,
     * independent of lifecycle-bound scopes like ViewModelScope.
     * We use a [SupervisorJob] to prevent failure propagation between coroutines.
     */
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        // Enable debug logging with Timber in debug builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Start Koin and load all DI modules
        startKoin {
            androidLogger() // Logs errors from Koin
            androidContext(this@RuniqueApp) // Provide application context
            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runPresentationModule,
                locationModule,
            )
        }
    }
}