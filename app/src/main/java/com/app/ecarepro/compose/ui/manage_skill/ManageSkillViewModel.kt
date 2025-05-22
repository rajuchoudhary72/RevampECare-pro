package com.app.ecarepro.compose.ui.manage_skill

import androidx.lifecycle.viewModelScope
import com.app.ecarepro.compose.model.UiState
import com.app.ecarepro.compose.ui.base.BaseViewModel
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ManageSkillViewModel @Inject constructor(
    private val appRepository: AppRepository
) : BaseViewModel() {

    val searchQuery = MutableStateFlow("")
    private val refreshData = MutableStateFlow(false)


    private val rawData: Flow<UiState<ManageSkillSuccessData>> =
        refreshData
            .flatMapLatest {
                appRepository.getSkillCategories()
            }.map { categories ->
                if (categories.isSuccess) {
                    UiState.Success(
                        ManageSkillSuccessData(
                            skillCategory = categories.getOrNull()?.categories ?: emptyList()
                        )
                    )
                } else {
                    UiState.Error(Exception(UNKNOWN_ERROR_MESSAGE))
                }
            }

    val uiState: StateFlow<UiState<ManageSkillSuccessData>> = combine(
        rawData,
        searchQuery
    ) { state, query ->
        when (state) {
            is UiState.Success -> {

                state.copy(
                    data = state.data.copy(
                        searchQuery = query,
                        skillCategory = state.data.skillCategory.filter { category ->
                            category.category?.contains(
                                query,
                                true
                            ) ?: true
                        }
                    ),
                )
            }
            else -> state
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = UiState.Loading,
        started = SharingStarted.WhileSubscribed(5000L)
    )
}


data class ManageSkillSuccessData(
    val searchQuery: String = "",
    val skillCategory: List<Category>
)