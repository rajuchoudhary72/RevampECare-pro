package com.app.ecarepro.ui.changeusername

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.ChangeUserNameRequestDto
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
class ChangeUsernameViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    val currentUsername = MutableStateFlow("")
    val newUsername = MutableStateFlow("")

    val isUsernameValid = combine(
        flow = currentUsername,
        flow2 = newUsername,
    ) { current, new ->
        validateUsername(current) && validateUsername(new)
    }.asLiveData()

    private fun validateUsername(username: String): Boolean {
        if (username.isEmpty()) return false
        val usernameRegex = "^[a-zA-Z0-9]{5,10}$".toRegex()
        return usernameRegex.matches(username)
    }

    fun changeUsername(result: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            userRepository
                .changeUserName(
                    ChangeUserNameRequestDto(
                        currentUsername = currentUsername.value,
                        newUsername = newUsername.value
                    )
                )
                .collectLatest { result ->
                    if (result.isSuccess) {
                        result(true, result.getOrNull()?.message ?: "Success")
                        sendAnalyticEvent(
                            event = AnalyticsConstants.Events.CHANGE_USER_NAME_DETAIL,
                            attributes = mapOf(
                                AnalyticsConstants.Attributes.OLD_USER_NAME to newUsername.value,
                                AnalyticsConstants.Attributes.USER_NAME to newUsername.value
                            )
                        )
                    } else {
                        result(false, result.exceptionOrNull()?.message ?: UNKNOWN_ERROR_MESSAGE)
                    }
                }
        }
    }

    fun sendScreenEvent() {
        analyticsManager.trackScreen(AnalyticsConstants.Screens.CHANGE_USER_NAME)
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