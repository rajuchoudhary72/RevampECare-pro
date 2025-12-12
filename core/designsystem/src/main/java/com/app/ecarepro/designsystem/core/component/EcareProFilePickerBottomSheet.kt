package com.app.ecarepro.designsystem.core.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

data class SelectedFileDetails(
    val uri: Uri?,
    val file: File,         // The actual file object (copied to cache)
    val name: String,       // e.g., "my_document.pdf"
    val size: Long,         // Size in bytes
    val formattedSize: String, // e.g., "2.5 MB"
    val mimeType: String,   // e.g., "application/pdf"
    val type: SelectedFileType, // IMAGE or DOCUMENT
)

enum class UploadOption {
    CAMERA, GALLERY, DOCUMENT
}

enum class SelectedFileType {
    IMAGE, DOCUMENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcareProFileUploadBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    allowedOptions: List<UploadOption> = listOf(
        UploadOption.CAMERA,
        UploadOption.GALLERY,
        UploadOption.DOCUMENT
    ),
    maxFileSizeInMb: Int = 10,
    allowMultiple: Boolean = false,
    maxFiles: Int = 5,
    onShowError: (String) -> Unit = {},
    onFilesSelected: (List<SelectedFileDetails>) -> Unit,
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    suspend fun getProcessedFileDetails(uri: Uri, type: SelectedFileType): SelectedFileDetails? {
        return withContext(Dispatchers.IO) {
            val fileSizeInBytes = getFileSizeFromCursor(context, uri)
            val limitInBytes = maxFileSizeInMb * 1024 * 1024

            if (fileSizeInBytes > limitInBytes) {
                withContext(Dispatchers.Main) {
                    onShowError("File size exceeds ${maxFileSizeInMb}MB")
                }
                null
            } else {
                getFileDetails(context, uri, type, fileSizeInBytes)
            }
        }
    }

    val processFiles = { uris: List<Uri>, type: SelectedFileType ->
        scope.launch {
            isLoading = true
            var finalUris = uris
            if (allowMultiple && uris.size > maxFiles) {
                onShowError("You can select a maximum of $maxFiles files.")
                finalUris = uris.take(maxFiles)
            }

            val validFiles = mutableListOf<SelectedFileDetails>()
            var hasError = false

            finalUris.forEach { uri ->
                val details = getProcessedFileDetails(uri, type)
                if (details != null) {
                    validFiles.add(details)
                } else {
                    hasError = true
                }
            }

            isLoading = false

            if (validFiles.isNotEmpty()) {
                onFilesSelected(validFiles)
                sheetState.hide()
                onDismiss()
            } else if (!hasError) {
                onShowError("Failed to process file(s).")
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            processFiles(listOf(tempCameraUri!!), SelectedFileType.IMAGE)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera(context) { uri ->
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            }
        } else {
            if (activity != null) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.CAMERA
                    )
                ) {
                    showRationaleDialog = true
                } else {
                    showSettingsDialog = true
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { processFiles(listOf(it), SelectedFileType.IMAGE) }
    }

    val galleryMultipleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) processFiles(uris, SelectedFileType.IMAGE)
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { processFiles(listOf(it), SelectedFileType.DOCUMENT) }
    }

    val documentMultipleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) processFiles(uris, SelectedFileType.DOCUMENT)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoading) "Processing..." else stringResource(R.string.core_designsystem_menu),
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp),
                    color = MaterialTheme.appColors.textPrimary
                )
                if (!isLoading) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.core_designsystem_close),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                scope.launch { sheetState.hide() }
                                    .invokeOnCompletion { onDismiss() }
                            },
                        tint = MaterialTheme.appColors.textPrimary
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.core_designsystem_preparing_file),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                allowedOptions.forEachIndexed { index, option ->
                    when (option) {
                        UploadOption.CAMERA -> {
                            UploadOptionItem(
                                painter = painterResource(R.drawable.icon_camera),
                                title = stringResource(R.string.core_designsystem_take_photo),
                                onClick = {
                                    val permission = Manifest.permission.CAMERA
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            permission
                                        ) == PermissionChecker.PERMISSION_GRANTED
                                    ) {
                                        launchCamera(context) { uri ->
                                            tempCameraUri = uri
                                            cameraLauncher.launch(uri)
                                        }
                                    } else {
                                        permissionLauncher.launch(permission)
                                    }
                                }
                            )
                        }

                        UploadOption.GALLERY -> {
                            UploadOptionItem(
                                painter = painterResource(R.drawable.icon_gallery),
                                title = if (allowMultiple) "Select photos" else stringResource(R.string.core_designsystem_select_photo),
                                onClick = {
                                    if (allowMultiple) {
                                        galleryMultipleLauncher.launch("image/*")
                                    } else {
                                        galleryLauncher.launch("image/*")
                                    }
                                }
                            )
                        }

                        UploadOption.DOCUMENT -> {
                            UploadOptionItem(
                                painter = painterResource(R.drawable.icon_document),
                                title = if (allowMultiple) "Select files (PDF, DOC, XLS)" else stringResource(
                                    R.string.core_designsystem_select_file_pdf_doc_docx_xlsx
                                ),
                                onClick = {
                                    val mimeTypes = arrayOf(
                                        "application/pdf",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                        "application/vnd.ms-excel",
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                    )
                                    if (allowMultiple) {
                                        documentMultipleLauncher.launch(mimeTypes)
                                    } else {
                                        documentLauncher.launch(mimeTypes)
                                    }
                                }
                            )
                        }
                    }
                    if (index < allowedOptions.lastIndex) {
                        CustomDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE))
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.appTypography.interMedium16px,
                        color = Color(0xFFEF5350)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text(stringResource(R.string.core_designsystem_permission_required)) },
            text = { Text(stringResource(R.string.core_designsystem_camera_access_is_needed_to_take_photos)) },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text(stringResource(R.string.core_designsystem_try_again)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                }) { Text(stringResource(R.string.core_designsystem_cancel)) }
            },
            containerColor = White
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(stringResource(R.string.core_designsystem_permission_denied)) },
            text = { Text(stringResource(R.string.core_designsystem_camera_permission_was_permanently_denied_please_enable_it_in_app_settings)) },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) { Text(stringResource(R.string.core_designsystem_open_settings)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                }) { Text(stringResource(R.string.core_designsystem_cancel)) }
            },
            containerColor = White
        )
    }
}

@Composable
private fun UploadOptionItem(
    painter: Painter,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.appColors.textPrimary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.appTypography.interRegular14px,
            color = MaterialTheme.appColors.textPrimary
        )
    }
}

@Composable
fun CustomDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = Color(0xFFEEEEEE)
    )
}

private suspend fun getFileDetails(
    context: Context,
    uri: Uri,
    type: SelectedFileType,
    fileSize: Long,
): SelectedFileDetails? {
    return withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

            // Get file name
            var fileName = "unknown_file"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }

            // Copy file to cache directory to ensure we have a File object
            val tempFile = File(
                context.cacheDir,
                "upload_${System.currentTimeMillis()}_${fileName.replace(" ", "_")}"
            )
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            SelectedFileDetails(
                uri = uri,
                file = tempFile,
                name = fileName,
                size = fileSize,
                formattedSize = formatFileSize(fileSize),
                mimeType = mimeType,
                type = type
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun getFileSizeFromCursor(context: Context, uri: Uri): Long {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (!cursor.isNull(sizeIndex)) {
                return cursor.getLong(sizeIndex)
            }
        }
    }
    // Fallback for content schemes that don't support OpenableColumns.SIZE
    return try {
        context.contentResolver.openFileDescriptor(uri, "r")?.use {
            it.statSize
        } ?: 0L
    } catch (e: Exception) {
        0L
    }
}

fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

internal fun launchCamera(context: Context, onUriCreated: (Uri) -> Unit) {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileProvider",
        file
    )
    onUriCreated(uri)
}
@Preview(showBackground = true)
@Composable
private fun FileUploadBottomSheetSinglePreview() {
    EcareProTheme {
        EcareProFileUploadBottomSheet (
            isVisible = true,
            onDismiss = {  },
            allowMultiple = false,
            onFilesSelected = { _ ->  }
        )
    }
}


