package com.ltl.recipes.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltl.recipes.databinding.RecipeCardBinding

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val clickListener: RecipeClickListener
    )
    : RecyclerView.Adapter<RecipeViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = RecipeCardBinding.inflate(from, parent, false)
        return RecipeViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size
}