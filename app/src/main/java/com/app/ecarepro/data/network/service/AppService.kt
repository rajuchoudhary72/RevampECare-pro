package com.app.ecarepro.data.network.service

import com.app.ecarepro.data.network.SaveSkillDto
import com.app.ecarepro.data.network.SaveSkillResponse
import com.app.ecarepro.data.network.model.AppLayoutDto
import com.app.ecarepro.data.network.model.CommonResponse
import com.app.ecarepro.data.network.model.Favourites
import com.app.ecarepro.data.network.model.FavouritesDto
import com.app.ecarepro.data.network.model.LMSAppLayoutDto
import com.app.ecarepro.data.network.model.NotificationsDto
import com.app.ecarepro.data.network.model.RegisterDevice
import com.app.ecarepro.data.network.model.SkillCategoriesDto
import com.app.ecarepro.data.network.model.SkillListDto
import com.app.ecarepro.data.network.model.SkillTypesDto
import com.app.ecarepro.data.network.model.SyncDataDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface AppService {
    @GET("App/Layout")
    suspend fun getAppLayout(
        @Query("Device") device: Int = 1,
    ): AppLayoutDto

    @GET
    suspend fun getLMSAppLayout(
        @Url url: String,
        @Query("Platform") platform: Int = 1,
    ): LMSAppLayoutDto

    @GET("App/Notifications")
    suspend fun getNotifications(): NotificationsDto

    @POST("App/RegisterDevice")
    suspend fun registerFirebaseToken(
        @Body registerDevice: RegisterDevice
    ): CommonResponse

    @GET("App/FavoriteMenus")
    suspend fun getFavourites(
        @Query("Device") device: Int = 1,
    ): FavouritesDto

    @POST("App/UpdateFavoriteMenus")
    suspend fun updateFavourites(
        @Body request: List<Favourites>
    ): CommonResponse

    @GET("App/NotificationSeen")
    suspend fun notificationSeen(
        @Query("ID") id: String
    ): CommonResponse

    @GET("App/Sync")
    suspend fun syncData(): SyncDataDto

    @GET
    suspend fun getSkillCategories(
        @Url url: String
    ): SkillCategoriesDto

    @GET
    suspend fun getSkillList(
        @Url url: String
    ): SkillListDto

    @DELETE
    suspend fun deleteSkill(
        @Url url: String,
        @Query("ID") id: String
    ): SkillListDto

    @GET
    suspend fun getSkillTypes(
        @Url url: String,
        @Query("SklCatID") id: String
    ): SkillTypesDto

    @POST
    suspend fun saveSkill(
        @Url url: String,
        @Body request: SaveSkillDto
    ): SaveSkillResponse
}