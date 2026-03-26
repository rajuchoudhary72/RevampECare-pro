package com.app.ecarepro.feature.fee.receipt

import android.content.Context
import android.os.Environment
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.domain.model.fee.FeeReceiptDomain
import com.app.ecarepro.core.domain.model.fee.FeeSessionDomain
import com.app.ecarepro.core.domain.repository.FeeRepository
import com.app.ecarepro.core.ui.viewmodel.BaseViewModel
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val repository: FeeRepository,
    @ApplicationContext private val context: Context,
) : BaseViewModel<ReceiptIntent, ReceiptEvent>() {

    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState = _uiState.asStateFlow()

    init {
        handleIntent(ReceiptIntent.LoadSessions)
    }

    override fun handleIntent(intent: ReceiptIntent) {
        when (intent) {
            is ReceiptIntent.LoadSessions -> loadInitialData()
            is ReceiptIntent.SelectSession -> selectSession(intent.sessionId)
            is ReceiptIntent.ToggleSessionPicker -> _uiState.update { it.copy(showSessionPicker = !it.showSessionPicker) }
            is ReceiptIntent.DismissSessionPicker -> _uiState.update { it.copy(showSessionPicker = false) }
            is ReceiptIntent.ToggleReceipt -> toggleReceipt(intent.recId)
            is ReceiptIntent.ViewReceipt -> viewReceipt(intent.recId, intent.sessionId, intent.stId)
            is ReceiptIntent.DownloadReceipt -> downloadReceipt(intent.recId, intent.sessionId, intent.stId, intent.receiptNumber)
            is ReceiptIntent.OnBackClicked -> sendEvent(ReceiptEvent.NavigateBack)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            repository.getReceipts(sessionId = 0).collect { result ->
                result.onSuccess { (sessions, receipts) ->
                    val activeSession = sessions.firstOrNull { it.isActive } ?: sessions.firstOrNull()
                    val receiptCards = receipts.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            sessions = sessions,
                            selectedSession = activeSession,
                            receipts = receiptCards,
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(ReceiptEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to load receipts", MessageType.ERROR)))
                }
            }
        }
    }

    private fun selectSession(sessionId: Int) {
        val session = _uiState.value.sessions.firstOrNull { it.yrid == sessionId } ?: return
        _uiState.update { it.copy(selectedSession = session, showSessionPicker = false, isLoading = true, isError = false) }
        viewModelScope.launch {
            repository.getReceipts(sessionId = sessionId).collect { result ->
                result.onSuccess { (_, receipts) ->
                    _uiState.update { it.copy(isLoading = false, receipts = receipts.map { r -> r.toUiModel() }) }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                    sendEvent(ReceiptEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to load receipts", MessageType.ERROR)))
                }
            }
        }
    }

    private fun toggleReceipt(recId: String) {
        _uiState.update { state ->
            state.copy(expandedReceiptId = if (state.expandedReceiptId == recId) null else recId)
        }
    }

    private fun viewReceipt(recId: String, sessionId: Int, stId: Int) {
        setReceiptLoading(recId, true)
        viewModelScope.launch {
            repository.downloadReceipt(recId, sessionId, stId).collect { result ->
                result.onSuccess { base64 ->
                    setReceiptLoading(recId, false)
                    val file = decodeAndSaveToCache(base64, "receipt_${recId}.pdf")
                    if (file != null) {
                        sendEvent(ReceiptEvent.OpenPdfViewer(file.absolutePath))
                    } else {
                        sendEvent(ReceiptEvent.ShowMessage(SnackbarMessage("Could not open receipt", MessageType.ERROR)))
                    }
                }.onFailure { error ->
                    setReceiptLoading(recId, false)
                    sendEvent(ReceiptEvent.ShowMessage(SnackbarMessage(error.message ?: "Failed to load receipt", MessageType.ERROR)))
                }
            }
        }
    }

    private fun downloadReceipt(recId: String, sessionId: Int, stId: Int, receiptNumber: String) {
        setReceiptLoading(recId, true)
        viewModelScope.launch {
            repository.downloadReceipt(recId, sessionId, stId).collect { result ->
                result.onSuccess { base64 ->
                    setReceiptLoading(recId, false)
                    val fileName = "receipt_${receiptNumber.ifEmpty { recId }}.pdf"
                    val file = decodeAndSaveToDownloads(base64, fileName)
                    if (file != null) {
                        sendEvent(ReceiptEvent.ShowMessage(SnackbarMessage("Receipt downloaded: ${file.name}", MessageType.SUCCESS)))
                    } else {
                        sendEvent(ReceiptEvent.ShowMessage(SnackbarMessage("Download failed", MessageType.ERROR)))
                    }
                }.onFailure { error ->
                    setReceiptLoading(recId, false)
                    sendEvent(ReceiptEvent.ShowMessage(SnackbarMessage(error.message ?: "Download failed", MessageType.ERROR)))
                }
            }
        }
    }

    private fun setReceiptLoading(recId: String, loading: Boolean) {
        _uiState.update { state ->
            state.copy(
                receipts = state.receipts.map { card ->
                    if (card.recId == recId) card.copy(isDownloading = loading) else card
                }
            )
        }
    }

    private fun decodeAndSaveToCache(base64: String, fileName: String): File? {
        return runCatching {
            val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            val file = File(context.cacheDir, fileName)
            file.writeBytes(bytes)
            file
        }.getOrNull()
    }

    private fun decodeAndSaveToDownloads(base64: String, fileName: String): File? {
        return runCatching {
            val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            file.writeBytes(bytes)
            file
        }.getOrNull()
    }
}

private fun FeeReceiptDomain.toUiModel() = ReceiptCardUi(
    recId = recId,
    recDate = recDate,
    paidAmount = paidAmount,
    receiptNumber = receiptNumber,
    paymentMode = paymentMode,
    installment = installment,
    stId = stId,
)

@Immutable
data class ReceiptUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val sessions: List<FeeSessionDomain> = emptyList(),
    val selectedSession: FeeSessionDomain? = null,
    val receipts: List<ReceiptCardUi> = emptyList(),
    val expandedReceiptId: String? = null,
    val showSessionPicker: Boolean = false,
)

@Immutable
data class ReceiptCardUi(
    val recId: String,
    val recDate: String,
    val paidAmount: String,
    val receiptNumber: String,
    val paymentMode: String,
    val installment: String,
    val stId: Int,
    val isDownloading: Boolean = false,
)

sealed interface ReceiptIntent {
    data object LoadSessions : ReceiptIntent
    data class SelectSession(val sessionId: Int) : ReceiptIntent
    data object ToggleSessionPicker : ReceiptIntent
    data object DismissSessionPicker : ReceiptIntent
    data class ToggleReceipt(val recId: String) : ReceiptIntent
    data class ViewReceipt(val recId: String, val sessionId: Int, val stId: Int) : ReceiptIntent
    data class DownloadReceipt(val recId: String, val sessionId: Int, val stId: Int, val receiptNumber: String) : ReceiptIntent
    data object OnBackClicked : ReceiptIntent
}

sealed interface ReceiptEvent {
    data object NavigateBack : ReceiptEvent
    data class OpenPdfViewer(val filePath: String) : ReceiptEvent
    data class ShowMessage(val message: SnackbarMessage) : ReceiptEvent
}
