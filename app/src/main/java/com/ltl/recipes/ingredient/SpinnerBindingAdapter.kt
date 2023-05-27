package com.ltl.recipes.ingredient

import android.R
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter

private val TAG = "adapter"

@BindingAdapter("entries")
fun Spinner.entries(values: Array<QuantityType>) {
    adapter = ArrayAdapter(
        context,
        R.layout.simple_spinner_dropdown_item,
        values
    )
    Log.d(TAG, "entries.values=${values};adapter=$adapter")
}


//@BindingAdapter(value = ["entries", "selectedValue", "selectedValueAttrChanged"], requireAll = false)
//fun Spinner.selectedValue(
//    values: Array<QuantityType>,
//    newSelectedValue: QuantityType?,
//    newTextAttrChanged: InverseBindingListener
//) {
//    adapter = ArrayAdapter(
//        context,
//        R.layout.simple_spinner_dropdown_item,
//        values
//    )
//    Log.d(TAG, "entries.values=${values};adapter=$adapter")
//    // Check if the adapter is null
//    if (adapter == null) {
//        // Set a default adapter if it's null
//        adapter = ArrayAdapter(
//            context,
//            R.layout.simple_spinner_dropdown_item,
//            QuantityType.values()
//        )
//        Log.e(TAG, "Adapter is null. New adapter=$adapter")
//    }
//
//    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//            parent.getItemAtPosition(position)
//            newTextAttrChanged.onChange()
////            parent.setSelection(position)
//        }
//        override fun onNothingSelected(parent: AdapterView<*>) {}
//    }
//
//    if (newSelectedValue != null) {
//        val pos = (adapter as ArrayAdapter<QuantityType>).getPosition(newSelectedValue)
//        Log.d(TAG, "selectedValue.newSelectedValue=${newSelectedValue.type}; pos=$pos")
//        this.setSelection(pos, true)
//    }
//}
//
//@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
//fun Spinner.getSelectedValue(): String {
//    Log.d(TAG, "getSelectedValue: $selectedItem")
//    return this.selectedItem as String
//}