package com.app.ecarepro.data


import com.app.ecarepro.data.cache.JsonCache
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.SaveSkillDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.model.NotificationsDto
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.network.model.SkillCategoriesDto
import com.app.ecarepro.data.network.model.SkillListDto
import com.app.ecarepro.data.network.model.SkillTypesDto
import com.app.ecarepro.data.network.model.SyncData
import com.app.ecarepro.data.network.model.toAppLayout
import com.app.ecarepro.data.network.service.AppService
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.model.AppLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val appService: AppService,
    private val jsonCache: JsonCache,
    private val userDataStore: UserDataStore
) : AppRepository {

    val lmsBasePath = "https://lmsapiuat.franciscanecare.net/"

    override fun getAppLayout(): Flow<Result<AppLayout>> {
        return flow {
            try {
                val response = if (userDataStore.isLMSEnabled().first()) {
                    appService.getLMSAppLayout(
                        lmsBasePath + "Workspace/Layout"
                    ).toAppLayout()
                } else {
                    appService.getAppLayout().toAppLayout()
                }

                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getNotifications(refresh: Boolean): Flow<Result<List<Notification>>> {
        return flow {
            try {
                val response: NotificationsDto? =
                    if (refresh.not() && jsonCache.isCacheAvailable(NOTIFICATION_CACHE_KEY)) {
                        jsonCache.retrieve(
                            NOTIFICATION_CACHE_KEY,
                            NotificationsDto::class.java
                        )
                    } else {
                        appService.getNotifications().also {
                            jsonCache.store(NOTIFICATION_CACHE_KEY, it)
                        }
                    }
                if (response?.errorCode == 0) {
                    emit(Result.success(response.recentNotifications ?: emptyList()))
                } else {
                    emit(Result.failure(IllegalArgumentException(response?.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun registerDevice(registerDevice: RegisterDevice): Flow<Result<String>> {
        return flow {
            if (userDataStore.isUserAuthenticated()) {
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

    override fun syncData(): Flow<Result<SyncData>> {
        return flow {
            try {
                val response = appService.syncData()
                if (response.errorCode == 0 && response.data != null) {
                    emit(Result.success(response.data))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getSkillCategories(): Flow<Result<SkillCategoriesDto>> {
        return flow {
            try {
                val response = appService.getSkillCategories(lmsBasePath + "Skills/Categories")
                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getSkillList(): Flow<Result<SkillListDto>> {
        return flow {
            try {
                val response = appService.getSkillList(lmsBasePath + "Skills/All")
                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun deleteSkill(id: String): Flow<Result<String>> {
        return flow {
            try {
                val response = appService.deleteSkill(lmsBasePath + "Skills/DeleteSkill", id)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message?:"Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getSkillTypes(id: String): Flow<Result<SkillTypesDto>> {
        return flow {
            try {
                val response = appService.getSkillTypes(lmsBasePath + "Skills/Types", id)
                if (response.errorCode == 0) {
                    emit(Result.success(response))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun saveSkill(saveSkillDto: SaveSkillDto): Flow<Result<String>> {
        return flow {
            try {
                val response = appService.saveSkill(lmsBasePath + "Skills/SaveSkill", saveSkillDto)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message?:"Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }


    override suspend fun notificationSeen(id: String): CommonResponse {
        return appService.notificationSeen(id)
    }

    companion object {
        private const val NOTIFICATION_CACHE_KEY = "notifications"
    }
}
