package com.app.ecarepro.data.datastore

import com.app.ecarepro.data.network.model.NetworkSchool
import com.app.ecarepro.model.Slide
import com.app.ecarepro.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataStore {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User
    suspend fun saveSchoolData(school: NetworkSchool)
    suspend fun getSchoolData(): NetworkSchool?

    suspend fun saveAuthToken(token: String)
    suspend fun setAsUserAuthenticated(isAuthenticated: Boolean)
    suspend fun isUserAuthenticated(): Boolean
    suspend fun getAuthToken(): String?

    suspend fun saveSlides(sliders: List<Slide>)

    fun getSlides(): Flow<List<Slide>>

}