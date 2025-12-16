package com.app.ecarepro.feature.navigation

/**
 * NavigationType - Defines how navigation should be performed
 *
 * Based on iOS NavigationType, supports:
 * - Push: Standard push navigation within current stack
 * - Present.Sheet: Modal bottom sheet presentation
 * - Present.FullScreen: Full screen modal presentation
 *
 * Example usage:
 * ```kotlin
 * // Push navigation
 * NavigationManager.navigateToRoute(
 *     RouteType.Assignment,
 *     NavigationType.Push
 * )
 *
 * // Sheet presentation
 * NavigationManager.navigateToRoute(
 *     RouteType.AddAssignment(null),
 *     NavigationType.Present.Sheet
 * )
 *
 * // Full screen modal
 * NavigationManager.navigateToRoute(
 *     RouteType.EditProfile,
 *     NavigationType.Present.FullScreen
 * )
 * ```
 */
sealed class NavigationType {

    /**
     * Push navigation - adds to current navigation stack
     */
    object Push : NavigationType()

    /**
     * Modal presentation - displays over current content
     */
    sealed class Present : NavigationType() {

        /**
         * Sheet presentation - modal bottom sheet (swipe to dismiss)
         * Maps to ModalBottomSheet in Jetpack Compose
         */
        object Sheet : Present()

        /**
         * Full screen presentation - modal covering entire screen
         * Maps to Dialog with full screen properties in Jetpack Compose
         */
        object FullScreen : Present()
    }

    /**
     * Check if this is a presentation style navigation
     */
    val isPresentation: Boolean
        get() = this is Present

    /**
     * Check if this is a push style navigation
     */
    val isPush: Boolean
        get() = this is Push

    /**
     * Check if this is a sheet presentation
     */
    val isSheet: Boolean
        get() = this is Present.Sheet

    /**
     * Check if this is a full screen presentation
     */
    val isFullScreen: Boolean
        get() = this is Present.FullScreen

    override fun toString(): String {
        return when (this) {
            is Push -> "Push"
            is Present.Sheet -> "Present.Sheet"
            is Present.FullScreen -> "Present.FullScreen"
        }
    }

    companion object {
        /**
         * Get default navigation type (Push)
         */
        val default: NavigationType = Push
    }
}
