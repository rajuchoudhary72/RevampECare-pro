package com.app.ecarepro.ui.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.database.databases.SchoolDatabase
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore,
    private val schoolDatabase: SchoolDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isStudentLoginBlocked = savedStateHandle.get<Boolean>("isStudentLoginBlocked")
    val schoolCode = savedStateHandle.get<String>("schoolCode")
        ?: throw IllegalArgumentException("School code required")

    val isUserAuthenticated = MutableLiveData(false)

    val school = userDataStore.getCurrentSchoolCodeAsFlow().flatMapLatest {
        if (it.isNullOrEmpty()) {
            schoolDatabase.getSchoolsFlow().map { it.lastOrNull() }
        } else {
            schoolDatabase.getSchoolFlow(it)
        }
    }.asLiveData()

    init {
        viewModelScope.launch {
            isUserAuthenticated.value = userDataStore.isUserAuthenticated()
        }
    }


    fun verifyUser(username: String, onResponse: (NetworkUserDetailsDto) -> Unit) {
        viewModelScope.launch {
            onResponse(
                userRepository.verifyUser(
                    schoolCode = schoolCode,
                    username = username
                )
            )
        }
    }
    suspend fun isUserAlreadyLogin(userId: Int?, userType: Int?):Boolean {
        val user = userDataStore.getUsersFlow().map { users ->
            users.firstOrNull { it.userId == userId && it.schoolCode == schoolCode && it.userType == userType }
        }.first()
        return user != null
    }

    fun login(username: String, password: String, onResponse: (LoginResponseDto) -> Unit) {
        viewModelScope.launch {
            onResponse(
                userRepository.login(
                    schoolCode = schoolCode,
                    userName = username,
                    password = password
                )
            )
        }
    }


}