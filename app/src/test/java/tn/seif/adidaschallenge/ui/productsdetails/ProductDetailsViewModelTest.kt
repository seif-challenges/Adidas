package tn.seif.adidaschallenge.ui.productsdetails

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import tn.seif.adidaschallenge.BaseTest
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.data.models.Review
import tn.seif.adidaschallenge.data.remote.ServerResponseException
import tn.seif.adidaschallenge.data.repositories.ProductsRepo
import tn.seif.adidaschallenge.data.repositories.ReviewsRepo
import tn.seif.adidaschallenge.utils.models.Answer
import tn.seif.adidaschallenge.utils.models.State

@ExperimentalCoroutinesApi
class ProductDetailsViewModelTest : BaseTest() {

    private val productsRepo: ProductsRepo = mock()
    private val reviewRepo: ReviewsRepo = mock()

    private val vm = ProductDetailsViewModel(productsRepo, reviewRepo)

    /* region success */
    @Test
    fun `product livedata should post State Success when fetching the product succeed`() {
        runBlockingTest {
            val product = Product("1", "Test name", "$", 50.0, "Test description", "url")
            whenever(productsRepo.getProductById(any())) doReturn Answer.Success(product)

            vm.getProductById("1")

            verify(productsRepo).getProductById(any())
            assert(vm.product.value is State.Success)
            assert((vm.product.value as State.Success).result == product)
        }
    }

    @Test
    fun `reviews livedata should post State Success when fetching the product's reviews succeed`() {
        runBlockingTest {
            val reviews = listOf(Review(1, "1", 2f, "Review"))
            whenever(reviewRepo.getReviewsForProduct(any())) doReturn Answer.Success(reviews)

            vm.getProductReviews("1")

            verify(reviewRepo).getReviewsForProduct(any())
            assert(vm.reviews.value is State.Success)
            assert((vm.reviews.value as State.Success).result == reviews)
        }
    }

    @Test
    fun `addReview livedata should post State Success when adding a new review succeed`() {
        runBlockingTest {
            whenever(reviewRepo.addPendingReview(any())) doReturn Answer.Success(Unit)

            vm.addReview("1", 3f, "Review")

            verify(reviewRepo).addPendingReview(any())
            assert(vm.addReview.value is State.Success)
        }
    }
    /* endregion */

    /* region error */
    @Test
    fun `product livedata should post State Error when fetching the product fails`() {
        runBlockingTest {
            whenever(productsRepo.getProductById(any())) doReturn Answer.Failure(ServerResponseException(0, ""))

            vm.getProductById("1")

            verify(productsRepo).getProductById(any())
            assert(vm.product.value is State.Error)
            assert((vm.product.value as State.Error).error is ServerResponseException)
        }
    }

    @Test
    fun `reviews livedata should post State Error when fetching the product's reviews fails`() {
        runBlockingTest {
            whenever(reviewRepo.getReviewsForProduct(any())) doReturn Answer.Failure(ServerResponseException(0, ""))

            vm.getProductReviews("1")

            verify(reviewRepo).getReviewsForProduct(any())
            assert(vm.reviews.value is State.Error)
            assert((vm.reviews.value as State.Error).error is ServerResponseException)
        }
    }

    @Test
    fun `addReview livedata should post State Error when adding a new review fails`() {
        runBlockingTest {
            whenever(reviewRepo.addPendingReview(any())) doReturn Answer.Failure(ServerResponseException(0, ""))

            vm.addReview("1", 3f, "Review")

            verify(reviewRepo).addPendingReview(any())
            assert(vm.addReview.value is State.Error)
            assert((vm.addReview.value as State.Error).error is ServerResponseException)
        }
    }
    /* endregion */
}
