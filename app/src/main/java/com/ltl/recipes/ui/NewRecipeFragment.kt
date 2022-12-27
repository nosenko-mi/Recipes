package com.ltl.recipes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ltl.recipes.R
import com.ltl.recipes.databinding.FragmentNewRecipeBinding


class NewRecipeFragment : Fragment() {

    private lateinit var binding: FragmentNewRecipeBinding
    private lateinit var recipeImg: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeImg = binding.recipeImgImageView

        recipeImg.setOnClickListener{
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.add_img_bottom_sheet_dialog);
        val camera = bottomSheetDialog.findViewById<LinearLayout>(R.id.cameraLayout)
        val gallery = bottomSheetDialog.findViewById<LinearLayout>(R.id.galleryLayout)

        if (camera != null && gallery!= null) {
            camera.setOnClickListener{
//                camera intent
                Toast.makeText(context, "camera", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss();
            }
            gallery.setOnClickListener{
//                gallery intent
                Toast.makeText(context, "gallery", Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss();
            }
        }

        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        bottomSheetDialog.show();

    }


}