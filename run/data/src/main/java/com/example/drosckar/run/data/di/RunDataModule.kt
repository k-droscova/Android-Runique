package com.example.drosckar.run.data.di

import com.example.drosckar.core.domain.run.SyncRunScheduler
import com.example.drosckar.run.data.CreateRunWorker
import com.example.drosckar.run.data.DeleteRunWorker
import com.example.drosckar.run.data.FetchRunsWorker
import com.example.drosckar.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}