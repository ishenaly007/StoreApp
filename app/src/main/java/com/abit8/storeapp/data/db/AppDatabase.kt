package com.abit8.storeapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abit8.storeapp.data.dao.CategoryDao
import com.abit8.storeapp.data.dao.ProductDao
import com.abit8.storeapp.data.dao.ProductDetailsDao
import com.abit8.storeapp.data.entities.CategoryEntity
import com.abit8.storeapp.data.entities.ProductEntity
import com.abit8.storeapp.data.entities.ProductDetailsEntity

//пришлось 3 раза версию менять из за изменении
@Database(entities = [CategoryEntity::class, ProductEntity::class, ProductDetailsEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun productDetailsDao(): ProductDetailsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "new2_store_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
