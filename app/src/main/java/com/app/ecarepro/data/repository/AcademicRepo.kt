package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.NetworkClassSyllabus

interface AcademicRepo {

    suspend fun getClassSyllabus( ): NetworkClassSyllabus

}