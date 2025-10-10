package com.app.ecarepro.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.ecarepro.core.database.dao.SchoolDao
import com.app.ecarepro.core.database.model.SchoolEntity

@Database(
    entities = [
        SchoolEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class EcareProDatabase: RoomDatabase() {
    abstract fun schoolDao(): SchoolDao

}