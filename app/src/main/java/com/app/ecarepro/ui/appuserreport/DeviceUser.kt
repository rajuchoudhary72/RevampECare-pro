package com.app.ecarepro.ui.appuserreport

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DeviceUser(
    val android: Int,
    val both: Int,
    val iOS: Int,
    val remaining: Int,
    val totalUser: Int,
    val userType: Int
):Parcelable