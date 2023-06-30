package com.ltl.recipes.data.recipe

import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.Gson
import com.ltl.recipes.constants.AppConstants
import com.ltl.recipes.database.recipe.RecipeEntity
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.utils.Validatable
import com.ltl.recipes.utils.Writable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.util.*

@Serializable
data class Recipe (
    val id: String = UUID.randomUUID().toString(),
    var coverImg: Int = 0,
    var author: String = "",
    @ServerTimestamp
    @Contextual
    var createdAt: Date? = null,
    var imgRef: String = AppConstants.defaultImgRef,
    var title: String = "",
    var description: String = "",
    var isPublic: Boolean = false,
    var servingsNum: Int = 1,
    var ingredients: List<Ingredient> = emptyList(),
    var steps: String = "",
    var tags: List<String> = emptyList()
)
    : Writable, Validatable, java.io.Serializable
{

    // W/Firestore: (24.4.2) [CustomClassMapper]: No setter/field for valid found on class Recipe
    override fun isValid(): Boolean {
        return title != "" && ingredients.isNotEmpty()
    }

    override fun toJson(): JSONObject {
        val gson = Gson()
        val jsonRecipe: String = gson.toJson(this)
        return JSONObject(jsonRecipe)
    }

    fun doesMatchSearchQuery(query: String): Boolean{
        return title.contains(query)
    }
}

fun List<Recipe>.asDatabaseModel(): List<RecipeEntity> {
    return map {
        RecipeEntity(
            it.id, it.coverImg, it.imgRef, it.author, it.createdAt, it.title, it.description,
            it.servingsNum, it.ingredients, it.steps, it.isPublic
        )
    }
}

fun Recipe.asDatabaseModel(): RecipeEntity {
    return RecipeEntity(
        this.id, this.coverImg, this.imgRef, this.author, this.createdAt, this.title, this.description,
        this.servingsNum, this.ingredients, this.steps, this.isPublic
    )
}