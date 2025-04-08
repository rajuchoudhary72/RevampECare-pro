package com.app.ecarepro

import android.app.Activity
import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.language.LanguageManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference
import javax.inject.Inject


@HiltAndroidApp
class ECateProApp : Application(),Application.ActivityLifecycleCallbacks  {

    private var currentActivity: WeakReference<Activity>? = null

    @Inject
    lateinit var appRepository: AppRepository


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.setLocale(newBase))
    }

    override fun onCreate() {
        super.onCreate()
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        /*App level  Firebase  setup*/
        FirebaseApp.initializeApp(this)
        // Set the custom crash handler
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
        // Register the activity lifecycle callbacks
        registerActivityLifecycleCallbacks(this)

        // Apply saved language on app startup
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