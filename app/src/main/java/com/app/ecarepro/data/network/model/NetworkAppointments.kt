package com.app.ecarepro.data.network.model

import com.app.ecarepro.model.Appointment

data class NetworkAppointments(
    val appointments: List<Appointment>,
    val errorCode: Int,
    val message: String,
    val pending: Int,
    val status: String,
    val todaysAppointment: Int
)