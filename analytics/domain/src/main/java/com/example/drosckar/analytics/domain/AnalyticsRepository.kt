package com.example.drosckar.analytics.domain

/**
 * Repository interface for fetching analytics values.
 *
 * Abstracts the data source (e.g., Room, remote API) so the ViewModel
 * doesn't need to know how analytics are calculated or stored.
 */
interface AnalyticsRepository {

    /**
     * Fetches calculated analytics values such as distance, time, pace, etc.
     */
    suspend fun getAnalyticsValues(): AnalyticsValues
}