package com.app.ecarepro.ui.widget

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

}