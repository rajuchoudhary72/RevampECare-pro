package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.ProfilePictureStudent
import com.app.ecarepro.core.domain.repository.UpdateProfilePictureRepository
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.model.admin.NetworkUploadStudentPhotoRequest
import com.app.ecarepro.core.network.model.staff.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UpdateProfilePictureRepositoryImpl @Inject constructor(
    private val staffRemoteDataSource: StaffRemoteDataSource,
    private val adminRemoteDataSource: AdminRemoteDataSource,
) : UpdateProfilePictureRepository {

    override fun getClassTeacherOf(): Flow<Result<List<Class>>> = asResultFlow {
        staffRemoteDataSource.getClassTeacherOf().map { it.toDomainModel() }
    }

    override fun getStudents(classId: String, orderBy: Int): Flow<Result<List<ProfilePictureStudent>>> = asResultFlow {
        adminRemoteDataSource.getStudentsForRollNumber(classId, orderBy).map { s ->
            ProfilePictureStudent(
                stID = s.stID,
                name = s.name,
                rollNumber = s.rollNumber ?: "",
                admissionNumber = s.admissionNumber ?: "",
                photo = s.photo ?: "",
                fatherName = s.fatherName ?: "",
            )
        }
    }

    override fun uploadStudentPhoto(stID: Int, photoBase64: String, photoExt: String): Flow<Result<String>> = asResultFlow {
        adminRemoteDataSource.uploadStudentPhoto(
            NetworkUploadStudentPhotoRequest(stID = stID, photo = photoBase64, photoExt = photoExt)
        )
    }
}
