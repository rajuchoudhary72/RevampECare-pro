package com.app.ecarepro.data.database

import com.app.ecarepro.data.database.dao.UserDao
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.database.model.UserEntity
import javax.inject.Inject

class UserDatabaseImpl @Inject constructor(
    private val userDao: UserDao
) : UserDatabase {
    override suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }
}