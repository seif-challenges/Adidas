package tn.seif.adidaschallenge.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.data.repositories.ProductsRepo
import tn.seif.adidaschallenge.mvvm.base.BaseViewModel
import tn.seif.adidaschallenge.utils.DispatcherHelper
import tn.seif.adidaschallenge.utils.models.Answer
import tn.seif.adidaschallenge.utils.models.State
import javax.inject.Inject

/**
 * View model responsible for managing [ProductsFragment] business operations.
 *
 * @property productsRepo - An instance of [ProductsRepo].
 */
class ProductsViewModel @Inject constructor(
    private val productsRepo: ProductsRepo
) : BaseViewModel() {

    private val _products = MutableLiveData<State<List<Product>, Exception>>()

    /**
     * A [LiveData] instance notifying observers of the different states of the products fetching operation.
     */
    val products: LiveData<State<List<Product>, Exception>> = _products

    /**
     * Gets the list of products. If the [keyword] is empty, no filtration will be applied.
     */
    fun getProducts(keyword: String = "") {
        _products.postValue(State.Loading())
        viewModelScope.launch(DispatcherHelper.dispatcher) {
            val answer = if (keyword.isEmpty()) {
                productsRepo.getProducts()
            } else {
                productsRepo.searchProducts(keyword)
            }
            when (answer) {
                is Answer.Success -> _products.postValue(State.Success(answer.result))
                is Answer.Failure -> _products.postValue(State.Error(answer.error))
            }
        }
    }
}