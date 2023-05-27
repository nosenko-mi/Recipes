package com.ltl.recipes.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.Navigation
import com.ltl.recipes.R
import com.ltl.recipes.databinding.FragmentAddEditIngredientBinding
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.ingredient.QuantityType
import com.ltl.recipes.viewmodels.NewRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditIngredientFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentAddEditIngredientBinding
    private val recipeViewModel: NewRecipeViewModel by hiltNavGraphViewModels(R.id.add_edit_recipe_nav_graph)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditIngredientBinding.inflate(inflater, container, false)
        binding.recipeViewModel = recipeViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("viewmodel", "AddIngredientFragment VM: $recipeViewModel")
        Log.d("viewmodel", "AddIngredientFragment VM currentIngredient: " +
                "${recipeViewModel.currentIngredient.value}")

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        configureSpinner()
        setListeners()

//        binding.quantityTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                parent.setSelection(position)
//                recipeViewModel.currentIngredient.value.qtyType = parent.getItemAtPosition(position) as QuantityType
//                Log.d(TAG, "Spinner: Item Selected: value=${recipeViewModel.currentIngredient.value.qtyType}; position=$position")
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        }

    }

    private fun configureSpinner(){
        binding.quantityTypeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            QuantityType.values())

        binding.quantityTypeSpinner.setSelection(
            recipeViewModel.currentIngredient.value.qtyType.toSpinnerPosition())
        binding.quantityTypeSpinner.onItemSelectedListener = this
    }

    private fun setListeners(){
        binding.addButton.setOnClickListener{
            addIngredientSequence()
        }
    }

    private fun addIngredientSequence(){
        val ingredient = collectData()
        Log.d(TAG, ingredient.toString())
        if (!ingredient.isValid()){
            Toast.makeText(context, "Invalid ingredient data", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "INVALID ingredient")
            return
        }
//        recipeViewModel.addIngredient(ingredient)
        recipeViewModel.upsertCurrentIngredient()
        recipeViewModel.clearCurrentIngredient()
        goToAddRecipe()
    }

    private fun collectData(): Ingredient {
        try {
            val title: String = binding.ingredientTitleEdit.text.toString()
            val qty: Float = binding.ingredientQuantityEdit.text.toString().toFloat()
            val qtyType: QuantityType = QuantityType.valueOf(
                binding.quantityTypeSpinner.selectedItem.toString().uppercase())
            if (title.isNotEmpty()){
                return Ingredient(title, qty, qtyType)
            } else {
                throw IllegalArgumentException()
            }
        } catch (e: NumberFormatException){
            Log.e(TAG, "INVALID ingredient quantity value: $e")
            Toast.makeText(context, "Invalid ingredient quantity", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalArgumentException){
            Log.e(TAG, "IllegalArgumentException: $e")
        }
        return Ingredient()
    }

    private fun goToAddRecipe() {
        recipeViewModel.setCurrentIngredient(Ingredient())
        view?.let { Navigation.findNavController(it).popBackStack() }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            goToAddRecipe()
        }
    }

    companion object {
        private const val TAG = "AddIngredient"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent?.let {
            it.setSelection(position)
//            recipeViewModel.currentIngredient.value.qtyType = it.getItemAtPosition(position) as QuantityType
            recipeViewModel.setCurrentQuantityType(it.getItemAtPosition(position) as QuantityType)
            Log.d(TAG, "Spinner: Item Selected: value=${recipeViewModel.currentIngredient.value.qtyType}; position=$position")
            return@let
        }
        Log.e(TAG, "Spinner: AdapterView<*> is null")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}