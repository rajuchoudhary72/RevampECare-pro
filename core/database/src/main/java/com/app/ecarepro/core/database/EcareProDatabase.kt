package com.app.ecarepro.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.ecarepro.core.database.dao.SchoolDao
import com.app.ecarepro.core.database.dao.UserDao
import com.app.ecarepro.core.database.model.SchoolEntity
import com.app.ecarepro.core.database.model.UserEntity

@Database(
    entities = [
        SchoolEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class EcareProDatabase: RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
    abstract fun userDao(): UserDao

}