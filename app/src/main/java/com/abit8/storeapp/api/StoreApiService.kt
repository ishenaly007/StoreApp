package com.abit8.storeapp.api

import com.abit8.storeapp.model.Product
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreApiService {

    //запросы

    //запрос на категории
    @GET("products/categories")
    suspend fun getCategories(): List<String>

    //запрос на список продуктов
    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String,
        @Query("sort") sort: String?
    ): List<Product>

    //запрос на продукт
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Product
}
