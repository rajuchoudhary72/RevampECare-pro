package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.leave.NetworkLeaveActionRequest
import com.app.ecarepro.core.network.model.leave.NetworkLeaveActionResponse
import com.app.ecarepro.core.network.model.leave.NetworkLeaveReportResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkAddQuestionRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkAddQuestionResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkAnswerListResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerRequest
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerResponse
import com.app.ecarepro.core.network.model.questionnaire.NetworkQuestionnaireResponse
import com.app.ecarepro.core.network.model.user.NetworkGetCredentialRequest
import com.app.ecarepro.core.network.model.user.NetworkGetCredentialsResponse
import com.app.ecarepro.core.network.model.user.NetworkGetUsernameByUIDResponse
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.NetworkResendOtpRequest
import com.app.ecarepro.core.network.model.user.NetworkValidateOtpRequest

interface UserRemoteDataSource {
    suspend fun login(request: NetworkLoginRequest): NetworkLoginResponse
    suspend fun getQuestionnaireList(pg: Int, myQuestions: Boolean): NetworkQuestionnaireResponse

    suspend fun getAnswerList(qid: Int): NetworkAnswerListResponse

    suspend fun postAnswer(request: NetworkPostAnswerRequest): NetworkPostAnswerResponse

    suspend fun addQuestion(request: NetworkAddQuestionRequest): NetworkAddQuestionResponse
    suspend fun resendOtp(request: NetworkResendOtpRequest): NetworkLoginResponse

    suspend fun validateOtp(request: NetworkValidateOtpRequest): NetworkLoginResponse

    suspend fun getCredentials(request: NetworkGetCredentialRequest): NetworkGetCredentialsResponse

    suspend fun getUsernameByUID(
        schoolCode: String,
        userID: Int,
        userType: Int,
        receivedOn: String,
    ): NetworkGetUsernameByUIDResponse

    suspend fun getLeaveReport(
        status: Int,
        order: Int,
        applType: Int,
        page: Int,
        showAttendance: Boolean,
        duration: Int
    ): NetworkLeaveReportResponse

    suspend fun leaveAction(request: NetworkLeaveActionRequest): NetworkLeaveActionResponse
}