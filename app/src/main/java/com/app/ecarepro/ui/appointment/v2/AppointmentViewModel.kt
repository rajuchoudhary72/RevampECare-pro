package com.app.ecarepro.ui.appointment.v2

import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
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
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    val loadingState = MutableStateFlow<LoadingState>(LoadingState.Success)
    val uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)


    init {
        viewModelScope.launch {
            combine(
                flow = userRepository.getFormData(),
                flow2 = userRepository.getFormDataPurpose(),
                flow3 = userRepository.getFormDataDepartment(),
                flow4 = userRepository.getVisitorDetails()
            ) { formData, purpose, departments, visitorDetails ->
                Pair(
                    Triple(
                        formData,
                        purpose,
                        departments
                    ), visitorDetails
                )
            }
                .map { response ->
                    val formData = response.first.first
                    val purpose = response.first.second
                    val departments = response.first.third
                    val visitorDetails = response.second

                    if (formData.isSuccess && purpose.isSuccess && departments.isSuccess && visitorDetails.isSuccess) {
                        val formDataResult = formData.getOrNull()
                        val purposeResult = purpose.getOrNull()
                        val departmentResult = departments.getOrNull()
                        formDataResult.let { response ->
                            if (response.isNullOrEmpty()) {
                                AppointmentUiState.NoDataFound
                            } else {
                                AppointmentUiState.Success(
                                    formData = response.map { form ->
                                        when (form.columnName) {
                                            "Name" -> {
                                                form.copy(value = visitorDetails.getOrNull()?.name)
                                            }

                                            "Mobile" -> {
                                                form.copy(value = visitorDetails.getOrNull()?.mobile)
                                            }

                                            "Email" -> {
                                                form.copy(value = visitorDetails.getOrNull()?.email)
                                            }

                                            "Address" -> {
                                                form.copy(value = visitorDetails.getOrNull()?.address)
                                            }

                                            "Company" -> {
                                                form.copy(value = visitorDetails.getOrNull()?.company)
                                            }

                                            else -> {
                                                form
                                            }
                                        }
                                    },
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


    fun updateValue(columnName: String?, toString: String, base64Image: String = "") {
        val uiState = uiState.value
        if (uiState is AppointmentUiState.Success) {
            this.uiState.update {
                uiState.copy(formData = uiState.formData.map { form ->
                    if (form.columnName == columnName) {
                        form.copy(value = toString, base64Image = base64Image)
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

    fun submitForm(func: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (isValid().not()) {
                func(false, "Please fill all required fields")
                return@launch
            }
            val uiState = uiState.value
            if (uiState is AppointmentUiState.Success) {
                loadingState.update { LoadingState.Loading }
                val data = mutableMapOf<String, String>()

                data["VisitorType"] = "2"
                data["captureImg"] = "null"
                data["VisitorPhoto"] = "null"
                data["userfrom"] = "3"

                uiState
                    .formData
                    .filter { it.active == true }
                    .forEach { form: Form ->
                        when (form.columnName) {
                            "Photo" -> {
                                data["photo"] = form.base64Image ?: ""
                            }

                            "IdproofImage" -> {
                                data["VisitorPhotoInbyte"] = form.base64Image ?: ""
                            }

                            "IdType" -> {
                                data["IdType"] =
                                    if (form.value == "Aadhar Card") "2" else if ("Pan Card" == form.value) "3" else "1"
                            }

                            "Purpose" -> {
                                uiState.purpose.firstOrNull { it.purposeName == form.value }?.let {
                                    data[form.columnName] = it.purposeID.toString()
                                }
                            }

                            "Department" -> {
                                uiState.departments.firstOrNull { it.departmentName == form.value }
                                    ?.let {
                                        data[form.columnName] = it.departmentID.toString()
                                    }
                            }

                            "Designation" -> {
                                uiState.designation.firstOrNull { it.designationName == form.value }
                                    ?.let {
                                        data[form.columnName] = it.designationID.toString()
                                    }
                            }

                            "Employee" -> {
                                uiState.employees.firstOrNull { it.employeeName == form.value }
                                    ?.let {
                                        data[form.columnName] = it.employeeID.toString()
                                    }
                            }

                            "usertype" -> {
                                data["usertype"] = userDataStore.getUser()?.userType.toString()
                            }

                            else -> {
                                data[form.columnName] = form.value ?: ""
                            }
                        }
                    }
                Log.d("FCM", "nultipart: " + data)


                userRepository.submitForm(data).collectLatest { result ->
                    loadingState.update { LoadingState.Success }
                    if (result.isSuccess) {
                        func(
                            true,
                            result.getOrNull()
                                ?: "We have successfully updated your appointment to the school for review.Kindly check your message or email for current status of the appointment and confirmation code."
                        )
                    } else {
                        val error = result.exceptionOrNull() ?: IllegalArgumentException(
                            UNKNOWN_ERROR_MESSAGE
                        )
                        if (error is HttpException) {
                            if (error.code() == 400) {
                                func(
                                    false,
                                    "One or more validation errors occurred."
                                )
                            } else {
                                func(
                                    false,
                                    error.message ?: UNKNOWN_ERROR_MESSAGE
                                )
                            }

                        } else {
                            func(
                                false,
                                error.message ?: UNKNOWN_ERROR_MESSAGE
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun isValid(): Boolean {
        val isValid = true
        (uiState.value as AppointmentUiState.Success).formData.forEach { form ->
            if (form.isrequired == true) {
                if (form.value.isNullOrEmpty()) {
                    return false
                }

                if (form.columnName.contains("mobile", true) && form.value.length < 10) {
                    return false
                }

                if (form.columnName.contains(
                        "email",
                        true
                    ) && isValidEmail(form.value).not()
                ) {
                    return false
                }
            }
        }
        return isValid
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