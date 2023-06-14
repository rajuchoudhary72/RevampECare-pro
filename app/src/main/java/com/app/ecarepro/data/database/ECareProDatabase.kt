package com.app.ecarepro.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.ecarepro.data.database.dao.UserDao
import com.app.ecarepro.data.database.model.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class ECareProDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}