package com.app.ecarepro.ui.firebaseAnalytics

import android.util.Log
import com.app.ecarepro.ui.firebaseAnalytics.providers.AnalyticsProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

interface AnalyticsManager {
    fun trackScreen(screenName: String)
    fun trackEvent(event: String, parameters: Map<String, Any>)
    fun setUserProperties()
}

class AppAnalyticsManager : AnalyticsManager {

    private var providers: AtomicReference<Set<AnalyticsProvider>> = AtomicReference(emptySet())


    fun initialize(vararg providers: AnalyticsProvider) {
        this.providers.set(providers.toSet())
        this@AppAnalyticsManager.providers.get().forEach { it.initialize() }
    }

    override fun trackScreen(screenName: String) {
        if (providers.get().isEmpty()) {
            // Log a warning or handle uninitialized state
            Log.w(TAG, "Analytics providers not initialized")
            return
        }
        providers.get().forEach { it.trackScreen(screenName) }
    }

    override fun trackEvent(event: String, parameters: Map<String, Any>) {
        if (providers.get().isEmpty()) {
            // Log a warning or handle uninitialized state
            Log.w(TAG, "Analytics providers not initialized")
            return
        }
        providers.get()
            .filter { it.shouldTrackEvent(event) }
            .forEach { it.trackEvent(event, parameters) }
    }

    override fun setUserProperties() {
        if (providers.get().isEmpty()) {
            Log.w(TAG, "Analytics providers not initialized")
        }
        GlobalScope.launch {
            providers.get().forEach {
                it.setUserProperties()
            }
        }
    }

    companion object {
        private const val TAG = "AnalyticsManager"
    }
}