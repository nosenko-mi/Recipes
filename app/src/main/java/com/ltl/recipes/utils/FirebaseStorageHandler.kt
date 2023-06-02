package com.ltl.recipes.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.io.File

class FirebaseStorageHandler(path: String) {

    private val firebaseStorage = FirebaseStorage.getInstance()
    private var imgRef: StorageReference

    init {
        imgRef = firebaseStorage.reference.child(path)
        Log.d(TAG, "imgRef: $imgRef")
    }

    suspend fun putPhoto(data: ByteArray){
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .build()
        imgRef.putBytes(data, metadata)
            .addOnSuccessListener {
                Log.d(TAG, it.metadata.toString())

            }
            .addOnFailureListener{
                Log.e(TAG, it.message.toString())
            }

//        val t = imgRef.putBytes(data, metadata).await()
    }

    suspend fun putPhoto(imgName: String, imgData: ByteArray){
        imgRef = imgRef.child(imgName)
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .build()
        imgRef.putBytes(imgData, metadata)
            .addOnSuccessListener {
                Log.d(TAG, it.metadata.toString())

            }
            .addOnFailureListener{
                Log.e(TAG, it.message.toString())
            }

//        val t = imgRef.putBytes(data, metadata).await()
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
                Log.d(TAG, it.bytesTransferred.toString())
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
            }
        return bitmap
    }

    companion object {
        private const val TAG = "FirebaseStorageHandler"
    }

}