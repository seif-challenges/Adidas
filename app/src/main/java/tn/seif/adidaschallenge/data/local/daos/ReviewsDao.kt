package tn.seif.adidaschallenge.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tn.seif.adidaschallenge.data.models.Review

/**
 * Room DAO responsible for reviews operations.
 */
@Dao
interface ReviewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Review>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: Review)

    @Query("DELETE FROM Review WHERE productId = :productId AND syncStatus = :syncStatus")
    suspend fun deleteAllForProduct(
        productId: String,
        syncStatus: Int = Review.SyncStatus.SYNCED.ordinal
    )

    @Query("DELETE FROM Review WHERE id = :id")
    suspend fun deleteReview(id: Int)

    @Query("SELECT * FROM review WHERE productId = :productId")
    suspend fun getReviewsForProduct(productId: String): List<Review>

    @Query("SELECT * FROM review WHERE syncStatus = :syncStatus")
    suspend fun getPendingReviews(syncStatus: Int = Review.SyncStatus.PENDING.ordinal): List<Review>
}