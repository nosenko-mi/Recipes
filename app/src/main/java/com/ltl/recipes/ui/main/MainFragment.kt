package com.ltl.recipes.ui.main

import android.icu.text.RelativeDateTimeFormatter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.ltl.recipes.R
import com.ltl.recipes.databinding.MainFragmentBinding
import com.ltl.recipes.recipe.Recipe
import com.ltl.recipes.recipe.RecipeAdapter
import com.ltl.recipes.recipe.RecipeClickListener
import com.ltl.recipes.recipe.recipeList


class MainFragment : Fragment(), RecipeClickListener {

    private lateinit var binding: MainFragmentBinding

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateRecipes()


        val fragment = this
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = RecipeAdapter(recipeList, fragment)
        }
    }

    private fun populateRecipes() {
        val r1 = Recipe(
            R.drawable.noodles_test,
            "First"
        )
        recipeList.add(r1)

        val r2 = Recipe(
            R.drawable.noodles_test,
            "Second"
        )

        val r3 = Recipe(
            R.drawable.noodles_test,
            "Third"
        )

        val r4= Recipe(
            R.drawable.ic_launcher_background,
            "Fifth"
        )

        recipeList.add(r2)
        recipeList.add(r3)
        recipeList.add(r4)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onClick(recipe: Recipe) {
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.container, RecipeDetailFragment())
//            .addToBackStack(null)
//            .commit()
        view?.let { Navigation.findNavController(it).navigate(R.id.mainFragmentToRecipeDetailFragment) }
    }

}