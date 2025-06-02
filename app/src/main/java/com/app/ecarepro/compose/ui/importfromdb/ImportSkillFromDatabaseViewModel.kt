package com.app.ecarepro.compose.ui.importfromdb

import androidx.lifecycle.viewModelScope
import com.app.ecarepro.compose.model.UiState
import com.app.ecarepro.compose.ui.base.BaseViewModel
import com.app.ecarepro.data.network.model.MasterCategory
import com.app.ecarepro.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportSkillFromDatabaseViewModel @Inject constructor(
    private val appRepository: AppRepository
) : BaseViewModel() {
    val searchQuery = MutableStateFlow("")
    private val refreshData = MutableStateFlow(false)
    private val _expandedCategoryIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _selectedSkillIds = MutableStateFlow<Set<Int>>(emptySet())


    val uiState: StateFlow<UiState<ImportSkillsUiState>> = combine(
        appRepository.getSkillFromMaster(),
        searchQuery,
        _selectedSkillIds,
        _expandedCategoryIds
    ) { skillsFromMaster, query, selectedSkillIds, expandedCategoryIds ->
        if (skillsFromMaster.isSuccess) {
            UiState.Success(
                ImportSkillsUiState(
                    categories = skillsFromMaster.getOrNull()?.categories?.filter { category ->
                        category.category?.contains(
                            query,
                            true
                        ) ?: true
                    } ?: emptyList(),
                    expandedCategoryIds = expandedCategoryIds,
                    selectedSkillIds = selectedSkillIds,
                    selectedSkillNames = getSelectedSkillNames(
                        skillsFromMaster.getOrNull()?.categories ?: emptyList(), selectedSkillIds
                    )
                )
            )
        } else {
            UiState.Error(skillsFromMaster.exceptionOrNull() ?: Exception("Error"))
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = UiState.Loading,
        started = SharingStarted.WhileSubscribed(5000L)
    )


    fun onSearchQueryChange(query: String) {
        searchQuery.update {
            query
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshData.update { it.not() }
        }
    }

    fun onCategoryClicked(categoryId: Int?) {
        categoryId ?: return

        _expandedCategoryIds.update { currentState ->
            val newExpandedIds = currentState.toMutableSet()
            if (newExpandedIds.contains(categoryId)) {
                newExpandedIds.remove(categoryId)
            } else {
                newExpandedIds.add(categoryId)
            }
            newExpandedIds
        }
    }

    fun onSkillSelectionChanged(skillId: Int?, isSelected: Boolean) {
        skillId ?: return

        _selectedSkillIds.update { currentState ->
            val newSelectedSkillIds = currentState.toMutableSet()
            if (isSelected) {
                newSelectedSkillIds.add(skillId)
            } else {
                newSelectedSkillIds.remove(skillId)
            }
            newSelectedSkillIds
        }
    }

    fun getSelectedSkillNames(
        categories: List<MasterCategory>,
        selectedSkillIds: Set<Int>
    ): List<String> {
        val names = mutableListOf<String>()
        categories.forEach { category ->
            category.types?.forEach { type ->
                type.skills?.forEach { skill ->
                    if (skill.sklID != null && selectedSkillIds.contains(skill.sklID)) {
                        skill.skill?.let { names.add(it) }
                    }
                }
            }
        }
        return names
    }

    fun importSkills(
        onSuccess:() -> Unit
    ) {
        viewModelScope.launch {
            val selectedSkillsIds = _selectedSkillIds.value
            val allCategories = (uiState.value as? UiState.Success)?.data?.categories ?: emptyList()

            val categoriesWithSelectedSkills: List<MasterCategory> =
                getSelectedSkillsByType(allCategories, selectedSkillsIds)

            appRepository
                .importSkills(categoriesWithSelectedSkills)
                .handleResultWithLoadState()
                .collectLatest { result ->
                    result
                        .onSuccess { message ->
                            showMessage(message)
                            onSuccess()
                        }
                        .onFailure { error ->
                            showError(error)
                        }
                }
        }
    }

    fun getSelectedSkillsByType(
        categories: List<MasterCategory>,
        selectedSkillIds: Set<Int>
    ): List<MasterCategory> {
        return categories.mapNotNull { category ->
            val typesWithSelectedSkills = category.types?.mapNotNull { type ->
                val selectedSkillsInType = type.skills?.filter { skill ->
                    skill.sklID != null && selectedSkillIds.contains(skill.sklID)
                }
                if (selectedSkillsInType?.isNotEmpty() == true) {
                    type.copy(skills = selectedSkillsInType)
                } else {
                    null
                }
            }
            if (typesWithSelectedSkills?.isNotEmpty() == true) {
                category.copy(types = typesWithSelectedSkills)
            } else {
                null
            }
        }
    }

}

data class ImportSkillsUiState(
    val categories: List<MasterCategory> = emptyList(),
    val expandedCategoryIds: Set<Int> = emptySet(),
    val selectedSkillIds: Set<Int> = emptySet(),
    val selectedSkillNames: List<String> = emptyList(),
)
