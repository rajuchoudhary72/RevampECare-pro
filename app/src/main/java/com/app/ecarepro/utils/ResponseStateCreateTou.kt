package com.app.ecarepro.utils

import com.app.ecarepro.data.network.model.CommonResponse

sealed class ResponseStateCreateTou {
    object Loading : ResponseStateCreateTou()
    class Failure(val msg:Throwable) : ResponseStateCreateTou()

    class Success(val data: CommonResponse):ResponseStateCreateTou()

    object Empty: ResponseStateCreateTou()
}