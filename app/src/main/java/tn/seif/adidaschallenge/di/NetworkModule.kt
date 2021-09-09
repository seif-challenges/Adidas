package tn.seif.adidaschallenge.di

import android.app.Application
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import tn.seif.adidaschallenge.BuildConfig
import tn.seif.adidaschallenge.data.remote.ProductsApi
import tn.seif.adidaschallenge.data.remote.ReviewsApi
import javax.inject.Named
import javax.inject.Singleton

/**
 * Provides network related dependencies.
 */
@Module
class NetworkModule {

    /**
     * Function to provide the Products base url to dagger.
     */
    @Named(PRODUCTS_BASE_URL_DEP_NAME)
    @Provides
    @Singleton
    internal fun providesProductsBaseUrl(): String {
        return "${BuildConfig.API_BASE_URL}:${BuildConfig.PRODUCTS_PORT}/"
    }

    /**
     * Function to provide the Reviews base url to Dagger.
     */
    @Named(REVIEWS_BASE_URL_DEP_NAME)
    @Provides
    @Singleton
    internal fun providesReviewsBaseUrl(): String {
        return "${BuildConfig.API_BASE_URL}:${BuildConfig.REVIEWS_PORT}/"
    }

    /**
     * Function to provide an instance of [Cache] to Dagger.
     */
    @Provides
    @Singleton
    internal fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    /**
     * Function to provide an instance of [Json] to Dagger.
     */
    @Provides
    @Singleton
    internal fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    /**
     * Function to provide an instance of [OkHttpClient] to Dagger.
     */
    @Provides
    @Singleton
    internal fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder().run {
            cache(cache)
            addInterceptor(logging)
        }.build()
    }

    /**
     * Function to provide an instance of [Retrofit] for products to Dagger.
     */
    @Named(PRODUCTS_RETROFIT_DEP_NAME)
    @ExperimentalSerializationApi
    @Provides
    @Singleton
    internal fun provideProductsRetrofit(
        json: Json,
        okHttpClient: OkHttpClient,
        @Named(PRODUCTS_BASE_URL_DEP_NAME) productsBaseUrl: String
    ): Retrofit {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        return Retrofit.Builder()
            .baseUrl(productsBaseUrl)
            .addConverterFactory(json.asConverterFactory(mediaType))
            .client(okHttpClient)
            .build()
    }

    /**
     * Function to provide an instance of [Retrofit] for reviews to Dagger.
     */
    @Named(REVIEWS_RETROFIT_DEP_NAME)
    @ExperimentalSerializationApi
    @Provides
    @Singleton
    internal fun provideReviewsRetrofit(
        json: Json,
        okHttpClient: OkHttpClient,
        @Named(REVIEWS_BASE_URL_DEP_NAME) reviewsBaseUrl: String
    ): Retrofit {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        return Retrofit.Builder()
            .baseUrl(reviewsBaseUrl)
            .addConverterFactory(json.asConverterFactory(mediaType))
            .client(okHttpClient)
            .build()
    }

    /**
     * Function to provide an instance of [ProductsApi] to Dagger.
     */
    @Provides
    fun provideProductsApi(
        @Named(PRODUCTS_RETROFIT_DEP_NAME)
        productsRetrofit: Retrofit
    ): ProductsApi = productsRetrofit.create(ProductsApi::class.java)

    /**
     * Function to provide an instance of [ReviewsApi] to Dagger.
     */
    @Provides
    fun provideReviewsApi(
        @Named(REVIEWS_RETROFIT_DEP_NAME)
        reviewsRetrofit: Retrofit
    ): ReviewsApi = reviewsRetrofit.create(ReviewsApi::class.java)

    companion object {
        private const val PRODUCTS_BASE_URL_DEP_NAME = "PRODUCTS_BASE_URL"
        private const val REVIEWS_BASE_URL_DEP_NAME = "REVIEWS_BASE_URL"

        private const val PRODUCTS_RETROFIT_DEP_NAME = "PRODUCTS_RETROFIT"
        private const val REVIEWS_RETROFIT_DEP_NAME = "REVIEWS_RETROFIT"
    }
}