package com.app.ecarepro.designsystem.core.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
fun FileUploadBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    allowedOptions: List<UploadOption> = listOf(
        UploadOption.CAMERA,
        UploadOption.GALLERY,
        UploadOption.DOCUMENT
    ),
    maxFileSizeInMb: Int = 10,
    onShowError: (String) -> Unit = {},
    onFileSelected: (SelectedFileDetails) -> Unit,
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

    val processFile = { uri: Uri, type: SelectedFileType ->
        val fileSizeInBytes = getFileSizeFromCursor(context, uri)
        val limitInBytes = maxFileSizeInMb * 1024 * 1024

        if (fileSizeInBytes > limitInBytes) {
            onShowError("File size exceeds ${maxFileSizeInMb}MB")
        } else {
            scope.launch {
                isLoading = true
                val details = getFileDetails(context, uri, type, fileSizeInBytes)
                isLoading = false
                if (details != null) {
                    onFileSelected(details)
                    sheetState.hide()
                    onDismiss()
                } else {
                    onShowError("Failed to process file.")
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            processFile(tempCameraUri!!, SelectedFileType.IMAGE)
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
        uri?.let { processFile(it, SelectedFileType.IMAGE) }
    }

    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { processFile(it, SelectedFileType.DOCUMENT) }
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
                                title = stringResource(R.string.core_designsystem_select_photo),
                                onClick = { galleryLauncher.launch("image/*") }
                            )
                        }

                        UploadOption.DOCUMENT -> {
                            UploadOptionItem(
                                painter = painterResource(R.drawable.icon_document),
                                title = stringResource(R.string.core_designsystem_select_file_pdf_doc_docx_xlsx),
                                onClick = {
                                    val mimeTypes = arrayOf(
                                        "application/pdf",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                        "application/vnd.ms-excel",
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                    )
                                    documentLauncher.launch(mimeTypes)
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
                TextButton(onClick = { showSettingsDialog = false }) { Text(stringResource(R.string.core_designsystem_cancel)) }
            },
            containerColor = White
        )
    }
}


private fun launchCamera(context: Context, onUriCreated: (Uri) -> Unit) {
    val photoFile = createImageFile(context)
    val authority = "${context.packageName}.myFileProvider"
    val uri = FileProvider.getUriForFile(context, authority, photoFile)
    onUriCreated(uri)
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

private fun getFileSizeFromCursor(context: Context, uri: Uri): Long {
    return try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) return it.getLong(sizeIndex)
            }
        }
        0
    } catch (e: Exception) {
        0
    }
}

private suspend fun getFileDetails(
    context: Context,
    uri: Uri,
    type: SelectedFileType,
    knownSize: Long,
): SelectedFileDetails? {
    return withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            var name = "temp_file"
            var mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex)
                    }
                }
            }

            if (!name.contains(".")) {
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                if (extension != null) {
                    name += ".$extension"
                }
            }

            val tempFile = File(context.cacheDir, name)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            val finalSize = tempFile.length()

            SelectedFileDetails(
                uri = uri,
                file = tempFile,
                name = name,
                size = finalSize,
                formattedSize = formatFileSize(finalSize),
                mimeType = mimeType,
                type = type
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

@Composable
private fun UploadOptionItem(painter: Painter, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter, null, Modifier.size(24.dp), tint = MaterialTheme.appColors.textSecondary)
        Spacer(Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.appTypography.interRegular16px,
            color = MaterialTheme.appColors.textPrimary
        )
    }
}

@Composable
private fun CustomDivider() {
    HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, Color(0xFFF5F5F5))
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun FileUploadBottomSheetPreview() {
    EcareProTheme {
        FileUploadBottomSheet(
            isVisible = true,
            onDismiss = {},
            allowedOptions = listOf(
                UploadOption.CAMERA,
                UploadOption.GALLERY,
                UploadOption.DOCUMENT
            ),
            onFileSelected = {}
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun FileUploadBottomSheetLoadingPreview() {

    EcareProTheme {
        Column(Modifier.height(400.dp)) {
            // A preview container to simulate bottom sheet background
            Text("Sheet Content Simulation", modifier = Modifier.padding(16.dp))

            FileUploadBottomSheet(
                isVisible = true,
                onDismiss = {},
                allowedOptions = listOf(UploadOption.CAMERA),
                onFileSelected = {}
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
private fun RationaleDialogPreview() {
    EcareProTheme {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Permission Required") },
            text = { Text("Camera access is needed to take photos.") },
            confirmButton = {
                TextButton(onClick = { }) { Text("Try Again") }
            },
            dismissButton = {
                TextButton(onClick = { }) { Text("Cancel") }
            },
            containerColor = White
        )
    }
}

