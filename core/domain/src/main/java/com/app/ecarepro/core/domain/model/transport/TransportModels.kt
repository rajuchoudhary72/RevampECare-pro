package com.app.ecarepro.core.domain.model.transport

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val routeID: Int,
    val routeName: String,
)

@Serializable
data class Stop(
    val stopID: Int,
    val stopName: String,
    val students: List<TransportStudent> = emptyList(),
    val checked: Boolean = false,
)

@Serializable
data class TransportStudent(
    val admissionNo: String,
    val className: String,
    val dropAtt: String,
    val dropStatus: Int,
    val dropTime: String,
    val isConstant: Boolean,
    val isDropped: Boolean,
    val photo: String,
    val pickupAtt: String,
    val pickupStatus: Int,
    val pickupTime: String,
    val rollNo: String,
    val route: String?,
    val stID: Int,
    val stName: String,
    val stop: String,
    val stopID: Int,
    val isSelected: Boolean = false,
)

data class StudentAttendanceRequest(
    val stID: Int,
    val status: Int,
    val stopID: Int,
)

data class MarkTransportAttendanceResult(
    val students: List<TransportStudent>,
    val freezDrop: Boolean,
    val freezPickup: Boolean,
)

object TransportConstants {
    const val PRESENT = 1
    const val ABSENT = 0
    const val OP = 2
    const val DROP_CONFORM = 5

    const val UP_TRIP = 1
    const val DOWN_TRIP = 2
    const val DROP_STUDENT_TRIP = 3
}
