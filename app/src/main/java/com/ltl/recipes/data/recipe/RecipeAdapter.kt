package com.ltl.recipes.data.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltl.recipes.databinding.RecipeCardBinding
import com.ltl.recipes.utils.GlideImageLoader

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val clickListener: RecipeClickListener,
    private val imageLoader: GlideImageLoader
    )
    : RecyclerView.Adapter<RecipeAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val from = LayoutInflater.from(parent.context)
    val binding = RecipeCardBinding.inflate(from, parent, false)
    return ViewHolder(binding, clickListener)
}

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    inner class ViewHolder(
        private val recipeCardBinding: RecipeCardBinding,
        private val clickListener: RecipeClickListener
    )
        : RecyclerView.ViewHolder(recipeCardBinding.root)
    {
        fun bind(recipe: Recipe){
            imageLoader.loadImage(recipe.imgRef, recipeCardBinding.recipeImg)

            recipeCardBinding.recipeTitle.text = recipe.title

            recipeCardBinding.cardView.setOnClickListener{
                clickListener.onClick(recipe)
            }

            recipeCardBinding.cardView.setOnLongClickListener {
                clickListener.onLongClick(recipe)
            }
        }

    }
}