package com.app.ecarepro.data.network.model

data class BusData(
    val date_time: String,
    val device_id: Any,
    val ignition_status: Any,
    val latitude: String?,
    val location_description: String,
    val longitude: String?,
    val odometer_reading: String,
    val speed: String,
    val vehicle_no: String
)