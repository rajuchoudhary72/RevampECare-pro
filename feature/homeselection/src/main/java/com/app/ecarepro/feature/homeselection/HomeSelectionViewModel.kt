package com.app.ecarepro.feature.homeselection

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.HomeScreenType
import com.app.ecarepro.core.domain.model.HomeSelection
import com.app.ecarepro.core.domain.model.SchoolDetail
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.domain.usecase.GetActiveUserSchoolUseCase
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeSelectionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getActiveUserSchoolUseCase: GetActiveUserSchoolUseCase,
) : BaseViewModel<HomeSelectionIntent, HomeSelectionEvent>() {
    private val _uiState = MutableStateFlow(HomeSelectionUiState())
    val uiState: StateFlow<HomeSelectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getActiveUserSchoolUseCase().collect { schoolDetail ->
                _uiState.update {
                    it.copy(
                        schoolDetails = schoolDetail,
                        selectedHomeScreenType = userRepository.getHomeScreenType()
                    )
                }
            }
        }
    }

    override fun handleIntent(intent: HomeSelectionIntent) {
        when (intent) {
            is HomeSelectionIntent.OnOptionSelected -> onOptionSelected(intent.homeScreenType)
            HomeSelectionIntent.OnSetAsHomeClicked -> onSetAsHomeClicked()
        }
    }

    fun onOptionSelected(homeScreenType: HomeScreenType) {
        _uiState.update { it.copy(selectedHomeScreenType = homeScreenType) }
    }

    // Logic for what happens when the "Set as Home" button is clicked
    fun onSetAsHomeClicked() {
        viewModelScope.launch {
            val selectedHomeScreenType = _uiState.value.selectedHomeScreenType
            userRepository.saveHomeScreenType(selectedHomeScreenType)
            sendEvent(HomeSelectionEvent.OnCompletion(selectedHomeScreenType))
        }
    }
}


// UI State for the screen
@Immutable
data class HomeSelectionUiState(
    val schoolDetails: SchoolDetail? = null,
    val options: List<HomeSelection> = listOf(
        HomeSelection(
            type = HomeScreenType.DASHBOARD,
            title = "Dashboard view",
            description = "Get a quick overview of everything important — best for tracking and managing at a glance.",
            // Replace with your actual drawable resource
            imageRes = R.drawable.img_dashboard
        ),
        HomeSelection(
            type = HomeScreenType.FEED_OR_TIMELINE,
            title = "Timeline/Feed",
            description = "See real-time updates and announcements — best for staying up to date with what's happening.",
            imageRes = R.drawable.img_timeline
        ),
        HomeSelection(
            type = HomeScreenType.BOOKMARK,
            title = "Bookmarks",
            description = "Access your pinned modules instantly — best for quick, everyday tasks you use the most.",
            imageRes = R.drawable.img_bookmark
        )
    ),
    val selectedHomeScreenType: HomeScreenType = HomeScreenType.DASHBOARD,
)

sealed interface HomeSelectionIntent {
    data class OnOptionSelected(val homeScreenType: HomeScreenType) : HomeSelectionIntent
    object OnSetAsHomeClicked : HomeSelectionIntent
}

sealed interface HomeSelectionEvent {
    data class OnCompletion(val homeScreenType: HomeScreenType) : HomeSelectionEvent
}