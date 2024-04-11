package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.GetCredentialsRequest
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkPaySlip
import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import com.app.ecarepro.data.network.model.NetworkThoughts
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.NetworkWhoLike
import com.app.ecarepro.data.network.model.UserLoginRequestDto
import com.app.ecarepro.data.network.model.AddThoughtsPostData
import com.app.ecarepro.data.network.model.NetworkAnswerDetails
import com.app.ecarepro.data.network.model.PostAnswerPostData
import com.app.ecarepro.data.network.model.post_question.AddQuestionPostData
import com.app.ecarepro.ui.award.ExcellenceAwardResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @GET("User/Verify")
    suspend fun verifyUser(
        @Query("SchCode") schoolCode: String,
        @Query("Username") username: String
    ): NetworkUserDetailsDto

    @POST("User/GetCredentials")
    suspend fun getCredentials(
        @Body request: GetCredentialsRequest,
    ): NetworkUserDetailsDto

    @POST("User/Login")
    suspend fun login(
        @Body request: UserLoginRequestDto,
    ): LoginResponseDto


    @GET("Academic/ClassSyllabus")
    suspend fun getClassSyllabus(): NetworkClassSyllabus

    @GET("Academic/ActivityCaledar")
    suspend fun getActivityCaledar(): NetworkActivityCalender


    @GET("Library/DTL")
    suspend fun getLibraryDetails(): NetworkLatestBook

    @GET("Library/BookDTL")
    suspend fun getBookDetails(
        @Query("BookID") bookID: Int,
        @Query("ID") id: Int,
    ): NetworkBookDetails

    @GET("Library/Search")
    suspend fun getLibrarySearch(
        @Query("query") query: String,
        @Query("pg") pg: Int,
    ): NetworkBookDetails

    @GET("Questionnaire/List")
    suspend fun getQuestionnaireList(
        @Query("pg") pg: Int,
        @Query("myque") myque: Boolean,
    ): NetworkQuestionnaire

    @GET("Staff/MyClass")
    suspend fun staffMyClass(
        /* @Query("SubID") subID: Int,
         @Query("ID") iD: Int*/
    ): NetworkMyClass

    @GET("Staff/Payslip")
    suspend fun getPayslip(): NetworkPaySlip

    @GET("Thoughts/List")
    suspend fun getThoughts(
        @Query("pg") pg: Int,
        @Query("dir") dir: Int,
        @Query("mythoughts") mythoughts: Boolean
    ): NetworkThoughts


    @GET("Thoughts/Like")
    suspend fun thoughtsLike(
        @Query("ThID") thID: Int,
        @Query("Like") like: Boolean
    ): CommonResponse

    @GET("Thoughts/WhoLiked")
    suspend fun whoLiked(
        @Query("ThID") thID: Int
    ): NetworkWhoLike

    @POST("Thoughts/Create")
    suspend fun thoughtsCreate(
        @Body request: AddThoughtsPostData,
    ): CommonResponse

    @GET("Thoughts/Delete")
    suspend fun thoughtsDelete(
        @Query("ThID") thID: Int
    ): CommonResponse


    @GET("Questionnaire/Like")
    suspend fun questionnaireLike(
        @Query("QID") qID: Int,
        @Query("Like") like: Boolean
    ): CommonResponse

    @GET("Questionnaire/DeleteAnswer")
    suspend fun deleteAnswer(
        @Query("AnsID") ansID: Int
    ): CommonResponse

    @GET("Questionnaire/AnswerList")
    suspend fun answerList(
        @Query("QID") qID: Int
    ): NetworkAnswerDetails

    @POST("Questionnaire/PostAnswer")
    suspend fun postAnswer(
        @Body request: PostAnswerPostData,
    ): CommonResponse

    @POST("Questionnaire/AddQuestion")
    suspend fun addQuestion(
        @Body request: AddQuestionPostData,
    ): CommonResponse

    @GET("Academic/ExcellenceAward")
    suspend fun excellenceAward(
    ): ExcellenceAwardResponse

}