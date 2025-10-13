package com.app.ecarepro.core.domain.exception

class ApiException(val code: Int, override val message: String) : Exception(message)


fun Throwable.errorMessage(): String{
  return  if(this is ApiException){
        message
    }else{
        "Something went wrong. Please try again later."
    }
}