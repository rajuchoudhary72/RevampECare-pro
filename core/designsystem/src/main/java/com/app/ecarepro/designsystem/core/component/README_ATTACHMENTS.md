# Attachment Components

Reusable Jetpack Compose components for displaying and managing file attachments, converted from SwiftUI design.

## Components

### 1. DownloadFilesView
Full-screen component for displaying a list of downloadable attachments.

### 2. AttachmentRowView
Individual row item showing attachment details with action buttons.

### 3. AttachmentFileIcon
Styled file icon with colored background.

## Usage

### Basic Usage

```kotlin
import com.app.ecarepro.core.domain.model.ECAttachment
import com.app.ecarepro.designsystem.core.component.DownloadFilesView

@Composable
fun MyScreen(navController: NavController) {
    val attachments = remember {
        listOf(
            ECAttachment(
                name = "Assignment_1.pdf",
                url = "https://example.com/file1.pdf"
            ),
            ECAttachment(
                name = "Syllabus_2024.pdf",
                url = "https://example.com/file2.pdf"
            )
        )
    }

    DownloadFilesView(
        attachments = attachments,
        onBackPressed = { navController.navigateUp() },
        onAttachmentClick = { attachment ->
            // Handle view attachment (e.g., open in viewer)
            navController.navigate("docViewer/${attachment.name}/${attachment.url}")
        },
        onDownloadClick = { attachment ->
            // Handle download
            downloadAttachment(attachment)
        }
    )
}
```

### With ViewModel

```kotlin
@HiltViewModel
class AttachmentsViewModel @Inject constructor(
    private val repository: AttachmentRepository
) : ViewModel() {

    private val _attachments = MutableStateFlow<List<ECAttachment>>(emptyList())
    val attachments = _attachments.asStateFlow()

    fun loadAttachments(itemId: String) {
        viewModelScope.launch {
            _attachments.value = repository.getAttachments(itemId)
        }
    }

    fun downloadAttachment(attachment: ECAttachment) {
        viewModelScope.launch {
            repository.downloadFile(attachment.url, attachment.name)
        }
    }
}

@Composable
fun AttachmentsScreen(
    viewModel: AttachmentsViewModel = hiltViewModel(),
    itemId: String,
    onBackPressed: () -> Unit
) {
    val attachments by viewModel.attachments.collectAsStateWithLifecycle()

    LaunchedEffect(itemId) {
        viewModel.loadAttachments(itemId)
    }

    DownloadFilesView(
        attachments = attachments,
        onBackPressed = onBackPressed,
        onDownloadClick = { attachment ->
            viewModel.downloadAttachment(attachment)
        }
    )
}
```

### Using Individual Row Component

```kotlin
@Composable
fun CustomAttachmentList() {
    LazyColumn {
        items(attachments) { attachment ->
            AttachmentRowView(
                attachment = attachment,
                onAttachmentClick = { /* Handle view */ },
                onDownloadClick = { /* Handle download */ }
            )
            HorizontalDivider()
        }
    }
}
```

## Navigation Integration

### Add to Navigation Graph

```kotlin
// In your NavHost
composable(
    route = "attachments/{itemId}",
    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
) { backStackEntry ->
    val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
    AttachmentsScreen(
        itemId = itemId,
        onBackPressed = { navController.navigateUp() }
    )
}
```

### Navigate to Attachments Screen

```kotlin
// From another screen
Button(onClick = {
    navController.navigate("attachments/$itemId")
}) {
    Text("View Attachments")
}
```

## Integration with Existing Features

### With Syllabus Feature

```kotlin
// Add attachments to syllabus model
data class Syllabus(
    val id: String,
    val title: String,
    val attachments: List<ECAttachment> = emptyList()
)

// In SyllabusScreen, add button to view attachments
IconButton(
    onClick = {
        navController.navigate("attachments/${syllabus.id}")
    }
) {
    Icon(Icons.Default.AttachFile, "View Attachments")
}
```

### With Download Feature

```kotlin
// Use existing FileDownloader
class AttachmentDownloader @Inject constructor(
    private val fileDownloader: FileDownloader
) {
    suspend fun download(attachment: ECAttachment): Result<Unit> {
        return try {
            fileDownloader.download(
                DownloadRequest(
                    url = attachment.url,
                    fileName = attachment.name
                )
            ).first()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### With DocViewer Feature

```kotlin
// View attachment in DocViewer
fun viewAttachment(attachment: ECAttachment) {
    navController.navigate(
        "docViewer" +
        "?title=${attachment.name}" +
        "&url=${attachment.url}"
    )
}
```

## Customization

### Custom Icons

```kotlin
// Replace default icons with custom ones
AttachmentRowView(
    attachment = attachment,
    modifier = Modifier,
    // Custom implementation if needed
)
```

### Custom Styling

```kotlin
// The components use Material3 theme colors
// Customize in your theme:
MaterialTheme(
    colorScheme = yourColorScheme.copy(
        primary = YourPrimaryColor,
        onSurface = YourTextColor
    )
) {
    DownloadFilesView(...)
}
```

## Data Model

### ECAttachment

```kotlin
data class ECAttachment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val url: String,
    val fileType: String = "pdf",
    val size: String? = null,
    val uploadedDate: String? = null
)
```

### Example Data

```kotlin
val sampleAttachments = listOf(
    ECAttachment(
        name = "Assignment_1.pdf",
        url = "https://example.com/file1.pdf",
        fileType = "pdf",
        size = "2.5 MB"
    ),
    ECAttachment(
        name = "Syllabus_2024.docx",
        url = "https://example.com/file2.docx",
        fileType = "docx",
        size = "1.2 MB"
    )
)
```

## Features

✅ **Reusable Components**: Can be used anywhere in the app
✅ **Material 3 Design**: Follows Material Design guidelines
✅ **Empty State**: Shows helpful message when no attachments
✅ **Long Filename Handling**: Truncates long names with ellipsis
✅ **Clickable Actions**: Separate callbacks for view and download
✅ **Type Safety**: Uses data classes for type-safe data
✅ **Preview Support**: Includes preview composables for development
✅ **Theme Integration**: Uses app's existing theme and colors

## API Reference

### DownloadFilesView

| Parameter | Type | Description |
|-----------|------|-------------|
| `attachments` | `List<ECAttachment>` | List of attachments to display |
| `onBackPressed` | `() -> Unit` | Callback when back button is pressed |
| `onAttachmentClick` | `(ECAttachment) -> Unit` | Callback when attachment icon is clicked |
| `onDownloadClick` | `(ECAttachment) -> Unit` | Callback when download icon is clicked |
| `modifier` | `Modifier` | Optional modifier for the container |

### AttachmentRowView

| Parameter | Type | Description |
|-----------|------|-------------|
| `attachment` | `ECAttachment` | The attachment to display |
| `onAttachmentClick` | `() -> Unit` | Callback when attachment icon is clicked |
| `onDownloadClick` | `() -> Unit` | Callback when download icon is clicked |
| `modifier` | `Modifier` | Optional modifier for the row |

## Best Practices

1. **Use with ViewModel**: Manage attachment state in ViewModel
2. **Handle Loading States**: Show loading indicator while fetching attachments
3. **Error Handling**: Show error messages if download fails
4. **Permission Handling**: Check storage permissions before downloading
5. **Progress Indication**: Show download progress for large files
6. **Cache Management**: Cache downloaded files appropriately

## Troubleshooting

### Icons not showing
- Ensure Material Icons dependency is added
- Check icon imports are correct

### Colors not matching design
- Verify your app theme colors are set correctly
- Check `MaterialTheme.appColors` extensions exist

### Click actions not working
- Ensure callbacks are properly passed
- Check for overlapping clickable modifiers

## Related Components

- **EcareProTopAppBar** - Used for the header
- **HorizontalDivider** - Used between list items
- **DocViewerScreen** - For viewing attachments
- **FileDownloader** - For downloading files

---

**Created:** December 2024
**Source:** SwiftUI DownloadFilesView conversion
