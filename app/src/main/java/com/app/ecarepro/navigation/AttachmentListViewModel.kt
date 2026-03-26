package com.app.ecarepro.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecarepro.core.download.FileDownloader
import com.app.ecarepro.core.download.model.DownloadRequest
import com.app.ecarepro.designsystem.core.component.ECAttachment
import com.app.ecarepro.designsystem.core.component.MessageType
import com.app.ecarepro.designsystem.core.component.SnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AttachmentListViewModel @Inject constructor(
    private val fileDownloader: FileDownloader
) : ViewModel() {

    private val _messageEvent = MutableSharedFlow<SnackbarMessage>()
    val messageEvent = _messageEvent.asSharedFlow()

    private val _downloadedFiles = MutableStateFlow<Set<String>>(emptySet())
    val downloadedFiles: StateFlow<Set<String>> = _downloadedFiles.asStateFlow()

    fun checkDownloadedFiles(attachments: List<ECAttachment>) {
        viewModelScope.launch(Dispatchers.IO) {
            val alreadyDownloaded = attachments
                .filter { fileDownloader.isFileDownloaded(it.name) }
                .map { it.name }
                .toSet()
            _downloadedFiles.value = alreadyDownloaded
        }
    }

    fun downloadAttachment(attachment: ECAttachment) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fileDownloader.download(
                    DownloadRequest(
                        url = attachment.url,
                        fileName = attachment.name,
                        destination = DownloadRequest.Destination.AppPrivateFiles
                    )
                ).first()

                _downloadedFiles.value = _downloadedFiles.value + attachment.name

                withContext(Dispatchers.Main) {
                    _messageEvent.emit(
                        SnackbarMessage("Download Started", MessageType.INFO)
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _messageEvent.emit(
                        SnackbarMessage(
                            "Download failed. Please try again.",
                            MessageType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun getDownloadedFilePath(fileName: String): String {
        return fileDownloader.getDownloadedFilePath(fileName)
    }
}
