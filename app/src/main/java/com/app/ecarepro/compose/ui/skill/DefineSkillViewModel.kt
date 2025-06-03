package com.app.ecarepro.compose.ui.skill

import androidx.lifecycle.viewModelScope
import com.app.ecarepro.compose.model.UiState
import com.app.ecarepro.compose.ui.base.BaseViewModel
import com.app.ecarepro.data.network.SaveSkillDto
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.Skill
import com.app.ecarepro.data.network.model.SkillType
import com.app.ecarepro.data.network.model.SkillTypesDto
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val selectedCategoryId = MutableStateFlow<String?>(null) // null = show all

    private val refreshData = MutableStateFlow(false)

    private val _skillTypes = MutableStateFlow<List<SkillType>>(emptyList())
    val skillTypes: StateFlow<List<SkillType>> = _skillTypes.asStateFlow()


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

    fun onCategorySelected(categoryId: String) {
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

    fun deleteSkill(id: String) {
        viewModelScope.launch {
            appRepository
                .deleteSkill(id)
                .handleResultWithLoadState()
                .collectLatest { result ->
                    result
                        .onSuccess { message ->
                            showMessage(message)
                            refresh()
                        }
                        .onFailure { error ->
                            showError(error)
                        }
                }
        }
    }

    fun loadSkillTypes(
        sklCatID: String,
    ) {
        viewModelScope.launch {
            appRepository
                .getSkillTypes(sklCatID)
                .handleResultWithLoadState()
                .collect { result ->
                    result
                        .onSuccess { response: SkillTypesDto ->
                            _skillTypes.update { response.types ?: emptyList() }
                            showMessage("Success")
                        }
                        .onFailure { error ->
                            showError(error)
                        }
                }
        }
    }

    fun saveSkill(
        id: String? = null,
        skill: String,
        sklCatID: String,
        sklTypeID: String,
    ) {
        viewModelScope.launch {
            appRepository.saveSkill(
                SaveSkillDto(
                    id = id,
                    skill = skill,
                    sklCatID = sklCatID,
                    sklTypeID = sklTypeID
                )
            )
                .handleResultWithLoadState()
                .collect { result ->
                    result
                        .onSuccess { message ->
                            showMessage(message)
                            refresh()
                        }
                        .onFailure { error ->
                            showError(error)
                        }
                }
        }
    }

    fun refresh() {
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