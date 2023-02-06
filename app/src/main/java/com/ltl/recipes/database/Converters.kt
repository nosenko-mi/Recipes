package com.ltl.recipes.database

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.utils.GsonParser
import java.util.*

class Converters {

    private val jsonParser = GsonParser(Gson())

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toIngredientJson(ingredients: List<Ingredient>) : String {
        return jsonParser.toJson(
            ingredients,
            object : TypeToken<ArrayList<Ingredient>>(){}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun fromIngredientJson(json: String): List<Ingredient>{
        return jsonParser.fromJson<ArrayList<Ingredient>>(
            json,
            object: TypeToken<ArrayList<Ingredient>>(){}.type
        ) ?: emptyList()
    }
}