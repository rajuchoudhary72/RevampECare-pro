package com.app.ecarepro.data.network.model
import com.google.gson.annotations.SerializedName

data class FormDataEmployeeResponseDto(
    @SerializedName("data")
    val `data`: List<Employee>?,
    @SerializedName("status")
    val status: Boolean?
)

data class Employee(
    @SerializedName("designation")
    val designation: String?,
    @SerializedName("employeeID")
    val employeeID: Int?,
    @SerializedName("employeeName")
    val employeeName: String?,
    @SerializedName("visitorPhoto")
    val visitorPhoto: String?
)