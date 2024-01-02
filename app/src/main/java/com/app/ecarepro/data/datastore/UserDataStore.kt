package com.app.ecarepro.data.datastore

import com.app.ecarepro.model.Slide
import com.app.ecarepro.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataStore {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User

    suspend fun getAuthToken(): String?

    suspend fun saveSlides(sliders: List<Slide>)

    fun getSlides(): Flow<List<Slide>>

}