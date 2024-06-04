package com.app.ecarepro.firebase_messaging

import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.repository.AppRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@AndroidEntryPoint
class ECareProMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var appRepository: AppRepository

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Firebase.messaging.token
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registerToken(token)

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun registerToken(token: String) {
        GlobalScope.launch {
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val wInfo = wifiManager.connectionInfo
            val macAddress = wInfo.macAddress
            appRepository
                .registerDevice(
                    RegisterDevice(
                        fcmToken = token,
                        osVersion = "OS " + Build.VERSION.SDK_INT,
                        deviceModel = Build.MANUFACTURER + " " + Build.MODEL,
                        deviceType = 1,
                        imeI1 = (application
                            .getSystemService(TELEPHONY_SERVICE) as TelephonyManager).primaryImei,
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