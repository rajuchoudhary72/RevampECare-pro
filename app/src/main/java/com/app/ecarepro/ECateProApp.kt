package com.app.ecarepro

import android.app.Application
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatDelegate
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.repository.AppRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltAndroidApp
class ECateProApp : Application() {

    @Inject
    lateinit var appRepository: AppRepository

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        FirebaseApp.initializeApp(this)
        registerToken()
    }

    private fun registerToken() {
        GlobalScope.launch {
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val wInfo = wifiManager.connectionInfo
            val macAddress = wInfo.macAddress
            appRepository
                .registerDevice(
                    RegisterDevice(
                        fcmToken = Firebase.messaging.token.await(),
                        osVersion = "OS " + Build.VERSION.SDK_INT,
                        deviceModel = Build.MANUFACTURER + " " + Build.MODEL,
                        deviceType = 1,
                        imeI1 = macAddress,
                        imeI2 = macAddress,
                        deviceID = Secure.getString(contentResolver, Secure.ANDROID_ID)
                    )
                )
                .collectLatest {
                    println(it)
                }
        }
    }
}