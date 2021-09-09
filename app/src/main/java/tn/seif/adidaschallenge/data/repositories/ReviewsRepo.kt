package tn.seif.adidaschallenge.data.repositories

import timber.log.Timber
import tn.seif.adidaschallenge.data.local.daos.ReviewsDao
import tn.seif.adidaschallenge.data.models.Review
import tn.seif.adidaschallenge.data.remote.ReviewsApi
import tn.seif.adidaschallenge.data.remote.ServerResponseException
import tn.seif.adidaschallenge.data.remote.requestAnswer
import tn.seif.adidaschallenge.utils.ErrorHandler
import tn.seif.adidaschallenge.utils.NetworkListener
import tn.seif.adidaschallenge.utils.models.Answer
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * A repository responsible of handling [Review] data source.
 * Relies on server to fetch the data first and store it in a SQLite database.
 * In case of server unreachability, data will be fetched from local database.
 *
 * @property reviewsDao - An instance of [ReviewsDao] responsible of handling database operations.
 * @property reviewsApi - An instance of [ReviewsApi] responsible of handling api operations.
 * @property networkListener - An instance of [NetworkListener] to handle server reachability status.
 * @property errorHandler - An instance of [ErrorHandler] to handle errors.
 */
open class ReviewsRepo @Inject constructor(
    private val reviewsDao: ReviewsDao,
    private val reviewsApi: ReviewsApi,
    private val networkListener: NetworkListener,
    private val errorHandler: ErrorHandler
) {

    /**
     * Gets the list of review for a given product from server first and store it in local database.
     * In case of server unreachability, data will be fetched from local database.
     *
     * @return - The list of [Review].
     */
    open suspend fun getReviewsForProduct(productId: String): Answer<List<Review>, Exception> {
        return try {
            fetchReviewFromApi(productId)
            Answer.Success(reviewsDao.getReviewsForProduct(productId))
        } catch (e: Exception) {
            // Print the exception stack trace, and log it in Crashlytics.
            errorHandler.handle(e)
            when (e) {
                is ServerResponseException -> {
                    val databaseData = reviewsDao.getReviewsForProduct(productId)

                    // return the API error only if the product can't be found in database as well
                    if (databaseData.isEmpty())
                        Answer.Failure(e)
                    else
                        Answer.Success(databaseData)
                }
                else -> Answer.Failure(e)
            }
        }
    }

    /**
     * Adds a [Review] to the local database as a pending review.
     *
     * @param review - The review to be added.
     */
    open suspend fun addPendingReview(review: Review): Answer<Unit, Exception> {
        return try {
            review.syncStatus = Review.SyncStatus.PENDING
            reviewsDao.insert(review)
            Answer.Success(Unit)
        } catch (e: Exception) {
            errorHandler.handle(e)
            Answer.Failure(e)
        }
    }

    /**
     * Fetches the local database for pending reviews.
     *
     * @return - The list of pending [Review]s.
     */
    open suspend fun getPendingReviews(): Answer<List<Review>, Exception> {
        return try {
            Answer.Success(reviewsDao.getPendingReviews())
        } catch (e: Exception) {
            errorHandler.handle(e)
            Answer.Failure(e)
        }
    }

    /**
     * Attempt to sync the review with the API.
     *
     * @param review - The [Review] to sync.
     */
    suspend fun syncReview(review: Review): Answer<Unit?, Exception> {
        return try {
            when (val answer = reviewsApi.addProductReview(
                review.productId,
                review.rating,
                review.text
            ).requestAnswer()) {
                is Answer.Success -> {
                    review.syncStatus = Review.SyncStatus.SYNCED
                    updateDatabase(review)
                    // Server may have returns to work normally, so if the connection attempt was successful
                    // update the connection status of the NetworkListener
                    networkListener.updateConnectionState(true)
                    answer
                }
                is Answer.Failure -> throw answer.error
            }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException,
                is ConnectException,
                is UnknownHostException -> {
                    Timber.e(e)
                    // If the server fails suddenly, update the connection status of the NetworkListener.
                    networkListener.updateConnectionState(false)
                }
                else -> errorHandler.handle(e)
            }
            Answer.Failure(e)
        }
    }

    private suspend fun fetchReviewFromApi(productId: String) {
        try {
            when (val answer = reviewsApi.getProductReviews(productId).requestAnswer()) {
                is Answer.Success -> {
                    updateDatabase(productId, answer.result)
                    // Server may have returns to work normally, so if the connection attempt was successful
                    // update the connection status of the NetworkListener
                    networkListener.updateConnectionState(true)
                }
                is Answer.Failure -> throw answer.error
            }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException,
                is ConnectException,
                is UnknownHostException -> {
                    Timber.e(e)
                    // If the server fails suddenly, update the connection status of the NetworkListener.
                    networkListener.updateConnectionState(false)
                }
                else -> throw e
            }
        }
    }

    private suspend fun updateDatabase(productId: String, reviews: List<Review>) {
        reviewsDao.deleteAllForProduct(productId)
        reviewsDao.insertAll(reviews)
    }

    private suspend fun updateDatabase(review: Review) {
        reviewsDao.deleteReview(review.id)
        reviewsDao.insert(review)
    }
}
