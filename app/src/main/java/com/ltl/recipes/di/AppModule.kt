package com.ltl.recipes.di

import android.app.Application
import androidx.room.Room
import com.ltl.recipes.constants.FirebaseConstants
import com.ltl.recipes.data.recipe.RecipeRepository
import com.ltl.recipes.database.RecipesDatabase
import com.ltl.recipes.utils.FirebaseStorageHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRecipesDatabase(app: Application): RecipesDatabase {
        return Room.databaseBuilder(
            app,
            RecipesDatabase::class.java,
            name = "recipes_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(db: RecipesDatabase): RecipeRepository {
        return RecipeRepository(db)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageHandler(): FirebaseStorageHandler {
        return FirebaseStorageHandler(FirebaseConstants.StorageBaseUrlTest)
    }
}