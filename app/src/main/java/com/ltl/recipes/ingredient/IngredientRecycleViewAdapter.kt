package com.ltl.recipes.ingredient

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ltl.recipes.databinding.IngredientListItemBinding

class IngredientRecycleViewAdapter (
    private val dataSet: MutableList<Ingredient>,
    private val type: IngredientAccessType,
    private var onDeleteCallback: ((Ingredient) -> Unit),
    private var onEditCallback: ((Ingredient) -> Unit)
    )
    :
    RecyclerView.Adapter<IngredientRecycleViewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: IngredientListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: Ingredient, position: Int) = binding.apply {

            ingredientNameText.text = ingredient.title
            ingredientQuantityText.text = ingredient.qty.toString()
            ingredientQuantityTypeText.text = ingredient.qtyType.toString()

            // think about Visitor + TypeFactory
            if (type == IngredientAccessType.EDIT){
                root.setOnClickListener{
                    Log.d("Ingredient", "INGREDIENT: edit $ingredient")
                    onEditCallback(ingredient)
                }

                ingredientDeleteButton.setOnClickListener{
                    Log.d("Ingredient", "INGREDIENT: delete $ingredient")
                    onDeleteCallback(ingredient)
                    notifyItemRemoved(position)
                }
            } else {
                ingredientDeleteButton.visibility = View.GONE
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = IngredientListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(dataSet[position], position)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}