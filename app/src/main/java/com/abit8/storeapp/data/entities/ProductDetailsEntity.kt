package com.abit8.storeapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_details")
data class ProductDetailsEntity(
    @PrimaryKey val productId: Long,
    val title: String,
    val price: Double,
    val description: String,
    val image: String
)