package com.app.ecarepro.ui.edit_profile.model.update_profile

data class UpdateTransportProfileModel(
    var transportTypeID: Int,
    var vehicleTypeID: Int,
    var vehicleNumber: String,
    var driverName: String,
    var driverMob: String,
    var driverAdd: String,
    var driverAadharNumber: String,
    var driverDrivingLNo: String,
    var driverClearanceNo: String,
    var vehicleUsingFrom: String,
    var isLadyGuardAvailabile: Boolean
)