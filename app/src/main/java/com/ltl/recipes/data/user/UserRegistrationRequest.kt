package com.ltl.recipes.data.user

import com.ltl.recipes.utils.EmailValidator
import com.ltl.recipes.utils.Validatable

data class UserRegistrationRequest(
    var email: String,
    var displayName: String,
    var password: String
    )
    : Validatable {
    override fun isValid(): Boolean {
        return EmailValidator.isValidEmail(email) && password.isNotEmpty()
    }
}