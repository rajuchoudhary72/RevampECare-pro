package com.app.ecarepro.ui.appointment.v2

import android.text.TextUtils
import android.util.Log
import android.util.Patterns
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
import org.json.JSONObject
import retrofit2.HttpException
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.AppointmentSavedData
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.app.ecarepro.ui.taskmanager.add.convertMillisToDateString
import androidx.lifecycle.SavedStateHandle
import com.app.ecarepro.data.network.model.VisitorDetails

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val loadingState = MutableStateFlow<LoadingState>(LoadingState.Success)
    val uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)

    val visitorDetails = savedStateHandle.getStateFlow<VisitorDetails?>("visitorDetails", null)
    val mobileNumber = savedStateHandle.get<String?>("mobileNumber")
    init {
        viewModelScope.launch {
            combine(
                flow = userRepository.getFormData(),
                flow2 = userRepository.getFormDataPurpose(),
                flow3 = userRepository.getFormDataDepartment(),
                flow4 = visitorDetails
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

                    if (formData.isSuccess && purpose.isSuccess && departments.isSuccess ) {
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
                                                form.copy(value = visitorDetails?.name)
                                            }
                                            "Mobile" -> {
                                                form.copy(value = mobileNumber)
                                            }
                                            "Email" -> {
                                                form.copy(value = visitorDetails?.email)
                                            }
                                            "Address" -> {
                                                form.copy(value = visitorDetails?.address)
                                            }
                                            "Company" -> {
                                                form.copy(value = visitorDetails?.company)
                                            }
                                            "VisitingDate" -> {
                                                form.copy(value = convertMillisToDateString())
                                            }
                                            "Appointmenttime" -> {
                                                form.copy(value = getCurrentTime())
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
    private fun getCurrentTime(): String? {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return currentTime.format(formatter)
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

    fun submitForm(func: (Boolean, String, AppointmentSavedData?) -> Unit) {
        viewModelScope.launch {
            if (isValid().not()) {
                func(false, "Please fill all required fields", null)
                return@launch
            }
            val uiState = uiState.value
            if (uiState is AppointmentUiState.Success) {
                loadingState.update { LoadingState.Loading }
                val data = mutableMapOf<String, String>()

               // data["VisitorType"] = "2" //1=visitor , 2=parent , 3=vendor(not in App.)

                data["VisitorType"] = visitorDetails.value?.visitorType?.toString()?:"2"
                data["captureImg"] = "null"
                data["VisitorPhoto"] = "null"
                data["userfrom"] = "2"  // 2 =walk in ,3=e-care,4= invitation form

                uiState
                    .formData
                    .filter { it.active == true }
                    .filter { it.value.orEmpty().isNotEmpty()}
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
                            "Appointmenttime" -> {
                                data["VisitingTime"] = form.value ?: ""
                            }
                            "usertype" -> {
                                data["usertype"] = if (form.value == "Parent") "2" else "1"
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
                            result.getOrNull()?.message
                                ?: "We have successfully updated your appointment to the school for review.Kindly check your message or email for current status of the appointment and confirmation code.",
                            result.getOrNull()
                        )
                    } else {
                        val error = result.exceptionOrNull() ?: IllegalArgumentException(
                            UNKNOWN_ERROR_MESSAGE
                        )
                        if (error is HttpException) {
                            if (error.code() == 400) {
                                func(
                                    false,
                                    "One or more validation errors occurred.",
                                    null
                                )
                            } else {
                                func(
                                    false,
                                    error.message ?: UNKNOWN_ERROR_MESSAGE,
                                    null
                                )
                            }

                        } else {
                            func(
                                false,
                                error.message ?: UNKNOWN_ERROR_MESSAGE,
                                null
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
        val userType: List<Pair<String, Int>> = listOf(Pair("Parent", 2), Pair("Visitor", 1)),
    ) : AppointmentUiState

    data class Error(
        val error: Throwable,
    ) : AppointmentUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}