package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.ProfilePictureStudent
import kotlinx.coroutines.flow.Flow

interface UpdateProfilePictureRepository {
    fun getClassTeacherOf(): Flow<Result<List<Class>>>
    fun getStudents(classId: String, orderBy: Int = 0): Flow<Result<List<ProfilePictureStudent>>>
    fun uploadStudentPhoto(stID: Int, photoBase64: String, photoExt: String): Flow<Result<String>>
}
