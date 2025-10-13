package com.app.ecarepro.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("auth_token")
    val authToken: String?,
    val authenticated: Boolean?,
    @ColumnInfo("class_id")
    val classID: String?,
    @ColumnInfo("class")
    val className: String?,
    @ColumnInfo("error_code")
    val errorCode: Int?,
    @ColumnInfo("is_defaulter")
    val isDefaulter: Boolean?,
    @ColumnInfo("mobile_number")
    val mobileNumber: String?,
    @ColumnInfo("name")
    val name: String?,
    @ColumnInfo("photo_path")
    val photoPath: String?,
    @ColumnInfo("role_name")
    val roleName: String?,
    @ColumnInfo("session_id")
    val sessionID: String?,
    @ColumnInfo("st_name")
    val stName: String?,
    @ColumnInfo("user_id")
    val userID: Int?,
    @ColumnInfo("user_type")
    val userType: Int?,
    @ColumnInfo("school_code")
    val schoolCode: String,
    @ColumnInfo("updated_on")
    val updatedOn: String,
)

