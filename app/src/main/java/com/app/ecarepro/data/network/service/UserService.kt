package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.NetworkUser
import retrofit2.http.GET

interface UserService {
    @GET("user/12")
   suspend fun getUser():NetworkUser

}