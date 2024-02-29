package com.app.ecarepro.ui.questionnaire.post_questionnaire

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.databinding.FragmentPostQustionnaireBinding
import com.app.ecarepro.utils.FileAccess
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostQuestionnaireFragment : Fragment() {

    private lateinit var imageExt: String
    private lateinit var imageString: String
    private lateinit var binding: FragmentPostQustionnaireBinding
    private val viewMode: PostQuestionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostQustionnaireBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.textFiledThoughts.doAfterTextChanged {
            if (it != null) {
                binding.btnAdd.isEnabled = it.isNotEmpty()
            }
        }

        binding.btnAdd.setOnClickListener {
            viewMode.addQuestion(
                binding.textFiledThoughts.text.toString(),
                imageString, "", imageExt  )

            Toast.makeText(activity,"Successfully!!!", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        binding.btnAddImage.setOnClickListener {
            selectImageOptionDialog()
        }



    }

    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission( this@PostQuestionnaireFragment)
            if (items[item] == "Take Photo") {
                cameraLauncher.launch(FileAccess.cameraIntent( ))
             } else if (items[item] == "Choose from Library") {
                galleryLauncher.launch(FileAccess.galleryIntent( ))
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                binding.ivAddedImage.setImageURI(imgUri)

                val bitmap=  FileAccess.bitmapFromUri(requireContext(),imgUri)

                imageString = FileAccess. bitmapToByteArrayBase64String(bitmap)

                imageExt = FileAccess.getImageExtFromUri(requireContext(),bitmap).toString()

            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                   val   bitmap = result.data?.extras?.get("data") as Bitmap
                    binding.ivAddedImage.setImageBitmap(bitmap)

                  imageString = FileAccess. bitmapToByteArrayBase64String(bitmap)

                    imageExt = FileAccess.getImageExtFromUri(requireContext(),bitmap).toString()


                }
            }
        }







}