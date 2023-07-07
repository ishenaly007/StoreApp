package com.abit8.storeapp.data.entities

import androidx.room.TypeConverter
import com.google.gson.Gson

class RatingEntityConverter {
//конвертер для сортировки
    @TypeConverter
    fun fromRatingEntity(ratingEntity: RatingEntity): String {
        return Gson().toJson(ratingEntity)
    }

    @TypeConverter
    fun toRatingEntity(json: String): RatingEntity {
        return Gson().fromJson(json, RatingEntity::class.java)
    }
}
