package com.ltl.recipes.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.icu.util.LocaleData
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.ltl.recipes.R
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.ingredient.IngredientRecycleViewAdapter
import com.ltl.recipes.ingredient.IngredientViewModel
import com.ltl.recipes.ingredient.QuantityType
import com.ltl.recipes.data.recipe.Recipe
import com.ltl.recipes.data.recipe.RecipeRepository
import com.ltl.recipes.data.user.UserViewModel
import com.ltl.recipes.database.RecipesDatabase
import com.ltl.recipes.database.getInstance
import com.ltl.recipes.database.recipe.RecipeEntity
import com.ltl.recipes.databinding.FragmentNewRecipeBinding
import com.ltl.recipes.utils.PhotoConverter
import com.ltl.recipes.utils.StorageHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class NewRecipeFragment : Fragment() {

    private var isPublic = false

    private lateinit var ingredientRecycleViewAdapter: IngredientRecycleViewAdapter

    private lateinit var binding: FragmentNewRecipeBinding
    private lateinit var recipeImg: ImageView
    private var imgName: String = "default"

    private val ingredientViewModel: IngredientViewModel by navGraphViewModels(R.id.nav_graph)
    private val userViewModel: UserViewModel by navGraphViewModels(R.id.nav_graph)
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var recipesDb: RecipesDatabase

    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
            if (isGranted) {
                chooseImageGallery()
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private var galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // do your operation from here....
            if (data != null && data.data != null) {
                val selectedImageUri: Uri? = data.data
                val selectedImageBitmap: Bitmap
                try {
                    selectedImageUri?.let {
                        if(Build.VERSION.SDK_INT < 28) {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                selectedImageUri
                            )
                            recipeImg.setImageBitmap(bitmap)
                        } else {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, selectedImageUri)
                            val bitmap = ImageDecoder.decodeBitmap(source)
                            recipeImg.setImageBitmap(bitmap)
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                    e.printStackTrace()
                }
//                recipeImg.setImageBitmap(selectedImageBitmap)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewRecipeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.ingredientRecycleView.setHasFixedSize(true)
//        val l = object: LinearLayoutManager(context) { override fun canScrollVertically() = false }
        val layoutManager = LinearLayoutManager(context)
        binding.ingredientRecycleView.layoutManager = layoutManager
        binding.ingredientRecycleView.itemAnimator = DefaultItemAnimator()

        ingredientViewModel.getIngredients().observe(viewLifecycleOwner){
            Log.d(TAG, "observer: ${it.size}")
            ingredientRecycleViewAdapter =
                IngredientRecycleViewAdapter(it, ::deleteIngredient, ::editIngredient)

            binding.ingredientRecycleView.adapter = ingredientRecycleViewAdapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipesDb = getInstance(requireContext())
        recipeRepository = RecipeRepository(recipesDb)

        recipeImg = binding.recipeImgImageView
        recipeImg.setOnClickListener{
            showBottomSheetDialog()
        }
//        Glide snippet
//        TODO: create standard img
        val storageReference = FirebaseStorage.getInstance().getReference("tests/name.jpg")
        Glide.with(requireContext())
            .load(storageReference)
            .dontAnimate()
            .into(recipeImg)

        binding.addIngredientButton.setOnClickListener{
//            Toast.makeText(context, "Add ingredient", Toast.LENGTH_SHORT).show()
            goToAddIngredient()
        }

        binding.addRecipeButton.setOnClickListener{
            val isSuccessful = addRecipeSequence()
            if (isSuccessful) {
                Log.d(TAG, "SAVE RECIPE: success")
                goToMainFragment()
            } else {
                Log.d(TAG, "SAVE RECIPE: error")
            }
        }

        binding.sharingRadioGroup.setOnCheckedChangeListener{ group, checkedId ->
            when (checkedId) {
                binding.publicRadioButton.id -> { isPublic = true }
                binding.privateRadioButton.id -> { isPublic = false }
            }
        }
    }

    private fun showBottomSheetDialog() {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.add_img_bottom_sheet_dialog)
        val camera = bottomSheetDialog.findViewById<LinearLayout>(R.id.cameraLayout)
        val gallery = bottomSheetDialog.findViewById<LinearLayout>(R.id.galleryLayout)

        if (camera != null && gallery!= null) {
            camera.setOnClickListener{
                takePhotoSequence()
                bottomSheetDialog.dismiss()
            }
            gallery.setOnClickListener{
//                gallery intent

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PERMISSION_DENIED){
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else{
                        chooseImageGallery()
                    }
                }else{
                    chooseImageGallery()
                }
//                https://developer.android.com/training/permissions/requesting#kotlin
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()

    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun takePhotoSequence(){
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults:IntArray) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun startCamera(){
        dispatchTakePictureIntent()
    }

    private var cameraLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // BitMap is data structure of image file which store the image in memory
            val photo = result.data!!.extras!!["data"] as Bitmap?

            // Create time stamped name and MediaStore entry.
            imgName = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())

            // Set the image in imageview for display
            recipeImg.setImageBitmap(photo)

            // upload the image
            val data = photo?.let { PhotoConverter().bitmapToByteArray(it) }
            val storageHandler = StorageHandler("tests", imgName)

            if (data != null){
                storageHandler.putPhoto(data)
            }

        }
        else {
            Toast.makeText(context, "Error while taking photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            cameraLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun addRecipeSequence(): Boolean {
        val recipe = collectRecipeData()

        Log.d(TAG, "SAVE RECIPE: $recipe")
        Log.d(TAG, "SAVE RECIPE: isValid: ${recipe.isValid()}")

        //        validate data
        if (!recipe.isValid()){
//            show error
            return false
        }

        addRecipe(recipe)
        addRecipeLocally(recipe)

        return true
    }

    private fun addRecipeLocally(recipe: Recipe): Boolean{
        if (!recipe.isValid()){
            return false
        }
        val entity = RecipeEntity(
            null, recipe.coverImg, recipe.imgRef, recipe.author, Date(), recipe.title,
            recipe.description, recipe.servingsNum, recipe.ingredients ,recipe.steps, recipe.isPublic
        )
        Log.d(TAG, "Add recipe locally: date=${entity.createdAt?.time}")

        GlobalScope.launch(Dispatchers.IO) {
            recipesDb.recipeDao().addRecipe(entity)
        }

        return true
    }

    private fun addRecipe(recipe: Recipe) {
        recipeRepository.addRecipe(recipe)
    }

    private fun collectRecipeData(): Recipe {
        val recipe = Recipe()
//        TODO validate input fields
        try {
            recipe.imgRef = imgName
            recipe.title = binding.recipeTitleEditText.text.toString()
            recipe.description = binding.recipeDescriptionEditText.text.toString()
            recipe.servingsNum = binding.servingSpinner.selectedItem.toString().toInt()
            if (ingredientViewModel.getIngredients().value != null){
                recipe.ingredients = ingredientViewModel.getIngredients().value!!.toList()
            }
            Log.d(TAG, "COLLECT DATA: ingredients: ${recipe.ingredients}")
            recipe.steps = binding.stepsEditText.text.toString()
            recipe.isPublic = isPublic

            recipe.author = userViewModel.getEmail()
        } catch (e: Exception){
            Log.e(TAG, "COLLECT DATA: error $e")
        }

        return recipe
    }

    private fun deleteIngredient(ingredient: Ingredient){
        ingredientViewModel.removeIngredient(ingredient)
    }

    private fun editIngredient(ingredient: Ingredient){
        goToaEditIngredient(ingredient)
    }

    private fun testRecipeRepository() {
        val recipe = Recipe()
        recipe.title = "test3"
        val i1 = Ingredient("i1", 1.0f, QuantityType.GRAM)
        val i2 = Ingredient("i2", 30f, QuantityType.MILL)
        val i3 = Ingredient("i3", 2.0f, QuantityType.SPOON)
        val iList = listOf(i1, i2, i3)

        recipe.ingredients = iList
        recipeRepository.addRecipe(recipe)
    }

    private fun goToAddIngredient() {
        view?.let { Navigation.findNavController(it).navigate(R.id.newRecipeFragmentToAddIngredientFragment) }
    }

    private fun goToMainFragment() {
        view?.let { Navigation.findNavController(it).navigate(R.id.newRecipeFragmentToMainFragment) }
    }

    private fun goToaEditIngredient(ingredient: Ingredient) {
        val action = NewRecipeFragmentDirections.newRecipeFragmentToEditIngredientFragment(ingredient)
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    companion object {
        private const val TAG = "NewRecipeFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}