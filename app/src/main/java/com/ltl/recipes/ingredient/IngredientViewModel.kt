package com.ltl.recipes.ingredient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ltl.recipes.ingredient.Ingredient

class IngredientViewModel: ViewModel() {

//    private val ingredients: MutableLiveData<MutableList<Ingredient?>> by lazy {
//        MutableLiveData<MutableList<Ingredient?>>()
//    }

    private val ingredients = MutableLiveData<MutableList<Ingredient>>()

    init {
        ingredients.value = ArrayList()
    }

    fun getIngredients(): LiveData<MutableList<Ingredient>> {
        return ingredients
    }

    fun getIngredientsAsList(): List<Ingredient>{
        var list = emptyList<Ingredient>()
        ingredients.value?.let { mutableList ->
            list = mutableList.toList()
        }
        return list
    }

    fun getIngredientsAsMutableList(): MutableList<Ingredient>{
        val list = mutableListOf<Ingredient>()
        ingredients.value?.let {
            list.addAll(it)
        }
        return list
    }

    fun add(newIngredients: List<Ingredient>){
        ingredients.value = newIngredients.toMutableList()
        ingredients.value = ingredients.value
    }

    fun addIngredient(ingredient: Ingredient) {
        ingredients.value?.add(ingredient)
        ingredients.value = ingredients.value
    }

    fun removeIngredient(ingredient: Ingredient){
        ingredients.value?.remove(ingredient)
        ingredients.value = ingredients.value
    }

    fun clear(){
        ingredients.value?.clear()
        ingredients.value = ingredients.value
    }


}