package com.app.ecarepro

import android.app.Activity
import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.data.repository.TranslationRepository
import com.app.ecarepro.ui.language.LanguageManager
import com.app.ecarepro.ui.language.LocalizationManager
import com.app.ecarepro.ui.MainActivity
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject


@HiltAndroidApp
class ECateProApp : Application(),Application.ActivityLifecycleCallbacks  {

    private var currentActivity: WeakReference<Activity>? = null

    @Inject
    lateinit var appRepository: AppRepository

//    @Inject
//    lateinit var translationRepository: TranslationRepository



    override fun onCreate() {
        super.onCreate()


//        // Initialize localization manager
//        LocalizationManager.initialize(translationRepository)
//
//        // Load translations when app starts
//        CoroutineScope(Dispatchers.IO).launch {
//            val spreadsheetId = "1cMpRUMhNa7ecB_ijAYyc3bbuFUqQuUrZhEB17AUvj8U"
//            val range = "Sheet1!A:C" // Use your actual sheet name
//            val apiKey = "AIzaSyAdGd2nT10vrag4zManlLI1PbZ1D4rBkBA"
//
//            LocalizationManager.loadTranslations(spreadsheetId, range, apiKey)
//                .onSuccess {
//                    // Notify all activities to refresh
//                    LocalizationManager.notifyTranslationsChanged()
//                }
//            Log.e("TranslationRepo", "Fetching data from google sheet")
//        }
//

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

//    override fun attachBaseContext(base: Context) {
//        // Add this method to wrap the application context
//        super.attachBaseContext(LocalizedContext(base))
//    }

    fun callMainActivityFunction() {

        MainActivity().extracted()
    }

}