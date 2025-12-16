package com.app.ecarepro.feature.navigation

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * NavigationManager - Singleton for centralized navigation management
 *
 * Based on iOS NavigationManager, this handles:
 * - Tab-based navigation
 * - Push navigation (via Router)
 * - Modal presentations (sheet/fullscreen)
 * - Bottom sheet presentations
 * - Keyboard visibility state
 *
 * Example usage:
 * ```kotlin
 * // Push navigation
 * NavigationManager.navigateToRoute(RouteType.Assignment)
 *
 * // Modal presentation
 * NavigationManager.navigateToRoute(
 *     RouteType.AddAssignment(null),
 *     NavigationType.Present.Sheet
 * )
 *
 * // Bottom sheet
 * NavigationManager.presentBottomSheet(
 *     content = { MyBottomSheetContent() },
 *     title = "Select Item"
 * )
 * ```
 */
object NavigationManager {

    // MARK: - Published Properties (StateFlow)

    private val _isKeyboardVisible = MutableStateFlow(false)
    val isKeyboardVisible: StateFlow<Boolean> = _isKeyboardVisible.asStateFlow()

    private val _shouldNavigateToDestination = MutableStateFlow(false)
    val shouldNavigateToDestination: StateFlow<Boolean> = _shouldNavigateToDestination.asStateFlow()

    private val _shouldPresentDestinationOnFullScreen = MutableStateFlow(false)
    val shouldPresentDestinationOnFullScreen: StateFlow<Boolean> = _shouldPresentDestinationOnFullScreen.asStateFlow()

    private val _shouldPresentDestinationOnSheet = MutableStateFlow(false)
    val shouldPresentDestinationOnSheet: StateFlow<Boolean> = _shouldPresentDestinationOnSheet.asStateFlow()

    // MARK: - Bottom Sheet State

    private val _isBottomSheetPresented = MutableStateFlow(false)
    val isBottomSheetPresented: StateFlow<Boolean> = _isBottomSheetPresented.asStateFlow()

    private val _shouldDismissBottomSheet = MutableStateFlow(false)
    val shouldDismissBottomSheet: StateFlow<Boolean> = _shouldDismissBottomSheet.asStateFlow()

    private val _bottomSheetContent = MutableStateFlow<(@Composable () -> Unit)?>(null)
    val bottomSheetContent: StateFlow<(@Composable () -> Unit)?> = _bottomSheetContent.asStateFlow()

    private val _bottomSheetConfig = MutableStateFlow(BottomSheetConfiguration())
    val bottomSheetConfig: StateFlow<BottomSheetConfiguration> = _bottomSheetConfig.asStateFlow()

    // MARK: - Tab State

    private val _selectedTab = MutableStateFlow(TabbarItem.HOME)
    val selectedTab: StateFlow<TabbarItem> = _selectedTab.asStateFlow()

    // MARK: - Private Properties

    private val tabRouters = mutableMapOf<TabbarItem, Router>()
    private var isInPresentationMode = false
    private var previousRouter: Router? = null
    private var _router: Router? = null

    // MARK: - Public Properties

    val router: Router?
        get() = _router ?: tabRouters[_selectedTab.value]

    var routeType: RouteType? = null
        private set

    // MARK: - Navigation Methods

    /**
     * Main navigation method - handles push, sheet, and fullscreen navigation
     *
     * @param route The destination route
     * @param navigationType Type of navigation (push, sheet, fullscreen)
     */
    fun navigateToRoute(route: RouteType, navigationType: NavigationType = NavigationType.Push) {
        routeType = route

        when (navigationType) {
            is NavigationType.Push -> {
                router?.navigateTo(route)
            }
            is NavigationType.Present -> {
                when (navigationType) {
                    is NavigationType.Present.Sheet -> {
                        _shouldPresentDestinationOnSheet.value = true
                    }
                    is NavigationType.Present.FullScreen -> {
                        _shouldPresentDestinationOnFullScreen.value = true
                    }
                }
            }
        }
    }

    /**
     * Navigate back in the current router's stack
     */
    fun navigateBack() {
        router?.navigateBack()
    }

    /**
     * Pop to root of current navigation stack
     */
    fun popToRoot() {
        router?.popToRoot()
    }

    /**
     * Switch to a different tab
     *
     * @param tab The tab to switch to
     */
    fun switchToTab(tab: TabbarItem) {
        if (!isInPresentationMode) {
            _selectedTab.value = tab
            _router = tabRouters[tab]
        }
    }

    /**
     * Register a router for a specific tab
     *
     * @param router The router instance
     * @param tab The tab it belongs to
     */
    fun registerRouter(router: Router, tab: TabbarItem) {
        tabRouters[tab] = router
        if (_selectedTab.value == tab && !isInPresentationMode) {
            _router = router
        }
    }

    /**
     * Temporarily set router for modal presentations
     *
     * @param router The presentation router
     */
    fun temporarilySetPresentationRouter(router: Router) {
        previousRouter = _router
        _router = router
        isInPresentationMode = true
    }

    /**
     * Restore previous router after modal dismissal
     */
    fun restoreRouterAfterPresentation() {
        _router = previousRouter
        previousRouter = null
        isInPresentationMode = false
    }

    /**
     * Called when a route is dismissed (modal or sheet)
     */
    fun routeDismissed() {
        _shouldPresentDestinationOnSheet.value = false
        _shouldPresentDestinationOnFullScreen.value = false
        routeType = null
    }

    // MARK: - Bottom Sheet Methods

    /**
     * Present a bottom sheet with custom content
     *
     * @param content The composable content to display
     * @param title Optional title for the header
     * @param icon Optional icon for the header
     * @param heightConfig Height configuration for the sheet
     * @param isDismissible Whether user can dismiss by tapping outside or swiping
     * @param showHeaderContentSeparator Show divider below header
     * @param style Visual styling for the sheet
     * @param dismissAction Callback invoked when dismissed
     */
    fun presentBottomSheet(
        content: @Composable () -> Unit,
        title: String? = null,
        icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
        heightConfig: BottomSheetHeightConfig = BottomSheetHeightConfig.Dynamic,
        isDismissible: Boolean = true,
        showHeaderContentSeparator: Boolean = true,
        style: BottomSheetStyle = BottomSheetStyle(),
        dismissAction: (() -> Unit)? = null
    ) {
        _bottomSheetContent.value = content
        _bottomSheetConfig.value = BottomSheetConfiguration(
            title = title,
            icon = icon,
            heightConfig = heightConfig,
            isDismissible = isDismissible,
            showHeaderContentSeparator = showHeaderContentSeparator,
            style = style,
            dismissAction = dismissAction
        )
        _isBottomSheetPresented.value = true
        _shouldDismissBottomSheet.value = false
    }

    /**
     * Trigger animated dismissal of the bottom sheet
     */
    fun dismissBottomSheet() {
        _shouldDismissBottomSheet.value = true
    }

    /**
     * Complete dismissal of bottom sheet (called after animation)
     */
    fun bottomSheetDismissCompleted() {
        _isBottomSheetPresented.value = false
        _shouldDismissBottomSheet.value = false
        _bottomSheetContent.value = null
        _bottomSheetConfig.value = BottomSheetConfiguration()
    }

    /**
     * Update keyboard visibility state
     *
     * @param isVisible Whether keyboard is visible
     */
    fun setKeyboardVisible(isVisible: Boolean) {
        _isKeyboardVisible.value = isVisible
    }

    /**
     * Clear all navigation state (useful on logout)
     */
    fun clearAllState() {
        tabRouters.values.forEach { it.popToRoot() }
        _router = null
        previousRouter = null
        isInPresentationMode = false
        routeType = null
        _isBottomSheetPresented.value = false
        _bottomSheetContent.value = null
        _selectedTab.value = TabbarItem.HOME
    }
}
