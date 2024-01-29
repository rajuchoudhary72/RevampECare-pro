package com.app.ecarepro.utils

import com.app.ecarepro.data.network.model.NetworkThoughts

sealed class ResponseState {
    object Loading : ResponseState()
    class Failure(val msg:Throwable) : ResponseState()

    class Success(val data: NetworkThoughts):ResponseState()

    object Empty: ResponseState()
}