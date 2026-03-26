package com.app.ecarepro.core.domain.usecase

import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for retrieving the active school associated with the currently logged-in user.
 */
class GetActiveUserSchoolUseCase @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<SchoolDetail> {
        return flow {
            val activeUser = userRepository.getActiveUser()
            if (activeUser != null) {
                emitAll(schoolRepository.getSchoolDetail(activeUser.schoolCode))
            } else {
                throw IllegalStateException("No active user found")
            }
        }
    }

}