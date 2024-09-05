package com.app.ecarepro.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.ecarepro.data.database.dao.SchoolDao
import com.app.ecarepro.data.database.dao.UserDao
import com.app.ecarepro.data.database.model.SchoolEntity
import com.app.ecarepro.data.database.model.UserEntity

@Database(
    entities = [UserEntity::class, SchoolEntity::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
    ]
)
abstract class ECareProDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun schoolDao(): SchoolDao
}