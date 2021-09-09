package tn.seif.adidaschallenge.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tn.seif.adidaschallenge.data.models.Product

/**
 * Room DAO responsible for products operations.
 */
@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Query("DELETE FROM Product")
    suspend fun deleteAll()

    @Query("DELETE FROM Product WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM Product")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM Product WHERE name LIKE '%'||:keyword||'%' OR description LIKE '%'||:keyword||'%'")
    suspend fun getProductsFiltered(keyword: String): List<Product>

    @Query("SELECT * FROM Product WHERE id = :id")
    suspend fun getProductById(id: String): Product?
}