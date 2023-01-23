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
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.ltl.recipes.R
import com.ltl.recipes.databinding.FragmentEditIngredientBinding
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.ingredient.IngredientViewModel
import com.ltl.recipes.ingredient.QuantityType

class EditIngredientFragment : Fragment() {

    val args: EditIngredientFragmentArgs by navArgs()
    private val viewModel: IngredientViewModel by navGraphViewModels(R.id.nav_graph)


    private lateinit var binding: FragmentEditIngredientBinding
    private lateinit var spinnerAdapter: ArrayAdapter<QuantityType>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {
            Log.d(TAG, "args: ${args.ingredientEditExtra}")
            Log.d(TAG, "viewModel: ${viewModel.getIngredients()}")
        } catch (e: Exception){
            Log.e(TAG, "ERROR args: ${e.printStackTrace()}")
        }

        // Inflate the layout for this fragment
        binding = FragmentEditIngredientBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, QuantityType.values())
        binding.eQuantityTypeSpinner.adapter = spinnerAdapter

        binding.editDoneButton.setOnClickListener{
            editIngredientSequence()
        }

        bindFields()
    }

    private fun bindFields() {

        val spinner = binding.eQuantityTypeSpinner

        binding.eIngredientTitleEdit.setText(args.ingredientEditExtra.title)
        binding.eIngredientQuantityEdit.setText(args.ingredientEditExtra.qty.toString())
        spinner.setSelection(
            spinnerAdapter.getPosition(args.ingredientEditExtra.qtyType)
        )
    }

    private fun editIngredientSequence(){
        val ingredient = collectData()
        Log.d(TAG, ingredient.toString())
        if (!ingredient.isValid()){
//            show error
            Toast.makeText(context, "Invalid ingredient data", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "INVALID ingredient")
        } else {
//            TODO send ingredient back to AddNewRecipeFragment
            viewModel.addIngredient(ingredient)
            viewModel.removeIngredient(args.ingredientEditExtra)
            goToAddRecipe()
        }
    }

    private fun collectData(): Ingredient {
        try {
            val title: String = binding.eIngredientTitleEdit.text.toString()
            val qty: Float = binding.eIngredientQuantityEdit.text.toString().toFloat()
            val qtyType: QuantityType = QuantityType.valueOf(
                binding.eQuantityTypeSpinner.selectedItem.toString().uppercase())
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
        val action = EditIngredientFragmentDirections.editIngredientFragmentToNewRecipeFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    companion object {
        private const val TAG = "EditIngredient"
    }
}