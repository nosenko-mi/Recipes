package com.ltl.recipes.data.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ltl.recipes.database.RecipesDatabase
import com.ltl.recipes.database.recipe.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

class RecipeRepository(private val localDb: RecipesDatabase) {

    companion object{
        private const val TAG = "RecipeRepository"
        private const val COLLECTION = "recipes"
        private const val COLLECTION_TEST = "recipes-test"
    }

    private val firestore = Firebase.firestore

    val recipes: LiveData<List<Recipe>> = localDb.recipeDao().getAll().map {
        it.asDomainModel()
    }

    val recipesAsFlow: Flow<List<Recipe>> = localDb.recipeDao().getAllAsStateFlow().map {
        it.asDomainModel()
    }

    suspend fun addRecipe(recipe: Recipe){
        firestore.collection(COLLECTION_TEST)
            .document(recipe.id)
            .set(recipe)

        localDb.recipeDao().upsertRecipe(recipe.asDatabaseModel())
    }

    suspend fun refreshRecipes(email: String){
        withContext(Dispatchers.IO){
//            fetch recipes from the network using the Retrofit service instance
            val recipes = getAllByEmail(email)
//            store the playlist in the Room database.
            localDb.recipeDao().insertAll(recipes.asDatabaseModel())
        }
    }

    suspend fun getRecipeById(id: String): Recipe? {
        var recipe = localDb.recipeDao().getRecipeById(id)?.asDomainModel()
        try {
            val document = firestore.collection(COLLECTION_TEST).document(id).get().await()
            Log.d(TAG, "GetRecipeById FIREBASE: documents ${document.id}")
            recipe = document.toObject(Recipe::class.java)
        } catch (networkError: IOException) {
            Log.d(TAG, "RecipeRepository ERROR: ${networkError.message}")
        }
        Log.d(TAG, "RecipeRepository getRecipeById: $recipe")
        return recipe
    }

    suspend fun getAllByEmail(email: String): List<Recipe> {
        val recipes: List<Recipe?>
        val documents = firestore.collection(COLLECTION_TEST).whereEqualTo("author", email)
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

    suspend fun editRecipe(recipe: Recipe){
        addRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe) {
        firestore.collection(COLLECTION_TEST).document(recipe.id)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }
}