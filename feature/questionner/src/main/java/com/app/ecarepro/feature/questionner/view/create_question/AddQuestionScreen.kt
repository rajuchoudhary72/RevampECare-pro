package com.app.ecarepro.feature.questionner.view.create_question

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.ecarepro.designsystem.core.component.TextComponent
import com.app.ecarepro.designsystem.core.component.TextComponentStyle
import com.app.ecarepro.designsystem.core.component.TextComponentView
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.EcareProTypography
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.questionner.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddQuestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showImagePickerSheet by remember { mutableStateOf(false) }

    // Image picker for gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.handleIntent(AddQuestionIntent.OnImageSelected(context, it))
        }
    }

    // Image picker for camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.handleIntent(AddQuestionIntent.OnCameraImageCaptured(context))
        }
    }

    // Show success message and navigate back
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.handleIntent(AddQuestionIntent.ClearMessages)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(4.dp),
                title = {
                    Text(
                        "Add Questionnaire",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.appColors.surface,
                    titleContentColor = MaterialTheme.appColors.textPrimary,
                    navigationIconContentColor = MaterialTheme.appColors.textPrimary
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.handleIntent(AddQuestionIntent.SubmitQuestion) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.questionText.isNotBlank() && !uiState.isLoading,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.appColors.primary,
                    disabledContainerColor = MaterialTheme.appColors.divider,
                    contentColor = Color.White,
                    disabledContentColor = MaterialTheme.appColors.textSecondary
                ),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.appColors.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Question input section
            Text(
                text = "Type your question",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.questionText,
                onValueChange = { viewModel.handleIntent(AddQuestionIntent.OnQuestionTextChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = {
                    Text(
                        text = "Question will come here",
                        color = MaterialTheme.appColors.textSecondary.copy(alpha = 0.5f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.appColors.divider,
                    unfocusedBorderColor = MaterialTheme.appColors.divider,
                    focusedTextColor = MaterialTheme.appColors.textPrimary,
                    unfocusedTextColor = MaterialTheme.appColors.textPrimary,
                    cursorColor = MaterialTheme.appColors.primary
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Image upload section
            Text(
                text = "Add Image",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            ImageUploadSection(
                selectedImageUri = uiState.selectedImageUri,
                onImageClick = { showImagePickerSheet = true },
                onRemoveImage = { viewModel.handleIntent(AddQuestionIntent.OnRemoveImage) },
                isEnabled = !uiState.isLoading
            )
        }

        // Image picker bottom sheet
        if (showImagePickerSheet) {
            ImagePickerBottomSheet(
                onDismiss = { showImagePickerSheet = false },
                onCameraClick = {
                    showImagePickerSheet = false
                    scope.launch {
                        val uri = viewModel.createCameraImageUri(context)
                        uri?.let { cameraLauncher.launch(it) }
                    }
                },
                onGalleryClick = {
                    showImagePickerSheet = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }
}

@Composable
private fun ImageUploadSection(
    selectedImageUri: Uri?,
    onImageClick: () -> Unit,
    onRemoveImage: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.appColors.divider,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (selectedImageUri != null) Color.Transparent
                       else MaterialTheme.appColors.surface
            )
            .clickable(enabled = isEnabled) {
                if (selectedImageUri == null) onImageClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            // Show selected image
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Selected image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Remove button
            IconButton(
                onClick = onRemoveImage,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Remove image",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            // Show upload placeholder
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_upload),
                    contentDescription = "Upload image",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.appColors.textSecondary.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Click here to add image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.appColors.textSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Max 10 MB files are allowed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.appColors.textSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImagePickerBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.appColors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Select Image Source",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Camera option
            TextButton(
                onClick = onCameraClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_camera),
                    contentDescription = "Camera",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.appColors.textPrimary
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "Take Photo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            // Gallery option
            TextButton(
                onClick = onGalleryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Gallery",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.appColors.textPrimary
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "Choose from Gallery",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.appColors.textPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ============================================
// Preview Section
// ============================================

@Preview(showBackground = true, name = "Add Question Screen - Light")
@Composable
private fun AddQuestionScreenPreviewLight() {
    EcareProTheme {
        AddQuestionScreenPreview()
    }
}

@Preview(showBackground = true, name = "Add Question Screen - Dark")
@Composable
private fun AddQuestionScreenPreviewDark() {
    EcareProTheme(darkTheme = true) {
        AddQuestionScreenPreview()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddQuestionScreenPreview() {
    var questionText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePickerSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(4.dp),
                title = {
                    Text(
                        "Add Questionnaire",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )

                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.appColors.surface,
                    titleContentColor = MaterialTheme.appColors.textPrimary,
                    navigationIconContentColor = MaterialTheme.appColors.textPrimary
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = questionText.isNotBlank(),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.appColors.primary,
                    disabledContainerColor = MaterialTheme.appColors.divider,
                    contentColor = Color.White,
                    disabledContentColor = MaterialTheme.appColors.textSecondary
                ),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(
                    text = "Add",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = MaterialTheme.appColors.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Question input section
            TextComponentView(
                component = TextComponent(
                    text = "Type your question",
                    style = TextComponentStyle(
                        foregroundColor = Color.Gray,
                        textStyle = EcareProTypography.interRegular14px
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = {
                    Text(
                        text = "Question will come here",
                        color = MaterialTheme.appColors.textSecondary.copy(alpha = 0.5f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.appColors.divider,
                    unfocusedBorderColor = MaterialTheme.appColors.divider,
                    focusedTextColor = MaterialTheme.appColors.textPrimary,
                    unfocusedTextColor = MaterialTheme.appColors.textPrimary,
                    cursorColor = MaterialTheme.appColors.primary
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Image upload section
            Text(
                text = "Add Image",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.appColors.textSecondary,
                fontSize = 14.sp
            )
            TextComponentView(
                component = TextComponent(
                    text = "Add Image",
                    style = TextComponentStyle(
                        foregroundColor = Color.Gray,
                        textStyle = EcareProTypography.interRegular14px
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            ImageUploadSection(
                selectedImageUri = selectedImageUri,
                onImageClick = { showImagePickerSheet = true },
                onRemoveImage = { selectedImageUri = null },
                isEnabled = true
            )
        }
    }
}
