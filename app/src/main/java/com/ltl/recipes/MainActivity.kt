package com.ltl.recipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.data.recipe.RecipeClickListener
import com.ltl.recipes.ui.main.MainFragment
import com.ltl.recipes.ui.main.RecipeDetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

    }

}