package com.app.ecarepro.feature.schoolcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay // For simulating network call

class SchoolCodeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SchoolCodeUiState())
    val uiState: StateFlow<SchoolCodeUiState> = _uiState.asStateFlow()

    fun onIntent(intent: SchoolCodeIntent) {
        when (intent) {
            is SchoolCodeIntent.OnDigitChanged -> handleDigitChanged(intent.index, intent.digit)
            SchoolCodeIntent.OnNextClicked -> submitCode()
            SchoolCodeIntent.OnFindCodeClicked -> { /* Handle navigation in UI layer */
            }

            SchoolCodeIntent.ClearError -> clearErrorMessage()
        }
    }

    private fun handleDigitChanged(index: Int, digit: String) {
        if (index < 0 || index >= _uiState.value.codeDigits.size) return
        // Allow only single digit, alphanumeric for flexibility, or enforce numeric if needed
        val newDigit = digit.take(1)

        _uiState.update { currentState ->
            val newDigits = currentState.codeDigits.toMutableList()
            newDigits[index] = newDigit
            currentState.copy(
                codeDigits = newDigits.toList(), // Ensure new list for recomposition
                errorMessage = null // Clear error when user starts typing
            )
        }
    }

    private fun submitCode() {
        if (!_uiState.value.isCodeComplete || _uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            delay(1500) // Simulate network verification
            val currentCode = _uiState.value.codeDigits.joinToString("")
            // --- Replace with actual validation logic ---
            if (currentCode == "123456") { // Example success code
                _uiState.update { it.copy(isLoading = false) }
                // Navigation to next screen will be triggered by the Composable observing this state
                // or via a separate event/sharedFlow if complex navigation logic is needed.
                // For this example, we'll assume the Composable handles it based on a null error
                // and successful submission.
            } else if (currentCode == "000000") { // Example known invalid code for testing error
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "We couldn't match that code. Enter a different one."
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Invalid school code. Please try again."
                    )
                }
            }
        }
    }

    private fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // This function can be called by the Composable when navigation to next screen should happen.
    // However, it's often better to react to state changes (e.g., successful validation and null error)
    // directly in the Composable for navigation.
    // For this example, we assume navigation is handled based on state in the UI.
    fun getEnteredCode(): String = _uiState.value.codeDigits.joinToString("")
}