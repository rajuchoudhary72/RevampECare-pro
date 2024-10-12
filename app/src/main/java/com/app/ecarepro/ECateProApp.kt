package com.app.ecarepro

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Secure
import androidx.appcompat.app.AppCompatDelegate
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.repository.AppRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject


@HiltAndroidApp
class ECateProApp : Application(),Application.ActivityLifecycleCallbacks  {

    private var currentActivity: WeakReference<Activity>? = null

    @Inject
    lateinit var appRepository: AppRepository

    override fun onCreate() {
        super.onCreate()
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseApp.initializeApp(this)
       // registerToken()


        // Register the activity lifecycle callbacks
        registerActivityLifecycleCallbacks(this)
    }

    fun getCurrentActivity(): Activity? {
        return currentActivity?.get()
    }

    fun getContext(): Context {
        return applicationContext
    }

    companion object {
        var instance: ECateProApp? = null
            private set
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
     }

    override fun onActivityStarted(p0: Activity) {
     }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = WeakReference(activity)
    }

    override fun onActivityPaused(p0: Activity) {
     }

    override fun onActivityStopped(p0: Activity) {
     }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
     }

    override fun onActivityDestroyed(p0: Activity) {
     }

}