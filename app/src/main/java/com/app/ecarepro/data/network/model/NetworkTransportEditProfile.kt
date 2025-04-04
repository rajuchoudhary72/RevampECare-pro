package com.app.ecarepro.data.network.model



data class NetworkTransportEditProfile(
    val errorCode: Int,
    val message: String,
    val transVehicles: List<TransportVehicle>,
    val transDetails: TransportDetails?,
    val status: String,
    val transportType: Int,
)
data class TransportVehicle(
    val vehicleTypeID: Int,
    val vehicleType: String
)
data class TransportDetails(
    val transportType: String?,
    val transportTypeID: Int,
    val vehicleTypeID: Int,
    val vehicleNumber: String?,
    val vehicleType: String?, // Nullable because it's null in JSON
    val driverName: String?,
    val driverMob: String?,
    val driverAdd: String?,
    val driverAadharNumber: String?,
    val driverVoterIDNo: String?,
    val driverDrivingLNo: String?,
    val driverClearanceNo: String?,
    val isLadyGuardAvailabile: Boolean,
    val transporterName: String?,
    val transporterMob: String?,
    val transporterAdd: String?,
    val transporterAadharNumber: String?,
    val transporterVoterIDNo: String?,
    val transporterDrivingLNo: String?,
    val routeNumber: String?,
    val stopName: String?,
    val vehicleUsingFrom: String?,
    val schoolTransport: String?
)