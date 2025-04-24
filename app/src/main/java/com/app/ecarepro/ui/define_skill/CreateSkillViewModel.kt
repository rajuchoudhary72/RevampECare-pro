package com.app.ecarepro.ui.define_skill

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.Category
import com.app.ecarepro.data.network.model.SkillType
import com.app.ecarepro.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateSkillViewModel @Inject constructor(
    private val appRepository: AppRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val skillTypes = MutableStateFlow<List<SkillType>>(emptyList())
    val categories = savedStateHandle.get<List<Category>>("skill_categories")

    fun loadSkillTypes(id: Int) {

    }
}