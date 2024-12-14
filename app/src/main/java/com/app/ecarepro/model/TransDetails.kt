package com.app.ecarepro.model

import android.os.Parcelable
import com.app.ecarepro.model.SchoolTransport
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransDetails(
    val driverAadharNumber: String,
    val driverAdd: String,
    val driverDrivingLNo: String,
    val driverMob: String,
    val driverName: String,
    val driverVoterIDNo: String,
    val key: String,
    val routeNumber: String,
    val schCode: String,
    val schoolTransport: SchoolTransport,
    val stID: Int,
    val stopName: String,
    val transportType: String,
    val transporterAadharNumber: String,
    val transporterAdd: String,
    val transporterDrivingLNo: String,
    val transporterMob: String,
    val transporterName: String,
    val transporterVoterIDNo: String,
    val vehicleNumber: String,
    val vehicleType: String,
    val vehicleTypeID: Int
): Parcelable