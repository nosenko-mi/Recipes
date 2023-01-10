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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.ltl.recipes.R
import com.ltl.recipes.databinding.FragmentNewRecipeBinding
import com.ltl.recipes.utils.PhotoConverter
import com.ltl.recipes.utils.StorageHandler
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class NewRecipeFragment : Fragment() {

    private lateinit var binding: FragmentNewRecipeBinding
    private lateinit var recipeImg: ImageView

    private val firebaseStorage = FirebaseStorage.getInstance()

    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                chooseImageGallery();
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        binding.addRecipeButton.setOnClickListener{
            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
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
                        chooseImageGallery();
                    }
                }else{
                    chooseImageGallery();
                }

//                when {
//                    ContextCompat.checkSelfPermission(
//                        requireContext(),
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ) == PackageManager.PERMISSION_GRANTED -> {
//                        // You can use the API that requires the permission.
//                    }
//                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
//                    // In an educational UI, explain to the user why your app requires this
//                    // permission for a specific feature to behave as expected, and what
//                    // features are disabled if it's declined. In this UI, include a
//                    // "cancel" or "no thanks" button that lets the user continue
//                    // using your app without granting the permission.
//                    showInContextUI()
//
//                }
//                    else -> {
//                        // You can directly ask for the permission.
//                        // The registered ActivityResultCallback gets the result of this request.
//                        requestPermissionLauncher.launch(
//                            Manifest.permission.READ_EXTERNAL_STORAGE)
//                    }
//                }

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
            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())

            // Set the image in imageview for display
            recipeImg.setImageBitmap(photo)

            // upload the image
            val data = photo?.let { PhotoConverter().bitmapToByteArray(it) }
            val storageHandler = StorageHandler("tests", name)

            if (data != null){
                storageHandler.putPhoto(data);
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

    private fun savePicture(){

    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val TAG = "CameraXApp"
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