package com.ltl.recipes.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.data.recipe.RecipeRepository
import com.ltl.recipes.data.user.UserModel
import com.ltl.recipes.database.getInstance
import com.ltl.recipes.ui_events.RecipeListEvent
import com.ltl.recipes.ui_events.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class RecipeViewModel(application: Application, currentUser: UserModel): ViewModel() {

    companion object {
        private const val TAG = "RecipeViewModel"
    }
    private val repository = RecipeRepository(getInstance(application))

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _recipes = MutableStateFlow(listOf<Recipe>())
    val recipes = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_recipes) { text, recipeList ->
            if(text.isBlank()) {
                recipeList
            } else {
                delay(500L)
                recipeList.filter {
                    it.doesMatchSearchQuery(text.trim())
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _recipes.value
        )


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onEvent(event: RecipeListEvent){
        when(event) {
            RecipeListEvent.OnAddFavoriteClick -> TODO()
            RecipeListEvent.OnAddRecipeClick -> TODO()
            is RecipeListEvent.OnDeleteRecipeClick -> TODO()
            RecipeListEvent.OnFavoriteClick -> TODO()
            RecipeListEvent.OnHomeClick -> TODO()
            RecipeListEvent.OnProfileClick -> TODO()
            is RecipeListEvent.OnRecipeClick -> TODO()
            is RecipeListEvent.OnRecipeLongClick -> TODO()
            RecipeListEvent.OnSearchClick -> TODO()
            RecipeListEvent.OnUndoDeleteClick -> TODO()
        }
    }

    private var _eventNetworkError = MutableLiveData<Boolean>(false)

    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)

    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    init {
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
                if(recipes.value.isEmpty())
                    _eventNetworkError.value = true
            }
        }
    }

    fun populateRecipesAsFlow(email: String){
        viewModelScope.launch {
            _recipes.value = repository.getAllByEmail(email).toMutableList()
        }
        Log.d(TAG, "RecipeViewModel: ${_recipes.value}")
    }

    fun deleteRecipe(recipe: Recipe){
        repository.deleteRecipe(recipe)

        _recipes.update { recipes ->
            recipes.toMutableList().apply {
                remove(recipe)
            }
        }
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