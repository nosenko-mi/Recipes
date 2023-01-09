package com.ltl.recipes.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class PhotoConverter {

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }
}