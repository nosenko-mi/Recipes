package com.ltl.recipes.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ltl.recipes.R
import com.ltl.recipes.constants.FirebaseConstants
import com.ltl.recipes.databinding.FragmentNewRecipeBinding
import com.ltl.recipes.ingredient.Ingredient
import com.ltl.recipes.ingredient.IngredientAccessType
import com.ltl.recipes.ingredient.IngredientRecycleViewAdapter
import com.ltl.recipes.ingredient.IngredientViewModel
import com.ltl.recipes.utils.PhotoConverter
import com.ltl.recipes.utils.FirebaseStorageHandler
import com.ltl.recipes.viewmodels.NewRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewRecipeFragment : Fragment() {

    private lateinit var ingredientRecycleViewAdapter: IngredientRecycleViewAdapter

    private lateinit var binding: FragmentNewRecipeBinding
    private lateinit var recipeImg: ImageView
    private var imgName: String = "default.jpg"

    private val ingredientViewModel: IngredientViewModel by navGraphViewModels(R.id.add_edit_recipe_nav_graph)
    val args: NewRecipeFragmentArgs by navArgs()

    private val viewModel: NewRecipeViewModel by hiltNavGraphViewModels(R.id.add_edit_recipe_nav_graph)

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
        Log.d(TAG, "NewRecipeFragment starts")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_recipe,container, false)
        binding.lifecycleOwner = this
        val view = binding.root
        binding.recipeViewModel = viewModel
        recipeImg = binding.recipeImgImageView
        subscribeToObservables()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "VM: $viewModel")
        Log.d(TAG, "VM value: ${viewModel.recipe.value.toString()}")
        Log.d(TAG, "VM imgRef= ${viewModel.imgRef.value}")
        Log.d(TAG, "VM recipe.imgRef= ${viewModel.recipe.value?.imgRef}")

        loadImg(viewModel.imgRef.value)
        setListeners()
    }

    private fun loadImg(ref: String){
        val location = buildString {
            append(FirebaseConstants.StorageBaseUrlTest)
            append(ref)
        }
        val fileRef: StorageReference = FirebaseStorage.getInstance()
            .getReference(location)
        Log.d(TAG, "loadImg: fileRef = $fileRef")

        Glide.with(requireContext())
            .load(fileRef)
            .placeholder(R.drawable.recipe_default)
            .into(binding.recipeImgImageView)
    }

    private fun setListeners(){
        recipeImg.setOnClickListener{
            showBottomSheetDialog()
        }

        binding.addIngredientButton.setOnClickListener{
            goToAddEditIngredient()
        }

        binding.addRecipeButton.setOnClickListener{
            addRecipeSequence()
        }
    }

    private fun subscribeToObservables(){
        binding.ingredientRecycleView.setHasFixedSize(true)
//        val l = object: LinearLayoutManager(context) { override fun canScrollVertically() = false }
        val layoutManager = LinearLayoutManager(context)
        binding.ingredientRecycleView.layoutManager = layoutManager
        binding.ingredientRecycleView.itemAnimator = DefaultItemAnimator()

        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ingredients.collectLatest {
                    Log.d(TAG, "viewModel.ingredients.collectLatest = $it")
                    ingredientRecycleViewAdapter = IngredientRecycleViewAdapter(
                        it,
                        IngredientAccessType.EDIT,
                        ::deleteIngredient,
                        ::editIngredient)
                    binding.ingredientRecycleView.adapter = ingredientRecycleViewAdapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.imgRef.collect {
                Log.d(TAG, "viewModel.imgRef.collectLatest = $it")
                loadImg(it)
            }
        }
    }

    private fun renderProgressBar(value: Boolean) {
        if (value){
            binding.progressBar.visibility = View.VISIBLE
            binding.newRecipeScrollLayout.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.newRecipeScrollLayout.visibility = View.VISIBLE
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

    private fun createFileName(): String {
        val ref = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        viewModel.setImgRef(ref)
        return ref
    }

    private var cameraLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            // BitMap is data structure of image file which store the image in memory
            val photo = result.data!!.extras!!["data"] as Bitmap?
            // Create time stamped name and MediaStore entry.
            imgName = createFileName()
            // Set the image in imageview for display
            recipeImg.setImageBitmap(photo)
            // upload the image
            val data = photo?.let { PhotoConverter().bitmapToByteArray(it) }
//            TODO: Change collection to prod
            val storageHandler = FirebaseStorageHandler("tests", imgName)

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

    private fun addRecipeSequence() {
//        viewModel.setIngredients(ingredientViewModel.ingredients.value)
        // TODO: Activities can use lifecycleScope directly, but Fragments should instead use
        // viewLifecycleOwner.lifecycleScope.
        lifecycleScope.launch(Dispatchers.IO){
            val isSuccess = async { viewModel.insertRecipe(args.userEmail) }
            if (isSuccess.await()){
                Log.d(TAG, "addRecipeSequence: success")
                withContext(Dispatchers.Main){
                    viewModel.clearIngredients()
                    ingredientViewModel.clear()
                    goToMainFragment()
                }
            } else {
                Log.d(TAG, "addRecipeSequence: error")
                Snackbar.make(binding.addRecipeButton, "Oops... something went wrong", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun deleteIngredient(ingredient: Ingredient){
        viewModel.removeIngredient(ingredient)
        ingredientViewModel.removeIngredient(ingredient)
    }

    private fun editIngredient(ingredient: Ingredient){
//        goToaEditIngredient(ingredient)
        viewModel.setCurrentIngredient(ingredient)
        goToAddEditIngredient()
    }

    private fun goToAddIngredient() {
//        view?.let { Navigation.findNavController(it).navigate(R.id.newRecipeFragmentToAddIngredientFragment) }
    }

    private fun goToMainFragment() {
//        viewModel.clearIngredients()
//        view?.let { Navigation.findNavController(it).navigate(R.id.newRecipeFragmentToMainFragment) }
        findNavController().popBackStack()
    }

    private fun goToAddEditIngredient(){
        view?.let {
            Navigation.findNavController(it)
                .navigate(R.id.action_newRecipeFragment2_to_addIngredientFragment2)
        }
    }

    private fun goToaEditIngredient(ingredient: Ingredient) {
//        val action = NewRecipeFragmentDirections.newRecipeFragmentToEditIngredientFragment(ingredient)
//        view?.let { Navigation.findNavController(it).navigate(action) }
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