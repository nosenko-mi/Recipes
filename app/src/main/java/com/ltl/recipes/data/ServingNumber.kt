package com.ltl.recipes.data

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ltl.recipes.BR

class ServingNumber (private var number: Int): BaseObservable() {

    @Bindable
    fun getNumber(): Int{
        Log.d("RecipeServingNumber", "getNumber(): ${number}")
        return number
    }

    fun setNumber(number: Int){
        this.number = number
        notifyPropertyChanged(BR.number)
        Log.d("RecipeServingNumber", "setNumber(): ${number}")
    }

}