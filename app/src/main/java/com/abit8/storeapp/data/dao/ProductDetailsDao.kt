package com.abit8.storeapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abit8.storeapp.data.entities.ProductDetailsEntity

@Dao
interface ProductDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductDetails(productDetails: ProductDetailsEntity)

    @Query("SELECT * FROM product_details WHERE productId = :productId")
    suspend fun getProductDetailsById(productId: Long): ProductDetailsEntity
}
