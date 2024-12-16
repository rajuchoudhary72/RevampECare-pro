package com.app.ecarepro.ui.firebaseAnalytics

class AnalyticsManager private constructor() {

    private val providers: MutableList<AnalyticsProvider> = mutableListOf()

    companion object {
        val shared: AnalyticsManager by lazy { AnalyticsManager() }
    }

    fun initialize(providers: List<AnalyticsProvider>) {
        this.providers.clear()
        this.providers.addAll(providers)

        for (provider in providers) {
            provider.initialize()
        }
    }

    fun trackScreen(screenName: String) {
        for (provider in providers) {
            provider.trackScreen(screenName)
        }
    }

    fun trackEvent(event: AnalyticsEvent, parameters: Map<AnalyticsParameters, Any>) {
        for (provider in providers) {
            if (provider.shouldTrackEvent(event)) {
                provider.trackEvent(event, parameters)
            }
        }
    }
}
