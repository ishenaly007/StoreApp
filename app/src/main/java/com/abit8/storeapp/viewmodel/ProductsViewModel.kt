package com.abit8.storeapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abit8.storeapp.model.Product
import com.abit8.storeapp.model.Rating
import com.abit8.storeapp.repository.StoreRepository

class ProductsViewModel(private val repository: StoreRepository) : ViewModel() {
    val products: LiveData<List<Product>> get() = repository.products
    val selectedCategory: LiveData<String> get() = repository.selectedCategory

    private val _products = MutableLiveData<List<Product>>()
    private val _selectedCategory = MutableLiveData<String>()

    val cachedProducts: LiveData<List<Product>> get() = _products
    val cachedSelectedCategory: LiveData<String> get() = _selectedCategory

    suspend fun fetchProductsByCategory(category: String, sort: String?, listener: StoreRepository.OnProductsSavedListener? = null) {
        repository.fetchProductsByCategory(category, sort, listener)
    }

    suspend fun getProductsFromCacheByCategory(category: String) {
        val products = repository.getProductsByCategoryFromCache(category)
        val productList = products.map { Product(it.id, it.title, it.price, it.description, it.category, it.image, Rating(it.rating.rate, it.rating.count)) }
        _products.postValue(productList)
        _selectedCategory.postValue(category)
    }
}


