package com.app.ecarepro.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SchoolTransport(
    val driverMob: String?,
    val driverName: String?,
    val routeInchargeMobile: String?,
    val routeInchargeName: String?,
    val routeNo: String?,
    val stopName: String?,
    val vehicleName: String?,
    val vehicleNumber: String?,
    val vehicleType: String?
):Parcelable