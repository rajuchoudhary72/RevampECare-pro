package com.app.ecarepro.feature.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * TabbarItem - Defines all tabs in the main tab bar
 *
 * Based on iOS TabbarItem enum, represents all available tabs
 * in the main navigation. Each tab has its own Router for
 * independent navigation stacks.
 *
 * Example usage:
 * ```kotlin
 * // Switch to a tab
 * NavigationManager.switchToTab(TabbarItem.HOME)
 *
 * // Get current tab
 * val currentTab = NavigationManager.selectedTab.collectAsState().value
 *
 * // Iterate over visible tabs
 * TabbarItem.visibleTabs.forEach { tab ->
 *     BottomNavigationItem(
 *         icon = tab.icon,
 *         label = tab.title,
 *         selected = currentTab == tab,
 *         onClick = { NavigationManager.switchToTab(tab) }
 *     )
 * }
 * ```
 */
enum class TabbarItem(
    /**
     * Display title for the tab
     */
    val title: String,

    /**
     * Icon for the tab
     */
    val icon: ImageVector,

    /**
     * Whether this tab is shown in the bottom navigation
     */
    val isVisible: Boolean = true
) {

    /**
     * Menu/Drawer tab (typically not shown in bottom nav)
     */
    MENU(
        title = "Menu",
        icon = Icons.Default.Menu,
        isVisible = false
    ),

    /**
     * Home tab - main dashboard/landing screen
     */
    HOME(
        title = "Home",
        icon = Icons.Default.Home
    ),

    /**
     * Profile tab - user profile and settings
     */
    PROFILE(
        title = "Profile",
        icon = Icons.Default.Person
    ),

    /**
     * Notification tab - notifications and alerts
     */
    NOTIFICATION(
        title = "Notifications",
        icon = Icons.Default.Notifications
    ),

    /**
     * Message tab - messaging and communication
     */
    MESSAGE(
        title = "Messages",
        icon = Icons.Default.Email
    );

    /**
     * Get the default/root screen content for this tab
     *
     * Override this method or use a when expression to return
     * the appropriate root composable for each tab.
     */
    @Composable
    fun Content() {
        // This should be implemented in your app module
        // where you have access to all screen composables
        // Example:
        // when (this) {
        //     HOME -> HomeScreen()
        //     PROFILE -> ProfileScreen()
        //     NOTIFICATION -> NotificationListScreen()
        //     MESSAGE -> MessageListScreen()
        //     MENU -> { /* Hidden */ }
        // }
    }

    companion object {
        /**
         * Get all visible tabs (excludes MENU)
         */
        val visibleTabs: List<TabbarItem>
            get() = values().filter { it.isVisible }

        /**
         * Get all tabs excluding menu
         */
        val excludingMenu: List<TabbarItem>
            get() = values().filter { it != MENU }

        /**
         * Get tab by ordinal index
         */
        fun fromOrdinal(ordinal: Int): TabbarItem? {
            return values().getOrNull(ordinal)
        }

        /**
         * Default tab (used on app launch)
         */
        val default: TabbarItem = HOME
    }

    /**
     * Get the tab index in visible tabs list
     */
    fun getVisibleIndex(): Int {
        return visibleTabs.indexOf(this)
    }

    /**
     * Check if this is the default tab
     */
    fun isDefault(): Boolean {
        return this == default
    }
}

/**
 * TabNavigationState - Holds state for tab navigation
 *
 * Use this to manage tab-specific navigation state if needed.
 */
data class TabNavigationState(
    val currentTab: TabbarItem = TabbarItem.default,
    val previousTab: TabbarItem? = null,
    val tabHistory: List<TabbarItem> = listOf(TabbarItem.default)
) {
    /**
     * Switch to a new tab
     */
    fun switchTo(newTab: TabbarItem): TabNavigationState {
        return if (newTab != currentTab) {
            copy(
                currentTab = newTab,
                previousTab = currentTab,
                tabHistory = tabHistory + newTab
            )
        } else {
            this
        }
    }

    /**
     * Go to previous tab if available
     */
    fun goToPrevious(): TabNavigationState {
        return previousTab?.let {
            switchTo(it)
        } ?: this
    }

    /**
     * Reset to default tab
     */
    fun reset(): TabNavigationState {
        return TabNavigationState()
    }
}
