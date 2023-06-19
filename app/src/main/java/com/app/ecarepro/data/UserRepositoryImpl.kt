package com.app.ecarepro.data

import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.database.model.asExternalModel
import com.app.ecarepro.data.datastore.UserDataStore
import com.app.ecarepro.data.network.model.NetworkUser
import com.app.ecarepro.data.network.model.asEntity
import com.app.ecarepro.data.network.service.UserService
import com.app.ecarepro.data.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDatabase: UserDatabase,
    private val userService: UserService,
    private val userDataStore: UserDataStore,
) : UserRepository {
    override suspend fun insertUser(user: NetworkUser) {
        userDatabase.insertUser(user = user.asEntity())
        userDataStore.saveUser(user.asEntity().asExternalModel())
        // userService.getUser()
    }

}