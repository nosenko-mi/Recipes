package com.ltl.recipes.database.recipe

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe_table")
    fun getAll(): LiveData<List<RecipeEntity>>
//    fun getAll(): List<RecipeEntity>

    @Query("SELECT * FROM recipe_table")
    fun getAllAsStateFlow(): Flow<List<RecipeEntity>>
//    fun getAll(): List<RecipeEntity>

    @Query("SELECT * FROM recipe_table WHERE author LIKE :email")
    suspend fun findByEmail(email: String): List<RecipeEntity>

    @Query("SELECT * FROM recipe_table WHERE id LIKE :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( recipes: List<RecipeEntity>)

    @Upsert
    suspend fun upsertRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)
}