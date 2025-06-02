package com.app.ecarepro.compose.ui.importfromdb

import com.app.ecarepro.compose.model.UiState
import com.app.ecarepro.compose.ui.base.BaseViewModel
import com.app.ecarepro.data.network.model.MasterCategory
import com.app.ecarepro.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ImportSkillFromDatabaseViewModel @Inject constructor(
    private val appRepository: AppRepository
) : BaseViewModel() {
    val searchQuery = MutableStateFlow("")
    private val _expandedCategoryIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _selectedSkillIds = MutableStateFlow<Set<Int>>(emptySet())


    val uiState = MutableStateFlow<UiState<ImportSkillsUiState>>(UiState.Loading)


    fun onSearchQueryChange(query: String) {
        searchQuery.update {
            query
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

    fun getSelectedSkillNames(categories:List<MasterCategory>, selectedSkillIds: Set<Int>): List<String> {
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

}

data class ImportSkillsUiState(
    val categories: List<MasterCategory> = emptyList(),
    val expandedCategoryIds: Set<Int> = emptySet(),
    val selectedSkillIds: Set<Int> = emptySet(),
    val selectedSkillNames: List<String> = emptyList(),
)
