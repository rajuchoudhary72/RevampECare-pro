package com.app.ecarepro.designsystem.core.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
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
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    onFileSelected: (Uri, SelectedFileType) -> Unit,
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            onFileSelected(tempCameraUri!!, SelectedFileType.IMAGE)
            onDismiss()
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

    // --- Gallery Setup ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onFileSelected(it, SelectedFileType.IMAGE)
            onDismiss()
        }
    }

    // --- Document Setup ---
    val documentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            onFileSelected(it, SelectedFileType.DOCUMENT)
            onDismiss()
        }
    }

    // --- UI Content ---
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
                    text = "Menu",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 20.sp),
                    color = MaterialTheme.appColors.textPrimary
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                        },
                    tint = MaterialTheme.appColors.textPrimary
                )
            }

            allowedOptions.forEachIndexed { index, option ->
                when (option) {
                    UploadOption.CAMERA -> {
                        UploadOptionItem(
                            painter = painterResource(R.drawable.icon_camera),
                            title = "Take Photo",
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
                            title = "Select Photo",
                            onClick = { galleryLauncher.launch("image/*") }
                        )
                    }

                    UploadOption.DOCUMENT -> {
                        UploadOptionItem(
                            painter = painterResource(R.drawable.icon_document),
                            title = "Select File (PDF/DOC/DOCX/XLSX)",
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

            Spacer(modifier = Modifier.height(24.dp))

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

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("Permission Required") },
            text = { Text("Camera access is needed to take photos for the syllabus.") },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Try Again") }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) { Text("Cancel") }
            },
            containerColor = White
        )
    }

    // --- Settings Dialog ---
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Permission Denied") },
            text = { Text("Camera permission was permanently denied. Please enable it in app settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) { Text("Cancel") }
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
            onFileSelected = { _, _ -> }
        )
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Document Only")
private fun FileUploadBottomSheetDocumentOnlyPreview() {
    EcareProTheme {
        FileUploadBottomSheet(
            isVisible = true,
            onDismiss = {},
            allowedOptions = listOf(UploadOption.DOCUMENT),
            onFileSelected = { _, _ -> }
        )
    }
}
