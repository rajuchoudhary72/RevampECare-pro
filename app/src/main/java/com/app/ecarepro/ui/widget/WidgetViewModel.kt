package com.app.ecarepro.ui.widget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val card = savedStateHandle.getStateFlow("cards", initialValue = emptyList<Card>())
}