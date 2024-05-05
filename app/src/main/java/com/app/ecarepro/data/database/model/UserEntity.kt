package com.app.ecarepro.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.ecarepro.data.network.model.NetworkUserDetailsDto

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "photo") val photo: String?,
    @ColumnInfo(name = "userType") val userType: Int,
    @ColumnInfo(name = "auth_token") val authToken: String?,
    @ColumnInfo(name = "roleName") val roleName: String?,
    @ColumnInfo(name = "is_user_authenticated") val isUserAuthenticated: Boolean?,
    @ColumnInfo(name = "is_verified") val isVerified: Boolean?,
)

fun UserEntity.asNetworkUserDetailsDto(): NetworkUserDetailsDto {
    return NetworkUserDetailsDto(
        userId = userId,
        name = name,
        photo = photo,
        userType = userType,
        authToken = authToken,
        isUserAuthenticated = isUserAuthenticated,
        isVerified = isVerified,
        roleName = roleName
    )
}
