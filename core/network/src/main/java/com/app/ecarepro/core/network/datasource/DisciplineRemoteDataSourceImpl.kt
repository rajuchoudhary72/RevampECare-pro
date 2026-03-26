package com.app.ecarepro.core.network.datasource

import com.app.ecarepro.core.network.DisciplineRemoteDataSource
import com.app.ecarepro.core.network.model.discipline.NetworkAddAppreciationResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAddInfractionResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAppreciationDetailsResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAppreciationType
import com.app.ecarepro.core.network.model.discipline.NetworkInfractionDetailsResponse
import com.app.ecarepro.core.network.model.discipline.NetworkInfractionType
import com.app.ecarepro.core.network.model.discipline.NetworkSaveAppreciationRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSaveComplianceRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSaveDisciplineLogRequest
import com.app.ecarepro.core.network.model.unwrapPayload
import com.app.ecarepro.core.network.retrofit.service.DisciplineService
import javax.inject.Inject

internal class DisciplineRemoteDataSourceImpl @Inject constructor(
    private val disciplineService: DisciplineService,
) : DisciplineRemoteDataSource {

    override suspend fun getStudentInfractions(stID: Int): NetworkInfractionDetailsResponse {
        return disciplineService.getDisciplineLog(stID)
    }

    override suspend fun getStaffInfractions(sID: Int): NetworkInfractionDetailsResponse {
        return disciplineService.getStaffDisciplineLog(sID)
    }

    override suspend fun getAddInfractionForm(stID: Int): NetworkAddInfractionResponse {
        return disciplineService.getAddDisciplineLogForm(stID)
    }

    override suspend fun getAddStaffInfractionForm(sID: Int): NetworkAddInfractionResponse {
        return disciplineService.getAddStaffDisciplineLogForm(sID)
    }

    override suspend fun getSubInfractionTypes(infrTypeID: Int): List<NetworkInfractionType> {
        return disciplineService.getSubInfractionTypes(mapOf("InfrTypeID" to infrTypeID))
            .unwrapPayload { infractionSubCategories ?: emptyList() }
    }

    override suspend fun getInfractionInstance(params: Map<String, Any>): String {
        @Suppress("UNCHECKED_CAST")
        val typedParams = params.mapValues { (_, v) ->
            when (v) {
                is Int -> v
                is String -> v.toIntOrNull() ?: v
                else -> v
            }
        }
        // Build request params with proper types
        val requestParams = mutableMapOf<String, Any>()
        params.forEach { (k, v) -> requestParams[k] = v }
        return disciplineService.getInfractionInstance(requestParams)
            .unwrapPayload { instance ?: "0" }
    }

    override suspend fun saveDisciplineLog(request: NetworkSaveDisciplineLogRequest): String {
        return disciplineService.saveDisciplineLog(request)
            .unwrapPayload { message }
    }

    override suspend fun saveCompliance(request: NetworkSaveComplianceRequest): String {
        return disciplineService.saveDisciplineLogCompliance(request)
            .unwrapPayload { message }
    }

    override suspend fun resolveCompliance(uType: Int, id: String): String {
        return disciplineService.resolveDisciplineLog(uType, id)
            .unwrapPayload { message }
    }

    override suspend fun deleteDisciplineLog(id: String, type: Int, uType: Int?): String {
        return disciplineService.deleteDisciplineLog(id, type, uType)
            .unwrapPayload { message }
    }

    // Appreciation
    override suspend fun getAppreciations(stID: Int): NetworkAppreciationDetailsResponse {
        return disciplineService.getAppreciation(stID)
    }

    override suspend fun getAddAppreciationForm(stID: Int): NetworkAddAppreciationResponse {
        return disciplineService.getAddAppreciationForm(stID)
    }

    override suspend fun getSubAppreciationTypes(aprID: Int): List<NetworkAppreciationType> {
        return disciplineService.getSubAppreciationTypes(mapOf("AprID" to aprID))
            .unwrapPayload { appreciationSubCategories ?: emptyList() }
    }

    override suspend fun getAppreciationInstance(aprSubID: Int, stID: Int): String {
        return disciplineService.getAppreciationInstance(
            mapOf("AprSubID" to aprSubID, "StID" to stID)
        ).unwrapPayload { instance ?: "0" }
    }

    override suspend fun saveAppreciation(request: NetworkSaveAppreciationRequest): String {
        return disciplineService.saveAppreciation(request)
            .unwrapPayload { message }
    }
}
