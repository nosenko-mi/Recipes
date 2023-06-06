package com.ltl.recipes.utils

import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ltl.recipes.R
import com.ltl.recipes.constants.FirebaseConstants

class GlideImageLoader(private val glide: RequestManager) {

    fun loadImage(fileName: String, view: ImageView){
        val location = buildString {
            append(FirebaseConstants.StorageBaseUrlTest)
            append(fileName)
        }
        val fileRef: StorageReference = FirebaseStorage.getInstance()
            .getReference(location)
        Log.d("GlideImageLoader", "loadImg: fileRef = $fileRef")

        glide
            .load(fileRef)
            .placeholder(R.drawable.recipe_default)
            .into(view)
    }

}