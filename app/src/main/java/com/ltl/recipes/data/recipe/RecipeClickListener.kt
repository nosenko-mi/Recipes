package com.ltl.recipes.data.recipe

interface RecipeClickListener {
    fun onClick(recipe: Recipe)
    fun onLongClick(recipe: Recipe): Boolean
}