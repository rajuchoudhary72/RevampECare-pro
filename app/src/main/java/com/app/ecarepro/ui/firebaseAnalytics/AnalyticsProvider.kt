package com.app.ecarepro.ui.firebaseAnalytics

interface AnalyticsProvider {

    fun shouldTrackEvent(event: AnalyticsEvent): Boolean
    fun trackEvent(event: AnalyticsEvent, parameters: Map<AnalyticsParameters, Any>)
    fun trackScreen(screenName: String)
    fun initialize()
}