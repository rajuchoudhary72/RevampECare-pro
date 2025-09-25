package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.NetworkSchoolOnboarding
import retrofit2.http.GET

 interface SchoolService {
    @GET("School/WalkThrough")
    suspend fun getSchoolOnboarding(): NetworkSchoolOnboarding
}