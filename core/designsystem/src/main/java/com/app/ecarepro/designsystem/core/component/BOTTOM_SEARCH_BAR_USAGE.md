# BottomSearchBarView & SearchBar Components

## Overview
Two reusable Jetpack Compose components for implementing search functionality:
- **SearchBar**: Standalone search bar with search icon, text input, and clear button
- **BottomSearchBarView**: Bottom-positioned search bar with optional right action button and shadow effect

## Components Created

### Files Added:
1. `SearchBar.kt` - Core search bar component
2. `BottomSearchBarView.kt` - Bottom positioned wrapper with optional icon
3. `ic_close.xml` - Close/clear icon drawable (13.5dp × 14dp)

## SearchBar Component

### Features:
- Magnifying glass search icon (left)
- Text input field with placeholder
- Clear button (X) when text is present
- Customizable placeholder text
- Optional search submit callback

### Usage:

```kotlin
import com.app.ecarepro.designsystem.core.component.SearchBar

@Composable
fun MyScreen() {
    var searchText by remember { mutableStateOf("") }

    // Basic usage
    SearchBar(
        searchText = searchText,
        onSearchTextChange = { searchText = it }
    )

    // With custom placeholder
    SearchBar(
        searchText = searchText,
        onSearchTextChange = { searchText = it },
        placeholder = "Search students..."
    )

    // With search submit action
    SearchBar(
        searchText = searchText,
        onSearchTextChange = { searchText = it },
        placeholder = "Search here...",
        onSearchSubmit = {
            // Handle search submission (e.g., trigger API call)
            performSearch(searchText)
        }
    )
}
```

### Parameters:
- `searchText: String` - Current search text value
- `onSearchTextChange: (String) -> Unit` - Callback when text changes
- `modifier: Modifier = Modifier` - Optional modifier
- `placeholder: String = "Search..."` - Placeholder text
- `onSearchSubmit: (() -> Unit)? = null` - Optional callback on search submit

## BottomSearchBarView Component

### Features:
- Bottom positioned with shadow effect
- Integrated SearchBar component
- Optional right icon button with custom action
- White background with subtle shadow
- Flexible icon input (Painter, ImageVector, or Resource ID)

### Usage:

```kotlin
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import com.app.ecarepro.core.designsystem.R

@Composable
fun MyScreenWithBottomBar() {
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            // Basic usage (no right icon)
            BottomSearchBarView(
                searchText = searchText,
                onSearchTextChange = { searchText = it }
            )
        }
    ) {
        // Screen content
    }
}

@Composable
fun WithDownloadIcon() {
    var searchText by remember { mutableStateOf("") }

    // With right icon (using drawable resource)
    BottomSearchBarView(
        searchText = searchText,
        onSearchTextChange = { searchText = it },
        placeholder = "Search assignments...",
        rightIconRes = R.drawable.ic_download,
        onRightIconClick = {
            // Handle download action
            downloadAllResults()
        }
    )
}

@Composable
fun WithVectorIcon() {
    var searchText by remember { mutableStateOf("") }

    // With right icon (using ImageVector)
    BottomSearchBarView(
        searchText = searchText,
        onSearchTextChange = { searchText = it },
        placeholder = "Search files...",
        rightIconVector = Icons.Default.Download,
        onRightIconClick = {
            // Handle icon action
        }
    )
}

@Composable
fun WithPainterIcon() {
    var searchText by remember { mutableStateOf("") }

    // With right icon (using Painter)
    BottomSearchBarView(
        searchText = searchText,
        onSearchTextChange = { searchText = it },
        placeholder = "Search documents...",
        rightIconPainter = painterResource(R.drawable.ic_download),
        onRightIconClick = {
            // Handle download
        }
    )
}
```

### Parameters:
- `searchText: String` - Current search text value
- `onSearchTextChange: (String) -> Unit` - Callback when text changes
- `modifier: Modifier = Modifier` - Optional modifier
- `placeholder: String = "Search here..."` - Placeholder text
- `rightIconPainter: Painter? = null` - Optional right icon as Painter
- `rightIconVector: ImageVector? = null` - Optional right icon as ImageVector
- `rightIconRes: Int? = null` - Optional right icon as drawable resource ID
- `onRightIconClick: (() -> Unit)? = null` - Optional callback for icon click
- `onSearchSubmit: (() -> Unit)? = null` - Optional callback on search submit

## Visual Specifications

### SearchBar:
- **Layout**: Horizontal with 12dp spacing
- **Padding**: Horizontal 14dp, Vertical 15dp
- **Border**: 0.5dp width, 12dp corner radius
- **Background**: App background color
- **Search Icon**: 18dp × 18dp
- **Clear Icon**: 13.5dp × 14dp (appears when text is present)
- **Font**: Inter Regular, 14sp
- **Text Color**: Primary text color
- **Placeholder Color**: Secondary text color

### BottomSearchBarView:
- **Layout**: Horizontal
- **Padding**: Horizontal 21dp, Vertical 10dp
- **Background**: White
- **Shadow**: 6dp elevation with black 6% opacity
- **Right Icon Button**:
  - Size: 20dp × 20dp
  - Padding: Horizontal 18dp, Vertical 14.5dp
  - Background: App background color
  - Corner Radius: 10dp
  - Icon Color: Primary text color

## Real-World Example

```kotlin
@Composable
fun SyllabusScreen(
    viewModel: SyllabusViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val syllabusItems by viewModel.filteredItems.collectAsState()

    Scaffold(
        bottomBar = {
            BottomSearchBarView(
                searchText = searchQuery,
                onSearchTextChange = { query ->
                    viewModel.updateSearchQuery(query)
                },
                placeholder = "Search syllabus...",
                rightIconRes = R.drawable.ic_download,
                onRightIconClick = {
                    viewModel.downloadAllSyllabus()
                },
                onSearchSubmit = {
                    viewModel.performSearch()
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(syllabusItems) { item ->
                SyllabusItem(item = item)
            }
        }
    }
}
```

## Design System Integration

The components use the app's design system:
- **Colors**: `MaterialTheme.appColors` (background, textPrimary, textSecondary, border)
- **Typography**: `MaterialTheme.appTypography.interRegular14px`
- **Icons**: Located in `core/designsystem/src/main/res/drawable/`
  - `ic_search.xml` - Magnifying glass icon
  - `ic_close.xml` - Clear/close icon (newly added)

## Tips

1. **State Management**: Use ViewModel or remember state for search text
2. **Debouncing**: Consider debouncing search queries to reduce API calls
3. **Keyboard Actions**: The SearchBar automatically handles "Search" IME action
4. **Clear Functionality**: Clear button appears automatically when text is present
5. **Accessibility**: Icon content descriptions are handled internally

## Example with Debouncing

```kotlin
@Composable
fun SearchWithDebounce() {
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var searchJob: Job? by remember { mutableStateOf(null) }

    BottomSearchBarView(
        searchText = searchText,
        onSearchTextChange = { query ->
            searchText = query
            // Cancel previous search job
            searchJob?.cancel()
            // Start new debounced search
            searchJob = scope.launch {
                delay(300) // 300ms debounce
                performSearch(query)
            }
        },
        placeholder = "Search..."
    )
}
```

## Notes

- Both components are fully responsive and adapt to light/dark themes
- The SearchBar can be used standalone in any layout
- The BottomSearchBarView is optimized for bottom bar placement
- Icons are tinted based on the current theme
- The shadow effect on BottomSearchBarView creates depth separation
