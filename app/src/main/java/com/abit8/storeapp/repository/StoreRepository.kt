package com.abit8.storeapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abit8.storeapp.api.ApiClient
import com.abit8.storeapp.data.db.AppDatabase
import com.abit8.storeapp.data.entities.CategoryEntity
import com.abit8.storeapp.data.entities.ProductDetailsEntity
import com.abit8.storeapp.data.entities.ProductEntity
import com.abit8.storeapp.data.entities.RatingEntity
import com.abit8.storeapp.model.Category
import com.abit8.storeapp.model.Product
import com.abit8.storeapp.model.Rating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoreRepository(private val database: AppDatabase) {
    private val storeApiService = ApiClient.storeApiService
    private val _categories = MutableLiveData<List<Category>>()
    private val _products = MutableLiveData<List<Product>>()
    private val _selectedCategory = MutableLiveData<String>()
    private val _product = MutableLiveData<Product>()

    val categories: LiveData<List<Category>> get() = _categories
    val products: LiveData<List<Product>> get() = _products
    val selectedCategory: LiveData<String> get() = _selectedCategory
    val product: LiveData<Product> get() = _product

    //изменитель типа данных на Product
    private fun mapToProduct(productEntity: ProductEntity): Product {
        return Product(
            id = productEntity.id,
            title = productEntity.title,
            price = productEntity.price,
            description = productEntity.description,
            category = productEntity.category,
            image = productEntity.image,
            rating = Rating(productEntity.rating.rate, productEntity.rating.count)
        )
    }

    //тоже что и выше но с другой тип изменяет
    //ИМЕННО ЭТОТ МЕТОД НЕ ДАЕТ ПОКОЯ И НЕ РАБОТАЕТ, НАДО БУДЕТ ЕГО ДОРАБОТАТЬ,
    // null приходит все время
    private fun mapToProduct(productDetailsEntity: ProductDetailsEntity): Product {
        return Product(
            id = productDetailsEntity?.productId ?: 0L,
            title = productDetailsEntity?.title ?: "",
            price = productDetailsEntity?.price ?: 0.0,
            description = productDetailsEntity?.description ?: "",
            category = "",
            image = productDetailsEntity?.image ?: "",
            rating = Rating(0.0, 0)
        )
    }

    //это я забыл где, аа это для экрана продукта, исправлю код, тогда и использую
    suspend fun fetchProductById(id: Long) {
        withContext(Dispatchers.IO) {
            try {
                val product = storeApiService.getProductById(id)
                _product.postValue(product)

                // Сохранение продукта в бд
                saveProductDetails(
                    ProductDetailsEntity(
                        product.id,
                        product.title,
                        product.price,
                        product.description,
                        product.image
                    )
                )
            } catch (e: Exception) {
                // Обработка при отсутствии интернета
                // Загрузка продукта из бд
                val cachedProduct = getProductByIdFromCache(id)
                _product.postValue(cachedProduct)
            }
        }
    }


    suspend fun getProductByIdFromCache(productId: Long): Product {
        return withContext(Dispatchers.IO) {
            val productDetails = database.productDetailsDao().getProductDetailsById(productId)
            mapToProduct(productDetails)
        }
    }

    private suspend fun saveProductDetails(productDetails: ProductDetailsEntity) {
        withContext(Dispatchers.IO) {
            database.productDetailsDao().insertProductDetails(productDetails)
        }
    }

    //тоже потом
    private suspend fun getProductDetailsFromCache(productId: Long): ProductDetailsEntity {
        return withContext(Dispatchers.IO) {
            database.productDetailsDao().getProductDetailsById(productId)
        }
    }

    suspend fun fetchProductsByCategory(
        category: String,
        sort: String?,
        listener: OnProductsSavedListener?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val products = storeApiService.getProductsByCategory(category, sort)
                _products.postValue(products)
                _selectedCategory.postValue(category)

                // Сохранение продуктов в бд
                saveProducts(products.map {
                    ProductEntity(
                        it.id,
                        it.title,
                        it.price,
                        it.description,
                        it.category,
                        it.image,
                        RatingEntity(it.rating.rate, it.rating.count)
                    )
                })

                // Уведомление обратного вызова о завершении сохранения продуктов
                listener?.onProductsSaved()
            } catch (e: Exception) {
                // Обработка ошибки без интернета
                // Загрузка продуктов из бд
                val cachedProducts = getProductsByCategoryFromCache(category)
                val products = cachedProducts.map { mapToProduct(it) }
                _products.postValue(products)
                _selectedCategory.postValue(category)
            }
        }
    }


    suspend fun saveCategories(categories: List<CategoryEntity>) {
        withContext(Dispatchers.IO) {
            database.categoryDao().insertAll(categories)
        }
    }

    suspend fun getCategoriesFromCache(): List<CategoryEntity> {
        return withContext(Dispatchers.IO) {
            database.categoryDao().getAllCategories()
        }
    }

    suspend fun getProductById(id: Long): Product {
        return withContext(Dispatchers.IO) {
            storeApiService.getProductById(id)
        }
    }

    suspend fun saveProducts(products: List<ProductEntity>) {
        withContext(Dispatchers.IO) {
            database.productDao().insertAll(products)
        }
    }

    suspend fun getProductsByCategoryFromCache(category: String): List<ProductEntity> {
        return withContext(Dispatchers.IO) {
            database.productDao().getProductsByCategory(category)
        }
    }

    //обработчик
    interface OnProductsSavedListener {
        fun onProductsSaved()
    }
}
