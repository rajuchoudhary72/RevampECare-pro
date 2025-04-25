package com.app.ecarepro.ui.define_skill

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.data.network.SaveSkillDto
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.Skill
import com.app.ecarepro.data.network.model.SkillType
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.define_skill.CreateSkillBottomSheetFragment.Companion.SKILL
import com.app.ecarepro.ui.define_skill.CreateSkillBottomSheetFragment.Companion.SKILL_CATEGORIES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSkillViewModel @Inject constructor(
    private val appRepository: AppRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val skill = savedStateHandle.get<Skill?>(SKILL)
    val skillTypes = MutableStateFlow<List<SkillType>>(emptyList())
    val categories = savedStateHandle.get<List<Category>>(SKILL_CATEGORIES)

    fun loadSkillTypes(id: Int, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            appRepository.getSkillTypes(id.toString()).collect { result ->
                result.onSuccess { response ->
                    skillTypes.value = response.types ?: emptyList()
                    onResult.invoke(null)
                }
                    .onFailure { error ->
                        onResult.invoke(error.message ?: "Unknown error")
                    }
            }
        }
    }

    fun saveSkill(
        category: String,
        type: String,
        skillName: String,
        result: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            appRepository.saveSkill(
                SaveSkillDto(
                    id = skill?.id,
                    skill = skillName,
                    sklCatID = categories?.find { it.category == category }?.sklCatID,
                    sklTypeID = skillTypes.value.find { it.type == type }?.sklTypeID
                )
            ).collect { result ->
                result
                    .onSuccess { response ->
                        result(true, response)
                    }
                    .onFailure { error ->
                        result(false, error.message ?: "Unknown error")
                    }
            }
        }
    }
}