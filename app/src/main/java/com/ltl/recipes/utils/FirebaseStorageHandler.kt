package com.ltl.recipes.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File

class FirebaseStorageHandler(directory: String, filename: String) {

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

    fun getPhoto(path: String): Bitmap{
        val file = File.createTempFile("tmpFile", ".jpg")
//        TODO: create basic image if couldn't fetch from Storage
        val conf = Bitmap.Config.ARGB_8888
        var bitmap: Bitmap = Bitmap.createBitmap(256, 256, conf)
        val ref = firebaseStorage.getReference(path)
        ref.getFile(file)
            .addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(file.absolutePath)
                Log.d("StorageHandler", it.bytesTransferred.toString())
            }
            .addOnFailureListener {
                Log.e("StorageHandler", it.message.toString())
            }
        return bitmap
    }

}