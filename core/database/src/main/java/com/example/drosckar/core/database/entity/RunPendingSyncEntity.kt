package com.example.drosckar.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a run that was created locally but not yet synced with the remote API.
 *
 * This is used in offline-first mode to ensure that runs created offline can later be
 * synchronized with the server when connectivity is restored. It also stores:
 * - the run itself (embedded),
 * - the image of the run’s map as bytes,
 * - and the ID of the user to whom the run belongs.
 *
 * We override equals and hashCode manually to properly compare `ByteArray` contents.
 */
@Entity
data class RunPendingSyncEntity(
    @Embedded val run: RunEntity, // All the run’s data (timestamp, metrics, etc.)

    @PrimaryKey(autoGenerate = false)
    val runId: String = run.id,   // Manually define the PK because Room doesn't inherit PKs from embedded classes.

    val mapPictureBytes: ByteArray, // Locally stored image data that needs to be uploaded.

    val userId: String              // ID of the user who owns this run; ensures sync correctness on logout/login.
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RunPendingSyncEntity

        if (run != other.run) return false
        if (runId != other.runId) return false
        if (!mapPictureBytes.contentEquals(other.mapPictureBytes)) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = run.hashCode()
        result = 31 * result + runId.hashCode()
        result = 31 * result + mapPictureBytes.contentHashCode() // Ensures content comparison of byte array.
        result = 31 * result + userId.hashCode()
        return result
    }
}