package com.ltl.recipes.recipe

import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.Gson
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.utils.Validatable
import com.ltl.recipes.utils.Writable
import org.json.JSONObject
import java.util.*

var recipeList = mutableListOf<Recipe>()

data class Recipe (
    var coverImg: Int = 0,
    var author: String = "",
    @ServerTimestamp
    var createdAt: Date? = null,
    var imgRef: String = "name.jpg",
    var title: String = "",
    var description: String = "",
    var isPublic: Boolean = false,
    var servingsNum: Int = 1,
    var ingredients: List<Ingredient> = emptyList(),
    var steps: String = "How to cook: \nFirstly, ... \nSecondly, ,,,\nFinally, ...",
    var tags: List<String> = emptyList(),
    val id: Int? = recipeList.size
)
    : Writable, Validatable
{

    override fun isValid(): Boolean {
        return title != "" && ingredients.isNotEmpty()
    }

    override fun toJson(): JSONObject {
        val gson = Gson()
        val jsonRecipe: String = gson.toJson(this)
        return JSONObject(jsonRecipe)
    }
}