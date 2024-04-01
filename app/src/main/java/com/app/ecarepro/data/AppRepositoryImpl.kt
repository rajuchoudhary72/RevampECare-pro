package com.app.ecarepro.data

import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.service.AppService
import com.app.ecarepro.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val appService: AppService
) : AppRepository {
    override fun getAppLayout(): Flow<Result<AppLayoutDto>> {
        return flow {
            try {
                val response = appService.getAppLayout()
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
}