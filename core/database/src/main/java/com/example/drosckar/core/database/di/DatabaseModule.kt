package com.example.drosckar.core.database.di

import androidx.room.Room
import com.example.drosckar.core.database.MIGRATION_1_2
import com.example.drosckar.core.database.RoomLocalRunDataSource
import com.example.drosckar.core.database.RunDatabase
import com.example.drosckar.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "run.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
    single { get<RunDatabase>().runDao }
    single { get<RunDatabase>().runPendingSyncDao }

    singleOf(::RoomLocalRunDataSource).bind<LocalRunDataSource>()
}