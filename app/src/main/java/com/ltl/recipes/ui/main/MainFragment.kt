package com.ltl.recipes.ui.main

import android.content.Context
import android.content.res.Configuration
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ltl.recipes.R
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.data.recipe.RecipeAdapter
import com.ltl.recipes.data.recipe.RecipeClickListener
import com.ltl.recipes.data.user.UserViewModel
import com.ltl.recipes.databinding.MainFragmentBinding
import com.ltl.recipes.utils.GlideImageLoader
import com.ltl.recipes.viewmodels.RecipeViewModel
import kotlinx.coroutines.launch

class MainFragment : Fragment(), RecipeClickListener {

    private lateinit var binding: MainFragmentBinding
    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    private val recipeViewModel: RecipeViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, RecipeViewModel.Factory(activity.application, userViewModel.getCurrentUser().value!!))
            .get(RecipeViewModel::class.java)
    }

    private lateinit var recipeAdapter: RecipeAdapter
    private var gridLayoutManager: GridLayoutManager? = null

    companion object {
        private const val TAG: String = "MainFragment"
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        gridLayoutManager?.spanCount = resources.getInteger(R.integer.grid_column_count)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

//        TODO set layout based on width

//        val gridLayoutManager = GridLayoutManager(context, resources.getInteger(R.integer.grid_column_count))
        gridLayoutManager = GridLayoutManager(requireContext(), resources.getInteger(R.integer.grid_column_count))
//        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

        lifecycleScope.launch {
            recipeViewModel.recipes.collect{
                Log.d(TAG, "recipe observer: elements ${it.size}")

                recipeAdapter = RecipeAdapter(
                    it,
                    this@MainFragment,
                    GlideImageLoader(Glide.with(this@MainFragment))
                )

                binding.recyclerView.adapter = recipeAdapter
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel.populateRecipesAsFlow(userViewModel.getEmail())


        binding.toolbar.toolBarAccountButton.setOnClickListener {
            goToUserProfileFragment()
        }

        binding.toolbar.toolBarSearchButton.setOnClickListener {
            if (recipeViewModel.isSearching.value){ // hide searchbar, set basic icon
                recipeViewModel.setSearchingState(false)


                if (binding.toolbar.toolBarSearchEdit.requestFocus()) {
                    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(binding.toolbar.toolBarSearchEdit.windowToken, 0)
                }

                binding.toolbar.toolBarSearchButton.setImageResource(R.drawable.ic_baseline_search_24)
                binding.toolbar.toolBarTitleTextView.visibility = View.VISIBLE
                binding.toolbar.toolBarSearchEdit.visibility = View.GONE
                binding.toolbar.toolBarAccountButton.visibility = View.VISIBLE

                binding.toolbar.toolBarSearchEdit.text.clear()

            } else { // show searchbar, set close icon
                recipeViewModel.setSearchingState(true)

                binding.toolbar.toolBarSearchButton.setImageResource(R.drawable.baseline_close_24)
                binding.toolbar.toolBarTitleTextView.visibility = View.GONE
                binding.toolbar.toolBarSearchEdit.visibility = View.VISIBLE
                binding.toolbar.toolBarAccountButton.visibility = View.GONE

                if (binding.toolbar.toolBarSearchEdit.requestFocus()) {
                    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.showSoftInput(binding.toolbar.toolBarSearchEdit, 0)
                }

            }
        }

//        binding.bottomAppBar.inflateMenu(R.menu.bottom_menu)
//        binding.bottomAppBar.setOnMenuItemClickListener{
//            when(it.itemId){
//                R.id.homeMenuButton -> {
//                    goToHomeFragment()
//                    true
//                }
//                R.id.accountMenuButton -> {
//                    Toast.makeText(context, "inactive", Toast.LENGTH_SHORT).show()
////                    goToUserProfileFragment()
//                    true
//                }
//                else -> super.onOptionsItemSelected(it)
//            }
//        }


        binding.toolbar.toolBarSearchEdit.addTextChangedListener {
            recipeViewModel.onSearchTextChange(it.toString())
        }
//        binding.searchEditText.addTextChangedListener {
//            recipeViewModel.onSearchTextChange(it.toString())
//        }

        binding.fab.setOnClickListener{
            goToNewRecipeFragment()
        }
    }

    private fun goToFavFragment() {
        Toast.makeText(context, "Favorite", Toast.LENGTH_SHORT).show()
    }

    private fun goToHomeFragment() {
        Toast.makeText(context, "Home", Toast.LENGTH_SHORT).show()
    }

    private fun goToUserProfileFragment() {
        Navigation.findNavController(binding.root).navigate(R.id.action_mainFragment_to_userProfileFragment)
    }

    private fun goToNewRecipeFragment(recipe: Recipe? = null) {
        Log.d(TAG, "Action: to LoginFragment")
        var action = MainFragmentDirections.actionMainFragmentToAddEditRecipeParentFragment(userEmail = userViewModel.getEmail())
        if (recipe != null){
            action = MainFragmentDirections.actionMainFragmentToAddEditRecipeParentFragment(
                recipeId = recipe.id,
                userEmail = userViewModel.getEmail()
            )
        }

        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    override fun onClick(recipe: Recipe) {
        val action = MainFragmentDirections.mainFragmentToRecipeDetailFragment(recipe)
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    override fun onLongClick(recipe: Recipe): Boolean {
        Log.d(TAG, "long click")

        showBottomSheetDialog(recipe)
        return true
    }

    private fun showBottomSheetDialog(recipe: Recipe) {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.recipe_actions_bottom_sheet_dialog)

        val delete = bottomSheetDialog.findViewById<LinearLayout>(R.id.deleteLayout)
        val edit = bottomSheetDialog.findViewById<LinearLayout>(R.id.editLayout)

        if (delete != null && edit != null) {
            delete.setOnClickListener {
                recipeViewModel.deleteRecipe(recipe)
                bottomSheetDialog.dismiss()
            }

            edit.setOnClickListener {
                goToNewRecipeFragment(recipe)
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.show()
        }
    }
}