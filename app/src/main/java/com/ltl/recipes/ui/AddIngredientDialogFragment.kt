package com.ltl.recipes.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ltl.recipes.databinding.FragmentAddIngredientDialogBinding


class AddIngredientDialogFragment : DialogFragment() {
    
    private lateinit var binding: FragmentAddIngredientDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments != null) {
            if (requireArguments().getBoolean("notAlertDialog")) {
                return super.onCreateDialog(savedInstanceState)
            }
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Alert Dialog")
        builder.setMessage("Alert Dialog inside DialogFragment")
        builder.setPositiveButton("Ok",
            DialogInterface.OnClickListener { dialog, which -> dismiss() })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dismiss() })
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddIngredientDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editText = binding.inEmail
        if (arguments != null && !TextUtils.isEmpty(requireArguments().getString("email"))) editText.setText(
            requireArguments().getString("email")
        )
        val btnDone = binding.btnDone
        btnDone.setOnClickListener {
//            val dialogListener = activity as DialogListener?
//            dialogListener!!.onFinishEditDialog(editText.text.toString())
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
            dismiss()
            goToAddRecipe()
        }
    }

    private fun goToAddRecipe() {
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("API123", "onCreate")
//        TODO Check device screen dimension
        var setFullScreen = true
//        if (arguments != null) {
//            setFullScreen = requireArguments().getBoolean("fullScreen")
//        }
        if (setFullScreen) setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    interface DialogListener {
        fun onFinishEditDialog(inputText: String?)
    }
}