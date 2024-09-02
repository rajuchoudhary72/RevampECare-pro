package com.app.ecarepro.data.database

import com.app.ecarepro.data.database.dao.SchoolDao
import com.app.ecarepro.data.database.dao.UserDao
import com.app.ecarepro.data.database.databases.SchoolDatabase
import com.app.ecarepro.data.database.databases.UserDatabase
import com.app.ecarepro.data.database.model.SchoolEntity
import com.app.ecarepro.data.database.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDatabaseImpl @Inject constructor(
    private val userDao: UserDao,
    private val schoolDao: SchoolDao
) : UserDatabase, SchoolDatabase {
    override suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun getUser(userId: Int): UserEntity? {
        val users = getUsersFlow().first()
        return users.firstOrNull { it.userId == userId } ?: users.firstOrNull()
    }

    override fun getUserFlow(userId: Int): Flow<UserEntity?> {
        return getUsersFlow().map { users ->
            users.firstOrNull { it.userId == userId } ?: users.firstOrNull()
        }
    }

    override fun getUsersFlow(): Flow<List<UserEntity>> {
        return userDao.getUsersFlow()
    }

    override suspend fun deleteUser(userEntity: UserEntity) {
        return userDao.deleteUser(userEntity)
    }

    override suspend fun deleteUser(userId: Int) {
        return userDao.deleteUser(userId)
    }

    override suspend fun insertSchool(school: SchoolEntity) {
        schoolDao.insertSchool(school)
    }

    override suspend fun getSchool(schoolCode: String): SchoolEntity {
        return schoolDao.getSchool(schoolCode)
    }

    override suspend fun getSchoolData(schoolCode: String): SchoolEntity? {
        return schoolDao.getSchoolData(schoolCode)
    }

    override fun getSchoolFlow(schoolCode: String): Flow<SchoolEntity> {
        return schoolDao.getSchoolFlow(schoolCode)
    }

    override fun getSchoolsFlow(): Flow<List<SchoolEntity>> {
        return schoolDao.getSchoolsFlow()
    }

    override suspend fun deleteSchool(schoolEntity: SchoolEntity) {
        schoolDao.deleteSchool(schoolEntity)
    }
}