package com.ltl.recipes.viewmodels

import android.text.Editable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ltl.recipes.data.ServingNumber
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.data.recipe.RecipeRepository
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewRecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object{
        private val TAG: String = "NewRecipeViewModel"
    }

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe = _recipe.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _servingNum = MutableStateFlow(1)
    val servingNum = _servingNum.asStateFlow()

    val servings = MutableStateFlow(ServingNumber(1))

    private val _steps = MutableStateFlow("")
    val steps = _steps.asStateFlow()

    private val _isPublic = MutableStateFlow(false)
    val isPublic = _isPublic.asStateFlow()

    private val _ingredients = MutableStateFlow<MutableList<Ingredient>>(ArrayList())
    val ingredients = _ingredients.asStateFlow()

    private val _imgRef = MutableStateFlow("default.jpg")
    val imgRef = _imgRef.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        var recipeId = savedStateHandle.get<String>("recipeId")
        if (recipeId == null){
            recipeId = "-1"
        }
        refresh(recipeId)
    }

    fun refresh(id: String){
        if (id != "-1"){
            viewModelScope.launch (Dispatchers.IO) {
                repository.getRecipeById(id)?. let { recipe ->
                    _title.value = recipe.title
                    _description.value = recipe.description
                    _steps.value = recipe.steps
                    servings.value.setNumber(recipe.servingsNum)
                    _isPublic.value = recipe.isPublic
                    _imgRef.value = recipe.imgRef
                    _ingredients.value = recipe.ingredients.toMutableList()
                    _recipe.value = recipe
                }
                Log.d(TAG, "recipe servings: ${servings.value.getNumber()}")
            }
        }
    }

    suspend fun insertRecipe(currentUser: String): Boolean{
        var isSuccess = false
        if (!isValid()){
            return isSuccess
        }
        val r = Recipe()
        try {
            with(r){
                imgRef = _imgRef.value
                title = _title.value
                description = _description.value
                servingsNum = _servingNum.value
                steps = _steps.value
                isPublic = _isPublic.value
                author = currentUser
                ingredients = _ingredients.value
            }
            _recipe.value = r
            if (r.ingredients.isNotEmpty()) {
                repository.addRecipe(r)
                isSuccess = true
            } else {
                Log.e("NewRecipeViewModel", "INSERT DATA: recipe.ingredients IS EMPTY")
            }

        } catch (e: Exception){
            Log.e("NewRecipeViewModel", "COLLECT DATA: error $e")
        }

        return isSuccess
    }

    fun isValid(): Boolean{
        if (_title.value.isEmpty() || _ingredients.value.isEmpty()) return false
        return true
    }

    fun setRecipe(recipe: Recipe){
        _recipe.value = recipe
    }

    fun updateTitle(editable: Editable){
        _title.value = editable.toString()
    }

    fun updateDescription(editable: Editable){
        _description.value = editable.toString()
    }

    fun updateSteps(editable: Editable){
        _steps.value = editable.toString()
    }

    fun setImgRef(ref: String){
        _imgRef.value = ref
    }

    fun setIngredients(newIngredients: MutableList<Ingredient>){
        _ingredients.value = newIngredients
    }

    fun addIngredient(ingredient: Ingredient) {
        _ingredients.value.add(ingredient)
        _ingredients.value = _ingredients.value
    }

    fun removeIngredient(ingredient: Ingredient){
        _ingredients.value.remove(ingredient)
        _ingredients.value = _ingredients.value
    }

    fun clearIngredients(){
        _ingredients.value.clear()
        _ingredients.value = _ingredients.value
    }
}