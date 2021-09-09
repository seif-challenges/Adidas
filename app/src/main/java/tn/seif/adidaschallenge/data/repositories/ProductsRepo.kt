package tn.seif.adidaschallenge.data.repositories

import timber.log.Timber
import tn.seif.adidaschallenge.data.local.daos.ProductsDao
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.data.remote.ProductsApi
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
 * A repository responsible of handling [Product] data source.
 * Relies on server to fetch the data first and store it in a SQLite database.
 * In case of server unreachability, data will be fetched from local database.
 *
 * @property productsDao - An instance of [ProductsDao] responsible of handling database operations.
 * @property productsApi - An instance of [ProductsApi] responsible of handling api operations.
 * @property networkListener - An instance of [NetworkListener] to handle server reachability status.
 * @property errorHandler - An instance of [ErrorHandler] to handle errors.
 */
open class ProductsRepo @Inject constructor(
    private val productsDao: ProductsDao,
    private val productsApi: ProductsApi,
    private val networkListener: NetworkListener,
    private val errorHandler: ErrorHandler
) {

    /**
     * Gets the list of products from server first and store it in local database.
     * In case of server unreachability, data will be fetched from local database.
     *
     * @return - The list of [Product].
     */
    open suspend fun getProducts(): Answer<List<Product>, Exception> {
        return try {
            fetchProductsFromApi()
            Answer.Success(productsDao.getAllProducts())
        } catch (e: Exception) {
            errorHandler.handle(e)
            when (e) {
                is ServerResponseException -> {
                    val databaseData = productsDao.getAllProducts()

                    // return the API error only if the database is also empty
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
     * Search for product with name or description containing the keyword.
     *
     * @param keyword - The keyword searched.
     */
    open suspend fun searchProducts(keyword: String): Answer<List<Product>, Exception> {
        return try {
            Answer.Success(productsDao.getProductsFiltered(keyword))
        } catch (e: Exception) {
            errorHandler.handle(e)
            Answer.Failure(e)
        }
    }

    /**
     * Gets the product from server first and store it in local database.
     * In case of server unreachability, data will be fetched from local database.
     *
     * @param id - The id of the product.
     *
     * @return - The product having the [id].
     */
    open suspend fun getProductById(id: String): Answer<Product, Exception> {
        return try {
            fetchProductFromApi(id)
            productsDao.getProductById(id)?.let { Answer.Success(it) }
                ?: throw NullPointerException("Product can't be found")
        } catch (e: Exception) {
            // Print the exception stack trace, and log it in Crashlytics.
            errorHandler.handle(e)
            when (e) {
                is ServerResponseException -> {
                    val databaseData = productsDao.getProductById(id)

                    // return the API error only if the product can't be found in database as well
                    if (databaseData != null)
                        Answer.Success(databaseData)
                    else
                        Answer.Failure(e)
                }
                else -> Answer.Failure(e)
            }
        }
    }

    private suspend fun fetchProductsFromApi() {
        try {
            when (val answer = productsApi.getProducts().requestAnswer()) {
                is Answer.Success -> {
                    updateDatabase(answer.result)
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

    private suspend fun fetchProductFromApi(id: String) {
        try {
            when (val answer = productsApi.getProductById(id).requestAnswer()) {
                is Answer.Success -> {
                    updateDatabase(answer.result)
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

    private suspend fun updateDatabase(products: List<Product>) {
        productsDao.deleteAll()
        productsDao.insertAll(products)
    }

    private suspend fun updateDatabase(product: Product) {
        productsDao.delete(product.id)
        productsDao.insert(product)
    }
}