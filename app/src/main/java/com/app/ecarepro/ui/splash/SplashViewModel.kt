package com.app.ecarepro.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {

    val school = MutableLiveData<NetworkSchool>()

    init {
        viewModelScope.launch {
            school.postValue(userDataStore.getSchoolData())
        }
    }

    suspend fun getSliders() {
        schoolRepository.fetchWalkThroughData()
    }

    suspend fun isUserAuthenticated() = userDataStore.isUserAuthenticated()
}