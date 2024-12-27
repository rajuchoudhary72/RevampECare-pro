package com.app.ecarepro.ui.firebaseAnalytics.providers

interface AnalyticsProvider {
    suspend fun setUserProperties()
    fun shouldTrackEvent(event: String): Boolean
    fun trackEvent(event: String, parameters: Map<String, Any>)
    fun trackScreen(screenName: String)
    fun initialize()
}