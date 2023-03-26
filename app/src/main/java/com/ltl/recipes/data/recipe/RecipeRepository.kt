package com.ltl.recipes.data.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ltl.recipes.database.RecipesDatabase
import com.ltl.recipes.database.recipe.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RecipeRepository(private val localDb: RecipesDatabase) {

    companion object{
        private const val TAG = "RecipeRepository"
        private const val COLLECTION = "recipes"
        private const val COLLECTION_TEST = "recipes-test"
    }

    private val firestore = Firebase.firestore

    val recipes: LiveData<List<Recipe>> = Transformations.map(localDb.recipeDao().getAll()) {
          it.asDomainModel()
    }

    fun addRecipe(recipe: Recipe){

        firestore.collection(COLLECTION_TEST)
            .document(recipe.id.toString())
            .set(recipe)

//        firestore.collection(COLLECTION_TEST)
//            .add(recipe)
//            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "Add recipe: success: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Add recipe: error ${e.printStackTrace()}")
//            }
    }

    suspend fun refreshRecipes(email: String){
        withContext(Dispatchers.IO){
//            fetch recipes from the network using the Retrofit service instance
            val recipes = getAllByEmail(email)
//            store the playlist in the Room database.
            localDb.recipeDao().insertAll(recipes.asDatabaseModel())
        }
    }

    private fun getAllByEmailOld(email: String): List<Recipe?>{
        var recipes: List<Recipe?> = emptyList()
        firestore.collection(COLLECTION_TEST)
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

    fun getAllLocal(){

    }

    fun deleteRecipe(recipe: Recipe) {
        firestore.collection(COLLECTION_TEST).document("DC")
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }
}