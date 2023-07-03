package com.ltl.recipes.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.databinding.FragmentRecipeDetailBinding
import com.ltl.recipes.ingredient.IngredientAccessType
import com.ltl.recipes.ingredient.IngredientRecycleViewAdapter
import com.ltl.recipes.utils.GlideImageLoader


class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private val args: RecipeDetailFragmentArgs by navArgs()

    private lateinit var ingredientRecycleViewAdapter: IngredientRecycleViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(context)
        binding.ingredientRecycleView.layoutManager = layoutManager
        binding.ingredientRecycleView.itemAnimator = DefaultItemAnimator()
        ingredientRecycleViewAdapter = IngredientRecycleViewAdapter(
            args.recipeAttr.ingredients.toMutableList(),
            IngredientAccessType.READ,
            {},
            {})

        binding.ingredientRecycleView.adapter = ingredientRecycleViewAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateRecipe(args.recipeAttr)
    }

    private fun populateRecipe(recipe: Recipe){
        binding.recipeTitle.text = recipe.title
        binding.descriptionText.text = recipe.description
        binding.stepsText.text = recipe.steps


        loadImg(recipe.imgRef)
    }

    private fun loadImg(ref: String){
        val imageLoader = GlideImageLoader(Glide.with(this))
        imageLoader.loadImage(ref, binding.recipeImg)
    }

    companion object {
        private const val TAG = "RecipeDetailFragment"
    }
}