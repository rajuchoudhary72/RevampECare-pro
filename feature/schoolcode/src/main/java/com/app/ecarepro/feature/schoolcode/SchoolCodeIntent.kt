package com.app.ecarepro.feature.schoolcode

sealed interface SchoolCodeIntent {
    data class OnDigitChanged(val index: Int, val digit: String) : SchoolCodeIntent
    data object OnNextClicked : SchoolCodeIntent
    object OnFindCodeClicked : SchoolCodeIntent
    data object ClearError : SchoolCodeIntent
}