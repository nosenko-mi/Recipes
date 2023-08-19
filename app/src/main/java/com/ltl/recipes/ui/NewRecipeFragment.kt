package com.ltl.recipes.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import androidx.core.content.FileProvider
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
import com.ltl.recipes.viewmodels.NewRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewRecipeFragment : Fragment() {

    private lateinit var ingredientRecycleViewAdapter: IngredientRecycleViewAdapter

    private lateinit var binding: FragmentNewRecipeBinding
    private lateinit var recipeImg: ImageView

    private val ingredientViewModel: IngredientViewModel by navGraphViewModels(R.id.add_edit_recipe_nav_graph)
    val args: NewRecipeFragmentArgs by navArgs()

    private val viewModel: NewRecipeViewModel by hiltNavGraphViewModels(R.id.add_edit_recipe_nav_graph)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            chooseImageGallery()
        } else {
            Toast.makeText(context, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private var galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultData = result.data
            if (resultData == null || resultData.data == null) {
                Log.e(TAG, "Gallery launcher: result.data || data.data is null")
                return@registerForActivityResult
            }

            val selectedImageUri: Uri = resultData.data!!
            Log.d(TAG, "Gallery launcher: selectedImageUri=$selectedImageUri")

            val fileName = createFileName()
            Log.d(TAG, "Gallery launcher: createFileName()=$fileName")
            val bitmap: Bitmap
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        selectedImageUri
                    )
                } else {
                    val source = ImageDecoder.createSource(
                        requireContext().contentResolver,
                        selectedImageUri
                    )
                    bitmap = ImageDecoder.decodeBitmap(source)
                }
                val fileData = PhotoConverter().bitmapToByteArray(bitmap)
                recipeImg.setImageBitmap(bitmap)
                viewModel.insertPhoto(fileName, fileData)
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "NewRecipeFragment starts")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_recipe, container, false)
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

    private fun loadImg(ref: String) {
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

    private fun setListeners() {
        recipeImg.setOnClickListener {
            showBottomSheetDialog()
        }

        binding.addIngredientButton.setOnClickListener {
            goToAddEditIngredient()
        }

        binding.addRecipeButton.setOnClickListener {
            addRecipeSequence()
        }

        binding.toolbar.backButton.setOnClickListener {
            popBackStack()
        }
    }

    private fun subscribeToObservables() {
        binding.ingredientRecycleView.setHasFixedSize(true)
//        val l = object: LinearLayoutManager(context) { override fun canScrollVertically() = false }
        val layoutManager = LinearLayoutManager(context)
        binding.ingredientRecycleView.layoutManager = layoutManager
        binding.ingredientRecycleView.itemAnimator = DefaultItemAnimator()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ingredients.collectLatest {
                    Log.d(TAG, "viewModel.ingredients.collectLatest = $it")
                    ingredientRecycleViewAdapter = IngredientRecycleViewAdapter(
                        it,
                        IngredientAccessType.EDIT,
                        ::deleteIngredient,
                        ::editIngredient
                    )
                    binding.ingredientRecycleView.adapter = ingredientRecycleViewAdapter
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.imgRef.collectLatest {
                Log.d(TAG, "viewModel.imgRef.collectLatest = $it")
                loadImg(it)
            }
        }
    }

    private fun renderProgressBar(value: Boolean) {
        if (value) {
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

        if (camera != null && gallery != null) {
            camera.setOnClickListener {
                takePhotoSequence()
                bottomSheetDialog.dismiss()
            }
            gallery.setOnClickListener {
//                gallery intent

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PERMISSION_DENIED
                    ) {
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        chooseImageGallery()
                    }
                } else {
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

    private fun takePhotoSequence() {
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.permissions_not_granted_by_the_user),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startCamera() {
        dispatchTakePictureIntent()
    }

    private fun createFileName(): String {
        val ref = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        return ref
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = createFileName()
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
            viewModel.currentPhotoPath = absolutePath
            viewModel.setCurrentImgRef(timeStamp)
        }
    }

    private var cameraLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val file = File(viewModel.currentPhotoPath)
//            val file = File(currentPhotoPath)
            val uri = Uri.fromFile(file)
            Log.d(TAG, "Camera launcher: uri= $uri")
            Log.d(TAG, "Camera launcher: file name= ${file.name}")
            Log.d(TAG, "Camera launcher: current img ref= ${viewModel.currentImgRef.value}")
            val bitmap: Bitmap
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        uri
                    )
                } else {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }
                val fileData = PhotoConverter().bitmapToByteArray(bitmap)
                recipeImg.setImageBitmap(bitmap)
                viewModel.insertPhoto(viewModel.currentImgRef.value, fileData)
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
                e.printStackTrace()
            }
        } else {
            Toast.makeText(context, "Error occurred while taking photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.ltl.recipes.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private fun addRecipeSequence() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val isSuccess = async { viewModel.insertRecipe(args.userEmail) }
            if (isSuccess.await()) {
                Log.d(TAG, "addRecipeSequence: success")
                withContext(Dispatchers.Main) {
                    viewModel.clearIngredients()
                    ingredientViewModel.clear()
                    goToMainFragment()
                }
            } else {
                Log.d(TAG, "addRecipeSequence: error")
                Snackbar.make(
                    binding.addRecipeButton,
                    getString(R.string.something_went_wrong),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun deleteIngredient(ingredient: Ingredient) {
        viewModel.removeIngredient(ingredient)
        ingredientViewModel.removeIngredient(ingredient)
    }

    private fun editIngredient(ingredient: Ingredient) {
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
        popBackStack()
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }


    private fun goToAddEditIngredient() {
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
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}