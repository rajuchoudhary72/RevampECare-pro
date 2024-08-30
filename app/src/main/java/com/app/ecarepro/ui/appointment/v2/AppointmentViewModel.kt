package com.app.ecarepro.ui.appointment.v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Department
import com.app.ecarepro.data.network.model.Designation
import com.app.ecarepro.data.network.model.Employee
import com.app.ecarepro.data.network.model.Form
import com.app.ecarepro.data.network.model.Purpose
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import com.app.ecarepro.ui.staff.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val loadingState = MutableStateFlow<LoadingState>(LoadingState.Success)
    val uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)


    init {
        viewModelScope.launch {
            combine(
                flow = userRepository.getFormData(),
                flow2 = userRepository.getFormDataPurpose(),
                flow3 = userRepository.getFormDataDepartment(),
            ) { formData, purpose, departments -> Triple(formData, purpose, departments) }

                .map { (formData, purpose, departments) ->
                    if (formData.isSuccess && purpose.isSuccess && departments.isSuccess) {
                        val formDataResult = formData.getOrNull()
                        val purposeResult = purpose.getOrNull()
                        val departmentResult = departments.getOrNull()
                        formDataResult.let { response ->
                            if (response.isNullOrEmpty()) {
                                AppointmentUiState.NoDataFound
                            } else {
                                AppointmentUiState.Success(
                                    formData = response,
                                    purpose = purposeResult ?: emptyList(),
                                    departments = departmentResult ?: emptyList(),
                                )
                            }
                        }
                    } else {
                        val error =
                            formData.exceptionOrNull() ?: IllegalArgumentException(
                                UNKNOWN_ERROR_MESSAGE
                            )
                        AppointmentUiState.Error(error)
                    }
                }.collectLatest { uiState ->
                    this@AppointmentViewModel.uiState.update { uiState }
                }
        }
    }


    fun updateValue(columnName: String?, toString: String) {
        val uiState = uiState.value
        if (uiState is AppointmentUiState.Success) {
            this.uiState.update {
                uiState.copy(formData = uiState.formData.map { form ->
                    if (form.columnName == columnName) {
                        form.copy(value = toString)
                    } else {
                        form
                    }
                })
            }
        }
        if (columnName == "Department") {
            loadDesignation(toString)
        }
        if (columnName == "Designation") {
            loadEmployee(toString)
        }
    }

    private fun loadEmployee(designationName: String) {
        viewModelScope.launch {
            val uiState = uiState.value
            if (uiState is AppointmentUiState.Success) {
                val department =
                    uiState.departments.firstOrNull { it.departmentName == uiState.formData.firstOrNull { it.columnName == "Department" }?.value }
                val designation =
                    uiState.designation.firstOrNull { it.designationName == designationName }

                if (department != null && designation != null) {
                    loadingState.update { LoadingState.Loading }
                    updateValue("Employee", "")
                    userRepository
                        .getFormDataEmployee(
                            department.departmentID.toString(),
                            designation.designationID.toString()
                        )
                        .collectLatest { employees ->
                            loadingState.update { LoadingState.Success }
                            this@AppointmentViewModel.uiState.update {
                                uiState.copy(employees = employees.getOrNull() ?: emptyList())
                            }
                        }

                }
            }
        }
    }

    private fun loadDesignation(departmentName: String) {
        viewModelScope.launch {
            val uiState = uiState.value
            if (uiState is AppointmentUiState.Success) {
                val department =
                    uiState.departments.firstOrNull { it.departmentName == departmentName }
                department?.departmentID?.let { departmentID ->
                    loadingState.update { LoadingState.Loading }
                    updateValue("Designation", "")
                    userRepository.getFormDataDesignationWithDepartment(departmentID.toString())
                        .collectLatest { designations ->
                            loadingState.update { LoadingState.Success }
                            this@AppointmentViewModel.uiState.update {
                                uiState.copy(designation = designations.getOrNull() ?: emptyList())
                            }
                        }
                }
            }

        }
    }

    fun addToGuestList(guest: String) {
        val uiState = uiState.value
        if (uiState is AppointmentUiState.Success) {
            this.uiState.update {
                uiState.copy(formData = uiState.formData.map { form ->
                    if (form.columnName == "CoVisitorName") {
                        val list: ArrayList<String> = if (form.guestList.isNullOrEmpty()) {
                            arrayListOf(guest)
                        } else {
                            form.guestList.add(guest)
                            form.guestList
                        }
                        form.copy(
                            guestList = list
                        )
                    } else {
                        form
                    }
                })
            }
        }

    }

    fun removeToGuestList(guest: String) {
        val uiState = uiState.value
        if (uiState is AppointmentUiState.Success) {
            this.uiState.update {
                uiState.copy(formData = uiState.formData.map { form ->
                    if (form.columnName == "CoVisitorName") {
                        val list = form.guestList?.filter { it != guest }
                        form.copy(
                            guestList = list as ArrayList
                        )
                    } else {
                        form
                    }
                })
            }
        }

    }
}

sealed interface AppointmentUiState {

    object Loading : AppointmentUiState

    object NoDataFound : AppointmentUiState

    data class Success(
        val formData: List<Form>,
        val purpose: List<Purpose>,
        val departments: List<Department>,
        val designation: List<Designation> = emptyList(),
        val employees: List<Employee> = emptyList(),
        val guestIdType: List<String> = listOf("Aadhar Card", "Pan Card", "Driving License"),
    ) : AppointmentUiState

    data class Error(
        val error: Throwable,
    ) : AppointmentUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}