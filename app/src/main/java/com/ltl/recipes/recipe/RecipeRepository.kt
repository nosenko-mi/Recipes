package com.ltl.recipes.recipe

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

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

    private fun getAllByEmailOld(email: String): List<Recipe?>{
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

    suspend fun getAllByEmail(email: String): List<Recipe> {
        val recipes: List<Recipe?>
        val documents = db.collection(COLLECTION_TEST).whereEqualTo("author", email)
        return try {
            val snapshot = documents.get().await()
            Log.d(TAG, "GetAllByEmailAsync: documents ${snapshot.documents.size}")
            recipes = snapshot.toObjects(Recipe::class.java)
            recipes.filterNotNull()
//            snapshot.toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Get Recipe: error ${e.printStackTrace()}")
            emptyList()
        }
    }

    fun getAllLocal(){

    }
}