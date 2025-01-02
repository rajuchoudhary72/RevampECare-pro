package com.app.ecarepro.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "photo") val photo: String?,
    @ColumnInfo(name = "userType") val userType: Int,
    @ColumnInfo(name = "auth_token") val authToken: String?,
    @ColumnInfo(name = "session_id", defaultValue = "") val sessionId: String?,
    @ColumnInfo(name = "roleName") val roleName: String?,
    @ColumnInfo(name = "schoolCode") val schoolCode: String?,
    @ColumnInfo(name = "is_user_authenticated") val isUserAuthenticated: Boolean?,
    @ColumnInfo(name = "is_verified") val isVerified: Boolean?,
    @ColumnInfo(name = "mobileNumber") val mobileNumber: String?,
    @ColumnInfo(name = "classID") val classID: String?,
    @ColumnInfo(name = "loginTime") val loginTime: String?,
    @ColumnInfo(name = "stName") val stName: String?,
    @ColumnInfo(name = "class") val className: String?,
)

fun UserEntity.asNetworkUserDetailsDto(): NetworkUserDetailsDto {
    return NetworkUserDetailsDto(
        id = id,
        userId = userId,
        name = name,
        photo = photo,
        userType = userType,
        authToken = authToken,
        isUserAuthenticated = isUserAuthenticated,
        isVerified = isVerified,
        roleName = roleName,
        schoolCode = schoolCode,
        mobileNumber = mobileNumber,
        classID = classID,
        loginTime = loginTime,
        className = className,
        stName = stName,
        sessionID = sessionId
    )
}
