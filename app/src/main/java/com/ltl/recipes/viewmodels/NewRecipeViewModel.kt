package com.ltl.recipes.viewmodels

import android.text.Editable
import android.util.Log
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ltl.recipes.constants.AppConstants
import com.ltl.recipes.data.ServingNumber
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.data.recipe.RecipeRepository
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.ingredient.QuantityType
import com.ltl.recipes.utils.FirebaseStorageHandler
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
    private val firebaseStorageHandler: FirebaseStorageHandler,
    savedStateHandle: SavedStateHandle
): ViewModel(), InverseBindingListener {

    companion object{
        private val TAG: String = "NewRecipeViewModel"
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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

    private val _currentIngredient = MutableStateFlow(Ingredient())
    val currentIngredient = _currentIngredient.asStateFlow()
    private var oldIngredient: Ingredient? = null

    fun setCurrentQuantityType(type: QuantityType){
        _currentIngredient.value.qtyType = type
    }

    fun clearCurrentIngredient(){
        _currentIngredient.value = Ingredient()
        oldIngredient = null
    }

    private val _imgRef = MutableStateFlow(AppConstants.defaultImgRef)
    val imgRef = _imgRef.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        _isLoading.value = true
        var recipeId = savedStateHandle.get<String>("recipeId")
        if (recipeId == null){
            recipeId = "-1"
        }
        refresh(recipeId)
    }

    fun refresh(id: String){
        if (id != "-1") {
            Log.d(TAG, "init VM: Load recipe from repository")
            viewModelScope.launch (Dispatchers.IO) {
                repository.getRecipeById(id)?. let { recipe ->
                    _title.value = recipe.title
                    _description.value = recipe.description
                    _steps.value = recipe.steps
                    servings.value.setNumber(recipe.servingsNum)
                    _isPublic.value = recipe.isPublic
                    _imgRef.value = recipe.imgRef
                    Log.d(TAG, "init VM: imgRef = ${_imgRef.value}")
                    _ingredients.value = recipe.ingredients.toMutableList()
                    _recipe.value = recipe
                }
                Log.d(TAG, "init VM: _recipe.value = ${_recipe.value}")
                _isLoading.value = false
            }
        } else {
            Log.d(TAG, "init VM: Set default recipe")
            _recipe.value = Recipe()
            _isLoading.value = false
        }
    }

    suspend fun insertRecipe(currentUser: String): Boolean{
        var isSuccess = false
        if (currentUser.isEmpty() || !isValid()){
            Log.e(TAG, "insertRecipe: currentUser is empty or not valid")
            return false
        }
        recipe.value?.let {
            try {
                val r = it.copy()
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
                    Log.e("NewRecipeViewModel", "repository.addRecipe(r): isSuccess = $isSuccess")
                } else {
                    Log.e("NewRecipeViewModel", "INSERT DATA: recipe.ingredients IS EMPTY")
                }

            } catch (e: Exception){
                Log.e(TAG, "COLLECT DATA: error $e")
            }
        }
//        val r = Recipe()
//        try {
//            with(r){
//                imgRef = _imgRef.value
//                title = _title.value
//                description = _description.value
//                servingsNum = _servingNum.value
//                steps = _steps.value
//                isPublic = _isPublic.value
//                author = currentUser
//                ingredients = _ingredients.value
//            }
//            _recipe.value = r
//            if (r.ingredients.isNotEmpty()) {
//                repository.addRecipe(r)
//                isSuccess = true
//            } else {
//                Log.e("NewRecipeViewModel", "INSERT DATA: recipe.ingredients IS EMPTY")
//            }
//
//        } catch (e: Exception){
//            Log.e("NewRecipeViewModel", "COLLECT DATA: error $e")
//        }

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

    fun updateQty(editable: Editable){
        if (editable.toString().isEmpty()){
            _currentIngredient.value.qty = 0f
        } else{
            _currentIngredient.value.qty = editable.toString().toFloat()
        }
    }

    fun updateSteps(editable: Editable){
        _steps.value = editable.toString()
    }

    // Method that returns the enum values
    fun getQuantityTypeValues(): Array<QuantityType> {
        return QuantityType.values()
    }


    fun setCurrentIngredient(ingredient: Ingredient){
        _currentIngredient.value = ingredient
        oldIngredient = ingredient
    }

    fun setImgRef(ref: String){
        _imgRef.value = ref
    }

    fun setIngredients(newIngredients: MutableList<Ingredient>){
        _ingredients.value = newIngredients
    }

    fun upsertCurrentIngredient(){
        if (oldIngredient != null && _ingredients.value.contains(oldIngredient)){
            _ingredients.value.remove(oldIngredient)
        }
        if (currentIngredient.value.isValid()){
            _ingredients.value.add(currentIngredient.value)
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        if (_ingredients.value.contains(oldIngredient)){
            _ingredients.value.remove(oldIngredient)
        }
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

    fun insertPhoto(imgName: String, imgData: ByteArray?){
        viewModelScope.launch (Dispatchers.IO){
            imgData?.let {
                firebaseStorageHandler.putPhoto(imgName, imgData)
            }
        }
    }

    fun isDefaultImgRef(): Boolean {
        return imgRef.value == AppConstants.defaultImgRef
    }

    override fun onChange() {
        Log.d(TAG, "NewRecipeViewModel: Ingredient spinner value changed")

    }
}