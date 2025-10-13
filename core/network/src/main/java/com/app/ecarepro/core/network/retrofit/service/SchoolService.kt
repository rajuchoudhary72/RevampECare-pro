package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.school.NetworkSchoolDetails
import com.app.ecarepro.core.network.model.school.NetworkSchoolList
import com.app.ecarepro.core.network.model.onboarding.NetworkSchoolOnboarding
import retrofit2.http.GET
import retrofit2.http.Query

interface SchoolService {
    @GET("School/WalkThrough")
    suspend fun getSchoolOnboarding(): NetworkSchoolOnboarding

    @GET("School/DTL")
    suspend fun getSchoolDetails(
        @Query("SchCode") schoolCode: String,
    ): NetworkSchoolDetails

    @GET("School/List")
    suspend fun getSchools(): NetworkSchoolList

}