package com.app.ecarepro.data


import com.app.ecarepro.data.cache.JsonCache
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.SaveSkillCategoryRequest
import com.app.ecarepro.data.network.SaveSkillDto
import com.app.ecarepro.data.network.SaveSkillTypeRequest
import com.app.ecarepro.data.network.model.BadgeCountResponse
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.MasterCategory
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.model.NotificationsDto
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.network.model.SkillCategoriesDto
import com.app.ecarepro.data.network.model.SkillListDto
import com.app.ecarepro.data.network.model.SkillTypesDto
import com.app.ecarepro.data.network.model.SkillsFromMasterDto
import com.app.ecarepro.data.network.model.SyncData
import com.app.ecarepro.data.network.model.toAppLayout
import com.app.ecarepro.data.network.service.AppService
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.model.AppLayout
import com.app.ecarepro.utils.LMSConstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val appService: AppService,
    private val jsonCache: JsonCache,
    private val userDataStore: UserDataStore
) : AppRepository {
    val lmsBasePath = LMSConstant.LMS_BASE_URL
    override fun getAppLayout(): Flow<Result<AppLayout>> {
        return flow {
            try {
                val response = if (userDataStore.isLMSEnabled().first()) {
                    appService.getLMSAppLayout(
                        lmsBasePath + LMSConstant.LAYOUT_API
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

    override suspend fun getNotificationCount(): BadgeCountResponse {
        return appService.getNotificationCount()
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

    /*LMS APi */
    /*get Skill Categories*/
    override fun getSkillCategories(): Flow<Result<SkillCategoriesDto>> {
        return flow {
            try {
                val response =
                    appService.getSkillCategories(lmsBasePath + LMSConstant.SKILL_CATEGORY_API)
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

    /*get Skill list*/
    override fun getSkillList(): Flow<Result<SkillListDto>> {
        return flow {
            try {
                val response = appService.getSkillList(lmsBasePath + LMSConstant.SKILL_ALL)
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
                val response =
                    appService.deleteSkill(lmsBasePath + LMSConstant.SKILL_DELETE_SKILL, id)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
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
                val response =
                    appService.getSkillTypes(lmsBasePath + LMSConstant.SKILL_SKILL_TYPE, id)
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
                val response =
                    appService.saveSkill(lmsBasePath + LMSConstant.SKILL_SKILL_SAVE, saveSkillDto)
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun deleteSkillCategory(sklCatID: String): Flow<Result<String>> {
        return flow {
            try {
                val response = appService.deleteSkillCategory(
                    lmsBasePath + LMSConstant.DELETE_SKILL_CATEGORY,
                    sklCatID
                )
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun saveSkillCategory(
        sklCatID: String?,
        value: String
    ): Flow<Result<String>> {
        return flow {
            try {
                val response = appService.saveSkillCategory(
                    lmsBasePath + LMSConstant.SAVE_SKILL_CATEGORY,
                    SaveSkillCategoryRequest(
                        sklCatID = sklCatID,
                        category = value
                    )
                )
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun saveSkillType(
        sklCatID: String,
        sklTypeID: String?,
        value: String
    ): Flow<Result<String>> {
        return flow {
            try {
                val response = appService.saveSkillType(
                    lmsBasePath + LMSConstant.SAVE_SKILL_TYPE, sklCatID,
                    SaveSkillTypeRequest(
                        sklTypeID = sklCatID,
                        value
                    )
                )
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }
            } catch (error: Throwable) {
                emit(Result.failure(error))
            }
        }
    }

    override fun getSkillFromMaster(): Flow<Result<SkillsFromMasterDto>> {
        return flow {
            try {
                val response = appService.getSkillFromMaster(
                    lmsBasePath + LMSConstant.GET_SKILL_FROM_MASTER
                )
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

    override fun importSkills(categories: List<MasterCategory>): Flow<Result<String>> {
        return flow {
            try {
                val response = appService.importSkills(
                    lmsBasePath + LMSConstant.IMPORT_SKILLS,
                    categories
                )
                if (response.errorCode == 0) {
                    emit(Result.success(response.message ?: "Success"))
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