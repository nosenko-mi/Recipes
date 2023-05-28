package com.ltl.recipes.data.recipe

import com.google.common.truth.Truth.assertThat
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.ingredient.QuantityType
import org.junit.Test

class RecipeTest{

    @Test
    fun emptyTittle_returnFalse(){
        val recipe = Recipe(
            title = "",
            ingredients = listOf(Ingredient("ingredient", 1f, QuantityType.NONE))
        )
        val result = recipe.isValid()
        assertThat(result).isFalse()
    }

    @Test
    fun emptyIngredients_returnFalse(){
        val recipe = Recipe(
            title = "as",
            ingredients = emptyList()
        )
        val result = recipe.isValid()
        assertThat(result).isFalse()
    }

    @Test
    fun validRecipe_returnTrue(){
        val recipe = Recipe(
            title = "as",
            ingredients = listOf(Ingredient("ingredient", 1f, QuantityType.NONE))
        )
        val result = recipe.isValid()
        assertThat(result).isTrue()
    }

}