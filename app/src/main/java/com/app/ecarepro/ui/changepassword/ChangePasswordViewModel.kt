package com.app.ecarepro.ui.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    val currentPassword = MutableStateFlow("")
    val newPassword = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    val isPasswordValid = combine(
        flow = currentPassword,
        flow2 = newPassword,
        flow3 = confirmPassword,
    ) { current, new, confirm ->
        (current.isNotEmpty() && validatePassword(new) && new == confirm)
    }.asLiveData()

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9!@\$%^&*])(?=.*[a-zA-Z])[a-zA-Z0-9!@\$%^&*]{5,10}$".toRegex()
        return passwordRegex.matches(password)
    }
    fun changePassword(result: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            userRepository
                .changePassword(confirmPassword.value, confirmPassword.value)
                .collectLatest { result ->
                    if (result.isSuccess) {
                        result(true, result.getOrNull()?.message ?: "Success")
                        sendAnalyticEvent(
                            event = AnalyticsConstants.Events.CHANGE_USER_PASSWORD_DETAIL,
                            attributes = mapOf(
                                AnalyticsConstants.Attributes.OLD_PASSWORD to currentPassword.value,
                                AnalyticsConstants.Attributes.NEW_PASSWORD to newPassword.value
                            )
                        )
                    } else {
                        result(false, result.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE)
                    }
                }
        }
    }

    fun sendScreenEvent(){
        analyticsManager.trackScreen(AnalyticsConstants.Screens.CHANGE_USER_PASSWORD)
    }
    fun sendAnalyticEvent(
        event: String,
        attributes: Map<String, String>
    ) {
        analyticsManager.trackEvent(
            event,
            attributes
        )
    }
}