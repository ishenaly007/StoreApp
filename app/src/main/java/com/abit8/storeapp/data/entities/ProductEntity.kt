package com.abit8.storeapp.data.entities



import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "products")
@TypeConverters(RatingEntityConverter::class)
data class ProductEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingEntity
)

