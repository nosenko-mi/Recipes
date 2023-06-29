package com.ltl.recipes.data.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel: ViewModel() {

    private val _user = MutableStateFlow<UserModel>(UserModel())
    val currentUser = _user.asStateFlow()
    private var user = MutableLiveData<UserModel>(UserModel())

    fun setNewUser(newUser: UserModel) {
        user.value = newUser
        _user.value = newUser
    }

    fun getCurrentUser(): LiveData<UserModel?>{
        return user
    }

    fun getEmail(): String{
        return user.value?.email.toString()
    }

    fun signOut() {
        // authClient.signOut()
    }

}