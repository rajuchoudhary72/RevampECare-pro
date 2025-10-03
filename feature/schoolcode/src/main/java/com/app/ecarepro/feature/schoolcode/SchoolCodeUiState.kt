package com.app.ecarepro.feature.schoolcode

import androidx.compose.runtime.Immutable

@Immutable
data class SchoolCodeUiState(
    val codeDigits: List<String> = List(6) { "" },
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isCodeComplete: Boolean = codeDigits.all { it.isNotEmpty() }
    val isNextButtonEnabled: Boolean = isCodeComplete && !isLoading
}


