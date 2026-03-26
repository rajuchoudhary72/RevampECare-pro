package com.app.ecarepro.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.core.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenTypeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _homeScreenType = MutableStateFlow(HomeScreenType.DASHBOARD)
    val homeScreenType = _homeScreenType.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _homeScreenType.value = userRepository.getHomeScreenType()
        }
    }
}
