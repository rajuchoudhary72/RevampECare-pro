package com.app.ecarepro.compose.ui.skill

import androidx.lifecycle.viewModelScope
import com.app.ecarepro.compose.model.UiState
import com.app.ecarepro.compose.ui.base.BaseViewModel
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.Skill
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefineSkillViewModel @Inject constructor(
    private val appRepository: AppRepository
) : BaseViewModel() {

    val searchViewActive = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")
    private val selectedCategoryId = MutableStateFlow<Int?>(null) // null = show all

    private val refreshData = MutableStateFlow(false)

    private val rawData: Flow<UiState<DefineSkillSuccessData>> = refreshData.flatMapLatest {
        combine(
            appRepository.getSkillCategories(),
            appRepository.getSkillList()
        ) { categories, skills ->
            if (categories.isSuccess && skills.isSuccess) {
                UiState.Success(
                    DefineSkillSuccessData(
                        skills = skills.getOrNull()?.skillList ?: emptyList(),
                        skillCategory = categories.getOrNull()?.categories ?: emptyList()
                    )
                )
            } else {
                UiState.Error(Exception(UNKNOWN_ERROR_MESSAGE))
            }
        }
    }

    val uiState: StateFlow<UiState<DefineSkillSuccessData>> = combine(
        rawData,
        searchQuery,
        selectedCategoryId
    ) { state, query, categoryId ->
        when (state) {
            is UiState.Success -> {
                val filtered = state.data.skills
                    .filter {
                        (categoryId == null || it.sklCatID == categoryId) &&
                                (query.isBlank() || it.skill?.contains(
                                    query,
                                    ignoreCase = true
                                ) ?: false)
                    }

                state.copy(
                    data = state.data.copy(
                        skills = filtered,
                        selectedSkillCategory = state.data.skillCategory.firstOrNull { it.sklCatID == categoryId },
                        searchQuery = query
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

    fun onCategorySelected(categoryId: Int) {
        selectedCategoryId.value = categoryId
    }

    fun setSearchViewActiveState(isActive: Boolean) {
        viewModelScope.launch {
            searchViewActive.update { isActive }
        }
    }

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            searchQuery.update { query }
        }
    }

    fun deleteSkill(skill: Skill) {
        viewModelScope.launch {
            appRepository
                .deleteSkill(skill.id)
                .handleResultWithLoadState()
                .collectLatest { result ->
                    result
                        .onSuccess { message ->
                            showMessage(message)
                            retry()
                        }
                        .onFailure { error ->
                            showError(error)
                        }
                }
        }
    }

    fun retry() {
        viewModelScope.launch {
            refreshData.update { it.not() }
        }
    }
}

data class DefineSkillSuccessData(
    val skills: List<Skill>,
    val skillCategory: List<Category>,
    val selectedSkillCategory: Category? = null,
    val searchQuery: String = ""
)