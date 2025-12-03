package com.app.ecarepro.feature.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Router - Manages navigation stack for a specific context (tab or presentation)
 *
 * Each tab or modal presentation has its own Router instance that maintains
 * an independent navigation stack.
 *
 * Example usage:
 * ```kotlin
 * val router = Router(associatedTab = TabbarItem.HOME)
 *
 * // Navigate to a screen
 * router.navigateTo(RouteType.Assignment)
 *
 * // Navigate back
 * router.navigateBack()
 *
 * // Clear stack
 * router.popToRoot()
 * ```
 */
class Router(
    /**
     * The tab this router is associated with (null for presentation routers)
     */
    var associatedTab: TabbarItem? = null,

    /**
     * Whether this router is for a modal presentation
     */
    var isPresentationRouter: Boolean = false
) {

    // MARK: - Navigation Stack

    private val _navStack: SnapshotStateList<RouteType> = mutableStateListOf()

    /**
     * Current navigation stack (read-only)
     */
    val navStack: List<RouteType>
        get() = _navStack.toList()

    /**
     * Current route (top of stack)
     */
    val currentRoute: RouteType?
        get() = _navStack.lastOrNull()

    /**
     * Whether the stack is empty
     */
    val isEmpty: Boolean
        get() = _navStack.isEmpty()

    /**
     * Stack size
     */
    val stackSize: Int
        get() = _navStack.size

    // MARK: - Navigation Methods

    /**
     * Navigate to a new route (push onto stack)
     *
     * @param route The destination route
     */
    fun navigateTo(route: RouteType) {
        _navStack.add(route)
    }

    /**
     * Navigate back (pop from stack)
     *
     * @return true if navigation was successful, false if stack was empty
     */
    fun navigateBack(): Boolean {
        return if (_navStack.isNotEmpty()) {
            _navStack.removeAt(_navStack.lastIndex)
            true
        } else {
            false
        }
    }

    /**
     * Pop to root (clear entire stack)
     */
    fun popToRoot() {
        _navStack.clear()
    }

    /**
     * Pop to a specific route in the stack
     *
     * @param route The route to pop to
     * @return true if route was found and popped to, false otherwise
     */
    fun popTo(route: RouteType): Boolean {
        val index = _navStack.indexOf(route)
        return if (index >= 0) {
            while (_navStack.size > index + 1) {
                _navStack.removeAt(_navStack.lastIndex)
            }
            true
        } else {
            false
        }
    }

    /**
     * Pop until a condition is met
     *
     * @param predicate The condition to check
     * @return true if a matching route was found, false otherwise
     */
    fun popUntil(predicate: (RouteType) -> Boolean): Boolean {
        for (i in _navStack.lastIndex downTo 0) {
            if (predicate(_navStack[i])) {
                while (_navStack.lastIndex > i) {
                    _navStack.removeAt(_navStack.lastIndex)
                }
                return true
            }
        }
        return false
    }

    /**
     * Replace current route with a new one
     *
     * @param route The new route
     */
    fun replace(route: RouteType) {
        if (_navStack.isNotEmpty()) {
            _navStack[_navStack.lastIndex] = route
        } else {
            _navStack.add(route)
        }
    }

    /**
     * Check if a route exists in the stack
     *
     * @param route The route to check
     * @return true if route exists in stack
     */
    fun contains(route: RouteType): Boolean {
        return _navStack.contains(route)
    }

    /**
     * Get the previous route (one before current)
     */
    fun getPreviousRoute(): RouteType? {
        return if (_navStack.size >= 2) {
            _navStack[_navStack.lastIndex - 1]
        } else {
            null
        }
    }

    override fun toString(): String {
        return "Router(tab=$associatedTab, isPresentation=$isPresentationRouter, stackSize=$stackSize)"
    }
}
