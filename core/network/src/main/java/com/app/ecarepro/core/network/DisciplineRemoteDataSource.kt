package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.discipline.NetworkAddAppreciationResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAddInfractionResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAppreciationDetailsResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAppreciationType
import com.app.ecarepro.core.network.model.discipline.NetworkInfractionDetailsResponse
import com.app.ecarepro.core.network.model.discipline.NetworkInfractionType
import com.app.ecarepro.core.network.model.discipline.NetworkSaveAppreciationRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSaveComplianceRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSaveDisciplineLogRequest

interface DisciplineRemoteDataSource {

    // Infraction
    suspend fun getStudentInfractions(stID: Int): NetworkInfractionDetailsResponse
    suspend fun getStaffInfractions(sID: Int): NetworkInfractionDetailsResponse
    suspend fun getAddInfractionForm(stID: Int): NetworkAddInfractionResponse
    suspend fun getAddStaffInfractionForm(sID: Int): NetworkAddInfractionResponse
    suspend fun getSubInfractionTypes(infrTypeID: Int): List<NetworkInfractionType>
    suspend fun getInfractionInstance(params: Map<String, Any>): String
    suspend fun saveDisciplineLog(request: NetworkSaveDisciplineLogRequest): String
    suspend fun saveCompliance(request: NetworkSaveComplianceRequest): String
    suspend fun resolveCompliance(uType: Int, id: String): String
    suspend fun deleteDisciplineLog(id: String, type: Int, uType: Int?): String

    // Appreciation
    suspend fun getAppreciations(stID: Int): NetworkAppreciationDetailsResponse
    suspend fun getAddAppreciationForm(stID: Int): NetworkAddAppreciationResponse
    suspend fun getSubAppreciationTypes(aprID: Int): List<NetworkAppreciationType>
    suspend fun getAppreciationInstance(aprSubID: Int, stID: Int): String
    suspend fun saveAppreciation(request: NetworkSaveAppreciationRequest): String
}
