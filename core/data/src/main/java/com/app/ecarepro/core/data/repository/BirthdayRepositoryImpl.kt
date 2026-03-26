package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.model.birthday.Birthday
import com.app.ecarepro.core.domain.repository.BirthdayRepository
import com.app.ecarepro.core.network.BirthdayRemoteDataSource
import com.app.ecarepro.core.network.model.birthday.toDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class BirthdayRepositoryImpl @Inject constructor(
    private val dataSource: BirthdayRemoteDataSource,
) : BirthdayRepository {
    override fun getBirthdays(userType: String, rptType: Int, monthNo: Int, date: String?): Flow<Result<List<Birthday>>> =
        asResultFlow {
            dataSource.getBirthdays(userType, rptType, monthNo, date).map { it.toDomainModel() }
        }
}
