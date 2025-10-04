package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.OnboardingResponseModel
import retrofit2.http.GET

 interface SchoolService {
    @GET("School/WalkThrough")
    suspend fun getSchoolOnboarding(): OnboardingResponseModel
}