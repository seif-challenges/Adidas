package tn.seif.adidaschallenge.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val rating: Float,
    val text: String,
    var syncStatus: SyncStatus = SyncStatus.SYNCED
) {
    enum class SyncStatus {
        SYNCED,
        PENDING
    }
}