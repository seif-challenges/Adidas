package tn.seif.adidaschallenge.data.repositories

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.Response
import tn.seif.adidaschallenge.BaseTest
import tn.seif.adidaschallenge.data.local.daos.ProductsDao
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.data.remote.ProductsApi
import tn.seif.adidaschallenge.data.remote.ServerResponseException
import tn.seif.adidaschallenge.utils.ErrorHandler
import tn.seif.adidaschallenge.utils.NetworkListener
import tn.seif.adidaschallenge.utils.models.Answer
import java.net.SocketTimeoutException

@ExperimentalCoroutinesApi
class ProductsRepoTest : BaseTest() {

    private val productsDao: ProductsDao = mock()
    private val productsApi: ProductsApi = mock()
    private val networkListener: NetworkListener = mock()
    private val errorHandler: ErrorHandler = mock()

    private val productsRepo = ProductsRepo(productsDao, productsApi, networkListener, errorHandler)

   /* region getProducts() */
    @Test
    fun `Should update database, update networkListener connection state to true and return Answer Success with data from database when fetching products from API succeeds`() {
        runBlockingTest {
            val products = listOf(Product("1", "Test name", "$", 50.0, "Test description", "url"))
            whenever(productsApi.getProducts()) doReturn Response.success(products)
            whenever(productsDao.getAllProducts()) doReturn products

            val answer = productsRepo.getProducts()

            verify(productsApi).getProducts()
            verify(productsDao).getAllProducts()
            verify(networkListener).updateConnectionState(true)
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == products)
        }
    }

    @Test
    fun `Should update networkListener connection state to false and return Answer Success with products from database if fetching products from API fails because of a network issue and database is not empty`() {
        runBlockingTest {
            val products = listOf(Product("1", "Test name", "$", 50.0, "Test description", "url"))
            whenever(productsApi.getProducts()) doAnswer { throw SocketTimeoutException() } // Can be ConnectException or UnknownHostException
            whenever(productsDao.getAllProducts()) doReturn products

            val answer = productsRepo.getProducts()

            verify(productsApi).getProducts()
            verify(productsDao).getAllProducts()
            verify(networkListener).updateConnectionState(false)
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == products)
        }
    }

    @Test
    fun `Should log error and return Answer Success with products from database if a server error occurred while fetching products from API and database is not empty`() {
        runBlockingTest {
            val products = listOf(Product("1", "Test name", "$", 50.0, "Test description", "url"))
            whenever(productsApi.getProducts()) doReturn Response.error(400, "".toResponseBody())
            whenever(productsDao.getAllProducts()) doReturn products

            val answer = productsRepo.getProducts()

            verify(productsApi).getProducts()
            verify(errorHandler).handle(any())
            verify(productsDao).getAllProducts()
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == products)
        }
    }

    @Test
    fun `Should log error and return Answer Failure with ServerResponseError if a server error occurred while fetching products from API and database is empty`() {
        runBlockingTest {
            whenever(productsApi.getProducts()) doReturn Response.error(400, "".toResponseBody())
            whenever(productsDao.getAllProducts()) doReturn listOf()

            val answer = productsRepo.getProducts()

            verify(productsApi).getProducts()
            verify(errorHandler).handle(any())
            verify(productsDao).getAllProducts()
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is ServerResponseException)
        }
    }

    @Test
    fun `Should log error and return Answer Failure with error if an error occurred while fetching products from database`() {
        runBlockingTest {
            val products = listOf(Product("1", "Test name", "$", 50.0, "Test description", "url"))
            whenever(productsApi.getProducts()) doReturn Response.success(products)
            whenever(productsDao.getAllProducts()) doAnswer { throw IllegalStateException() }

            val answer = productsRepo.getProducts()

            verify(productsApi).getProducts()
            verify(productsDao).getAllProducts()
            verify(errorHandler).handle(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is IllegalStateException)
        }
    }
    /* endregion */

    /* region searchProducts(keyword) */
    @Test
    fun `Should return Answer Success with the filtered list of products if fetching data from database succeeds`() {
        runBlockingTest {
            val products = listOf(Product("1", "Test name", "$", 50.0, "Test description", "url"))
            whenever(productsDao.getProductsFiltered(any())) doReturn products

            val answer = productsRepo.searchProducts("test")

            verify(productsDao).getProductsFiltered(any())
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == products)
        }
    }

    @Test
    fun `Should return Answer Failure if fetching data from database fails`() {
        runBlockingTest {
            whenever(productsDao.getProductsFiltered(any())) doAnswer { throw IllegalStateException() }

            val answer = productsRepo.searchProducts("test")

            verify(productsDao).getProductsFiltered(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is IllegalStateException)
        }
    }
    /* endregion */

    /* region getProductById(id) */
    @Test
    fun `Should update database, update networkListener connection state to true and return Answer Success with data from database when fetching the product from API succeeds`() {
        runBlockingTest {
            val product = Product("1", "Test name", "$", 50.0, "Test description", "url")
            whenever(productsApi.getProductById(any())) doReturn Response.success(product)
            whenever(productsDao.getProductById(any())) doReturn product

            val answer = productsRepo.getProductById("1")

            verify(productsApi).getProductById(any())
            verify(productsDao).getProductById(any())
            verify(networkListener).updateConnectionState(true)
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == product)
        }
    }

    @Test
    fun `Should update networkListener connection state to false and return Answer Success with product from database if fetching the product from API fails because of a network issue and database is not empty`() {
        runBlockingTest {
            val product = Product("1", "Test name", "$", 50.0, "Test description", "url")
            whenever(productsApi.getProductById(any())) doAnswer { throw SocketTimeoutException() } // Can be ConnectException or UnknownHostException
            whenever(productsDao.getProductById(any())) doReturn product

            val answer = productsRepo.getProductById("1")

            verify(productsApi).getProductById(any())
            verify(productsDao).getProductById(any())
            verify(networkListener).updateConnectionState(false)
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == product)
        }
    }

    @Test
    fun `Should log error and return Answer Success with product from database if a server error occurred while fetching the product from API and database is not emoty`() {
        runBlockingTest {
            val product = Product("1", "Test name", "$", 50.0, "Test description", "url")
            whenever(productsApi.getProductById(any())) doReturn Response.error(400, "".toResponseBody())
            whenever(productsDao.getProductById(any())) doReturn product

            val answer = productsRepo.getProductById("1")

            verify(productsApi).getProductById(any())
            verify(errorHandler).handle(any())
            verify(productsDao).getProductById(any())
            assert(answer is Answer.Success)
            assert((answer as Answer.Success).result == product)
        }
    }

    @Test
    fun `Should log error and return Answer Failure with ServerResponseError if a server error occurred while fetching the product from API and can't be found in database`() {
        runBlockingTest {
            whenever(productsApi.getProductById(any())) doReturn Response.error(400, "".toResponseBody())
            whenever(productsDao.getProductById(any())) doReturn null

            val answer = productsRepo.getProductById("1")

            verify(productsApi).getProductById(any())
            verify(errorHandler).handle(any())
            verify(productsDao).getProductById(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is ServerResponseException)
        }
    }

    @Test
    fun `Should log error and return Answer Failure with error if an error occurred while fetching the product from database`() {
        runBlockingTest {
            val product = Product("1", "Test name", "$", 50.0, "Test description", "url")
            whenever(productsApi.getProductById(any())) doReturn Response.success(product)
            whenever(productsDao.getProductById(any())) doAnswer { throw IllegalStateException() }

            val answer = productsRepo.getProductById("1")

            verify(productsApi).getProductById(any())
            verify(productsDao).getProductById(any())
            verify(errorHandler).handle(any())
            assert(answer is Answer.Failure)
            assert((answer as Answer.Failure).error is IllegalStateException)
        }
    }
    /* endregion */
}