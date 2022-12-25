package com.ltl.recipes.recipe

var recipeList = mutableListOf<Recipe>()

class Recipe (
    var cover: Int,
    var title: String,
    val id: Int? = recipeList.size
        )