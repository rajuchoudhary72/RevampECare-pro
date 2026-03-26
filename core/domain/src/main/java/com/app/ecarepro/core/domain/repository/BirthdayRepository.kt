package com.app.ecarepro.core.domain.repository

import com.app.ecarepro.core.domain.model.birthday.Birthday
import kotlinx.coroutines.flow.Flow

interface BirthdayRepository {
    fun getBirthdays(userType: String, rptType: Int, monthNo: Int, date: String?): Flow<Result<List<Birthday>>>
}
