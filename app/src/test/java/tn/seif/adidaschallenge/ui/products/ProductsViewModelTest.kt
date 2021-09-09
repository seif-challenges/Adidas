package tn.seif.adidaschallenge.ui.products

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.kotlin.*
import tn.seif.adidaschallenge.BaseTest
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.data.remote.ServerResponseException
import tn.seif.adidaschallenge.data.repositories.ProductsRepo
import tn.seif.adidaschallenge.utils.models.Answer
import tn.seif.adidaschallenge.utils.models.State

@ExperimentalCoroutinesApi
class ProductsViewModelTest : BaseTest() {

    private val productsRepo: ProductsRepo = mock()
    private val vm = ProductsViewModel(productsRepo)

    /* region success */
    @Test
    fun `products livedata should post a State Success with the fetched products on success`() {
        runBlockingTest {
            val products = listOf(Product("1", "Test name", "$", 50.0, "Test description", "url"))
            whenever(productsRepo.getProducts()) doReturn Answer.Success(products)

            vm.getProducts()

            verify(productsRepo).getProducts()
            assert(vm.products.value is State.Success)
            assert((vm.products.value as State.Success).result == products)
        }
    }

    @Test
    fun `products livedata should post a State Success with the filtered list of products`() {
        runBlockingTest {
            val searchedProduct = Product("2", "Test name 2", "$", 50.0, "Test description 2", "url")
            val products = listOf(searchedProduct)
            val keyword = "2"
            whenever(productsRepo.searchProducts(any())) doReturn Answer.Success(products)

            vm.getProducts(keyword)

            // verify(productsRepo).searchProducts(any())
            assert(vm.products.value is State.Success)
            assert((vm.products.value as State.Success).result.first().id == searchedProduct.id)
        }
    }
    /* endregion */

    /* region errors */
    @Test
    fun `products livedata should post a State Error when fetching the products fails`() {
        runBlockingTest {
            whenever(productsRepo.getProducts()) doReturn Answer.Failure(ServerResponseException(0, ""))

            vm.getProducts()

            verify(productsRepo).getProducts()
            assert(vm.products.value is State.Error)
            assert((vm.products.value as State.Error).error is ServerResponseException)
        }
    }

    @Test
    fun `products livedata should post a State Error when fetching filtered products fails`() {
        runBlockingTest {
            whenever(productsRepo.searchProducts(any())) doReturn Answer.Failure(ServerResponseException(0, ""))

            val keyword = "2"
            vm.getProducts(keyword)

            verify(productsRepo).searchProducts(any())
            assert(vm.products.value is State.Error)
            assert((vm.products.value as State.Error).error is ServerResponseException)
        }
    }
    /* endregion */
}
