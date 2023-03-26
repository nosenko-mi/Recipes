package com.ltl.recipes.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.data.recipe.RecipeRepository
import com.ltl.recipes.data.user.UserModel
import com.ltl.recipes.database.getInstance
import kotlinx.coroutines.launch
import java.io.IOException

class RecipeViewModel(application: Application, currentUser: UserModel): ViewModel() {

    companion object {
        private const val TAG = "RecipeViewModel"
    }
    private val repository = RecipeRepository(getInstance(application))
    private val recipes = MutableLiveData<MutableList<Recipe>>()

    /**
     *  A list of recipes visible on the screen
    */
    val visibleRecipes = repository.recipes

    /**
     * Event triggered for network error. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    /**
     * Event triggered for network error. Views should use this to get access
     * to the data.
     */
    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    /**
     * Flag to display the error message. This is private to avoid exposing a
     * way to set this value to observers.
     */
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    /**
     * Flag to display the error message. Views should use this to get access
     * to the data.
     */
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    init {
        recipes.value = ArrayList()
        refreshDataFromRepository(currentUser)
    }

    private fun refreshDataFromRepository(currentUser: UserModel) {
        viewModelScope.launch {
            try {
                repository.refreshRecipes(currentUser.email)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                if(visibleRecipes.value.isNullOrEmpty())
                    _eventNetworkError.value = true
            }
        }
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

    fun deleteRecipe(recipe: Recipe){
        repository.deleteRecipe(recipe)

        recipes.value?.remove(recipe)
        recipes.value = recipes.value
    }

    fun setRecipes(new: List<Recipe>){
        recipes.value = new.toMutableList()
        recipes.value = recipes.value
    }

//    TODO make another way to add user to viewmodel
    class Factory(val app: Application, val user: UserModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecipeViewModel(app, user) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}