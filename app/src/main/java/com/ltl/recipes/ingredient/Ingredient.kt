package com.ltl.recipes.ingredient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ltl.recipes.utils.Writable
import org.json.JSONObject

class Ingredient(
    var title: String = "abstract",
    var amount: Int = 1
)
    : Writable
{
    override fun toJson(): JSONObject {
        val gson = Gson()
        val jsonIngredient: String = gson.toJson(this)

//        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
//        val jsonIngredientPretty: String = gsonPretty.toJson(this)

        return JSONObject(jsonIngredient)
    }
}