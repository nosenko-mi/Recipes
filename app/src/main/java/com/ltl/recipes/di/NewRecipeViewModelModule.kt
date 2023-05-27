package com.ltl.recipes.di

import androidx.lifecycle.SavedStateHandle
import com.ltl.recipes.data.recipe.RecipeRepository
import com.ltl.recipes.viewmodels.NewRecipeViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class) // Assuming the ViewModel is used in a Fragment
object NewRecipeViewModelModule {

    @Provides
    @FragmentScoped // Use the appropriate scope for your use case
    fun provideNewRecipeViewModel(
        repository: RecipeRepository,
        savedStateHandle: SavedStateHandle
    ): NewRecipeViewModel {
        return NewRecipeViewModel(repository, savedStateHandle)
    }
}