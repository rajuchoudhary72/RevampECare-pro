package com.app.ecarepro.core.network.retrofit.service

import com.app.ecarepro.core.network.model.leave.NetworkApplyLeaveRequest
import com.app.ecarepro.core.network.model.leave.NetworkApplyLeaveResponse
import com.app.ecarepro.core.network.model.leave.NetworkLeaveActionRequest
import com.app.ecarepro.core.network.model.leave.NetworkLeaveActionResponse
import com.app.ecarepro.core.network.model.leave.NetworkLeaveReportResponse
import com.app.ecarepro.core.network.model.leave.NetworkLeaveSettingResponse
import com.app.ecarepro.core.network.model.user.NetworkGetCredentialRequest
import com.app.ecarepro.core.network.model.user.NetworkGetCredentialsResponse
import com.app.ecarepro.core.network.model.user.NetworkGetUsernameByUIDResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkAddQuestionRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkAddQuestionResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkAnswerListResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkQuestionnaireResponse
import com.app.ecarepro.core.network.model.user.NetworkGenerateTokenResponse
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.NetworkResendOtpRequest
import com.app.ecarepro.core.network.model.user.NetworkValidateOtpRequest
import com.app.ecarepro.core.network.model.CommonNetworkResponse
import com.app.ecarepro.core.network.model.profile.NetworkMyProfileResponse
import com.app.ecarepro.core.network.model.profile.NetworkStaffProfileRequest
import com.app.ecarepro.core.network.model.profile.NetworkUpdateParentProfileRequest
import com.app.ecarepro.core.network.model.user.NetworkChangeCredentialsRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("User/TwoFactorLogin")
    suspend fun login(
        @Body loginRequest: NetworkLoginRequest,
    ): NetworkLoginResponse

    @POST("User/ResendOTP")
    suspend fun resendOtp(
        @Body loginRequest: NetworkResendOtpRequest,
    ): NetworkLoginResponse

    @POST("User/ValidateOTP")
    suspend fun validateOtp(
        @Body loginRequest: NetworkValidateOtpRequest,
    ): NetworkLoginResponse

    @POST("User/GetCredentials")
    suspend fun getCredentials(
        @Body request: NetworkGetCredentialRequest,
    ): NetworkGetCredentialsResponse

    @GET("User/GetUsernameByUID")
    suspend fun getUsernameByUID(
        @Query("SchCode") schoolCode: String,
        @Query("UserID") userID: Int,
        @Query("UserType") userType: Int,
        @Query("RcvOn") receivedOn: String,
    ): NetworkGetUsernameByUIDResponse


    @GET("Questionnaire/List")
    suspend fun getQuestionnaireList(
        @Query("pg") pg: Int,
        @Query("myque") myQuestions: Boolean,
    ): NetworkQuestionnaireResponse

    @GET("Questionnaire/AnswerList")
    suspend fun getAnswerList(
        @Query("QID") qid: Int
    ): NetworkAnswerListResponse

    @POST("Questionnaire/PostAnswer")
    suspend fun postAnswer(
        @Body request: NetworkPostAnswerRequest
    ): NetworkPostAnswerResponse

    @POST("Questionnaire/AddQuestion")
    suspend fun addQuestion(
        @Body request: NetworkAddQuestionRequest
    ): NetworkAddQuestionResponse

    @GET("Leave/Report")
    suspend fun getLeaveReport(
        @Query("Status") status: Int,
        @Query("ord") order: Int,
        @Query("ApplType") applType: Int,
        @Query("pg") page: Int,
        @Query("Attper") showAttendance: Boolean,
        @Query("Duration") duration: Int = 0
    ): NetworkLeaveReportResponse

    @POST("Leave/Action")
    suspend fun leaveAction(
        @Body request: NetworkLeaveActionRequest
    ): NetworkLeaveActionResponse

    @POST("Leave/Apply")
    suspend fun applyLeave(
        @Body request: NetworkApplyLeaveRequest
    ): NetworkApplyLeaveResponse

    @GET("Leave/Setting")
    suspend fun getLeaveSettings(): NetworkLeaveSettingResponse

    @POST("User/ChangeUsername")
    suspend fun changeUsername(
        @Body request: NetworkChangeCredentialsRequest,
    ): CommonNetworkResponse

    @POST("User/ChangePassword")
    suspend fun changePassword(
        @Body request: NetworkChangeCredentialsRequest,
    ): CommonNetworkResponse

    @GET("User/MyProfile")
    suspend fun getMyProfile(
        @Query("edit") edit: String,
    ): NetworkMyProfileResponse

    @POST("User/SendStaffProfileRequest")
    suspend fun sendStaffProfileRequest(
        @Body request: NetworkStaffProfileRequest,
    ): CommonNetworkResponse

    @POST("User/UpdateParentProfile")
    suspend fun updateParentProfile(
        @Body request: NetworkUpdateParentProfileRequest,
    ): CommonNetworkResponse

    /**
     * Generates a short-lived token used to perform a single-sign-on login into the
     * Mark Manager web portal.
     *
     * GET /User/GenerateToken?Device=1
     * Device: 1 = Android
     */
    @GET("User/GenerateToken")
    suspend fun generateToken(
        @Query("Device") device: Int,
    ): NetworkGenerateTokenResponse

    @GET("User/LogOut")
    suspend fun logout(
        @Query("DeviceType") deviceType: Int = 1,
        @Query("deviceID") deviceID: String,
        @Query("SessionID") sessionID: String,
    ): CommonNetworkResponse

}