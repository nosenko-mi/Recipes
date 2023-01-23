package com.ltl.recipes

import android.content.Context
import android.content.res.Resources
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.util.*


class AppConfig {

    companion object {
        fun getConfigValue(context: Context?, s: String): String? {
            val resources: Resources = context!!.resources
            try {
                val rawResource: InputStream = resources.openRawResource(R.raw.app)
                val properties = Properties()
                properties.load(rawResource)
                return properties.getProperty(s)
            } catch (e: Resources.NotFoundException) {
                Log.e(TAG, "Unable to find the config file: " + e.message)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to open config file.")
            }
            return null
        }

        private const val TAG = "Config"
    }
}