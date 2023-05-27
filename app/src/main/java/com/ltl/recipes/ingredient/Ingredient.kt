package com.ltl.recipes.ingredient

import com.google.gson.Gson
import com.ltl.recipes.utils.Writable
import org.json.JSONObject

@kotlinx.serialization.Serializable
data class Ingredient(
    var title: String = "",
    var qty: Float = 1f,
    var qtyType: QuantityType = QuantityType.NONE
)
    : Writable, java.io.Serializable
{
    override fun toJson(): JSONObject {
        val gson = Gson()
        val jsonIngredient: String = gson.toJson(this)

//        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
//        val jsonIngredientPretty: String = gsonPretty.toJson(this)

        return JSONObject(jsonIngredient)
    }

    fun isValid(): Boolean {
        if (title.isEmpty() || qty.isNaN() || qty < 0){
            return false
        }
        return true
    }
}