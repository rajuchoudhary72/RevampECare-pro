package com.app.ecarepro.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localizations")
data class LocalizationEntity(
    @PrimaryKey
    val key: String,
    val englishValue: String,
    val hindiValue: String,
    val lastUpdated: Long = System.currentTimeMillis()
)