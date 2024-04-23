package com.app.ecarepro.model

import com.app.ecarepro.model.SchoolTransport

data class TransDetails(
    val driverAadharNumber: Any,
    val driverAdd: Any,
    val driverDrivingLNo: Any,
    val driverMob: Any,
    val driverName: String,
    val driverVoterIDNo: Any,
    val key: Any,
    val routeNumber: String,
    val schCode: Any,
    val schoolTransport: SchoolTransport,
    val stID: Int,
    val stopName: Any,
    val transportType: String,
    val transporterAadharNumber: Any,
    val transporterAdd: Any,
    val transporterDrivingLNo: Any,
    val transporterMob: Any,
    val transporterName: Any,
    val transporterVoterIDNo: Any,
    val vehicleNumber: String,
    val vehicleType: String,
    val vehicleTypeID: Int
)