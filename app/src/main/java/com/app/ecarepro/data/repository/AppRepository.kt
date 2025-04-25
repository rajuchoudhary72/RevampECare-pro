package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.SaveSkillDto
import com.app.ecarepro.data.network.SaveSkillResponse
import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.model.RegisterDevice
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.FavouritesUpdateDto
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.SkillCategoriesDto
import com.app.ecarepro.data.network.model.SkillListDto
import com.app.ecarepro.data.network.model.SkillTypesDto
import retrofit2.http.Query
import com.app.ecarepro.data.network.model.SyncData
import com.app.ecarepro.model.AppLayout

interface AppRepository {
    fun getAppLayout(): Flow<Result<AppLayout>>
    fun getNotifications(refresh: Boolean): Flow<Result<List<Notification>>>
    fun registerDevice(registerDevice: RegisterDevice): Flow<Result<String>>
    fun getFavourites(): Flow<Result<List<Favourites>>>
    fun updateFavourites(items:List<Favourites>): Flow<Result<String>>
    suspend fun notificationSeen(  id: String ): CommonResponse
    fun syncData(): Flow<Result<SyncData>>
    fun getSkillCategories(): Flow<Result<SkillCategoriesDto>>
    fun getSkillList(): Flow<Result<SkillListDto>>
    fun deleteSkill(id: String): Flow<Result<String>>
    fun getSkillTypes(id: String): Flow<Result<SkillTypesDto>>
    fun saveSkill(saveSkillDto: SaveSkillDto): Flow<Result<String>>
}