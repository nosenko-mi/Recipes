package com.ltl.recipes.ui_events

import com.ltl.recipes.data.recipe.Recipe

sealed class RecipeListEvent{
    data class OnDeleteRecipeClick(val recipe: Recipe): RecipeListEvent()
    data class OnRecipeClick(val recipe: Recipe): RecipeListEvent()
    data class OnRecipeLongClick(val recipe: Recipe): RecipeListEvent()
    object OnUndoDeleteClick: RecipeListEvent()
    object OnAddRecipeClick: RecipeListEvent()
    object OnHomeClick: RecipeListEvent()
    object OnFavoriteClick: RecipeListEvent()
    object OnAddFavoriteClick: RecipeListEvent()
    object OnSearchClick: RecipeListEvent()
    object OnProfileClick: RecipeListEvent()


}
