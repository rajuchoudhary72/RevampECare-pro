package com.app.ecarepro.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.ecarepro.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String
)

fun UserEntity.asExternalModel(): User {
    return User(
        id = id,
        name = name
    )
}
