package com.app.ecarepro.ui.otpverification

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.submit_assignment.UserDTL
import com.app.ecarepro.data.repository.UserRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userName = savedStateHandle.getLiveData<String>("userName")
    val userName: LiveData<String> get() = _userName

    private val _oTPAuthKey = savedStateHandle.getLiveData<String>("oTPAuthKey")
    val oTPAuthKey: LiveData<String> get() = _oTPAuthKey

    private val _schoolCode = savedStateHandle.getLiveData<String>("schoolCode")
    val schoolCode: LiveData<String> get() = _schoolCode

    private val _message = savedStateHandle.getLiveData<String>("message")
    val message: LiveData<String> get() = _message

    init {
        startCountdown()
    }

    // MutableLiveData to hold the remaining time
    private val _remainingTime = MutableLiveData<Long>()
    val remainingTime: LiveData<Long> get() = _remainingTime

    private var countDownTimer: CountDownTimer? = null

    // Function to start or restart the countdown timer
    private fun startCountdown() {
        // Cancel any existing timer if already running
        countDownTimer?.cancel()

        // Start a new timer with 60 seconds
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the remaining time
                _remainingTime.postValue(millisUntilFinished / 1000) // seconds remaining
            }

            override fun onFinish() {
                // When the timer finishes, set time to 0
                _remainingTime.postValue(0)
            }
        }

        // Start the countdown timer
        countDownTimer?.start()
    }

    // Function to restart the countdown from the beginning
    private fun restartCountdown() {
        startCountdown()
    }

    // Optionally, you can override onCleared to cancel the timer when ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }

    fun resendOtp(result: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            userRepository.resendOtp(
                schoolCode = schoolCode.value.toString(),
                oTPAuthKey = oTPAuthKey.value.toString()
            )
                .collectLatest { result ->
                    result
                        .onSuccess {
                            restartCountdown()
                            _message.postValue(it.message)
                            _oTPAuthKey.postValue(it.otpAuthKey)
                            result(true, null)
                        }.onFailure {
                            result(false, it.message ?: UNKNOWN_ERROR_MESSAGE)
                        }
                }
        }
    }

    fun validateOtp(otp: String, result: (Boolean, String?, UserDTL?) -> Unit) {
        viewModelScope.launch {
            userRepository.validateOtp(
                schoolCode = schoolCode.value.toString(),
                otp = otp,
                oTPAuthKey = oTPAuthKey.value.toString(),
                userName = userName.value.toString()
            )
                .collectLatest { result ->
                    result
                        .onSuccess {
                            _message.postValue(it.message)
                            result(true, null, it.userDTL)
                        }.onFailure {
                            result(false, it.message ?: UNKNOWN_ERROR_MESSAGE, null)
                        }
                }
        }
    }
}