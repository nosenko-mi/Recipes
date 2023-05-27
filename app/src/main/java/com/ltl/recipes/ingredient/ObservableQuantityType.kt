package com.ltl.recipes.ingredient

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.ltl.recipes.BR

class ObservableQuantityType(private var type: QuantityType): BaseObservable() {

    @Bindable
    fun getType(): Int{
        Log.d("ObservableQuantityType", "get: $type")
        return type.toSpinnerPosition()
    }

    fun setType(type: QuantityType){
        this.type = type
        notifyPropertyChanged(BR.quantityType)
        Log.d("ObservableQuantityType", "set: $type")
    }

}