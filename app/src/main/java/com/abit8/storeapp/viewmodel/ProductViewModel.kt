package com.abit8.storeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abit8.storeapp.model.Product
import com.abit8.storeapp.repository.StoreRepository


class ProductViewModel(private val repository: StoreRepository) : ViewModel() {
    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> get() = _product

    suspend fun fetchProductById(id: Long) {
        val product = repository.getProductById(id)
        _product.postValue(product)
    }

    fun setProduct(product: Product) {
        _product.value = product
    }
}
