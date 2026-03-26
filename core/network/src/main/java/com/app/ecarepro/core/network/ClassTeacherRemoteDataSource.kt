package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.classteacher.NetworkClassTeacher

interface ClassTeacherRemoteDataSource {
    suspend fun getClassTeachers(): List<NetworkClassTeacher>
}
