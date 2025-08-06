package com.example.drosckar.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a run that was deleted locally but the deletion has not yet
 * been synced with the remote API.
 *
 * Used to ensure correct deletion syncing during the next online session.
 * Contains only the necessary metadata to request deletion.
 */
@Entity
data class DeletedRunSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String, // The ID of the run we tried to delete.

    val userId: String // The ID of the user who initiated the deletion.
)