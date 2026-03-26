package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.discipline.AddAppreciationFormData
import com.app.ecarepro.core.domain.model.discipline.AddInfractionFormData
import com.app.ecarepro.core.domain.model.discipline.AppreciationDetails
import com.app.ecarepro.core.domain.model.discipline.AppreciationType
import com.app.ecarepro.core.domain.model.discipline.InfractionDetails
import com.app.ecarepro.core.domain.model.discipline.InfractionType
import kotlinx.coroutines.flow.Flow

interface DisciplineRepository {

    // Infraction
    fun getStudentInfractions(stID: Int): Flow<Result<InfractionDetails>>
    fun getStaffInfractions(sID: Int): Flow<Result<InfractionDetails>>
    fun getAddInfractionForm(stID: Int): Flow<Result<AddInfractionFormData>>
    fun getAddStaffInfractionForm(sID: Int): Flow<Result<AddInfractionFormData>>
    fun getSubInfractionTypes(infrTypeID: Int): Flow<Result<List<InfractionType>>>
    fun getInfractionInstance(params: Map<String, Any>): Flow<Result<String>>
    fun saveDisciplineLog(
        userId: Int,
        userType: Int,
        action: Int,
        infrSubTypeID: Int,
        consID: Int,
        instance: String,
        correctiveAction: String?,
        infractionOn: String,
        isComplianceActive: Boolean,
        fileBase64: String?,
        fileExt: String?,
    ): Flow<Result<String>>

    fun saveCompliance(
        uType: Int,
        id: String,
        compliance: String,
        fileBase64: String?,
        fileExt: String?,
    ): Flow<Result<String>>

    fun resolveCompliance(uType: Int, id: String): Flow<Result<String>>
    fun deleteInfraction(id: String, uType: Int?): Flow<Result<String>>

    // Appreciation
    fun getAppreciations(stID: Int): Flow<Result<AppreciationDetails>>
    fun getAddAppreciationForm(stID: Int): Flow<Result<AddAppreciationFormData>>
    fun getSubAppreciationTypes(aprID: Int): Flow<Result<List<AppreciationType>>>
    fun getAppreciationInstance(aprSubID: Int, stID: Int): Flow<Result<String>>
    fun saveAppreciation(
        action: Int,
        stID: Int,
        aprSubID: Int,
        rwdID: Int?,
        instance: String,
        appreciationOn: String,
        remark: String?,
    ): Flow<Result<String>>

    fun deleteAppreciation(id: String): Flow<Result<String>>
}
