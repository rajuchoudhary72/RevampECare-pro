package com.app.ecarepro.core.domain.model

data class Location(
    val latitude: Double,
    val longitude: Double
){
    companion object {
        val EMPTY = Location(0.0, 0.0)
    }
}