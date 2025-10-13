package com.app.ecarepro.core.domain.usecase

import com.app.ecarepro.core.domain.R
import com.app.ecarepro.core.domain.model.OnboardingItem
import com.app.ecarepro.core.domain.repository.SchoolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetOnboardingItemUseCase @Inject constructor(
    private val schoolRepository: SchoolRepository
) {
    private val drawableResIds = listOf(
        R.drawable.onboarding_1,
        R.drawable.onboarding_2,
        R.drawable.onboarding_3,
        R.drawable.onboarding_4
    )

    private fun getImageResIdForItem(index: Int): Int {
        return drawableResIds[index % drawableResIds.size]
    }

    operator fun invoke(): Flow<Result<List<OnboardingItem>>> {
        return flow {
            val onboardingItems = schoolRepository.getOnboardingData()
                .mapIndexed { index, item ->
                    item.copy(imageRes = getImageResIdForItem(index))
                }
            emit(
                Result.success(onboardingItems)
            )
        }
            .catch { e ->
                emit(Result.failure(e))
            }
    }
}


