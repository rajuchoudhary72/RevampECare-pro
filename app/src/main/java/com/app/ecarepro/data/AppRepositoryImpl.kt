package com.app.ecarepro.data

import android.util.Log
import com.app.ecarepro.data.cache.JsonCache
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.BadgeCountResponse
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.network.service.AppService
import com.app.ecarepro.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.NotificationsDto
import retrofit2.HttpException

import com.app.ecarepro.data.network.model.SyncData
import com.app.ecarepro.ui.language.LanguageRepository
import com.app.ecarepro.ui.language.model.TranslationItem

class AppRepositoryImpl @Inject constructor(
    private val appService: AppService,
    private val jsonCache: JsonCache,
    private val userDataStore: UserDataStore,
    private val languageRepository: LanguageRepository

) : AppRepository {
    override fun getAppLayout(): Flow<Result<AppLayoutDto>> {
        return flow {
            try {
                val response = appService.getAppLayout(1,languageRepository.getSavedLanguage())
                emit(Result.success(response))
               /* if (response.errorCode == 0) {
                    *//*response.userInfo.let {
                        userDataStore.getUser()?.copy(
                            photo = it.photo,
                            userId = it.userID
                        )?.let { it1 ->
                            userDataStore.saveUser(
                                it1
                            )
                        }
                    }*//*

                } else {
                    emit(Result.failure(IllegalArgumentException(response.message)))
                }*/
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
                    emit(Result.success(response.allMenus?: emptyList()))
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
                    emit(Result.success(response.message?:""))
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


    override suspend fun notificationSeen(id: String): CommonResponse {
        return appService.notificationSeen(id)
    }

    companion object {
        private const val NOTIFICATION_CACHE_KEY = "notifications"
    }

    override suspend fun getTranslations(
        spreadsheetId: String,
        range: String
    ): Flow<Result<List<TranslationItem>>> = flow {
        try {
            val apiKey = "AIzaSyAdGd2nT10vrag4zManlLI1PbZ1D4rBkBA" // Store your API key securely
            val response = appService.getSheetValues(spreadsheetId, range, apiKey)

            if (response.values.isNotEmpty()) {
                val translations = parseTranslations(response.values)
                // saveTranslations(translations)
                Log.d("SheetAPI", "Translations retrieved successfully $translations")
                emit(Result.success(translations))
            } else {
                emit(Result.failure(IllegalStateException("No data found in sheet")))
                Log.d("SheetAPI", "Translations Exception  ${response.values}")

            }
        } catch (error: HttpException) {
            val errorBody = error.response()?.errorBody()?.string()
            Log.e("SheetAPI", "HTTP Error: ${error.code()}, Body: $errorBody")
            emit(Result.failure(error))
        } catch (error: Throwable) {
            Log.e("SheetAPI", "Error: ${error.message}")
            emit(Result.failure(error))
        }
    }

    private fun parseTranslations(values: List<List<String>>): List<TranslationItem> {
        // Skip header row if present
        val dataRows = if (values.firstOrNull()?.firstOrNull() == "key") {
            values.drop(1)
        } else {
            values
        }

        return dataRows.mapNotNull { row ->
            if (row.size >= 3) {
                TranslationItem(
                    key = row[0],
                    english = row[1],
                    hindi = row[2]
                )
            } else null
        }
    }


}