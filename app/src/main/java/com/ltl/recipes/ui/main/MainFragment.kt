package com.ltl.recipes.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ltl.recipes.R
import com.ltl.recipes.data.user.UserModel
import com.ltl.recipes.data.user.UserViewModel
import com.ltl.recipes.databinding.MainFragmentBinding
import com.ltl.recipes.recipe.Recipe
import com.ltl.recipes.recipe.RecipeAdapter
import com.ltl.recipes.recipe.RecipeClickListener
import com.ltl.recipes.recipe.recipeList


class MainFragment : Fragment(), RecipeClickListener {

    private lateinit var binding: MainFragmentBinding
    private lateinit var viewModel: MainViewModel
    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private lateinit var googleAuth: GoogleSignInClient
    private lateinit var userModel: UserModel
    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)

    companion object {
        private const val TAG: String = "MainFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)

        val user = getCurrentUser()

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
        }

        val googleUser = GoogleSignIn.getLastSignedInAccount(requireContext())
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleAuth = GoogleSignIn.getClient(requireActivity(), gso)

//        if (accountFirebase != null) {
//            currentUser = getCurrentUser(accountFirebase!!)
//            Log.d(TAG, "accountFirebase : ok")
//        }
//        if (googleUser != null) {
//            currentUser = getCurrentUser(googleUser!!)
//            Log.d(TAG, "accountGoogle : ok")
//        }

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
//        view?.let { Navigation.findNavController(it).navigate(R.id.mainFragmentToLoginFragment) }

    }

    private fun goToNewRecipeFragment() {
        Log.d(TAG, "Action: to LoginFragment")
        view?.let { Navigation.findNavController(it).navigate(R.id.mainFragmentToNewRecipeFragment) }
    }

    private fun populateRecipes() {
        val r1 = Recipe(
            R.drawable.noodles_test,
            "First"
        )
        recipeList.add(r1)

        Log.d(TAG, r1.toJson().toString())

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
        view?.let { Navigation.findNavController(it).navigate(R.id.mainFragmentToRecipeDetailFragment) }
    }

}