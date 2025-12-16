package com.app.ecarepro.feature.navigation

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * BottomSheetConfiguration - Configuration for bottom sheet appearance and behavior
 *
 * Based on iOS BottomSheetConfiguration, provides comprehensive customization
 * for bottom sheet presentations.
 *
 * Example usage:
 * ```kotlin
 * BottomSheetConfiguration(
 *     title = "Select Subject",
 *     heightConfig = BottomSheetHeightConfig.Dynamic,
 *     isDismissible = true,
 *     showHeaderContentSeparator = true
 * )
 * ```
 */
@Stable
data class BottomSheetConfiguration(
    /**
     * Optional title displayed in the header
     */
    val title: String? = null,

    /**
     * Optional icon displayed next to the title
     */
    val icon: ImageVector? = null,

    /**
     * Height configuration for the sheet
     */
    val heightConfig: BottomSheetHeightConfig = BottomSheetHeightConfig.Dynamic,

    /**
     * Whether user can dismiss by tapping outside or swiping down
     */
    val isDismissible: Boolean = true,

    /**
     * Show divider line below the header
     */
    val showHeaderContentSeparator: Boolean = true,

    /**
     * Visual styling configuration
     */
    val style: BottomSheetStyle = BottomSheetStyle(),

    /**
     * Callback invoked when the sheet is dismissed
     */
    val dismissAction: (() -> Unit)? = null
)

/**
 * BottomSheetHeightConfig - Defines how the bottom sheet height is determined
 *
 * Based on iOS BottomSheetHeightConfig with three modes:
 * - Dynamic: Wraps content height (up to max screen height)
 * - Static: Fixed height value
 * - BelowStatusBar: Nearly full screen (screen height - status bar)
 */
sealed class BottomSheetHeightConfig {

    /**
     * Dynamic height - wraps content (recommended for most cases)
     * Automatically adjusts to content size up to maximum screen height
     */
    object Dynamic : BottomSheetHeightConfig()

    /**
     * Static height - fixed height value
     *
     * @param height The fixed height in dp
     */
    data class Static(val height: Dp) : BottomSheetHeightConfig()

    /**
     * Below status bar - nearly full screen
     * Height = screen height - 20dp (for status bar peek)
     */
    object BelowStatusBar : BottomSheetHeightConfig()

    /**
     * Check if this is dynamic height
     */
    val isDynamic: Boolean
        get() = this is Dynamic

    /**
     * Get the height value for this configuration
     *
     * @param screenHeight The total screen height
     * @return The calculated height, or null for dynamic
     */
    fun getHeight(screenHeight: Dp): Dp? {
        return when (this) {
            is Dynamic -> null // Will be calculated based on content
            is Static -> height
            is BelowStatusBar -> screenHeight - 20.dp
        }
    }

    override fun toString(): String {
        return when (this) {
            is Dynamic -> "Dynamic"
            is Static -> "Static($height)"
            is BelowStatusBar -> "BelowStatusBar"
        }
    }
}

/**
 * BottomSheetStyle - Visual styling configuration for bottom sheet
 *
 * Controls appearance aspects like colors, padding, and behavior.
 */
@Stable
data class BottomSheetStyle(
    /**
     * Background color of the sheet
     */
    val background: Color = Color.White,

    /**
     * Whether drag-to-dismiss is enabled
     */
    val isDraggable: Boolean = true,

    /**
     * Top padding for content area
     */
    val contentTopPadding: Dp = 10.dp,

    /**
     * Bottom padding for content area (auto-adjusted for safe area)
     */
    val contentBottomPadding: Dp = 0.dp,

    /**
     * Whether to show scroll indicators in scrollable content
     */
    val showsScrollIndicators: Boolean = true,

    /**
     * Corner radius for the top corners
     */
    val cornerRadius: Dp = 16.dp,

    /**
     * Background scrim color (color behind the sheet)
     */
    val scrimColor: Color = Color.Black.copy(alpha = 0.5f),

    /**
     * Drag handle color
     */
    val dragHandleColor: Color = Color.Gray.copy(alpha = 0.3f)
)

/**
 * BottomSheetViewState - Observable state for bottom sheets with loading/error states
 *
 * Use this when your bottom sheet content needs to manage async operations.
 */
@Stable
data class BottomSheetViewState(
    /**
     * Current state of the bottom sheet content
     */
    val state: State = State.Idle,

    /**
     * Title for the sheet header
     */
    val title: String? = null,

    /**
     * Icon for the sheet header
     */
    val icon: ImageVector? = null,

    /**
     * Whether keyboard is currently visible
     */
    val isKeyboardVisible: Boolean = false,

    /**
     * Error message if state is Error
     */
    val errorMessage: String? = null
) {
    /**
     * Check if ready to render content
     */
    val isReadyToRender: Boolean
        get() = state != State.Loading

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }
}

/**
 * Predefined BottomSheet configurations for common use cases
 */
object BottomSheetDefaults {

    /**
     * Selection list configuration (for pickers/selectors)
     */
    fun selectionList(
        title: String,
        maxItems: Int = 6
    ): BottomSheetConfiguration {
        val itemHeight = 54.dp
        val calculatedHeight = (maxItems * itemHeight.value).dp
        return BottomSheetConfiguration(
            title = title,
            heightConfig = BottomSheetHeightConfig.Static(calculatedHeight),
            isDismissible = true,
            showHeaderContentSeparator = true
        )
    }

    /**
     * Date picker configuration
     */
    fun datePicker(title: String = "Select Date"): BottomSheetConfiguration {
        return BottomSheetConfiguration(
            title = title,
            heightConfig = BottomSheetHeightConfig.Dynamic,
            isDismissible = true,
            showHeaderContentSeparator = false
        )
    }

    /**
     * Form configuration (for input forms)
     */
    fun form(title: String): BottomSheetConfiguration {
        return BottomSheetConfiguration(
            title = title,
            heightConfig = BottomSheetHeightConfig.BelowStatusBar,
            isDismissible = false, // Prevent accidental dismissal
            showHeaderContentSeparator = true
        )
    }
    /**
     * Info/detail configuration (for displaying information)
     */
    fun info(title: String): BottomSheetConfiguration {
        return BottomSheetConfiguration(
            title = title,
            heightConfig = BottomSheetHeightConfig.Dynamic,
            isDismissible = true,
            showHeaderContentSeparator = true
        )
    }

    /**
     * Full screen configuration
     */
    fun fullScreen(title: String): BottomSheetConfiguration {
        return BottomSheetConfiguration(
            title = title,
            heightConfig = BottomSheetHeightConfig.BelowStatusBar,
            isDismissible = true,
            showHeaderContentSeparator = true,
            style = BottomSheetStyle(isDraggable = false)
        )
    }
}
