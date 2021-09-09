package tn.seif.adidaschallenge.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tn.seif.adidaschallenge.data.local.daos.ProductsDao
import tn.seif.adidaschallenge.data.local.daos.ReviewsDao
import tn.seif.adidaschallenge.data.models.Product
import tn.seif.adidaschallenge.data.models.Review

@Database(
    entities = [
        Product::class,
        Review::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDao
    abstract fun reviewsDao(): ReviewsDao

    companion object {
        fun build(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, "AdidasDb")
                .build()
        }
    }
}
