package com.app.ecarepro.ui.firebaseAnalytics.providers

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.app.ecarepro.BuildConfig
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAnalyticsService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataStore: UserDataStore
) : AnalyticsProvider {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    private val BLOCKED_TRACKED_EVENTS = mutableMapOf<String, String>()

    private var commonEventAttributes: Map<String, String> = emptyMap()

    override fun shouldTrackEvent(event: String): Boolean {
        return BLOCKED_TRACKED_EVENTS.contains(event).not()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun initialize() {
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        GlobalScope.launch {
            setUserProperties()
        }
    }

    override suspend fun setUserProperties() {
        userDataStore.getUser()?.let { user ->
            firebaseAnalytics.setUserId(user.userId.toString())
            firebaseAnalytics.setUserProperty(
                AnalyticsConstants.Attributes.USER_TYPE,
                user.userType.toString()
            )
        }
        userDataStore.getSchoolData()?.let { schoolData ->
            firebaseAnalytics.setUserProperty(
                AnalyticsConstants.Attributes.SCHOOL_CODE,
                schoolData.schoolCode
            )
        }
        firebaseAnalytics.setUserProperty(
            AnalyticsConstants.Attributes.APP_VERSION,
            BuildConfig.VERSION_NAME
        )
        firebaseAnalytics.setUserProperty(AnalyticsConstants.Attributes.DEVICE_MODEL, Build.MODEL)
        firebaseAnalytics.setUserProperty(
            AnalyticsConstants.Attributes.OS_VERSION,
            "${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})"
        )

        if (userDataStore.isUserAuthenticated()) {
            commonEventAttributes = mapOf(
                AnalyticsConstants.Attributes.USER_ID to userDataStore.getUser()?.userId.toString(),
                AnalyticsConstants.Attributes.USER_TYPE to userDataStore.getUser()?.userType.toString(),
                AnalyticsConstants.Attributes.SCHOOL_CODE to userDataStore.getSchoolData()?.schoolCode.toString()
            )
        }
    }

    override fun trackEvent(event: String, parameters: Map<String, Any>) {
        val attributes = mutableMapOf<String, Any>()
        attributes.putAll(parameters)
        attributes.putAll(commonEventAttributes)
        val bundle = Bundle().apply {
            attributes.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Double -> putDouble(key, value)
                    else -> Log.w(
                        TAG,
                        "Unsupported parameter type for key ${key}: ${value::class.java.simpleName}"
                    )
                }
            }
        }
        firebaseAnalytics.logEvent(event, bundle)
        Log.d(TAG, "Event: $event, Parameters: $parameters")
    }

    override fun trackScreen(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        })
        Log.d(TAG, "Screen: $screenName")
    }

    companion object {
        private const val TAG = "GoogleAnalyticsService"
    }
}
