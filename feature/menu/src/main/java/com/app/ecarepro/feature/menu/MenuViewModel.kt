package com.app.ecarepro.feature.menu

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.menu.MenuCategory
import com.app.ecarepro.core.domain.model.menu.MenuHeader
import com.app.ecarepro.core.domain.model.menu.MenuItem
import com.app.ecarepro.core.domain.repository.MenuRepository
import com.app.ecarepro.core.domain.repository.SchoolRepository
import com.app.ecarepro.core.domain.repository.UserRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val userRepository: UserRepository,
    private val schoolRepository: SchoolRepository,
) : BaseViewModel<MenuIntent, MenuEvent>() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState = _uiState.asStateFlow()

    private val expandedCategories = mutableSetOf<Int>()
    private val expandedMenuItems = mutableSetOf<Int>()

    init {
        loadMenu()
    }

    override fun handleIntent(intent: MenuIntent) {
        when (intent) {
            is MenuIntent.ToggleCategory -> toggleCategory(intent.categoryId)
            is MenuIntent.ToggleMenuItem -> toggleMenuItem(intent.menuId)
            is MenuIntent.OnMenuItemClicked -> onMenuItemClicked(intent.menuItem)
            is MenuIntent.ToggleAccountSection -> _uiState.update { it.copy(isAccountSectionExpanded = !it.isAccountSectionExpanded) }
            is MenuIntent.Retry -> {
                _uiState.update { it.copy(isLoaded = false) }
                loadMenu()
            }
        }
    }

    private fun loadMenu() {
        if (_uiState.value.isLoaded) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            val header = buildHeader()
            menuRepository.fetchMenu("en").collect { result ->
                result
                    .onSuccess { categories ->
                        expandedCategories.clear()
                        expandedCategories.addAll(categories.map { it.id })
                        expandedMenuItems.clear()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoaded = true,
                                categories = categories,
                                header = header,
                                expandedCategoryIds = expandedCategories.toSet(),
                                expandedMenuItemIds = expandedMenuItems.toSet(),
                            )
                        }
                    }
                    .onFailure {
                        _uiState.update { it.copy(isLoading = false, isError = true) }
                    }
            }
        }
    }

    private suspend fun buildHeader(): MenuHeader {
        val user = userRepository.getActiveUser()
        val schoolCode = user?.schoolCode ?: return MenuHeader(
            schoolName = "",
            schoolAddress = "",
            schoolLogoUrl = null,
            currentAccountName = user?.name ?: "",
            currentAccountRole = user?.roleName ?: "",
            userPhotoUrl = user?.photoPath,
        )
        return try {
            val school = schoolRepository.getSchoolDetail(schoolCode).first()
            val address = listOfNotNull(school.schAdd1, school.city)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            MenuHeader(
                schoolName = school.schoolName ?: "",
                schoolAddress = address,
                schoolLogoUrl = school.logo,
                currentAccountName = user.name ?: "",
                currentAccountRole = user.roleName ?: "",
                userPhotoUrl = user.photoPath,
            )
        } catch (e: Exception) {
            MenuHeader(
                schoolName = "",
                schoolAddress = "",
                schoolLogoUrl = null,
                currentAccountName = user.name ?: "",
                currentAccountRole = user.roleName ?: "",
                userPhotoUrl = user.photoPath,
            )
        }
    }

    private fun toggleCategory(categoryId: Int) {
        if (expandedCategories.contains(categoryId)) expandedCategories.remove(categoryId)
        else expandedCategories.add(categoryId)
        _uiState.update { it.copy(expandedCategoryIds = expandedCategories.toSet()) }
    }

    private fun toggleMenuItem(menuId: Int) {
        if (expandedMenuItems.contains(menuId)) expandedMenuItems.remove(menuId)
        else expandedMenuItems.add(menuId)
        _uiState.update { it.copy(expandedMenuItemIds = expandedMenuItems.toSet()) }
    }

    private fun onMenuItemClicked(menuItem: MenuItem) {
        if (menuItem.hasChildren) {
            toggleMenuItem(menuItem.id)
        } else if (menuItem.isNavigable) {
            sendEvent(MenuEvent.NavigateTo(menuItem.id))
            sendEvent(MenuEvent.CloseDrawer)
        }
    }
}

@Immutable
data class MenuUiState(
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
    val isError: Boolean = false,
    val categories: List<MenuCategory> = emptyList(),
    val header: MenuHeader? = null,
    val expandedCategoryIds: Set<Int> = emptySet(),
    val expandedMenuItemIds: Set<Int> = emptySet(),
    val isAccountSectionExpanded: Boolean = false,
)

sealed interface MenuIntent {
    data class ToggleCategory(val categoryId: Int) : MenuIntent
    data class ToggleMenuItem(val menuId: Int) : MenuIntent
    data class OnMenuItemClicked(val menuItem: MenuItem) : MenuIntent
    data object ToggleAccountSection : MenuIntent
    data object Retry : MenuIntent
}

sealed interface MenuEvent {
    data class NavigateTo(val menuId: Int) : MenuEvent
    data object CloseDrawer : MenuEvent
}
