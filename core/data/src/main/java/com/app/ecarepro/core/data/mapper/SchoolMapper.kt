package com.app.ecarepro.core.data.mapper

import com.app.ecarepro.core.domain.model.School
import com.app.ecarepro.core.network.model.NetworkSchool

fun NetworkSchool.toDomainModel() = School(
    schoolCode = schoolCode,
    name = name,
    address = address,
    city = city,
    logo = logo,
    state = state
)

