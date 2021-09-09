package tn.seif.adidaschallenge.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import tn.seif.adidaschallenge.data.models.Product

/**
 * Retrofit service interface responsible for products operations.
 */
interface ProductsApi {
    companion object {
        private const val GET_PRODUCTS = "/product"
        private const val GET_PRODUCT_ID = "/product/{id}"
    }

    @GET(GET_PRODUCTS)
    suspend fun getProducts(): Response<List<Product>>

    @GET(GET_PRODUCT_ID)
    suspend fun getProductById(
        @Path("id")
        id: String
    ): Response<Product>

}