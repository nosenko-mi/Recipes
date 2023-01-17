package com.ltl.recipes.recipe

import com.google.gson.Gson
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.utils.Writable
import org.json.JSONObject

var recipeList = mutableListOf<Recipe>()

class Recipe (
    var coverImg: Int = 0,
    var title: String = "abstract",
    var description: String = "abstract",
    var isPublic: Boolean = false,
    var servingsNum: Int = 1,
    var ingredients: List<Ingredient> = listOf(Ingredient("i1", 1f), Ingredient("i2", 2f)),
    var steps: List<String> = listOf("step1", "step2", "step3"),
    var tags: List<String> = emptyList(),
    val id: Int? = recipeList.size
        )
    : Writable
{

    override fun toJson(): JSONObject {
        val gson = Gson()
        val jsonRecipe: String = gson.toJson(this)
        return JSONObject(jsonRecipe)
    }
}