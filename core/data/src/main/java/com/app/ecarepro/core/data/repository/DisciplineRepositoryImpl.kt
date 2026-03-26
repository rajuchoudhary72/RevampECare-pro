package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.discipline.AddAppreciationFormData
import com.app.ecarepro.core.domain.model.discipline.AddInfractionFormData
import com.app.ecarepro.core.domain.model.discipline.AppreciationDetails
import com.app.ecarepro.core.domain.model.discipline.AppreciationType
import com.app.ecarepro.core.domain.model.discipline.InfractionDetails
import com.app.ecarepro.core.domain.model.discipline.InfractionType
import com.app.ecarepro.core.domain.repository.DisciplineRepository
import com.app.ecarepro.core.network.DisciplineRemoteDataSource
import com.app.ecarepro.core.network.model.discipline.NetworkBrowsedFile
import com.app.ecarepro.core.network.model.discipline.NetworkSaveAppreciationRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSaveComplianceRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSaveDisciplineLogRequest
import com.app.ecarepro.core.network.model.discipline.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class DisciplineRepositoryImpl @Inject constructor(
    private val disciplineRemoteDataSource: DisciplineRemoteDataSource,
) : DisciplineRepository {

    override fun getStudentInfractions(stID: Int): Flow<Result<InfractionDetails>> {
        return asResultFlow {
            disciplineRemoteDataSource.getStudentInfractions(stID).toDomainModel()
        }
    }

    override fun getStaffInfractions(sID: Int): Flow<Result<InfractionDetails>> {
        return asResultFlow {
            disciplineRemoteDataSource.getStaffInfractions(sID).toDomainModel()
        }
    }

    override fun getAddInfractionForm(stID: Int): Flow<Result<AddInfractionFormData>> {
        return asResultFlow {
            disciplineRemoteDataSource.getAddInfractionForm(stID).toDomainModel()
        }
    }

    override fun getAddStaffInfractionForm(sID: Int): Flow<Result<AddInfractionFormData>> {
        return asResultFlow {
            disciplineRemoteDataSource.getAddStaffInfractionForm(sID).toDomainModel()
        }
    }

    override fun getSubInfractionTypes(infrTypeID: Int): Flow<Result<List<InfractionType>>> {
        return asResultFlow {
            disciplineRemoteDataSource.getSubInfractionTypes(infrTypeID)
                .map { it.toDomainModel() }
        }
    }

    override fun getInfractionInstance(params: Map<String, Any>): Flow<Result<String>> {
        return asResultFlow {
            disciplineRemoteDataSource.getInfractionInstance(params)
        }
    }

    override fun saveDisciplineLog(
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
    ): Flow<Result<String>> {
        return asResultFlow {
            val request = NetworkSaveDisciplineLogRequest(
                stID = if (userType == 1) userId else null,
                sID = if (userType == 3) userId else null,
                uType = userType,
                action = action,
                infrSubTypeID = infrSubTypeID,
                consID = consID,
                instance = instance,
                correctiveAction = correctiveAction,
                infractionOn = infractionOn,
                isComplianceActive = isComplianceActive,
                browsedFile = if (fileBase64 != null && fileExt != null) {
                    NetworkBrowsedFile(
                        attachment = fileBase64,
                        fileExt = fileExt,
                    )
                } else null,
            )
            disciplineRemoteDataSource.saveDisciplineLog(request)
        }
    }

    override fun saveCompliance(
        uType: Int,
        id: String,
        compliance: String,
        fileBase64: String?,
        fileExt: String?,
    ): Flow<Result<String>> {
        return asResultFlow {
            val request = NetworkSaveComplianceRequest(
                uType = uType,
                id = id,
                compliance = compliance,
                browsedFile = if (fileBase64 != null && fileExt != null) {
                    NetworkBrowsedFile(
                        attachment = fileBase64,
                        fileExt = fileExt,
                    )
                } else null,
            )
            disciplineRemoteDataSource.saveCompliance(request)
        }
    }

    override fun resolveCompliance(uType: Int, id: String): Flow<Result<String>> {
        return asResultFlow {
            disciplineRemoteDataSource.resolveCompliance(uType, id)
        }
    }

    override fun deleteInfraction(id: String, uType: Int?): Flow<Result<String>> {
        return asResultFlow {
            disciplineRemoteDataSource.deleteDisciplineLog(id, type = 1, uType = uType)
        }
    }

    // Appreciation
    override fun getAppreciations(stID: Int): Flow<Result<AppreciationDetails>> {
        return asResultFlow {
            disciplineRemoteDataSource.getAppreciations(stID).toDomainModel()
        }
    }

    override fun getAddAppreciationForm(stID: Int): Flow<Result<AddAppreciationFormData>> {
        return asResultFlow {
            disciplineRemoteDataSource.getAddAppreciationForm(stID).toDomainModel()
        }
    }

    override fun getSubAppreciationTypes(aprID: Int): Flow<Result<List<AppreciationType>>> {
        return asResultFlow {
            disciplineRemoteDataSource.getSubAppreciationTypes(aprID)
                .map { it.toDomainModel() }
        }
    }

    override fun getAppreciationInstance(aprSubID: Int, stID: Int): Flow<Result<String>> {
        return asResultFlow {
            disciplineRemoteDataSource.getAppreciationInstance(aprSubID, stID)
        }
    }

    override fun saveAppreciation(
        action: Int,
        stID: Int,
        aprSubID: Int,
        rwdID: Int?,
        instance: String,
        appreciationOn: String,
        remark: String?,
    ): Flow<Result<String>> {
        return asResultFlow {
            disciplineRemoteDataSource.saveAppreciation(
                NetworkSaveAppreciationRequest(
                    action = action,
                    stID = stID,
                    aprSubID = aprSubID,
                    rwdID = rwdID,
                    instance = instance,
                    appreciationOn = appreciationOn,
                    remark = remark,
                )
            )
        }
    }

    override fun deleteAppreciation(id: String): Flow<Result<String>> {
        return asResultFlow {
            disciplineRemoteDataSource.deleteDisciplineLog(id, type = 2, uType = null)
        }
    }
}
