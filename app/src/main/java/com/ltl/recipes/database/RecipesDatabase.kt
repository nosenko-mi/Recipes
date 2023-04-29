package com.ltl.recipes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ltl.recipes.database.recipe.RecipeDao
import com.ltl.recipes.database.recipe.RecipeEntity

@Database(entities = [RecipeEntity::class], version = 6)
@TypeConverters(Converters::class)
abstract  class RecipesDatabase: RoomDatabase(){

    abstract fun recipeDao(): RecipeDao

}

private lateinit var INSTANCE: RecipesDatabase

fun getInstance(context: Context): RecipesDatabase{
    synchronized(RecipesDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                RecipesDatabase::class.java,
                "recipes_db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}