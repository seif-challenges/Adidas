package tn.seif.adidaschallenge.di

import dagger.Module
import dagger.Provides
import tn.seif.adidaschallenge.data.local.AppDatabase
import tn.seif.adidaschallenge.data.local.daos.ProductsDao
import tn.seif.adidaschallenge.data.local.daos.ReviewsDao
import javax.inject.Singleton

/**
 * Provides database related dependencies.
 */
@Module
class DatabaseModule(private val database: AppDatabase) {

    /**
     * Function to provide an instance of [AppDatabase] to Dagger.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        return database
    }

    /**
     * Function to provide an instance of [ProductsDao] to Dagger.
     */
    @Provides
    @Singleton
    fun provideProductsDao(): ProductsDao {
        return database.productsDao()
    }

    /**
     * Function to provide an instance of [ReviewsDao] to Dagger.
     */
    @Provides
    @Singleton
    fun provideReviewsDao(): ReviewsDao {
        return database.reviewsDao()
    }
}
