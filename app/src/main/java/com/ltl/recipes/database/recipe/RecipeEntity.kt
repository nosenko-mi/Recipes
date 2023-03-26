package com.ltl.recipes.database.recipe

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.ingredient.Ingredient
import java.util.*

@Entity(tableName = "recipe_table")
data class RecipeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var coverImg: Int = 0,
    var imgRef: String = "name.jpg",
    var author: String = "",
    var createdAt: Date? = null,
    var title: String = "",
    var description: String = "",
    var servingsNum: Int = 1,
    var ingredients: List<Ingredient> = emptyList(),
    var steps: String = "",
    var isPublic: Boolean = false,
//    var tags: List<String> = emptyList(),
)

fun List<RecipeEntity>.asDomainModel(): List<Recipe> {
    return map {
        Recipe(
            it.id, it.coverImg, it.author, it.createdAt, it.imgRef, it.title, it.description,
            it.isPublic, it.servingsNum, it.ingredients, it.steps, emptyList()
        )
    }
}