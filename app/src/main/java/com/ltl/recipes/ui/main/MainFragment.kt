package com.ltl.recipes.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserViewModel
import com.ltl.recipes.databinding.MainFragmentBinding
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.data.recipe.RecipeAdapter
import com.ltl.recipes.data.recipe.RecipeClickListener
import com.ltl.recipes.viewmodels.RecipeViewModel


class MainFragment : Fragment(), RecipeClickListener {

    private lateinit var binding: MainFragmentBinding
    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private lateinit var googleAuth: GoogleSignInClient
    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    private val recipeViewModel: RecipeViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, RecipeViewModel.Factory(activity.application, userViewModel.getCurrentUser().value!!))
            .get(RecipeViewModel::class.java)
    }

    private lateinit var recipeAdapter: RecipeAdapter

    companion object {
        private const val TAG: String = "MainFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        getCurrentUser()

//        TODO set layout based on width
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        recipeViewModel.getRecipes().observe(viewLifecycleOwner){
            Log.d(TAG, "recipe observer: elements ${it.size}")
            recipeAdapter = RecipeAdapter(it, this)
            binding.recyclerView.adapter = recipeAdapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeViewModel.populateRecipes(userViewModel.getEmail())

        binding.bottomAppBar.inflateMenu(R.menu.bottom_menu)

        binding.bottomAppBar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.homeMenuButton -> {
                    goToHomeFragment()
                    true
                }
                R.id.searchMenuButton -> {
                    true
                }
                R.id.accountMenuButton -> {
                    tmpSignOut()
                    goToAccountFragment()
                    true
                }
                R.id.favMenuButton -> {
                    goToFavFragment()
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }

        binding.fab.setOnClickListener{
            goToNewRecipeFragment()
        }
    }

    private fun getCurrentUser(): FirebaseUser? {
        Log.d(TAG, "UserViewModel: ${userViewModel.getCurrentUser().value}")
        val firebaseUser = firebaseAuth.currentUser
        Log.d(TAG, "FirebaseAuth: $firebaseUser")

        firebaseAuth.addAuthStateListener {
            Log.d(TAG, "Auth listener: ${it.currentUser?.email}")
            recipeViewModel.populateRecipes(it.currentUser?.email.toString())
        }

//        val googleUser = GoogleSignIn.getLastSignedInAccount(requireContext())
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleAuth = GoogleSignIn.getClient(requireActivity(), gso)

        return firebaseUser
    }

    private fun tmpSignOut() {
        firebaseAuth.signOut()
//        TODO check googleAuth first
        googleAuth.signOut()
    }

    private fun goToFavFragment() {
        Toast.makeText(context, "Favorite", Toast.LENGTH_SHORT).show()
    }

    private fun goToHomeFragment() {
        Toast.makeText(context, "Home", Toast.LENGTH_SHORT).show()
    }

    private fun goToAccountFragment() {
        Navigation.findNavController(binding.root).navigate(R.id.mainFragmentToLoginFragment)
    }

    private fun goToNewRecipeFragment(recipe: Recipe? = null) {
        Log.d(TAG, "Action: to LoginFragment")
        var action = MainFragmentDirections.mainFragmentToNewRecipeFragment()
        if (recipe != null){
            action = MainFragmentDirections.mainFragmentToNewRecipeFragment(recipe.id)
        }
//        action = MainFragmentDirections.mainFragmentToNewRecipeFragment()
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
                // go to edit recipe fragment
                goToNewRecipeFragment(recipe)
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.show()
        }
    }
}