package com.ltl.recipes.utils

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference

class StorageHandler(directory: String, filename: String) {

    private val firebaseStorage = FirebaseStorage.getInstance()
    private var dirRef: StorageReference
    private var imgRef: StorageReference

    init {
        dirRef = firebaseStorage.reference.child(directory)
        imgRef = dirRef.child(filename)
    }

    fun putPhoto(data: ByteArray){
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .build()

        imgRef.putBytes(data, metadata)
            .addOnSuccessListener {
                Log.d("StorageHandler", it.metadata.toString())

            }
            .addOnFailureListener{
                Log.e("StorageHandler", it.message.toString())
            }
    }

}