package com.ltl.recipes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.ltl.recipes.R
import com.ltl.recipes.databinding.FragmentAddEditRecipeParentBinding
import com.ltl.recipes.viewmodels.NewRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditRecipeParentFragment : Fragment() {

    private lateinit var bindind: FragmentAddEditRecipeParentBinding
    private val viewModel: NewRecipeViewModel by hiltNavGraphViewModels(R.id.add_edit_recipe_nav_graph)

    companion object {
        private const val TAG = "AddEditRecipeParentFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindind = FragmentAddEditRecipeParentBinding.inflate(inflater, container, false)

        return bindind.root
    }

}