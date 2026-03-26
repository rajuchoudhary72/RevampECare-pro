package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAddAppreciationResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAddInfractionResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAppreciationDetailsResponse
import com.app.ecarepro.core.network.model.discipline.NetworkAppreciationInstanceResponse
import com.app.ecarepro.core.network.model.discipline.NetworkInfractionDetailsResponse
import com.app.ecarepro.core.network.model.discipline.NetworkInfractionInstanceResponse
import com.app.ecarepro.core.network.model.discipline.NetworkSaveDisciplineLogRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSaveAppreciationRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSaveComplianceRequest
import com.app.ecarepro.core.network.model.discipline.NetworkSubInfractionTypesResponse
import com.app.ecarepro.core.network.model.discipline.NetworkSubAppreciationTypesResponse
import kotlinx.serialization.InternalSerializationApi
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DisciplineService {

    // Infraction endpoints
    @GET("DisciplineLog/Infractions")
    suspend fun getDisciplineLog(
        @Query("stID") stID: Int,
    ): NetworkInfractionDetailsResponse

    @GET("DisciplineLog/StaffInfractions")
    suspend fun getStaffDisciplineLog(
        @Query("sID") sID: Int,
    ): NetworkInfractionDetailsResponse

    @GET("DisciplineLog/AddStaffInfraction")
    suspend fun getAddDisciplineLogForm(
        @Query("stID") stID: Int,
    ): NetworkAddInfractionResponse

    @GET("DisciplineLog/AddStaffInfraction")
    suspend fun getAddStaffDisciplineLogForm(
        @Query("sID") sID: Int,
    ): NetworkAddInfractionResponse

    @POST("DisciplineLog/SubInfractionTypes")
    suspend fun getSubInfractionTypes(
        @Body params: Map<String, Int>,
    ): NetworkSubInfractionTypesResponse

    @POST("DisciplineLog/InfractionInstance")
    suspend fun getInfractionInstance(
        @Body params: Map<String, @JvmSuppressWildcards Any>,
    ): NetworkInfractionInstanceResponse

    @POST("DisciplineLog/SaveInfraction")
    suspend fun saveDisciplineLog(
        @Body request: NetworkSaveDisciplineLogRequest,
    ):  CommonNetworkResponse

    @POST("DisciplineLog/PostCompliance")
    suspend fun saveDisciplineLogCompliance(
        @Body request: NetworkSaveComplianceRequest,
    ):  CommonNetworkResponse

    @POST("DisciplineLog/ResolvedCompliance")
    suspend fun resolveDisciplineLog(
        @Query("uType") uType: Int,
        @Query("id") id: String,
    ):  CommonNetworkResponse

    @POST("Discipline/DeleteInfraction")
    suspend fun deleteDisciplineLog(
        @Query("id") id: String,
        @Query("type") type: Int,
        @Query("uType") uType: Int? = null,
    ): CommonNetworkResponse

    // Appreciation endpoints
    @GET("Discipline/GetAppreciation")
    suspend fun getAppreciation(
        @Query("stID") stID: Int,
    ): NetworkAppreciationDetailsResponse

    @GET("Discipline/AddAppreciation")
    suspend fun getAddAppreciationForm(
        @Query("stID") stID: Int,
    ): NetworkAddAppreciationResponse

    @POST("Discipline/GetSubAppreciationTypes")
    suspend fun getSubAppreciationTypes(
        @Body params: Map<String, Int>,
    ): NetworkSubAppreciationTypesResponse

    @POST("Discipline/GetAppreciationInstance")
    suspend fun getAppreciationInstance(
        @Body params: Map<String, Int>,
    ): NetworkAppreciationInstanceResponse

    @POST("Discipline/SaveAppreciation")
    suspend fun saveAppreciation(
        @Body request: NetworkSaveAppreciationRequest,
    ): CommonNetworkResponse
}