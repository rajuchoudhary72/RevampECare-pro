package com.app.ecarepro.firebase_messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.media.RingtoneManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.ecarepro.R
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.utils.Constant.Companion.boldFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.boldFindStartIndexes
import com.app.ecarepro.utils.Constant.Companion.italicFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.italicFindStartIndexes
import com.app.ecarepro.utils.Constant.Companion.strikethroughFindEndStarIndexes
import com.app.ecarepro.utils.Constant.Companion.strikethroughFindStartIndexes
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

        val channelId = "e-Care"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_luncher)
            .setContentTitle(title)
            .setContentText(getFormatedString(messageBody))
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_luncher)
            notificationBuilder.setColor(resources.getColor(R.color.md_theme_light_primary))
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = "Channel human readable title"
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

    fun getFormatedString(data: String?): SpannableStringBuilder? {
        val ssb = SpannableStringBuilder(data)
        try {
            val boldStartIndexes: List<Int>? = data?.let { boldFindStartIndexes(it) }
            val boldEndIndexes: List<Int>? = data?.let { boldFindEndStarIndexes(it) }
            val italicStartIndexes: List<Int>? = data?.let { italicFindStartIndexes(it) }
            val italicEndIndexes: List<Int>? = data?.let { italicFindEndStarIndexes(it) }
            val strikethroughStartIndexes: List<Int>? = data?.let { strikethroughFindStartIndexes(it) }
            val strikethroughEndIndexes: List<Int>? = data?.let { strikethroughFindEndStarIndexes(it) }
            var cs: CharacterStyle
            var deleteIndesx = 0
            var boldstart = 0
            var boldend = 0
            var len = 0
            if (boldEndIndexes != null) {
                if (boldStartIndexes?.size!! >= 1 && boldEndIndexes.size >= 1) {
                    for (i in boldStartIndexes.indices) {
                        boldstart = boldStartIndexes[i]
                        if (boldEndIndexes != null) {
                            for (j in i until boldEndIndexes.size) {
                                boldend = boldEndIndexes[j]
                                cs = StyleSpan(Typeface.BOLD)
                                len = ssb.length
                                if (boldstart == 0) {
                                    ssb.setSpan(cs, boldstart, boldend, 1)
                                    ssb.delete(boldstart, boldstart + 1)
                                    ssb.delete(boldend - 1, boldend)
                                } else {
                                    ssb.setSpan(cs, boldstart - deleteIndesx, boldend - deleteIndesx, 1)
                                    ssb.delete(boldstart - deleteIndesx, boldstart - deleteIndesx + 1)
                                    ssb.delete(boldend - deleteIndesx - 1, boldend - deleteIndesx)
                                }
                                deleteIndesx = deleteIndesx + 2
                                len = 0
                                break
                            }
                        }
                    }
                }
            }
            var italicstart = 0
            var italicdend = 0
            if (italicStartIndexes?.size!! >= 1 && italicEndIndexes?.size!! >= 1) {
                for (i in italicStartIndexes.indices) {
                    italicstart = italicStartIndexes[i]
                    for (j in i until italicEndIndexes?.size!!) {
                        italicdend = italicEndIndexes[j]
                        cs = StyleSpan(Typeface.ITALIC)
                        if (italicstart == 0) {
                            ssb.setSpan(cs, italicstart, italicdend, 1)
                            ssb.delete(italicstart, italicstart + 1)
                            ssb.delete(italicdend - 1, italicdend)
                        } else {
                            ssb.setSpan(
                                cs,
                                italicstart - deleteIndesx,
                                italicdend - deleteIndesx,
                                1
                            )
                            ssb.delete(italicstart - deleteIndesx, italicstart - deleteIndesx + 1)
                            ssb.delete(italicdend - deleteIndesx - 1, italicdend - deleteIndesx)
                        }
                        deleteIndesx = deleteIndesx + 2
                        break
                    }
                }
            }
            var strikethroughstart = 0
            var strikethroughend = 0
            if (strikethroughStartIndexes?.size!! >= 1 && strikethroughEndIndexes?.size!! >= 1) {
                for (i in strikethroughStartIndexes.indices) {
                    strikethroughstart = strikethroughStartIndexes[i]
                    for (j in i until strikethroughEndIndexes.size) {
                        strikethroughend = strikethroughEndIndexes[j]
                        cs = UnderlineSpan()
                        if (strikethroughstart == 0) {
                            ssb.setSpan(cs, strikethroughstart, strikethroughend, 1)
                            ssb.delete(strikethroughstart, strikethroughstart + 1)
                            ssb.delete(strikethroughend - 1, strikethroughend)
                        } else {
                            ssb.setSpan(
                                cs,
                                strikethroughstart - deleteIndesx,
                                strikethroughend - deleteIndesx,
                                1
                            )
                            ssb.delete(
                                strikethroughstart - deleteIndesx,
                                strikethroughstart - deleteIndesx + 1
                            )
                            ssb.delete(
                                strikethroughend - deleteIndesx - 1,
                                strikethroughend - deleteIndesx
                            )
                        }
                        deleteIndesx = deleteIndesx + 2
                        break
                    }
                }
            }
        } catch (ignored: Exception) {
        }
        return ssb
    }



}