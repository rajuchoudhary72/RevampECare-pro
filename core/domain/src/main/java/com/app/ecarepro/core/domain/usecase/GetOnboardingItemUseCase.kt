package com.app.ecarepro.core.domain.usecase

import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.domain.repository.SchoolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetOnboardingItemUseCase @Inject constructor(
    private val schoolRepository: SchoolRepository
) {
    operator fun invoke(): Flow<Result<List<OnboardingItem>>> {
        return flow {
           try {
               val items =  schoolRepository.getOnboardingData()
               emit(Result.success(items))
           } catch (e: Exception) {
              emit(Result.failure(e))
           }
        }
    }
}