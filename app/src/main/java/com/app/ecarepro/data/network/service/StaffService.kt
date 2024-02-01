package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkPaySlip
import com.app.ecarepro.data.network.model.VerifyUserDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface StaffService {

    @Headers("Accept: application/json")
    @GET("Staff/MyClass")
    suspend fun myClass(
       /* @Query("SubID") subID: Int,
        @Query("ID") iD: Int*/
    ): NetworkMyClass

    @Headers("Accept: application/json")
    @GET("Staff/Payslip")
    suspend fun getPayslip( ): NetworkPaySlip



}