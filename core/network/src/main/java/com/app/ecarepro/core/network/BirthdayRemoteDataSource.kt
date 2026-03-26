package com.app.ecarepro.core.network

import com.app.ecarepro.core.network.model.birthday.NetworkUserBirthday

interface BirthdayRemoteDataSource {
    suspend fun getBirthdays(userType: String, rptType: Int, monthNo: Int, date: String?): List<NetworkUserBirthday>
}
