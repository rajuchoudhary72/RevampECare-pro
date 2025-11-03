package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.data.mapper.asEntity
import com.app.ecarepro.core.data.mapper.toDomainModel
import com.app.ecarepro.core.data.mapper.toNetworkModel
import com.app.ecarepro.core.database.dao.UserDao
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.AddQuestionRequest
import com.app.ecarepro.core.domain.model.AddQuestionResponse
import com.app.ecarepro.core.domain.model.AnswerListResponse
import com.app.ecarepro.core.domain.model.AppConfig
import com.app.ecarepro.core.domain.model.PostAnswerResponse
import com.app.ecarepro.core.domain.model.QuestionnaireResponse
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.network.UserRemoteDataSource
import com.app.ecarepro.core.network.model.questionnaire.NetworkPostAnswerRequest
import com.app.ecarepro.core.network.model.user.NetworkDeviceInfo
import com.app.ecarepro.core.network.model.user.NetworkLoginRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.let

class UserRepositoryImpl @Inject constructor(
    private val appConfig: AppConfig,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
) : UserRepository {


    override fun login(
        userName: String,
        password: String,
        schoolCode: String,
    ): Flow<Result<User>> {
        return asResultFlow {
            userRemoteDataSource.login(
                NetworkLoginRequest(
                    userName = userName,
                    password = password,
                    schoolCode = schoolCode,
                    deviceInfo = NetworkDeviceInfo(
                        appVersion = appConfig.appVersion,
                        deviceType = appConfig.deviceType.id,
                        model = appConfig.deviceModel,
                        ipAddress = appConfig.deviceId,
                        locationCity = appConfig.location,
                        osVersion = appConfig.osVersion
                    )
                )
            ).let { response ->
                val entity = response.userDTL!!.asEntity(schoolCode)
                userDao.insert(entity)
                entity.toDomainModel()
            }
        }
    }

    override fun getQuestions(
        page: Int,
        myQuestions: Boolean
    ): Flow<Result<QuestionnaireResponse>> {
        return asResultFlow {
            userRemoteDataSource.getQuestionnaireList(page, myQuestions).toDomainModel()
        }
    }

    override fun getAnswerList(qid: Int): Flow<Result<AnswerListResponse>> {
        return asResultFlow {
            userRemoteDataSource.getAnswerList(qid).toDomainModel()
        }
    }

    override fun postAnswer(qid: Int, answer: String): Flow<Result<PostAnswerResponse>> {
        return asResultFlow {
            userRemoteDataSource.postAnswer(
                NetworkPostAnswerRequest(
                    qid = qid,
                    answer = answer
                )
            ).toDomainModel()
        }
    }

    override fun addQuestion(request: AddQuestionRequest): Flow<Result<AddQuestionResponse>> {
        return asResultFlow {
            userRemoteDataSource.addQuestion(request.toNetworkModel()).toDomainModel()
        }
    }
}
