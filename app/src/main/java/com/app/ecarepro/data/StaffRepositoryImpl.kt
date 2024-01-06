package com.app.ecarepro.data
import com.app.ecarepro.data.network.model.asExternalModel
import com.app.ecarepro.model.MyClasse
import com.app.ecarepro.data.network.service.StaffService
import com.app.ecarepro.data.repository.StaffRepository
import com.app.ecarepro.di.annotations.UserDataStore
import javax.inject.Inject

class StaffRepositoryImpl @Inject constructor(
    private val staffService: StaffService
    ) : StaffRepository {
    override suspend fun getMyClass(subID: Int, iD: Int): List<MyClasse> {
        return  staffService.myClass(subID,iD).MyClasses.map { it.asExternalModel() }
    }

}