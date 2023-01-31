package com.ltl.recipes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ltl.recipes.recipe.Recipe

class RecipeViewModel: ViewModel() {

    private val recipes = MutableLiveData<MutableList<Recipe>>()

    init {
        recipes.value = ArrayList()
    }

    fun getRecipes(): LiveData<MutableList<Recipe>> {
        return recipes
    }

    fun addRecipe(recipe: Recipe){
        recipes.value?.add(recipe)
        recipes.value = recipes.value
    }

    fun setRecipes(new: List<Recipe>){
        recipes.value = new.toMutableList()
        recipes.value = recipes.value
    }



}