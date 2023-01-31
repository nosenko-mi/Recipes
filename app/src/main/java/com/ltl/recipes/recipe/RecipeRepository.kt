package com.ltl.recipes.recipe

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecipeRepository {

    companion object{
        private const val TAG = "RecipeRepository"
        private const val COLLECTION = "recipes"
        private const val COLLECTION_TEST = "recipes-test"
    }

    private val db = Firebase.firestore

    fun addRecipe(recipe: Recipe){
        db.collection(COLLECTION_TEST)
            .add(recipe)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Add recipe: success: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Add recipe: error ${e.printStackTrace()}")
            }
    }

    fun getAllByEmail(email: String): List<Recipe?>{
        var recipes: List<Recipe?> = emptyList()
        db.collection(COLLECTION_TEST)
            .whereEqualTo("author", email)
            .get()
            .addOnSuccessListener { documents ->
                try{
                    recipes = documents.toObjects(Recipe::class.java)
                    Log.d(TAG, "Get All By Email: success $recipes")
                } catch (e: Exception){
                    Log.d(TAG, "Get All By Email: error ${e.printStackTrace()}")
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Get Recipe: error ${e.printStackTrace()}")
            }
        return recipes
    }

    fun getAllLocal(){

    }
}