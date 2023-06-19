package com.app.ecarepro.data.datastore

import com.app.ecarepro.model.User

interface UserDataStore {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User

}