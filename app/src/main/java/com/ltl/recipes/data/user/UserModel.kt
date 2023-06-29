package com.ltl.recipes.data.user

data class UserModel(
    var userId: String = "",
    var displayName: String? = "local",
    var profilePictureUrl: String? = null,
    var email: String = "local@mail.com"
)
