package tn.seif.adidaschallenge.data.local

import androidx.room.TypeConverter
import tn.seif.adidaschallenge.data.models.Review

class Converters {
    @TypeConverter
    fun toReviewSyncStatus(value: Int) = enumValues<Review.SyncStatus>()[value]

    @TypeConverter
    fun fromReviewSyncStatus(value: Review.SyncStatus) = value.ordinal
}