package com.app.ecarepro.core.data.mapper

import com.app.ecarepro.core.database.model.UserEntity
import com.app.ecarepro.core.domain.model.LoginResult
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.core.network.model.user.NetworkLoginResponse
import com.app.ecarepro.core.network.model.user.UserDetails
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun UserDetails.asEntity(
    schoolCode: String,
) = UserEntity(
    authToken = authToken,
    id = 0,
    authenticated = authenticated,
    classID = classID,
    className = classX,
    errorCode = errorCode,
    isDefaulter = isDefaulter,
    mobileNumber = mobileNumber,
    name = name,
    photoPath = photoPath,
    roleName = roleName,
    sessionID = sessionID,
    stName = stName,
    userID = userID,
    userType = userType,
    schoolCode = schoolCode,
    updatedOn = Clock.System.now().toString()
)


fun UserEntity.toDomainModel() = User(
    authToken = authToken,
    classID = classID,
    className = className,
    isDefaulter = isDefaulter,
    message = null,
    mobileNumber = mobileNumber,
    name = name,
    photoPath = photoPath,
    roleName = roleName,
    sessionID = sessionID,
    stName = stName,
    status = null,
    userID = userID ?: 0,
    userType = userType,
    schoolCode = schoolCode,
    authenticated = authenticated
)


fun NetworkLoginResponse.toLoginResult() = LoginResult(
    authenticated = authenticated,
    errorCode = errorCode,
    isDefaulter = isDefaulter,
    isOTPEnabled = isOTPEnabled,
    isOTPValidated = isOTPValidated,
    message = message,
    otpAuthKey = otpAuthKey,
    otpMode = otpMode,
    remainAttempts = remainAttempts,
    resendWaitSeconds = resendWaitSeconds,
    schCode = schCode,
    status = status,
    userDetail = null

)