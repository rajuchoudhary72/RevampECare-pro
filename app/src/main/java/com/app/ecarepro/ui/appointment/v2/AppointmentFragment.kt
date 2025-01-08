package com.app.ecarepro.ui.appointment.v2

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.appointmentPhotoPicker
import com.app.ecarepro.button
import com.app.ecarepro.data.network.model.AppointmentSavedData
import com.app.ecarepro.databinding.FragmentAppointmentBinding
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.textFiled
import com.app.ecarepro.textFiledDropdown
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.ui.taskmanager.add.selectDate
import com.app.ecarepro.utils.FileAccess
import com.app.ecarepro.utils.ItemSelectListener
import com.app.ecarepro.utils.makeTextWatcher
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null

    private val binding get() = _binding!!

    private val viewModel: AppointmentViewModel by viewModels()

    private var photoColumName: String? = null

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)
                    createFileFromBitmapInCache(bitmap, "$photoColumName.png")?.let { file ->
                        viewModel.updateValue(photoColumName, file.absolutePath, imageString)
                    }

                }
            }
        }

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                val bitmap = FileAccess.bitmapFromUri(requireContext(), imgUri)
                val imageString = FileAccess.bitmapToByteArrayBase64String(bitmap)

                createFileFromBitmapInCache(bitmap, "$photoColumName.png")?.let { file ->
                    viewModel.updateValue(photoColumName, file.absolutePath, imageString)
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setObservers()
    }

    private fun setUpViews() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            recyclerView.addItemDecoration(
                LinearMarginDecoration.create(
                    margin = resources.getDimensionPixelOffset(R.dimen.horizontal_margin)
                )
            )
        }
    }


    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.loadingState.collectLatest { loadingState ->
                    mainActivity().showLoader(loadingState.isLoading())
                }
            }

            launch {
                viewModel.uiState.collectLatest { uiState ->
                    buildModel(uiState)
                }
            }

        }

    }


    private fun buildModel(uiState: AppointmentUiState) {
        mainActivity().showLoader(uiState.isLoading())

        uiState.getErrorOrNull()?.let { error ->
            mainActivity().showMessage(error.message ?: "")
        }

        if (uiState is AppointmentUiState.Success || uiState == AppointmentUiState.NoDataFound) {
            binding.recyclerView.withModels {
                when (uiState) {
                    AppointmentUiState.NoDataFound -> {
                        noDataFoundView {
                            id(R.id.empty_view)
                        }
                    }

                    is AppointmentUiState.Success -> {

                        uiState
                            .formData
                            .filter { it.active == true }
                            .filterNot { it.columnName == "IdproofImage" }
                            .forEach { form ->
                                if(form.active == true){
                                    if (isDropDown(form.columnName)) {
                                        when (form.columnName) {
                                            "Purpose" -> {
                                                textFiledDropdown {
                                                    id(form.columnName)
                                                    filedName(form.columnName)
                                                    hintText(form.columnDisplayName)
                                                    text(form.value)
                                                    items(uiState.purpose.map { it.purposeName })
                                                    isMandatory(form.isrequired)
                                                    itemSelectListener(object : ItemSelectListener {
                                                        override fun onItemSelect(item: String) {
                                                            viewModel.updateValue(form.columnName, item)
                                                        }
                                                    })
                                                }
                                            }

                                            "Department" -> {
                                                textFiledDropdown {
                                                    id(form.columnName)
                                                    filedName(form.columnName)
                                                    hintText(form.columnDisplayName)
                                                    text(form.value)
                                                    items(uiState.departments.map { it.departmentName })
                                                    isMandatory(form.isrequired)
                                                    itemSelectListener(object : ItemSelectListener {
                                                        override fun onItemSelect(item: String) {
                                                            viewModel.updateValue(form.columnName, item)
                                                        }
                                                    })
                                                }
                                            }

                                            "Designation" -> {
                                                textFiledDropdown {
                                                    id(form.columnName)
                                                    filedName(form.columnName)
                                                    hintText(form.columnDisplayName)
                                                    text(form.value)
                                                    items(uiState.designation.map { it.designationName })
                                                    isMandatory(form.isrequired)
                                                    itemSelectListener(object : ItemSelectListener {
                                                        override fun onItemSelect(item: String) {
                                                            viewModel.updateValue(form.columnName, item)
                                                        }
                                                    })
                                                }
                                            }

                                            "Employee" -> {
                                                textFiledDropdown {
                                                    id(form.columnName)
                                                    filedName(form.columnName)
                                                    hintText(form.columnDisplayName)
                                                    text(form.value)
                                                    items(uiState.employees.map { it.employeeName })
                                                    isMandatory(form.isrequired)
                                                    itemSelectListener(object : ItemSelectListener {
                                                        override fun onItemSelect(item: String) {
                                                            viewModel.updateValue(form.columnName, item)
                                                        }
                                                    })
                                                }
                                            }
                                            "IdType" -> {
                                                textFiledDropdown {
                                                    id(form.columnName)
                                                    filedName(form.columnName)
                                                    hintText(form.columnDisplayName)
                                                    text(form.value)
                                                    items(uiState.guestIdType)
                                                    isMandatory(form.isrequired)
                                                    itemSelectListener(object : ItemSelectListener {
                                                        override fun onItemSelect(item: String) {
                                                            viewModel.updateValue(form.columnName, item)
                                                        }
                                                    })
                                                }
                                            }

                                            else -> {
                                                textFiledDropdown {
                                                    id(form.columnName)
                                                    filedName(form.columnName)
                                                    hintText(form.columnDisplayName)
                                                    isMandatory(form.isrequired)
                                                    text(form.value)
                                                    itemSelectListener(object : ItemSelectListener {
                                                        override fun onItemSelect(item: String) {
                                                            viewModel.updateValue(form.columnName, item)
                                                        }
                                                    })
                                                }
                                            }
                                        }

                                    } else if (form.columnName == "IdproofImage" || form.columnName == "Photo") {
                                        val idProof =
                                            uiState.formData.firstOrNull { it.columnName == "IdproofImage" }
                                        val photo =
                                            uiState.formData.firstOrNull { it.columnName == "Photo" }
                                        appointmentPhotoPicker {
                                            id("photoPicker")
                                            filedName1(idProof?.columnDisplayName)
                                            isPhotoCapture1Mandatory(idProof?.isrequired)
                                            filedName2(photo?.columnDisplayName)
                                            image1(idProof?.value)
                                            image2(photo?.value)
                                            isPhotoCapture2Mandatory(photo?.isrequired)
                                            clickListener1 { _ ->
                                                photoColumName = idProof?.columnName
                                                selectImageOptionDialog()
                                            }
                                            clickListener2 { _ ->
                                                photoColumName = photo?.columnName
                                                selectImageOptionDialog()
                                            }
                                        }
                                    } else {
                                        textFiled {
                                            id(form.columnName)
                                            filedName(form.columnName)
                                            hintText(form.columnDisplayName)
                                            isMandatory(form.isrequired)
                                            text(form.value)
                                            if (form.columnName == "VisitingDate") {
                                                clickListener { _ ->
                                                    selectDate("Select Visiting Date") {
                                                        viewModel.updateValue(form.columnName, it)
                                                    }
                                                }
                                            } else if (form.columnName == "Appointmenttime") {
                                                clickListener { _ ->
                                                    pickTime("Select Appointment Time") {
                                                        viewModel.updateValue(form.columnName, it)
                                                    }
                                                }
                                            }
                                            textWatcher(makeTextWatcher {
                                                viewModel.updateValue(form.columnName, it.toString())
                                            })
                                        }
                                    }
                                }
                            }

                        button {
                            id("button")
                            clickListener { _ ->
                                viewModel.submitForm { isSuccess, message, data: AppointmentSavedData? ->
                                    mainActivity().showMessage(message)
                                    if (isSuccess) {
                                        if (arguments?.getBoolean("toAppointment") == true) {
                                            findNavController().navigate(
                                                R.id.printOutAppointenentFragment,
                                                bundleOf("appointmentData" to data?.appdetails)
                                            )
                                        } else {
                                            findNavController().popBackStack()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun pickTime(title: String, onTimeSet: (String) -> Unit) {
        if (isAdded.not()) return
        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(INPUT_MODE_CLOCK)
            .setTitleText(title)
            .build()

        materialTimePicker.addOnPositiveButtonClickListener {
            val hour = materialTimePicker.hour
            val minute = materialTimePicker.minute
            onTimeSet("$hour:$minute")
        }
        materialTimePicker.show(childFragmentManager, "timePicker")
    }

    private fun isDropDown(columnName: String?): Boolean {
        val dropDownColumns =
            mutableListOf("Purpose", "Department", "Designation", "Employee", "IdType")
        return dropDownColumns.contains(columnName)
    }

    private fun selectImageOptionDialog() {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Photo!")
        builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
            FileAccess.checkPermission(this)
            if (items[item] == "Take Photo") {
                cameraLauncher.launch(FileAccess.cameraIntent())
            } else if (items[item] == "Choose from Library") {
                galleryLauncher.launch(FileAccess.galleryIntent())
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }


    private fun createFileFromBitmapInCache(bitmap: Bitmap, fileName: String): File? {
        val cacheDir = requireContext().cacheDir
        val file = File(cacheDir, fileName)

        return try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}