package com.app.ecarepro.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import com.app.ecarepro.data.database.model.SchoolEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Update
import com.app.ecarepro.data.database.model.LocalizationEntity

@Dao
interface LocalizationDao {
    @Query("SELECT * FROM localizations")
    fun getAllLocalizations(): Flow<List<LocalizationEntity>>

    @Query("SELECT * FROM localizations WHERE `key` = :key")
    suspend fun getLocalizationByKey(key: String): LocalizationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(localizations: List<LocalizationEntity>)

    @Query("DELETE FROM localizations")
    suspend fun deleteAll()

    @Transaction
    suspend fun refreshLocalizations(localizations: List<LocalizationEntity>) {
        deleteAll()
        insertAll(localizations)
    }
}

