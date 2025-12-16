# EcareProClassTabs - Component Documentation

## Overview
`EcareProClassTabs` is a reusable scrollable tab row component for displaying class/category tabs with automatic ordinal conversion and custom styling. Perfect for filtering content by class, category, or any tabbed navigation.

## Features

✅ **Scrollable Tabs** - Handles long lists of tabs with smooth scrolling
✅ **Automatic Ordinal Conversion** - Converts numbers to ordinals (1 → 1st, 2 → 2nd, etc.)
✅ **Custom Text Transform** - Apply custom transformations to tab labels
✅ **Selected State Styling** - Visual feedback for selected tabs
✅ **Flexible Customization** - Colors, spacing, and typography
✅ **Reusable** - Works across all features
✅ **Theme-Aware** - Adapts to light/dark themes
✅ **Preview Functions** - Multiple preview variants available

## Visual Design

```
┌────────────────────────────────────────────────┐
│  All   1st   2nd   3rd   4th   5th   UKG  LKG │
│  ───   ───   ───   ───   ───   ───   ───  ─── │
│       ^^^^                                      │
│      Selected                                   │
└────────────────────────────────────────────────┘
```

## Basic Usage

### Simple Implementation

```kotlin
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs

@Composable
fun MyScreen() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    EcareProClassTabs(
        selectedTabIndex = selectedIndex,
        tabs = listOf("All", "1", "2", "3", "4", "5"),
        onTabClick = { index ->
            selectedIndex = index
            // Handle tab selection
        }
    )
}
```

### In Scaffold Top Bar

```kotlin
@Composable
fun MyScreenWithTabs() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "UKG", "LKG", "1", "2", "3", "4", "5")

    Scaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Syllabus",
                    onNavigationClicked = { /* back */ }
                )

                EcareProClassTabs(
                    selectedTabIndex = selectedIndex,
                    tabs = tabs,
                    onTabClick = { selectedIndex = it }
                )
            }
        }
    ) { paddingValues ->
        // Content filtered by selected tab
        FilteredContent(selectedIndex = selectedIndex)
    }
}
```

## Parameters

### Required Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `selectedTabIndex` | `Int` | Index of the currently selected tab (0-based) |
| `tabs` | `List<String>` | List of tab labels to display |
| `onTabClick` | `(Int) -> Unit` | Callback when a tab is clicked, receives index |

### Optional Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `modifier` | `Modifier` | `Modifier` | Modifier for the tab row |
| `containerColor` | `Color` | `White` | Background color of tab row |
| `selectedContentColor` | `Color` | `appColors.primary` | Color of selected tab text |
| `unselectedContentColor` | `Color` | `appColors.textPrimary` | Color of unselected tab text |
| `edgePadding` | `Dp` | `0.dp` | Padding at start/end of tab row |
| `minTabWidth` | `Dp` | `70.dp` | Minimum width for each tab |
| `applyOrdinalTransform` | `Boolean` | `true` | Auto-convert numbers to ordinals |
| `textTransform` | `((String) -> String)?` | `null` | Custom text transformation |

## Use Cases

### 1. Class Selection (with Ordinals)

```kotlin
// Numbers automatically converted to ordinals: 1 → 1st, 2 → 2nd, etc.
EcareProClassTabs(
    selectedTabIndex = selectedClassIndex,
    tabs = listOf("All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
    onTabClick = { index ->
        viewModel.filterByClass(index)
    }
)
```

**Result:** `All   1st   2nd   3rd   4th   5th   6th   7th   8th   9th   10th`

### 2. Mixed Content (Numbers + Text)

```kotlin
// Mixed numeric and text labels
EcareProClassTabs(
    selectedTabIndex = selectedIndex,
    tabs = listOf("All", "UKG", "LKG", "1", "2", "3", "4", "5"),
    onTabClick = { selectedIndex = it }
)
```

**Result:** `All   UKG   LKG   1st   2nd   3rd   4th   5th`

### 3. Text-Only Tabs (No Ordinals)

```kotlin
// Pure text labels without ordinal conversion
EcareProClassTabs(
    selectedTabIndex = selectedIndex,
    tabs = listOf("All", "Mathematics", "Science", "English", "History"),
    onTabClick = { selectedIndex = it },
    applyOrdinalTransform = false
)
```

**Result:** `All   Mathematics   Science   English   History`

### 4. Status/Filter Tabs

```kotlin
// Status tabs for filtering content
EcareProClassTabs(
    selectedTabIndex = statusIndex,
    tabs = listOf("All", "Active", "Pending", "Completed", "Cancelled"),
    onTabClick = { statusIndex = it },
    applyOrdinalTransform = false
)
```

### 5. Custom Text Transformation

```kotlin
// Apply custom transformation (e.g., uppercase)
EcareProClassTabs(
    selectedTabIndex = selectedIndex,
    tabs = listOf("all", "math", "science", "english"),
    onTabClick = { selectedIndex = it },
    applyOrdinalTransform = false,
    textTransform = { it.uppercase() }
)
```

**Result:** `ALL   MATH   SCIENCE   ENGLISH`

### 6. Custom Colors

```kotlin
// Custom color scheme
EcareProClassTabs(
    selectedTabIndex = selectedIndex,
    tabs = listOf("All", "1", "2", "3", "4"),
    onTabClick = { selectedIndex = it },
    selectedContentColor = Color(0xFF4CAF50),
    unselectedContentColor = Color(0xFF757575),
    containerColor = Color(0xFFF5F5F5)
)
```

## Integration with ViewModel

### Complete Example

```kotlin
@Composable
fun SyllabusScreen(
    viewModel: SyllabusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                EcareProTopAppBar(
                    title = "Syllabus",
                    onNavigationClicked = { viewModel.navigateBack() }
                )

                if (uiState is UiState.Success) {
                    EcareProClassTabs(
                        selectedTabIndex = uiState.data.selectedClassIndex,
                        tabs = uiState.data.classTabs,
                        onTabClick = { index ->
                            viewModel.handleIntent(
                                SyllabusIntent.OnClassSelected(index)
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Filtered content based on selected tab
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(uiState.data.filteredItems) { item ->
                ItemRow(item = item)
            }
        }
    }
}
```

## Styling Details

### Selected Tab
- **Font:** Inter SemiBold, 16sp
- **Color:** Primary color (from theme)
- **Indicator:** Underline in primary color

### Unselected Tab
- **Font:** Inter Medium, 14sp
- **Color:** Text primary color (from theme)
- **No Indicator**

### Container
- **Background:** White (customizable)
- **Edge Padding:** 0dp by default
- **Min Tab Width:** 70dp

## Ordinal Conversion Rules

The component automatically converts numeric strings to ordinals:

| Input | Output | Rule |
|-------|--------|------|
| "1" | "1st" | Ends with 1 (except 11) |
| "2" | "2nd" | Ends with 2 (except 12) |
| "3" | "3rd" | Ends with 3 (except 13) |
| "4" - "10" | "4th" - "10th" | All others use "th" |
| "11" | "11th" | Special case (11-13) |
| "12" | "12th" | Special case (11-13) |
| "13" | "13th" | Special case (11-13) |
| "21" | "21st" | Ends with 1 (not 11) |
| "UKG" | "UKG" | Non-numeric kept as-is |

## Preview Functions

The component includes 5 preview variants:

1. **EcareProClassTabsPreview_Numbers** - Numbers with ordinals
2. **EcareProClassTabsPreview_Mixed** - Mixed numbers and text
3. **EcareProClassTabsPreview_TextOnly** - Text labels only
4. **EcareProClassTabsPreview_CustomTransform** - With custom transformation
5. **EcareProClassTabsPreview_AllVariants** - All variants together

### Viewing Previews in Android Studio

1. Open `EcareProClassTabs.kt`
2. Click **Split** or **Design** view
3. View all preview variants

## Migration from Feature-Specific Component

### Old Implementation (Feature-Specific)

```kotlin
import ClassTabs

ClassTabs(
    selectedClassIndex = selectedIndex,
    classes = classList,
    onClickClassTabs = { index -> /* handle */ }
)
```

### New Implementation (Common Component)

```kotlin
import com.app.ecarepro.designsystem.core.component.EcareProClassTabs

EcareProClassTabs(
    selectedTabIndex = selectedIndex,
    tabs = classList,
    onTabClick = { index -> /* handle */ }
)
```

### Parameter Mapping

| Old Parameter | New Parameter |
|---------------|---------------|
| `selectedClassIndex` | `selectedTabIndex` |
| `classes` | `tabs` |
| `onClickClassTabs` | `onTabClick` |

## Real-World Examples

### Example 1: Attendance Screen

```kotlin
@Composable
fun AttendanceScreen() {
    var selectedClass by remember { mutableIntStateOf(0) }

    Column {
        EcareProClassTabs(
            selectedTabIndex = selectedClass,
            tabs = listOf("All", "UKG", "LKG", "1", "2", "3", "4", "5"),
            onTabClick = { selectedClass = it }
        )

        AttendanceList(classFilter = selectedClass)
    }
}
```

### Example 2: Assignment Screen

```kotlin
@Composable
fun AssignmentScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending", "Submitted", "Graded", "Overdue")

    Column {
        EcareProClassTabs(
            selectedTabIndex = selectedTab,
            tabs = tabs,
            onTabClick = { selectedTab = it },
            applyOrdinalTransform = false
        )

        AssignmentList(status = tabs[selectedTab])
    }
}
```

### Example 3: Subject Filter

```kotlin
@Composable
fun SubjectScreen() {
    var selectedSubject by remember { mutableIntStateOf(0) }
    val subjects = listOf("All", "Math", "Science", "English", "History")

    EcareProClassTabs(
        selectedTabIndex = selectedSubject,
        tabs = subjects,
        onTabClick = { selectedSubject = it },
        applyOrdinalTransform = false,
        minTabWidth = 80.dp
    )
}
```

## Best Practices

✅ **Use State Management** - Store selected index in ViewModel or State
✅ **Disable Ordinals for Text** - Set `applyOrdinalTransform = false` for text-only tabs
✅ **Provide Clear Labels** - Use concise, descriptive tab names
✅ **Handle Selection** - Always respond to tab clicks with filtering/navigation
✅ **Consistent Styling** - Use default theme colors for consistency
✅ **Accessibility** - Tab labels are automatically accessible

## Common Patterns

### Pattern 1: Filter Content by Tab

```kotlin
val items = remember(selectedTabIndex) {
    if (selectedTabIndex == 0) {
        allItems
    } else {
        allItems.filter { it.classId == selectedTabIndex }
    }
}
```

### Pattern 2: Tab Labels from Enum

```kotlin
enum class Status { ALL, ACTIVE, PENDING, COMPLETED }

val tabs = Status.values().map { it.name.lowercase().capitalize() }
```

### Pattern 3: Dynamic Tabs from API

```kotlin
val classes by viewModel.availableClasses.collectAsState()

EcareProClassTabs(
    selectedTabIndex = selectedIndex,
    tabs = listOf("All") + classes.map { it.name },
    onTabClick = { selectedIndex = it }
)
```

## Troubleshooting

### Issue: Tabs not scrolling
**Solution:** Ensure tabs list is long enough. Component auto-enables scrolling when needed.

### Issue: Ordinals not appearing
**Solution:** Check `applyOrdinalTransform = true` (it's true by default).

### Issue: Wrong tab selected on click
**Solution:** Make sure you're updating the `selectedTabIndex` state in `onTabClick`.

### Issue: Custom colors not applying
**Solution:** Verify you're using `MaterialTheme.appColors` or passing explicit colors.

## Performance Notes

- ✅ Efficient rendering with `forEachIndexed`
- ✅ Lazy scrolling for long lists
- ✅ No unnecessary recompositions
- ✅ Optimized text transformation

## Component Location

**File:** `core/designsystem/src/main/java/com/app/ecarepro/designsystem/core/component/EcareProClassTabs.kt`

## Related Components

- **EcareProTopAppBar** - Often used together in top bar
- **BottomSearchBarView** - Often used in bottom bar
- **EcareProScaffold** - Container for screen layout

---

**Status:** ✅ Production Ready
**Version:** 1.0
**Created:** December 2024
**Reusable:** Yes - Available to all features
