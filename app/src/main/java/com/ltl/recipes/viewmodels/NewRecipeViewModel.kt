package com.ltl.recipes.viewmodels

import android.text.Editable
import android.util.Log
import android.widget.RadioGroup
import androidx.lifecycle.*
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

    var _recipe = MutableLiveData<Recipe?>(null)

//    private var _title = MutableLiveData("")
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

//    private var _description = MutableLiveData("")
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

//    private var _servingNum = MutableLiveData(1)
    private val _servingNum = MutableStateFlow(1)
    val servingNum = _servingNum.asStateFlow()

    val servings = MutableStateFlow(ServingNumber(1))

//    private var _steps = MutableLiveData("")
    private val _steps = MutableStateFlow("")
    val steps = _steps.asStateFlow()

//    private var _isPublic = MutableLiveData(false)
    private val _isPublic = MutableStateFlow(false)
    val isPublic = _isPublic.asStateFlow()

    private val _ingredients = MutableLiveData<MutableList<Ingredient>>(ArrayList())

//    private val _imgRef = MutableLiveData("default.jpg")
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
                    setIngredients(recipe.ingredients)
                    setRecipe(recipe)
                }
                Log.d(TAG, "recipe servings: ${servings.value.getNumber()}")
            }
        }
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        Log.w("tag", "onTextChanged $s")
    }

    fun insertRecipe(author: String): Boolean{
        if (!isValid()){
            return false
        }
        val recipe = Recipe()
        try {
            recipe.imgRef = _imgRef.value!!
            recipe.title = _title.value.toString()
            recipe.description = _description.value.toString()
            recipe.servingsNum = _servingNum.value!!
            recipe.steps = _steps.value.toString()
            recipe.isPublic = _isPublic.value!!
            recipe.author = author
            if (getIngredients().value != null){
                recipe.ingredients = getIngredients().value!!.toList()
            }
            Log.d("NewRecipeViewModel", "COLLECT DATA: ingredients: ${recipe.ingredients}")

        } catch (e: Exception){
            Log.e("NewRecipeViewModel", "COLLECT DATA: error $e")
        }

        setRecipe(recipe)
        viewModelScope.launch (Dispatchers.IO) {
            repository.addRecipe(_recipe.value!!)
        }

        return true
    }

    fun isValid(): Boolean{
        if (_title.value!!.isEmpty() || _ingredients.value!!.isEmpty()) return false

        return true
    }

    fun setRecipe(recipe: Recipe){
        _recipe.postValue(recipe)
    }

    fun getRecipe(): LiveData<Recipe?>{
        return _recipe
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

    fun updatePublicity(radioGroup: RadioGroup, id: Int){
        when(id){
            -1 -> return
        }
    }

    fun setImgRef(ref: String){
        _imgRef.value = ref
    }

    fun setIngredients(newIngredients: List<Ingredient>){
        _ingredients.postValue(newIngredients.toMutableList())
    }

    fun addIngredient(ingredient: Ingredient) {
        _ingredients.value?.add(ingredient)
        _ingredients.value = _ingredients.value
    }

    fun getIngredients(): LiveData<MutableList<Ingredient>> {
        return _ingredients
    }

    fun removeIngredient(ingredient: Ingredient){
        _ingredients.value?.remove(ingredient)
        _ingredients.value = _ingredients.value
    }

    fun clear(){
        _ingredients.value?.clear()
        _ingredients.value = _ingredients.value
    }



}