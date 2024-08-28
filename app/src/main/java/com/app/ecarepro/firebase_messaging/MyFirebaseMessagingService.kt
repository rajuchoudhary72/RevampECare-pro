package com.app.ecarepro.firebase_messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.MainActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var appRepository: AppRepository
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")
        Log.v("MyFirebaseMessagingService","message received ---> ${remoteMessage.data} notif--> ${remoteMessage.notification}")
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: " + remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            sendNotification(it.body,it.title)
        }
        Firebase.messaging.token
        if (null != remoteMessage) {
            //      var modelNotificationBody: ModelNotificationBody
            val intent = Intent(
                applicationContext,
                MainActivity::class.java
            )
            var dataMap: Map<String?, String?>
            val title = ""
            val body = ""
        }
    }
    private fun sendNotification(messageBody: String?, title: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //End
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val channelId = "99999"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
            notificationBuilder.setColor(resources.getColor(R.color.md_theme_light_primary))
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = "Default channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(100, notificationBuilder.build())
    }
    override fun onNewToken(token: String) {
        // Handle new or refreshed FCM registration token
        Log.d(TAG, "Refreshed token: $token")
        // Send token to your server or save it locally
        registerToken(token)
        // You may want to send this token to your server for further use
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
                        imeI1 = macAddress,
                        imeI2 = macAddress,
                        deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                    )
                )
                .collectLatest {
                    println(it)
                }
        }
    }
    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}