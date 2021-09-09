package tn.seif.adidaschallenge.data.repositories

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.Response
import tn.seif.adidaschallenge.BaseTest
import tn.seif.adidaschallenge.data.local.daos.ReviewsDao
import tn.seif.adidaschallenge.data.models.Review
import tn.seif.adidaschallenge.data.remote.ReviewsApi
import tn.seif.adidaschallenge.data.remote.ServerResponseException
import tn.seif.adidaschallenge.utils.ErrorHandler
import tn.seif.adidaschallenge.utils.NetworkListener
import tn.seif.adidaschallenge.utils.models.Answer
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
class ReviewsRepoTest : BaseTest() {

    private val reviewsDao: ReviewsDao = mock()
    private val reviewsApi: ReviewsApi = mock()
    private val networkListener: NetworkListener = mock()
    private val errorHandler: ErrorHandler = mock()

    private val reviewsRepo = ReviewsRepo(reviewsDao, reviewsApi, networkListener, errorHandler)

    /* region getReviewsForProduct(productId) */
    @Test
    fun `Should update database, update networkListener connection state to true and return Answer Success with data from database when fetching reviews from API succeeds`() {
        runBlockingTest {
            val reviews = listOf(Review(1, "1", 2f, "Review"))
            whenever(reviewsApi.getProductReviews(any())) doReturn Response.success(reviews)
            whenever(reviewsDao.getReviewsForProduct(any())) doReturn reviews

            val answer = reviewsRepo.getReviewsForProduct("1")

            verify(reviewsApi).getProductReviews(any())
            verify(reviewsDao).getReviewsForProduct(any())
            verify(networkListener).updateConnectionState(true)
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == reviews)
        }
    }

    @Test
    fun `Should update networkListener connection state to false and return Answer Success with reviews from database if fetching reviews from API fails because of a network issue and database is not empty`() {
        runBlockingTest {
            val reviews = listOf(Review(1, "1", 2f, "Review"))
            whenever(reviewsApi.getProductReviews(any())) doAnswer { throw SocketTimeoutException() } // Can be ConnectException or UnknownHostException
            whenever(reviewsDao.getReviewsForProduct(any())) doReturn reviews

            val answer = reviewsRepo.getReviewsForProduct("1")

            verify(reviewsApi).getProductReviews(any())
            verify(reviewsDao).getReviewsForProduct(any())
            verify(networkListener).updateConnectionState(false)
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == reviews)
        }
    }

    @Test
    fun `Should log error and return Answer Success with reviews from database if a server error occurred while fetching reviews from API and database is not empty`() {
        runBlockingTest {
            val reviews = listOf(Review(1, "1", 2f, "Review"))
            whenever(reviewsApi.getProductReviews(any())) doReturn Response.error(
                400,
                "".toResponseBody()
            )
            whenever(reviewsDao.getReviewsForProduct(any())) doReturn reviews

            val answer = reviewsRepo.getReviewsForProduct("1")

            verify(reviewsApi).getProductReviews(any())
            verify(errorHandler).handle(any())
            verify(reviewsDao).getReviewsForProduct(any())
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == reviews)
        }
    }

    @Test
    fun `Should log error and return Answer Failure with ServerResponseError if a server error occurred while fetching reviews from API and database is empty`() {
        runBlockingTest {
            whenever(reviewsApi.getProductReviews(any())) doReturn Response.error(
                400,
                "".toResponseBody()
            )
            whenever(reviewsDao.getReviewsForProduct(any())) doReturn listOf()

            val answer = reviewsRepo.getReviewsForProduct("1")

            verify(reviewsApi).getProductReviews(any())
            verify(errorHandler).handle(any())
            verify(reviewsDao).getReviewsForProduct(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is ServerResponseException)
        }
    }

    @Test
    fun `Should log error and return Answer Failure with error if an error occurred while fetching products from database`() {
        runBlockingTest {
            val reviews = listOf(Review(1, "1", 2f, "Review"))
            whenever(reviewsApi.getProductReviews(any())) doReturn Response.success(reviews)
            whenever(reviewsDao.getReviewsForProduct(any())) doAnswer { throw IllegalStateException() }

            val answer = reviewsRepo.getReviewsForProduct("1")

            verify(reviewsApi).getProductReviews(any())
            verify(reviewsDao).getReviewsForProduct(any())
            verify(errorHandler).handle(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is IllegalStateException)
        }
    }
    /* endregion */

    /* region addReview(review) */
    @Test
    fun `Should update review syncStatus to Pending, save to database and return Answer Success if adding the review to database succeeds`() {
        runBlockingTest {
            val review = Review(1, "1", 2f, "Review")

            val answer = reviewsRepo.addPendingReview(review)

            verify(reviewsDao).insert(any())
            assert(answer is Answer.Success)
            assert(review.syncStatus == Review.SyncStatus.PENDING)
        }
    }

    @Test
    fun `Should log error and return Answer Failure if adding review to database fails`() {
        runBlockingTest {
            val review = Review(1, "1", 2f, "Review")
            whenever(reviewsDao.insert(review)) doAnswer { throw IllegalStateException() }

            val answer = reviewsRepo.addPendingReview(review)

            verify(reviewsDao).insert(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is IllegalStateException)
        }
    }
    /* endregion */

    /* region getPendingReviews() */
    @Test
    fun `Should return Answer Success with the list of pending reviews if fetching the reviews from database succeeds`() {
        runBlockingTest {
            val reviews = listOf(Review(1, "1", 2f, "Review", Review.SyncStatus.PENDING))
            whenever(reviewsDao.getPendingReviews()) doReturn reviews

            val answer = reviewsRepo.getPendingReviews()

            verify(reviewsDao).getPendingReviews()
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == reviews)
        }
    }

    @Test
    fun `Should log error and return Answer Failure if fetching the pending review from database fails`() {
        runBlockingTest {
            whenever(reviewsDao.getPendingReviews()) doAnswer { throw IllegalStateException() }

            val answer = reviewsRepo.getPendingReviews()

            verify(reviewsDao).getPendingReviews()
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is IllegalStateException)
        }
    }
    /* endregion */

    /* region syncReview(review) */
    @Test
    fun `Should add the review to API, update the review syncStatus to SYNCED, update database, update the networkListener connectionState with ture and return an Answer Success if syncing succeeds`() {
        runBlockingTest {
            val review = Review(1, "1", 2f, "Review", Review.SyncStatus.PENDING)
            whenever(reviewsApi.addProductReview(any(), any(), any())) doReturn Response.success(Unit)

            val answer = reviewsRepo.syncReview(review)

            verify(reviewsApi).addProductReview(any(), any(), any())
            verify(reviewsDao).deleteReview(any())
            verify(reviewsDao).insert(any())
            verify(networkListener).updateConnectionState(true)
            assert(answer is Answer.Success)
            assert(review.syncStatus == Review.SyncStatus.SYNCED)
        }
    }

    @Test
    fun `Should log error and return Answer Failure if an error occurred while adding the review to the API`() {
        runBlockingTest {
            val review = Review(1, "1", 2f, "Review", Review.SyncStatus.PENDING)
            whenever(reviewsApi.addProductReview(any(), any(), any())) doReturn Response.error(400, "".toResponseBody())

            val answer = reviewsRepo.syncReview(review)

            verify(reviewsApi).addProductReview(any(), any(), any())
            verify(errorHandler).handle(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is ServerResponseException)
            // Check sync status didn't change when API call failed
            assert(review.syncStatus == Review.SyncStatus.PENDING)
        }
    }

    @Test
    fun `Should update networkListener connectionState with false and return Answer Failure if syncing review failed for network issu`() {
        runBlockingTest {
            val review = Review(1, "1", 2f, "Review", Review.SyncStatus.PENDING)
            whenever(reviewsApi.addProductReview(any(), any(), any())) doAnswer { throw SocketTimeoutException() } // Can be ConnectException or UnknownHostException

            val answer = reviewsRepo.syncReview(review)

            verify(reviewsApi).addProductReview(any(), any(), any())
            verify(networkListener).updateConnectionState(false)
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is SocketTimeoutException)
            // Check sync status didn't change when API call failed
            assert(review.syncStatus == Review.SyncStatus.PENDING)
        }
    }

    @Test
    fun `Should log error and return Answer Failure if API call succeeds but updating database fails`() {
        runBlockingTest {
            val review = Review(1, "1", 2f, "Review", Review.SyncStatus.PENDING)
            whenever(reviewsApi.addProductReview(any(), any(), any())) doReturn Response.success(Unit)
            // Same test should be valid for error occurring in reviewDao.insertReview(review)
            whenever(reviewsDao.deleteReview(any())) doAnswer { throw IllegalStateException() }

            val answer = reviewsRepo.syncReview(review)

            verify(reviewsApi).addProductReview(any(), any(), any())
            verify(reviewsDao).deleteReview(any())
            verify(errorHandler).handle(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is IllegalStateException)
        }
    }

    /* endregion */
}