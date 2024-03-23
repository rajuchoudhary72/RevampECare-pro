package com.app.ecarepro.ui.class_promo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.NetworkResult
import com.app.ecarepro.data.repository.SchoolRepository
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.model.AppResponse
import com.app.ecarepro.model.ClassPromotionModel
import com.app.ecarepro.model.PromotionModel
import com.app.ecarepro.model.RequestClassPromotion
import com.app.ecarepro.model.StudentPromotedClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassPromotionsViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val classList: MutableStateFlow<NetworkResult<ClassPromotionModel>> =
        MutableStateFlow(NetworkResult.Loading())
    val _classList: StateFlow<NetworkResult<ClassPromotionModel>> = classList

    private val promotionModel: MutableStateFlow<NetworkResult<PromotionModel>> =
        MutableStateFlow(NetworkResult.Loading())
    val _promotionModel: StateFlow<NetworkResult<PromotionModel>> = promotionModel


    private val saveResponse: MutableStateFlow<NetworkResult<AppResponse>> =
        MutableStateFlow(NetworkResult.Loading())
    val _saveResponse: StateFlow<NetworkResult<AppResponse>> = saveResponse

    private var yrID = 0
    private var nYrID = 0

    fun getClassList() = viewModelScope.launch {
        runCatching {
            classList.value = NetworkResult.Loading()
            schoolRepository.getClass()
        }.onSuccess {
            classList.value = NetworkResult.Success(it)
        }.onFailure {
            classList.value = NetworkResult.Error(it.message)
        }

    }

    fun getClassPromotions(classId: String) = viewModelScope.launch {
        runCatching {
            promotionModel.value = NetworkResult.Loading()
            schoolRepository.getClassPromotions(classId)
        }.onSuccess {
            yrID = it.yrID ?: 0
            nYrID = it.nYrID ?: 0
            promotionModel.value = NetworkResult.Success(it)
        }.onFailure {
            promotionModel.value = NetworkResult.Error(it.message)
        }
    }


    fun submitClassPromotions(requestList: MutableList<StudentPromotedClass>) =
        viewModelScope.launch {
            val requestClassPromotion = RequestClassPromotion(yrID = "$yrID", nYrID = "$nYrID")
            requestClassPromotion.studentPromotedClasses = requestList
            runCatching {
                saveResponse.value = NetworkResult.Loading()
                schoolRepository.submitClassPromotions(requestClassPromotion)
            }.onSuccess {
                saveResponse.value = NetworkResult.Success(it)
            }.onFailure {
                saveResponse.value = NetworkResult.Error(it.message)
            }

        }

}