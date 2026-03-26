package com.app.ecarepro.core.ui.component

import androidx.compose.runtime.Immutable

/**
 * Data class representing a tab in the profile details screen.
 *
 * @param T The type of the section enum associated with this tab
 * @param displayName The name to display for this tab
 * @param section The section enum value associated with this tab
 */
@Immutable
data class ProfileTab<T>(
    val displayName: String,
    val section: T
)
