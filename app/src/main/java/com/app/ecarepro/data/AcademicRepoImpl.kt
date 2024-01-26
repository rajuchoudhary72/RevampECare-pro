package com.app.ecarepro.data

import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.service.AcademicService
import com.app.ecarepro.data.repository.AcademicRepo
import javax.inject.Inject

class AcademicRepoImpl @Inject constructor(private val academicService: AcademicService) : AcademicRepo{
    override suspend fun getClassSyllabus(): NetworkClassSyllabus {
         return academicService.getClassSyllabus()
    }
}