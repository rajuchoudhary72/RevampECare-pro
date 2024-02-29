package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkActivityCalender
import com.app.ecarepro.data.network.model.NetworkAnswerDetails
import com.app.ecarepro.data.network.model.NetworkBookDetails
import com.app.ecarepro.data.network.model.NetworkClassSyllabus
import com.app.ecarepro.data.network.model.NetworkLatestBook
import com.app.ecarepro.data.network.model.NetworkMyClass
import com.app.ecarepro.data.network.model.NetworkPaySlip
import com.app.ecarepro.data.network.model.NetworkQuestionnaire
import com.app.ecarepro.data.network.model.NetworkThoughts
import com.app.ecarepro.data.network.model.NetworkUser
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.NetworkWhoLike

interface UserRepository {
    suspend fun insertUser(user: NetworkUser)

    suspend fun verifyUser(schoolCode: String, username: String): NetworkUserDetailsDto

    suspend fun getCredentials(
        schoolCode: String,
        userType: Int,
        rcvOn: String,
        mobile: String?,
        email: String?
    ): NetworkUserDetailsDto
    suspend fun login(
        schoolCode: String,
        userName: String,
        password: String
    ): LoginResponseDto


    suspend fun getClassSyllabus( ): NetworkClassSyllabus
    suspend fun getActivityCalender(  ): NetworkActivityCalender

    suspend fun getLibraryDetails( ): NetworkLatestBook
    suspend fun getBookDetails(bookID: Int, id: Int ): NetworkBookDetails
    suspend fun getLibrarySearch( query: String,pg: Int ): NetworkBookDetails


    suspend fun staffMyClass(subID: Int, iD: Int): NetworkMyClass
    suspend fun getPayslip( ): NetworkPaySlip

    suspend fun getThoughts(pg: Int,
                            dir: Int,
                            mythoughts: Boolean): NetworkThoughts

    suspend fun thoughtsLike(thID: Int,
                             like: Boolean): CommonResponse
    suspend fun thoughtsDelete( thID: Int ): CommonResponse

    suspend fun whoLiked( thID: Int ): NetworkWhoLike

    suspend fun thoughtsCreate (quotation:String,author:String): CommonResponse

    suspend fun getQuestionnaireList( pg: Int, myque: Boolean ): NetworkQuestionnaire

    suspend fun questionnaireLike(qID: Int,
                             like: Boolean): CommonResponse

    suspend fun answerList(qID: Int ): NetworkAnswerDetails

    suspend fun postAnswer (qid:String,answer:String): CommonResponse

    suspend fun deleteAnswer(ansID: Int ): CommonResponse
    suspend fun addQuestion (question:String,attachment:String,fileURL:String,fileExt:String ): CommonResponse



}