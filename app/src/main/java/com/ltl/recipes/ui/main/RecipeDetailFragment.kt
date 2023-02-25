package com.ltl.recipes.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ltl.recipes.R
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.databinding.FragmentRecipeDetailBinding
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.ingredient.IngredientAccessType
import com.ltl.recipes.ingredient.IngredientRecycleViewAdapter


class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding
    private val args: RecipeDetailFragmentArgs by navArgs()

    private lateinit var ingredientRecycleViewAdapter: IngredientRecycleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
            ::deleteIngredient,
            ::editIngredient)

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
//        TODO create single db access, add default img path
        val location = "tests/$ref.jpg"
        Log.d(TAG, "loadImg: location = $location")
        val storageReference = FirebaseStorage.getInstance().getReference("tests")
        // Create a reference to the file to delete
        val fileRef: StorageReference = storageReference.child(ref)

        Glide.with(requireContext())
            .load(fileRef)
            .placeholder(R.drawable.recipe_default)
            .into(binding.recipeImg)


    }

    private fun deleteIngredient(ingredient: Ingredient){
    }

    private fun editIngredient(ingredient: Ingredient){
    }

    companion object {
        private const val TAG = "RecipeDetailFragment"
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = RecipeDetailFragment()
    }
}