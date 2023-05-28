package com.ltl.recipes.database.recipe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.ltl.recipes.database.RecipesDatabase
import com.ltl.recipes.ingredient.Ingredient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RecipeDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: RecipesDatabase
    private lateinit var dao: RecipeDao

    @Before
    fun setup(){
        db = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RecipesDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        dao = db.recipeDao()
    }

    @After
    fun teardown(){
        db.close()
    }

    @Test
    fun insertRecipe() = runTest {
        val recipe = RecipeEntity(title = "title", ingredients = listOf(Ingredient()))
        val id = recipe.id

        dao.addRecipe(recipe)
        val fetched = dao.getRecipeById(id)

        assertThat(recipe).isEqualTo(fetched)
    }

    @Test
    fun insertExistingRecipe() = runTest {
        val recipe = RecipeEntity(title = "title", ingredients = listOf(Ingredient()))
        val id = recipe.id
        dao.addRecipe(recipe)
//        dao.addRecipe(recipe)
    }

    @Test
    fun upsertRecipe() = runTest {
        val recipe = RecipeEntity(title = "title", ingredients = listOf(Ingredient()))
        val id = recipe.id
        dao.addRecipe(recipe)
        val newRecipe = recipe.copy(title = "new title")
        dao.upsertRecipe(newRecipe)
        val fetched = dao.getRecipeById(id)
        assertThat(newRecipe).isEqualTo(fetched)
    }

    @Test
    fun deleteRecipe() = runTest {
        val recipe = RecipeEntity(title = "title", ingredients = listOf(Ingredient()))
        dao.addRecipe(recipe)
        dao.deleteRecipe(recipe)
        val allRecipes = dao.getAll()
        assertThat(allRecipes.value).isNull()
    }

}