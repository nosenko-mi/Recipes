package com.ltl.recipes.utils

import android.text.TextUtils

class EmailValidator {

    companion object {
        fun isValidEmail(target: String): Boolean{
            return if (TextUtils.isEmpty(target)) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
            }
        }
    }

}