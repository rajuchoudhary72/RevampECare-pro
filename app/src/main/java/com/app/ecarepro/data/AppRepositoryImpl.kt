package com.app.ecarepro.data

import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.network.model.asUserEntity
import com.app.ecarepro.data.network.service.AppService
import com.app.ecarepro.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val appService: AppService,
    private val userDataStore: UserDataStore,
    private val userDatabase: UserDatabase
) : AppRepository {
    override fun getAppLayout(): Flow<Result<AppLayoutDto>> {
        return flow {
            try {
                val response = appService.getAppLayout()
                if (response.errorCode == 0) {
                    /*response.userInfo.let {
                        userDataStore.getUser()?.copy(
                            photo = it.photo,
                            userId = it.userID
                        )?.let { it1 ->
                            userDataStore.saveUser(
                                it1
                            )
                        }
                    }*/
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getNotifications(): Flow<Result<List<Notification>>> {
        return flow {
            try {
                val response = appService.getNotifications()
                if (response.errorCode == 0) {
                    emit(Result.success(response.recentNotifications ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun registerDevice(registerDevice: RegisterDevice): Flow<Result<String>> {
        return flow {
            try {
                val response = appService.registerFirebaseToken(registerDevice)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: ""))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getFavourites(): Flow<Result<List<Favourites>>> {
        return flow {
            try {
                val response = appService.getFavourites()
                if (response.errorCode == 0) {
                    emit(Result.success(response.allMenus ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun updateFavourites(items: List<Favourites>): Flow<Result<String>> {
        return flow {
            try {
                val response = appService.updateFavourites(items)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: ""))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun syncData(): Flow<Result<Boolean>> {
        return flow {
            try {
                val response = appService.syncData()
                if (response.errorCode == 0) {
                    response.data?.asUserEntity()?.let { user ->
                        userDataStore.getUser()?.let { currentUserInDatabase ->
                            userDatabase.deleteUserById(currentUserInDatabase.id)
                            val id = userDatabase.insertUser(user.copy(schoolCode = userDataStore.getCurrentSchoolCode(), loginTime = getCurrentDateTimeAmPm()))
                            userDataStore.setCurrentUserId(id.toInt())
                        }
                    }
                    emit(Result.success(true))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    fun getCurrentDateTimeAmPm(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    override suspend fun notificationSeen(id: String): CommonResponse {
        return appService.notificationSeen(id)
    }
}