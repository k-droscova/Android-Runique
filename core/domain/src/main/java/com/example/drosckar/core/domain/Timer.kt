package com.example.drosckar.core.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Emits time deltas as durations every 200 milliseconds.
 *
 * This flow is used to track the elapsed time during a run.
 * Instead of emitting the absolute time, it emits the difference (delta)
 * from the last emission. This is useful for accumulating total run duration
 * in a reactive pipeline.
 */
object Timer {
    fun timeAndEmit(): Flow<Duration> {
        return flow {
            var lastEmitTime = System.currentTimeMillis()
            while(true) {
                delay(200L) // Wait before emitting the next delta
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastEmitTime
                emit(elapsedTime.milliseconds) // Emit the time passed since last emission
                lastEmitTime = currentTime
            }
        }
    }
}