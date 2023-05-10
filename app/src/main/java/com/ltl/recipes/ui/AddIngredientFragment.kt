package com.ltl.recipes.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.ltl.recipes.R
import com.ltl.recipes.databinding.FragmentAddIngredientBinding
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.ingredient.IngredientViewModel
import com.ltl.recipes.ingredient.QuantityType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIngredientFragment : Fragment() {

    private lateinit var binding: FragmentAddIngredientBinding
    private val viewModel: IngredientViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddIngredientBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.quantityTypeSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, QuantityType.values())

        binding.addButton.setOnClickListener{
            addIngredientSequence()
        }
    }

    private fun addIngredientSequence(){
        val ingredient = collectData()
        Log.d(TAG, ingredient.toString())
        if (!ingredient.isValid()){
//            show error
            Toast.makeText(context, "Invalid ingredient data", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "INVALID ingredient")
        } else {
//            TODO send ingredient back to AddNewRecipeFragment
            viewModel.addIngredient(ingredient)
            goToAddRecipe()
        }
    }

    private fun collectData(): Ingredient {
        try {
            val title: String = binding.ingredientTitleEdit.text.toString()
            val qty: Float = binding.ingredientQuantityEdit.text.toString().toFloat()
            val qtyType: QuantityType = QuantityType.valueOf(
                binding.quantityTypeSpinner.selectedItem.toString().uppercase())
//            TODO: validate data
            return Ingredient(title, qty, qtyType)
        } catch (e: NumberFormatException){
            Log.e(TAG, "INVALID ingredient quantity value: $e")
            Toast.makeText(context, "Invalid ingredient quantity", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalArgumentException){
            Log.e(TAG, "INVALID QuantityType value: $e")
        }
        return Ingredient()
    }

    private fun goToAddRecipe() {
        val action = AddIngredientFragmentDirections.addIngredientFragmentToNewRecipeFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    companion object {
        private const val TAG = "AddIngredient"
    }
}