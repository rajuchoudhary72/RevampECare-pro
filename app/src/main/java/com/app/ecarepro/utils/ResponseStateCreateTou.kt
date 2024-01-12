package com.app.ecarepro.utils

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.LoginResponseDto

sealed class ResponseStateCreateTou {
    object Loading : ResponseStateCreateTou()
    class Failure(val msg:Throwable) : ResponseStateCreateTou()

    class Success(val data: CommonResponse):ResponseStateCreateTou()

    object Empty: ResponseStateCreateTou()
}