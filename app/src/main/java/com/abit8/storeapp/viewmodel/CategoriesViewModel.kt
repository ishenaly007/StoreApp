package com.abit8.storeapp.viewmodel

import  androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abit8.storeapp.api.ApiClient.storeApiService
import com.abit8.storeapp.data.entities.CategoryEntity
import com.abit8.storeapp.model.Category
import com.abit8.storeapp.repository.StoreRepository

class CategoriesViewModel(private val repository: StoreRepository) : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    suspend fun fetchCategories() {
        val cachedCategories = repository.getCategoriesFromCache()
        if (cachedCategories.isNotEmpty()) {
            _categories.postValue(cachedCategories.map { Category(it.name) })
        } else {
            val categories = storeApiService.getCategories()
            val categoryEntities = categories.map { CategoryEntity(it) }
            repository.saveCategories(categoryEntities)
            _categories.postValue(categories.map { Category(it) })
        }
    }
}
