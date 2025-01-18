package com.app.ecarepro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.ecarepro.data.database.ECareProDatabase
import com.app.ecarepro.data.database.databases.SchoolDatabase
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.database.model.asNetworkSchool
import com.app.ecarepro.data.database.model.asNetworkUserDetailsDto
import com.app.ecarepro.data.network.model.LoginResponseDto
import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto
import com.app.ecarepro.data.network.model.MessageSettings

import com.app.ecarepro.data.network.model.UserDashboardDto
import com.app.ecarepro.data.network.model.asNetworkSchool
import com.app.ecarepro.data.network.model.asUserEntity
import com.app.ecarepro.model.Feed
import com.app.ecarepro.data.network.model.submit_assignment.UserDTL
import com.app.ecarepro.data.network.model.submit_assignment.asUserEntity
import com.app.ecarepro.model.FeedsDto
import com.app.ecarepro.model.Slide
import com.google.gson.Gson
import com.app.ecarepro.data.network.Setting
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_datastore")

@Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_AGAINST_NOT_NOTHING_EXPECTED_TYPE")
class UserDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDatabase: UserDatabase,
    private val schoolDatabase: SchoolDatabase,
    private val eCareProDatabase: ECareProDatabase,
    private val gson: Gson
) : UserDataStore {

    override suspend fun saveUser(user: NetworkUserDetailsDto) {
        //userDatabase.insertUser(user.asUserEntity())
    }
    override suspend fun saveMessageSettings(messageSettings: MessageSettings) {
        context.dataStore.edit { preferences ->
            preferences[messageSettingsKey] = gson.toJson(messageSettings)
        }
    }
    override  fun getMessageSettings(): Flow<MessageSettings?> {
        return context.dataStore.data.map { preferences ->
            val itemType = object : TypeToken<MessageSettings>() {}.type
            gson.fromJson(preferences[messageSettingsKey], itemType)
        }
    }
    override suspend fun saveUserDetails(user: LoginResponseDto, schoolCode: String, time: String) {
        val id = userDatabase.insertUser(
            user.asUserEntity().copy(schoolCode = schoolCode, loginTime = time)
        )
        val userId = getCurrentUserId()
        if (userId == null || userId == 0)
            setCurrentUserId(id.toInt())

    }
    override suspend fun saveUserDetails(user: UserDTL, schoolCode: String, time: String) {
        val id = userDatabase.insertUser(
            user.asUserEntity().copy(schoolCode = schoolCode, loginTime = time)
        )
        val userId = getCurrentUserId()
        if (userId == null || userId == 0)
            setCurrentUserId(id.toInt())
    }
    override suspend fun getUser(): NetworkUserDetailsDto? {
        val userId = getCurrentUserId()
        if (userId == null || userId == 0) return null
        return userDatabase.getUser(userId)?.asNetworkUserDetailsDto()
    }

    override fun getUsersFlow(): Flow<List<NetworkUserDetailsDto>> {
        return userDatabase
            .getUsersFlow()
            .map {
                it?.map {
                    it.asNetworkUserDetailsDto().copy(
                        school = schoolDatabase.getSchool(it.schoolCode ?: "").asNetworkSchool()
                    )
                }?: emptyList()
            }
    }

    override suspend fun setCurrentUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[currentUserId] = userId
        }
    }
    override suspend fun saveGeneralSettings(settings: List<Setting>) {
        context.dataStore.edit { preferences ->
            preferences[generalSettingsKey] = gson.toJson(settings)
        }
    }

    override suspend fun isGeneralSettingEnabled(key: String): Boolean {
        return context.dataStore.data.map { preferences ->
            val itemType = object : TypeToken<List<Setting>>() {}.type
            gson.fromJson<List<Setting>>(preferences[generalSettingsKey], itemType)
                .firstOrNull { it.settingName == key }?.isEnabled ?: false
        }.first()
    }

    override suspend fun getCurrentUserId(): Int? {
        return context.dataStore.data.map { preferences ->
            preferences[currentUserId]
        }.first() ?: getUsersFlow().first().firstOrNull()?.id
    }

    override fun getCurrentUserIdAsFlow(): Flow<Int?> {
        return context.dataStore.data.map { preferences ->
            preferences[currentUserId]
        }
    }

    override suspend fun setCurrentSchoolCode(schoolCode: String) {
        context.dataStore.edit { preferences ->
            preferences[currentSchoolCode] = schoolCode
        }
    }

    override suspend fun getCurrentSchoolCode(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[currentSchoolCode]
        }.first()
    }

    override fun getCurrentSchoolCodeAsFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[currentSchoolCode]
        }
    }

    override fun getUserAsFlow(): Flow<NetworkUserDetailsDto?> {
        return runBlocking {
            if (getCurrentUserId() == null || getCurrentUserId() == 0) flow {
                emit(
                    null
                )
            }
            else userDatabase.getUserFlow(getCurrentUserId()!!).map { it?.asNetworkUserDetailsDto() }
        }
    }

    override suspend fun saveSchoolData(school: NetworkSchool) {
        if (schoolDatabase.getSchoolData(school.schoolCode) == null)
            schoolDatabase.insertSchool(school.asNetworkSchool())
        /* val schoolCode = getCurrentSchoolCode()
         if (schoolCode.isNullOrEmpty())
             setCurrentSchoolCode(school.schoolCode)*/
        setCurrentSchoolCode(school.schoolCode)
    }


    override suspend fun getSchoolData(): NetworkSchool? {
        val user = getUser()
        if (user == null || user.schoolCode.isNullOrEmpty())
            return null
        return schoolDatabase.getSchool(user.schoolCode).asNetworkSchool()
    }

    override fun getSchoolAsFlow(): Flow<NetworkSchool?> {
        val user = runBlocking { getUser() }
        if (user == null || user.schoolCode.isNullOrEmpty())
            return flowOf(null)
        return schoolDatabase.getSchoolFlow(user.schoolCode).map { it.asNetworkSchool() }
    }

    override suspend fun saveFeeds(feeds: FeedsDto) {
        context.dataStore.edit { preferences ->
            preferences[feedsKey] = gson.toJson(feeds)
        }
    }

    override fun getFeeds(): Flow<List<Feed>> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[feedsKey]
            if (json == null) {
                null
            } else gson.fromJson(json, FeedsDto::class.java)
        }.map { it?.updates ?: emptyList() }
    }

    override suspend fun saveDashboardData(school: UserDashboardDto) {
        context.dataStore.edit { preferences ->
            preferences[dashboardData] = gson.toJson(school)
        }
    }

    override fun getDashboardData(): Flow<UserDashboardDto?> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[dashboardData]
            if (json == null) {
                null
            } else {
                try {
                    gson.fromJson(json, UserDashboardDto::class.java)
                }catch (e:Exception){
                    e.printStackTrace()
                    null
                }
            }
        }
    }


    override suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[authTokenKey] = token
        }
    }

    override suspend fun saveUserType(userType: Int) {
        context.dataStore.edit { preferences ->
            preferences[userTypeKey] = userType
        }
    }



    override suspend fun saveRoleName(roleName: String) {
        context.dataStore.edit { preferences ->
            preferences[roleNameKey] = roleName
        }
    }

    override suspend fun saveUserNameID(userNameId: String) {
        context.dataStore.edit { preferences ->
            preferences[userNameIdKey] = userNameId
        }
    }

    override suspend fun getUserNameID(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[userNameIdKey]
        }.first()
    }

    override suspend fun getUserType(): Int? {
        return context.dataStore.data.map { preferences ->
            preferences[userTypeKey]
        }.first()
    }



    override suspend fun getRoleName(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[roleNameKey]
        }.first()
    }

    override suspend fun setAsUserAuthenticated(isAuthenticated: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isAuthenticatedKey] = isAuthenticated
        }
    }

    override suspend fun isUserAuthenticated(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[isAuthenticatedKey] ?: false
        }.first()
    }
    override suspend fun getCityName(): String {
        return context.dataStore.data.map { preferences ->
            preferences[cityNameKey]
        }.first() ?: Locale.getDefault().displayName
    }
    override suspend fun setCityName(city: String) {
        context.dataStore.edit { preferences ->
            preferences[cityNameKey] = city
        }
    }
    override suspend fun getAuthToken(): String? {
        val userId = getCurrentUserId()
        if (userId == null || userId == 0) return null
        return userDatabase.getUser(userId)?.authToken
    }
    override suspend fun getUserSessionId(): String? {
        val userId = getCurrentUserId()
        if (userId == null || userId == 0) return null
        return userDatabase.getUser(userId)?.sessionId
    }
    override suspend fun saveSessionId(sessionId: String) {
        val userId = getCurrentUserId()
        if (userId == null || userId == 0) return
        userDatabase.insertUser(userDatabase.getUser(userId)!!.copy(sessionId = sessionId))
    }
    override suspend fun saveSlides(sliders: List<Slide>) {

        context.dataStore.edit { preferences ->
            preferences[slidesKey] = gson.toJson(sliders)
        }
    }

    override fun getSlides(): Flow<List<Slide>> {
        return context.dataStore.data.map { preferences ->
            val itemType = object : TypeToken<List<Slide>>() {}.type
            gson.fromJson(preferences[slidesKey], itemType)
        }
    }

    override suspend fun clear() {
        GlobalScope.launch {
            context.dataStore.edit { it.clear() }
            eCareProDatabase.userDao().nukeTable()
            eCareProDatabase.schoolDao().nukeTable()
        }
    }


    companion object {
        private val currentUserId = intPreferencesKey("currentUserId")
        private val currentSchoolCode = stringPreferencesKey("currentSchoolCode")
        //private val schoolDataKey = stringPreferencesKey("schoolData")
        private val feedsKey = stringPreferencesKey("feeds")
        private val dashboardData = stringPreferencesKey("dashboardData")
       // private val userPreferenceKey = stringPreferencesKey("user")
        private val authTokenKey = stringPreferencesKey("auth_token")
        private val slidesKey = stringPreferencesKey("slides")
        private val generalSettingsKey = stringPreferencesKey("generalSettings")
        private val messageSettingsKey = stringPreferencesKey("messageSettings")

        private val roleNameKey = stringPreferencesKey("roleName")
        private val userNameIdKey = stringPreferencesKey("userNameId")
        private val userTypeKey = intPreferencesKey("userType")
      //  private val classIDKey = intPreferencesKey("classID")
        private val isAuthenticatedKey = booleanPreferencesKey("isAuthenticated")
        private val cityNameKey = stringPreferencesKey("cityNameKey")
    }
}