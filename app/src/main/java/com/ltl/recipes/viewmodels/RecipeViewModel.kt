package com.ltl.recipes.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ltl.recipes.recipe.Recipe
import com.ltl.recipes.recipe.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel: ViewModel() {

    companion object {
        private const val TAG = "RecipeViewModel"
    }
    private val repository = RecipeRepository()
    private val recipes = MutableLiveData<MutableList<Recipe>>()

    init {
        recipes.value = ArrayList()
    }

    fun populateRecipes(email: String){
        viewModelScope.launch {
            val new = repository.getAllByEmail(email)
            recipes.value = new.toMutableList()
        }
        Log.d(TAG, "RecipeViewModel: ${recipes.value}")
    }

    fun getRecipesList(): List<Recipe>{
        return recipes.value?.toList() ?: emptyList()
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