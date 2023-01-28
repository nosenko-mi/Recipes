package com.ltl.recipes.data.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {

    private val userRepository = UserRepository()
    private var user = MutableLiveData<UserModel>()

    fun setNewUser(newUser: UserModel) {
        user.value = newUser
    }

    fun getCurrentUser(): LiveData<UserModel>{
        return user
    }

}