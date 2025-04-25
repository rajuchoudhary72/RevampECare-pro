package com.app.ecarepro.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkCircularDetails
import com.app.ecarepro.data.network.model.NetworkContactUrl
import com.app.ecarepro.data.network.model.NetworkNoticDetails
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
): ViewModel() {


    private val contactDTLStateFlow: MutableStateFlow<NetworkResult<NetworkContactUrl>> = MutableStateFlow(
        NetworkResult.Loading())
    val _contactUrlDTLStateFlow: StateFlow<NetworkResult<NetworkContactUrl>> = contactDTLStateFlow
    fun getContactUrl() = viewModelScope.launch {
        runCatching {
            contactDTLStateFlow.value = NetworkResult.Loading()
            schoolRepository.getContactDTL()

        }.onSuccess {
            contactDTLStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            contactDTLStateFlow.value = NetworkResult.Error(it.message)
        }
    }

}