package com.app.ecarepro.data.repository

import com.app.ecarepro.data.network.SaveSkillDto
import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.BadgeCountResponse
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Notification
import com.app.ecarepro.data.network.model.RegisterDevice
import kotlinx.coroutines.flow.Flow
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.FavouritesUpdateDto
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.MasterCategory
import com.app.ecarepro.data.network.model.PedagogyDto
import com.app.ecarepro.data.network.model.SkillCategoriesDto
import com.app.ecarepro.data.network.model.SkillListDto
import com.app.ecarepro.data.network.model.SkillTypesDto
import com.app.ecarepro.data.network.model.SkillsFromMasterDto
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
    suspend fun getNotificationCount(): BadgeCountResponse
    fun getSkillCategories(): Flow<Result<SkillCategoriesDto>>
    fun getSkillList(): Flow<Result<SkillListDto>>
    fun getSkillFromMaster(): Flow<Result<SkillsFromMasterDto>>
    fun importSkills(categories: List<MasterCategory>): Flow<Result<String>>
    fun deleteSkill(id: String): Flow<Result<String>>
    fun getSkillTypes(id: String): Flow<Result<SkillTypesDto>>
    fun saveSkill(saveSkillDto: SaveSkillDto): Flow<Result<String>>
    fun deleteSkillCategory(sklCatID: String): Flow<Result<String>>
    fun saveSkillCategory(sklCatID: String? = null, value: String): Flow<Result<String>>
    fun saveSkillType(
        sklCatID: String,
        sklTypeID: String? = null,
        value: String
    ): Flow<Result<String>>

     fun getPedagogy(): Flow<Result<PedagogyDto>>

    fun deletePedagogy(id: Int): Flow<Result<String>>

    fun deletePedagogyStep(id:Int): Flow<Result<String>>

}