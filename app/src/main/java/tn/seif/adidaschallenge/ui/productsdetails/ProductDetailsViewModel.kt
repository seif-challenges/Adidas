package tn.seif.adidaschallenge.ui.productsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.data.models.Review
import tn.seif.adidaschallenge.data.repositories.ProductsRepo
import tn.seif.adidaschallenge.data.repositories.ReviewsRepo
import tn.seif.adidaschallenge.mvvm.base.BaseViewModel
import tn.seif.adidaschallenge.utils.DispatcherHelper
import tn.seif.adidaschallenge.utils.models.Answer
import tn.seif.adidaschallenge.utils.models.State
import javax.inject.Inject

/**
 * View model responsible for managing [ProductDetailsFragment] business operations.
 *
 * @property productsRepo - An instance of [ProductsRepo].
 * @property reviewsRepo - An instance of [ReviewsRepo].
 */
class ProductDetailsViewModel @Inject constructor(
    private val productsRepo: ProductsRepo,
    private val reviewsRepo: ReviewsRepo
) : BaseViewModel() {

    private val _product = MutableLiveData<State<Product, Exception>>()

    /**
     * A [LiveData] instance notifying observers of the different states of the product fetching operation.
     */
    val product: LiveData<State<Product, Exception>> = _product

    private val _reviews = MutableLiveData<State<List<Review>, Exception>>()

    /**
     * A [LiveData] instance notifying observers of the different states of the reviews fetching operation.
     */
    val reviews: LiveData<State<List<Review>, Exception>> = _reviews

    private val _addReview = MutableLiveData<State<Unit, Exception>>()

    /**
     * A [LiveData] instance notifying observers of the different states of the add review operation.
     */
    val addReview: LiveData<State<Unit, Exception>> = _addReview

    /**
     * Gets the product with the provided [id].
     *
     * @param id - The id of the produce.
     */
    fun getProductById(id: String) {
        _product.postValue(State.Loading())
        viewModelScope.launch(DispatcherHelper.dispatcher) {
            when (val answer = productsRepo.getProductById(id)) {
                is Answer.Success -> _product.postValue(State.Success(answer.result))
                is Answer.Failure -> _product.postValue(State.Error(answer.error))
            }
        }
    }

    /**
     * Gets the lis of [Review]s for the product with id [productId].
     *
     * @param productId - The id of the product.
     */
    fun getProductReviews(productId: String) {
        _reviews.postValue(State.Loading())
        viewModelScope.launch(DispatcherHelper.dispatcher) {
            when (val answer = reviewsRepo.getReviewsForProduct(productId)) {
                is Answer.Success -> _reviews.postValue(State.Success(answer.result))
                is Answer.Failure -> _reviews.postValue(State.Error(answer.error))
            }
        }
    }

    /**
     * Adds a review to the database, with a pending sync state.
     *
     * @param productId - The id of the product.
     * @param rating - The rating value.
     * @param text - The message.
     */
    fun addReview(productId: String, rating: Float, text: String) {
        _addReview.postValue(State.Loading())
        viewModelScope.launch(DispatcherHelper.dispatcher) {
            // Default value for sync state is PENDING.
            val review = Review(0, productId, rating, text)
            when (val answer = reviewsRepo.addPendingReview(review)) {
                is Answer.Success -> _addReview.postValue(State.Success(Unit))
                is Answer.Failure -> _addReview.postValue(State.Error(answer.error))
            }
        }
    }
}
