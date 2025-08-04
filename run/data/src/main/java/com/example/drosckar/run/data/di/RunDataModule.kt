package com.example.drosckar.run.data.di

import com.example.drosckar.run.data.CreateRunWorker
import com.example.drosckar.run.data.DeleteRunWorker
import com.example.drosckar.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)
}