package com.app.ecarepro.compose.ui.pedagogy

import androidx.lifecycle.viewModelScope
import com.app.ecarepro.compose.model.UiState
import com.app.ecarepro.compose.ui.base.BaseViewModel
import com.app.ecarepro.data.network.model.Pedagogy
import com.app.ecarepro.data.repository.AppRepository
import com.app.ecarepro.ui.message.sent.UNKNOWN_ERROR_MESSAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PedagogyViewModel @Inject constructor(
    private val appRepository: AppRepository
) : BaseViewModel() {

    val searchQuery = MutableStateFlow("")
    private val refreshData = MutableStateFlow(false)
    private val _expandedPedagogies = MutableStateFlow<Set<Int>>(emptySet())


    private val rawData: Flow<UiState<PedagogyUiState>> = refreshData.flatMapLatest {
        appRepository.getPedagogy()
    }.map { pedagogy ->
        if (pedagogy.isSuccess) {
            UiState.Success(
                PedagogyUiState(
                    pedagogies = pedagogy.getOrNull()?.pedagogies ?: emptyList(),
                )
            )
        } else {
            UiState.Error(Exception(UNKNOWN_ERROR_MESSAGE))
        }
    }

    val uiState: StateFlow<UiState<PedagogyUiState>> = combine(
        rawData,
        searchQuery,
        _expandedPedagogies

    ) { state, query, expandedPedagogies ->
        when (state) {
            is UiState.Success -> {
                val filtered = state.data.pedagogies
                    .filter {
                        (query.isBlank() || it.name.contains(
                            query,
                            ignoreCase = true
                        ))
                    }.map { pedagogy ->
                        pedagogy.copy(isExpanded = expandedPedagogies.contains(pedagogy.pdgID))
                    }

                state.copy(
                    data = state.data.copy(
                        pedagogies = filtered
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

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            searchQuery.update { query }
        }
    }

    fun deletePedagogy(id: Int) {
        viewModelScope.launch {
            appRepository
                .deletePedagogy(id)
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
    fun deletePedagogyStep(id: Int) {
        viewModelScope.launch {
            appRepository
                .deletePedagogyStep(id)
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


    fun refresh() {
        viewModelScope.launch {
            refreshData.update { it.not() }
        }
    }

    fun toggleExpand(pedagogy: Pedagogy) {
        viewModelScope.launch {
            _expandedPedagogies.update { currentExpandedPedagogies ->
                val mutableSet = currentExpandedPedagogies.toMutableSet()
                if (pedagogy.pdgID in mutableSet) {
                    mutableSet.remove(pedagogy.pdgID)
                } else {
                    mutableSet.add(pedagogy.pdgID)
                }
                mutableSet
            }
        }
    }
}

data class PedagogyUiState(
    val pedagogies: List<Pedagogy> = emptyList(),
)