package com.app.ecarepro.core.network.model.transport

import com.app.ecarepro.core.domain.model.transport.MarkTransportAttendanceResult
import com.app.ecarepro.core.domain.model.transport.Route
import com.app.ecarepro.core.domain.model.transport.Stop
import com.app.ecarepro.core.domain.model.transport.StudentAttendanceRequest
import com.app.ecarepro.core.domain.model.transport.TransportStudent
import com.app.ecarepro.core.network.model.NetworkResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Route List Response ──

@Serializable
data class NetworkRouteListResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("routeLST")
    val routeLST: List<NetworkRoute>? = null,
) : NetworkResponse

@Serializable
data class NetworkRoute(
    @SerialName("routeID")
    val routeID: Int,
    @SerialName("routeName")
    val routeName: String,
)

fun NetworkRoute.toDomainModel() = Route(
    routeID = routeID,
    routeName = routeName,
)

// ── Stoppage List Response ──

@Serializable
data class NetworkStoppageResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("stopLST")
    val stopLST: List<NetworkStop>? = null,
) : NetworkResponse

@Serializable
data class NetworkStop(
    @SerialName("stopID")
    val stopID: Int,
    @SerialName("stopName")
    val stopName: String,
    @SerialName("stuLst")
    val stuLst: List<NetworkTransportStudent>? = null,
)

fun NetworkStop.toDomainModel() = Stop(
    stopID = stopID,
    stopName = stopName,
    students = stuLst?.map { it.toDomainModel() } ?: emptyList(),
)

// ── Student to Mark Attendance Response ──

@Serializable
data class NetworkStudentToMarkTransAttResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("stuLst")
    val stuLst: List<NetworkTransportStudent>? = null,
    @SerialName("freezDrop")
    val freezDrop: Boolean = false,
    @SerialName("freezPickup")
    val freezPickup: Boolean = false,
) : NetworkResponse

fun NetworkStudentToMarkTransAttResponse.toDomainResult() = MarkTransportAttendanceResult(
    students = stuLst?.map { it.toDomainModel() } ?: emptyList(),
    freezDrop = freezDrop,
    freezPickup = freezPickup,
)

// Unwrapped result used by RemoteDataSource
data class NetworkStudentMarkAttResult(
    val students: List<NetworkTransportStudent>,
    val freezDrop: Boolean,
    val freezPickup: Boolean,
)

fun NetworkStudentMarkAttResult.toDomainResult() = MarkTransportAttendanceResult(
    students = students.map { it.toDomainModel() },
    freezDrop = freezDrop,
    freezPickup = freezPickup,
)

@Serializable
data class NetworkTransportStudent(
    @SerialName("admissionNo")
    val admissionNo: String = "",
    @SerialName("className")
    val className: String = "",
    @SerialName("dropAtt")
    val dropAtt: String? = null,
    @SerialName("dropStatus")
    val dropStatus: Int = 0,
    @SerialName("dropTime")
    val dropTime: String? = null,
    @SerialName("isConstant")
    val isConstant: Boolean = false,
    @SerialName("isdropped")
    val isdropped: Boolean = false,
    @SerialName("photo")
    val photo: String = "",
    @SerialName("pickupAtt")
    val pickupAtt: String? = null,
    @SerialName("pickupStatus")
    val pickupStatus: Int = 0,
    @SerialName("pickupTime")
    val pickupTime: String? = null,
    @SerialName("rollNo")
    val rollNo: String = "",
    @SerialName("route")
    val route: String? = null,
    @SerialName("stID")
    val stID: Int = 0,
    @SerialName("stName")
    val stName: String = "",
    @SerialName("stop")
    val stop: String? = null,
    @SerialName("stopID")
    val stopID: Int = 0,
)

fun NetworkTransportStudent.toDomainModel() = TransportStudent(
    admissionNo = admissionNo,
    className = className,
    dropAtt = dropAtt.orEmpty(),
    dropStatus = dropStatus,
    dropTime = dropTime.orEmpty(),
    isConstant = isConstant,
    isDropped = isdropped,
    photo = photo,
    pickupAtt = pickupAtt.orEmpty(),
    pickupStatus = pickupStatus,
    pickupTime = pickupTime.orEmpty(),
    rollNo = rollNo,
    route = route.orEmpty(),
    stID = stID,
    stName = stName,
    stop = stop.orEmpty(),
    stopID = stopID,
)

// ── Transport Attendance Report Response ──

@Serializable
data class NetworkTransAttReportResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("stopLST")
    val stopLST: List<NetworkStop>? = null,
) : NetworkResponse

// ── Out Pass Report Response ──

@Serializable
data class NetworkOutPassReportResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
    @SerialName("stuLst")
    val stuLst: List<NetworkTransportStudent>? = null,
) : NetworkResponse

// ── Post Attendance Request ──

@Serializable
data class NetworkPostTransAttRequest(
    @SerialName("attDate")
    val attDate: String,
    @SerialName("routeID")
    val routeID: Int,
    @SerialName("stopID")
    val stopID: Int,
    @SerialName("stuAtt")
    val stuAtt: List<NetworkStuAtt>,
    @SerialName("trip")
    val trip: Int,
)

@Serializable
data class NetworkStuAtt(
    @SerialName("stID")
    val stID: Int,
    @SerialName("status")
    val status: Int,
    @SerialName("stopID")
    val stopID: Int,
)

fun StudentAttendanceRequest.toNetworkModel() = NetworkStuAtt(
    stID = stID,
    status = status,
    stopID = stopID,
)

// ── Drop Student Response ──

@Serializable
data class NetworkDropStudentResponse(
    @SerialName("errorCode")
    override val errorCode: Int,
    @SerialName("message")
    override val message: String,
    @SerialName("status")
    override val status: String,
) : NetworkResponse
