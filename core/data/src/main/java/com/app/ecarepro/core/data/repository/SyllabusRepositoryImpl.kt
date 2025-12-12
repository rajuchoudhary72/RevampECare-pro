package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.Class
import com.app.ecarepro.core.domain.model.SaveSyllabus
import com.app.ecarepro.core.domain.model.Section
import com.app.ecarepro.core.domain.model.Student
import com.app.ecarepro.core.domain.model.Subject
import com.app.ecarepro.core.domain.model.Syllabus
import com.app.ecarepro.core.domain.repository.SyllabusRepository
import com.app.ecarepro.core.network.AdminRemoteDataSource
import com.app.ecarepro.core.network.StaffRemoteDataSource
import com.app.ecarepro.core.network.model.admin.toDomainModel
import com.app.ecarepro.core.network.model.admin.toNetworkModel
import com.app.ecarepro.core.network.model.staff.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.app.ecarepro.core.network.SmsRemoteDataSource
import com.app.ecarepro.core.network.model.sms.toDomainModel


internal class SyllabusRepositoryImpl @Inject constructor(
    private val adminRemoteDataSource: AdminRemoteDataSource,
    private val staffRemoteDataSource: StaffRemoteDataSource,
    private val smsRemoteDataSource: SmsRemoteDataSource,
    ) : SyllabusRepository {


    override fun getSyllabus(): Flow<Result<List<Syllabus>>> {
        return asResultFlow {
            adminRemoteDataSource.getSyllabus().map { it.toDomainModel() }
        }
    }

    override fun deleteSyllabus(syllabusId: String): Flow<Result<Boolean>> {
        return asResultFlow {
            adminRemoteDataSource.deleteSyllabus(syllabusId)
        }
    }

    override fun getClasses(): Flow<Result<List<Class>>> {
        return asResultFlow {
            staffRemoteDataSource.getClasses().map { it.toDomainModel() }
        }
    }
    override fun getAssignmentClasses(): Flow<Result<List<Class>>> {
        return asResultFlow {
            staffRemoteDataSource.getMyClasses().map { it.toDomainModel() }
        }
    }
    override fun getSections(classStd: String): Flow<Result<List<Section>>> {
        return asResultFlow {
            staffRemoteDataSource.getSections(classStd).map { it.toDomainModel() }
        }
    }

    override fun getSubjects(classStd: String): Flow<Result<List<Subject>>> {
        return asResultFlow {
            staffRemoteDataSource.getSubjects(classStd).map { it.toDomainModel() }
        }
    }
    override fun getAssignmentSubjects(): Flow<Result<List<Subject>>> {
        return asResultFlow {
            staffRemoteDataSource.getMySubjects().map { it.toDomainModel() }
        }
    }

    override fun getStudents(
        teacherId: String,
        classId: String,
        scholarType: String,
    ): Flow<Result<List<Student>>> {
        return asResultFlow {
            smsRemoteDataSource.getStudents(
                teacherId = teacherId,
                classId = classId,
                scholarType = scholarType
            ).students?.map { it.toDomainModel() } ?: emptyList()
        }
    }

    override fun saveSyllabus(syllabus: SaveSyllabus): Flow<Result<String>> {
        return asResultFlow {
            adminRemoteDataSource.saveSyllabus(syllabus.toNetworkModel())
        }
    }

}