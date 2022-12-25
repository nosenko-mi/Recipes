package com.ltl.recipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.ltl.recipes.recipe.Recipe
import com.ltl.recipes.recipe.RecipeClickListener
import com.ltl.recipes.ui.main.MainFragment
import com.ltl.recipes.ui.main.RecipeDetailFragment

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

//    override fun onClick(recipe: Recipe) {
//        val bdf = RecipeDetailFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, bdf)
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .addToBackStack(null)
//            .commit()
//    }
}