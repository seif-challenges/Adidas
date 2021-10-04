package tn.seif.adidaschallenge.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Product(
    @PrimaryKey var id: String,
    var name: String,
    val currency: String,
    val price: Double,
    val description: String,
    val imgUrl: String
)

val product = Product("1", "", "", 0.0, "", "")
val product2 = product.copy(id = "2")
