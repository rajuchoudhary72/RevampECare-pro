package com.app.ecarepro.ui.firebaseAnalytics

class GoogleAnalyticsService : AnalyticsProvider {

    private var analytics: String = ""

    private val events: List<AnalyticsEvent> = listOf(
        AnalyticsEvent.LOGIN
    )

    override fun shouldTrackEvent(event: AnalyticsEvent): Boolean {
        return events.contains(event)
    }

    override fun initialize() {
        analytics = "123"
    }

    override fun trackEvent(event: AnalyticsEvent, parameters: Map<AnalyticsParameters, Any>) {
        // Track the event name here with Firebase
    }

    override fun trackScreen(screenName: String) {
        // Track the screen name here with Firebase
    }
}
