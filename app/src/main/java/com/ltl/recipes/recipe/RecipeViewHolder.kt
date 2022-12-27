package com.ltl.recipes.recipe

import androidx.recyclerview.widget.RecyclerView
import com.ltl.recipes.databinding.RecipeCardBinding

class RecipeViewHolder(
    private val recipeCardBinding: RecipeCardBinding,
    private val clickListener: RecipeClickListener
    )
    : RecyclerView.ViewHolder(recipeCardBinding.root)
{
        fun bind(recipe: Recipe){
            recipeCardBinding.recipeImg.setImageResource(recipe.coverImg)
            recipeCardBinding.recipeTitle.text = recipe.title

            recipeCardBinding.cardView.setOnClickListener{
                clickListener.onClick(recipe)
            }
        }
}