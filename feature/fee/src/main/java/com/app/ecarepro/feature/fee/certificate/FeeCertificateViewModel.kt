package com.app.ecarepro.feature.fee.certificate

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.fee.FeeSessionDomain
import com.app.ecarepro.core.domain.repository.FeeRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FeeCertificateViewModel @Inject constructor(
    private val repository: FeeRepository,
    @ApplicationContext private val context: Context,
) : BaseViewModel<FeeCertificateIntent, FeeCertificateEvent>() {

    private val _uiState = MutableStateFlow(FeeCertificateUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    override fun handleIntent(intent: FeeCertificateIntent) {
        when (intent) {
            is FeeCertificateIntent.OnBackClicked -> sendEvent(FeeCertificateEvent.NavigateBack)
            is FeeCertificateIntent.OnSessionSelected -> {
                _uiState.update { it.copy(selectedSession = intent.session, showSessionPicker = false) }
                fetchCertificate()
            }
            is FeeCertificateIntent.ShowSessionPicker -> _uiState.update { it.copy(showSessionPicker = true) }
            is FeeCertificateIntent.DismissSessionPicker -> _uiState.update { it.copy(showSessionPicker = false) }
            is FeeCertificateIntent.OnShareClicked -> {
                _uiState.value.pdfFile?.let { sendEvent(FeeCertificateEvent.SharePdf(it)) }
            }
            is FeeCertificateIntent.Retry -> loadSessions()
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getReceipts(sessionId = 0).collect { result ->
                result.fold(
                    onSuccess = { (sessions, _) ->
                        val activeSession = sessions.firstOrNull { it.isActive } ?: sessions.firstOrNull()
                        _uiState.update {
                            it.copy(sessions = sessions, selectedSession = activeSession)
                        }
                        if (activeSession != null) fetchCertificate()
                        else _uiState.update { it.copy(isLoading = false) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message ?: "Failed to load sessions") }
                    },
                )
            }
        }
    }

    private fun fetchCertificate() {
        val session = _uiState.value.selectedSession ?: return
        _uiState.update { it.copy(isLoading = true, pdfFile = null, error = null) }
        viewModelScope.launch {
            repository.getCertificate(
                sessionId = session.yrid,
                sessionName = session.yearname,
            ).collect { result ->
                result.fold(
                    onSuccess = { base64 ->
                        val file = base64?.let { decodeAndSavePdf(it, session.yrid) }
                        _uiState.update { it.copy(isLoading = false, pdfFile = file) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message ?: "Failed to load certificate") }
                    },
                )
            }
        }
    }

    private fun decodeAndSavePdf(base64: String, sessionId: Int): File? {
        return runCatching {
            val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            val file = File(context.cacheDir, "FeeCertificate-$sessionId.pdf")
            file.writeBytes(bytes)
            file
        }.getOrNull()
    }
}

@Immutable
data class FeeCertificateUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val sessions: List<FeeSessionDomain> = emptyList(),
    val selectedSession: FeeSessionDomain? = null,
    val pdfFile: File? = null,
    val showSessionPicker: Boolean = false,
)

sealed interface FeeCertificateIntent {
    data object OnBackClicked : FeeCertificateIntent
    data class OnSessionSelected(val session: FeeSessionDomain) : FeeCertificateIntent
    data object ShowSessionPicker : FeeCertificateIntent
    data object DismissSessionPicker : FeeCertificateIntent
    data object OnShareClicked : FeeCertificateIntent
    data object Retry : FeeCertificateIntent
}

sealed interface FeeCertificateEvent {
    data object NavigateBack : FeeCertificateEvent
    data class SharePdf(val file: File) : FeeCertificateEvent
}
