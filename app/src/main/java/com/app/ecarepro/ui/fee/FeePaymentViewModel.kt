package com.app.ecarepro.ui.fee

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkFeeCollection
import com.app.ecarepro.data.network.model.NetworkGenerateTokenFeePay
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.repository.FomApiRepository
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeePaymentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore,
    ) : ViewModel() {

    val schoolData = MutableLiveData<NetworkSchool>()


    var feePayemtURL : String = ""

    init {
        viewModelScope.launch {
            feePayemtURL = userDataStore.getSchoolData()?.feePayemtURL.toString()
        }
    }


    private val genTokenMutableStateFlow: MutableStateFlow<NetworkResult<NetworkGenerateTokenFeePay>> = MutableStateFlow(
        NetworkResult.Loading())
    val genTokenStateFlow: StateFlow<NetworkResult<NetworkGenerateTokenFeePay>> = genTokenMutableStateFlow



    fun getGenerateToken(
        device : Int
    )=viewModelScope.launch {
        runCatching {
            genTokenMutableStateFlow.value = NetworkResult.Loading()
            userRepository.getGenerateToken( device)
        }.onSuccess {
            genTokenMutableStateFlow.value = NetworkResult.Success(it)
        }.onFailure {
            genTokenMutableStateFlow.value = NetworkResult.Error(it.message)
        }

    }


    init {
        viewModelScope.launch {
            schoolData.postValue(userDataStore.getSchoolData())
        }
    }



}