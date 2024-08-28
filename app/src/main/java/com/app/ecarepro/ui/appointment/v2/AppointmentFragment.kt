package com.app.ecarepro.ui.appointment.v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.ecarepro.R
import com.app.ecarepro.appointmentPhotoPicker
import com.app.ecarepro.button
import com.app.ecarepro.databinding.FragmentAppointmentBinding
import com.app.ecarepro.noDataFoundView
import com.app.ecarepro.textFiled
import com.app.ecarepro.textFiledDropdown
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.ItemSelectListener
import com.app.ecarepro.utils.makeTextWatcher
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null

    private val binding get() = _binding!!

    private val viewModel: AppointmentViewModel by viewModels()

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
                            .filterNot { it.columnName == "IdproofImage" }
                            .forEach { form ->
                                if (isDropDown(form.columnName)) {
                                    if (form.columnName == "Purpose") {
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
                                    } else if (form.columnName == "Department") {
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
                                    } else if (form.columnName == "Designation") {
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
                                    } else if (form.columnName == "Employee") {
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
                                    } else {
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
                                        isPhotoCapture2Mandatory(photo?.isrequired)
                                        clickListener1 { _ ->
                                            // id proof picker
                                        }
                                        clickListener2 { _ ->
                                            // photo picker
                                        }
                                    }
                                } else {
                                    textFiled {
                                        id(form.columnName)
                                        filedName(form.columnName)
                                        hintText(form.columnDisplayName)
                                        isMandatory(form.isrequired)
                                        text(form.value)
                                        textWatcher(makeTextWatcher {
                                            viewModel.updateValue(form.columnName, it.toString())
                                        })
                                    }
                                }
                            }

                        button {
                            id("button")
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun isDropDown(columnName: String?): Boolean {
        val dropDownColumns =
            mutableListOf("Purpose", "Department", "Designation", "Employee", "Guest IdType")
        return dropDownColumns.contains(columnName)
    }


}