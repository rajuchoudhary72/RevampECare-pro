package com.app.ecarepro.ui.define_skill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.Skill
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {

    val searchViewVisibility = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")
    private val selectedCategoryId = MutableStateFlow<Int?>(null) // null = show all

    private val refreshData = MutableStateFlow(false)

    private val rawData = refreshData.flatMapLatest {
        combine(
            appRepository.getSkillCategories(),
            appRepository.getSkillList()
        ) { categories, skills ->
            if (categories.isSuccess && skills.isSuccess) {
                DefineSkillUiState.Success(
                    skills = skills.getOrNull()?.skillList ?: emptyList(),
                    skillCategory = categories.getOrNull()?.categories ?: emptyList()
                )
            } else {
                DefineSkillUiState.Error(Exception(UNKNOWN_ERROR_MESSAGE))
            }
        }
    }

    val uiState: StateFlow<DefineSkillUiState> = combine(
        rawData,
        searchQuery,
        selectedCategoryId
    ) { state, query, categoryId ->
        when (state) {
            is DefineSkillUiState.Success -> {
                val filtered = state.skills
                    .filter {
                        (categoryId == null || it.sklCatID == categoryId) &&
                                (query.isBlank() || it.skill?.contains(
                                    query,
                                    ignoreCase = true
                                ) ?: false)
                    }

                state.copy(
                    skills = filtered
                )
            }

            else -> state
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = DefineSkillUiState.Loading,
        started = SharingStarted.WhileSubscribed(5000L)
    )

    fun onCategorySelected(categoryId: Int) {
        selectedCategoryId.value = categoryId
    }

    fun clearSearchQuery() {
        if (searchQuery.value.isEmpty()) {
            toggleSearchView()
        } else {
            searchQuery.value = ""
        }
    }

    fun toggleSearchView() {
        searchViewVisibility.value = !searchViewVisibility.value
    }

    fun deleteSkill(skill: Skill, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            appRepository
                .deleteSkill(skill.id)
                .collectLatest { result ->
                    onSuccess(
                        result.getOrNull() ?: result.exceptionOrNull()?.message
                        ?: UNKNOWN_ERROR_MESSAGE
                    )
                    if (result.isSuccess) {
                        refreshData.update { it.not() }
                    }
                }
        }
    }
}

sealed interface DefineSkillUiState {
    object Loading : DefineSkillUiState

    data class Success(
        val skills: List<Skill>,
        val skillCategory: List<Category>,
    ) : DefineSkillUiState

    data class Error(
        val error: Throwable,
    ) : DefineSkillUiState

    fun isLoading() = this == Loading
    fun getErrorOrNull() = if (this is Error) this.error else null
    fun getValueOrNull() = if (this is Success) this else null
}