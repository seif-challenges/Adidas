package tn.seif.adidaschallenge.data.remote

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import tn.seif.adidaschallenge.data.models.Review

/**
 * Retrofit service interface responsible for reviews operations.
 */
interface ReviewsApi {
    companion object {
        private const val PRODUCT_REVIEWS = "/reviews/{productId}"
    }

    @GET(PRODUCT_REVIEWS)
    suspend fun getProductReviews(
        @Path("productId")
        productId: String
    ): Response<List<Review>>

    @FormUrlEncoded
    @POST(PRODUCT_REVIEWS)
    suspend fun addProductReview(
        @Path("productId")
        productId: String,
        @Field("rating")
        rating: Float,
        @Field("text")
        text: String
    ): Response<Unit?>
}
