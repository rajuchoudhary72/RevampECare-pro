package com.app.ecarepro.data.database

import com.app.ecarepro.data.database.dao.UserDao
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.database.model.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDatabaseImpl @Inject constructor(
    private val userDao: UserDao
) : UserDatabase {
    override suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun getUser(userId: Int): UserEntity {
        return userDao.getUser(userId)
    }

    override fun getUserFlow(userId: Int): Flow<UserEntity> {
        return userDao.getUserFlow(userId)
    }

    override fun getUsersFlow(): Flow<List<UserEntity>> {
        return userDao.getUsersFlow()
    }

    override suspend fun deleteUser(userEntity: UserEntity) {
        return userDao.deleteUser(userEntity)
    }
}