package com.example.drosckar.analytics.data

import com.example.drosckar.analytics.domain.AnalyticsRepository
import com.example.drosckar.analytics.domain.AnalyticsValues
import com.example.drosckar.core.database.dao.AnalyticsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

/**
 * Concrete implementation of [AnalyticsRepository] that fetches analytics data
 * from the Room database using [AnalyticsDao].
 *
 * Executes all DAO queries in parallel using coroutines for optimal performance.
 */
class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao
) : AnalyticsRepository {

    /**
     * Performs all aggregate queries in parallel and builds an [AnalyticsValues] object.
     *
     * The database operations run on the IO dispatcher to avoid blocking the main thread.
     */
    override suspend fun getAnalyticsValues(): AnalyticsValues {
        return withContext(Dispatchers.IO) {
            // Launch all DAO calls in parallel
            val totalDistance = async { analyticsDao.getTotalDistance() }
            val totalTimeMillis = async { analyticsDao.getTotalTimeRun() }
            val maxRunSpeed = async { analyticsDao.getMaxRunSpeed() }
            val avgDistancePerRun = async { analyticsDao.getAvgDistancePerRun() }
            val avgPacePerRun = async { analyticsDao.getAvgPacePerRun() }

            // Await all results and convert them into the domain model
            AnalyticsValues(
                totalDistanceRun = totalDistance.await(),
                totalTimeRun = totalTimeMillis.await().milliseconds,
                fastestEverRun = maxRunSpeed.await(),
                avgDistancePerRun = avgDistancePerRun.await(),
                avgPacePerRun = avgPacePerRun.await()
            )
        }
    }
}