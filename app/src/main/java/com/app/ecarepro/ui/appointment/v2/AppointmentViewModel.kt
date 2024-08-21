package com.app.ecarepro.ui.appointment.v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Form
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    val uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)


    init {
        viewModelScope.launch {
            userRepository.getFormData()
                .map { result ->
                    if (result.isSuccess) {
                        result.getOrNull().let { response ->
                            if (response.isNullOrEmpty()) {
                                AppointmentUiState.NoDataFound
                            } else {
                                AppointmentUiState.Success(response)
                            }
                        }
                    } else {
                        val error =
                            result.exceptionOrNull() ?: IllegalArgumentException(
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
                AppointmentUiState.Success(uiState.formData.map { form ->
                    if (form.columnName == columnName) {
                        form.copy(value = toString)
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
        val formData: List<Form>
    ) : AppointmentUiState

    data class Error(
        val error: Throwable,
    ) : AppointmentUiState

    fun isLoading() = this == Loading

    fun getErrorOrNull() = if (this is Error) this.error else null
}