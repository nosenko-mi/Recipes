package com.ltl.recipes.database.recipe

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe_table")
    fun getAll(): LiveData<List<RecipeEntity>>
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