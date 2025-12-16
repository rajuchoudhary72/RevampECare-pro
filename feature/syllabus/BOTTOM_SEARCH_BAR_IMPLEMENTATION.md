# Bottom Search Bar Implementation

## Overview
Successfully migrated the search bar from top to bottom of the Syllabus screen, using the **common BottomSearchBarView component** from the designsystem module.

## Latest Update (December 2024)
**Status:** ✅ Migrated to Common Component

The Syllabus screen now uses the reusable `BottomSearchBarView` component from the designsystem module, ensuring consistency across the entire app.

## Changes Made

### 1. Common Component Used
**Component:** `com.app.ecarepro.designsystem.core.component.BottomSearchBarView`

This is a common, reusable component available to all features in the app. It includes:
- **SearchBar** - Internal search input component with magnifying glass and clear button
- **BottomSearchBarView** - Wrapper with shadow effect and optional right icon

#### Features:
✅ **Bottom positioning** with top shadow effect
✅ **Search icon** on the left (magnifying glass)
✅ **Clear button** (X icon) - appears when text is entered
✅ **Optional right icon** - supports ImageVector, Painter, or drawable resource
✅ **Local search** - same functionality as before
✅ **Reusable** - Available to all features
✅ **Theme-aware** - Adapts to light/dark themes

### 2. SyllabusScreen.kt Updates
**File:** `feature/syllabus/src/main/java/com/app/ecarepro/feature/syllabus/SyllabusScreen.kt`

#### Changes:
- ❌ Removed feature-specific BottomSearchBar import
- ✅ Added import for common BottomSearchBarView from designsystem
- ✅ Updated bottomBar to use BottomSearchBarView
- ✅ Added filter icon using Material Icons (FilterList)
- ✅ Maintained all search functionality

## Visual Specifications

### Bottom Search Bar Container
- **Position:** Bottom of screen
- **Background:** White
- **Shadow:** 6dp elevation with 6% black opacity (top shadow)
- **Padding:** Horizontal 21dp, Vertical 10dp

### Search Input Field
- **Border:** 0.5dp with divider color
- **Corner Radius:** 12dp
- **Padding:** Horizontal 14dp, Vertical 15dp
- **Icons:**
  - Search (left): 18dp
  - Clear (right): 13.5dp (when text present)

### Filter Button
- **Size:** 20dp
- **Background:** App background color
- **Corner Radius:** 10dp
- **Padding:** Horizontal 18dp, Vertical 14.5dp

## Usage

### Current Implementation (Using Common Component)
```kotlin
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList

// With right icon (ImageVector)
BottomSearchBarView(
    searchText = searchText,
    onSearchTextChange = { text -> /* handle search */ },
    placeholder = "Search by title or subject",
    rightIconVector = Icons.Default.FilterList,
    onRightIconClick = {
        // Handle filter/sort action
    }
)
```

### Without Right Icon
```kotlin
BottomSearchBarView(
    searchText = searchText,
    onSearchTextChange = { text -> /* handle search */ },
    placeholder = "Search here..."
)
```

### With Drawable Resource Icon
```kotlin
BottomSearchBarView(
    searchText = searchText,
    onSearchTextChange = { text -> /* handle search */ },
    placeholder = "Search...",
    rightIconRes = R.drawable.ic_download,
    onRightIconClick = { /* handle download */ }
)
```

## Features Comparison

| Feature | Old (Top) | New (Bottom) |
|---------|-----------|--------------|
| Position | Top (in header) | Bottom (fixed) |
| Search Icon | ✅ | ✅ |
| Clear Button | ❌ | ✅ (auto-show) |
| Filter Icon | ❌ | ✅ (optional) |
| Shadow Effect | ❌ | ✅ (top shadow) |
| Local Search | ✅ | ✅ |
| Placeholder | ✅ | ✅ |

## Screen Layout

### Before (Old Layout)
```
┌──────────────────────────────┐
│ ← Syllabus        Add New    │ TopBar
├──────────────────────────────┤
│ 🔍 Search by title...        │ SearchBar (Top)
├──────────────────────────────┤
│ All  LKG  HKG  UKG  1st 2nd  │ ClassTabs
├──────────────────────────────┤
│                              │
│  Syllabus List Items         │
│                              │
│                              │
└──────────────────────────────┘
```

### After (New Layout)
```
┌──────────────────────────────┐
│ ← Syllabus        Add New    │ TopBar
├──────────────────────────────┤
│ All  LKG  HKG  UKG  1st 2nd  │ ClassTabs
├──────────────────────────────┤
│                              │
│  Syllabus List Items         │
│                              │
│                              │
├──────────────────────────────┤
│ 🔍 Search...          [≡]    │ SearchBar (Bottom)
└──────────────────────────────┘
```

## Implementation Details

### Scaffold Integration
```kotlin
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList

EcareProScaffold(
    topBar = { /* TopAppBar + ClassTabs */ },
    bottomBar = {
        BottomSearchBarView(
            searchText = uiState.data.searchQuery,
            onSearchTextChange = {
                handleIntent(SyllabusIntent.OnSearchQueryChanged(it))
            },
            placeholder = "Search by title or subject",
            rightIconVector = Icons.Default.FilterList,
            onRightIconClick = { /* Filter action */ }
        )
    }
) { paddingValues ->
    /* Content */
}
```

### Search Functionality
The search functionality remains exactly the same:
- Searches by title and subject (local filtering)
- Updates in real-time as user types
- Case-insensitive search
- Works with class filter

### Filter Icon (TODO)
The filter icon is currently a placeholder. To implement:

1. **Add Intent:**
```kotlin
sealed interface SyllabusIntent {
    // ... existing intents
    data object OnFilterClicked : SyllabusIntent
}
```

2. **Handle in ViewModel:**
```kotlin
when (intent) {
    is SyllabusIntent.OnFilterClicked -> showFilterBottomSheet()
}
```

3. **Update Screen:**
```kotlin
onRightIconClick = {
    handleIntent(SyllabusIntent.OnFilterClicked)
}
```

## Testing

### Preview Functions Available:
**Common Component Previews** (in BottomSearchBarView.kt):
- `BottomSearchBarViewPreview_Empty()` - Empty search bar
- `BottomSearchBarViewPreview_WithText()` - With text entered
- `BottomSearchBarViewPreview_WithIcon()` - With download icon
- `BottomSearchBarViewPreview_Complete()` - With text and icon
- `BottomSearchBarViewPreview_AllVariants()` - All states together

**SearchBar Component Previews** (in SearchBar.kt):
- `SearchBarPreview_Empty()` - Empty search bar
- `SearchBarPreview_WithText()` - With sample text
- `SearchBarPreview_CustomPlaceholder()` - Long placeholder example
- `SearchBarPreview_AllStates()` - Empty and active states

### Test Scenarios:
✅ Empty search (placeholder visible)
✅ Typing text (placeholder hides, clear button appears)
✅ Clear button (clears text)
✅ Filter icon click (handles callback)
✅ Search filtering (works with existing logic)
✅ Theme changes (adapts to light/dark mode)

## Migration Guide for Other Screens

To add bottom search bar to other screens:

1. **Import Common Component:**
```kotlin
import com.app.ecarepro.designsystem.core.component.BottomSearchBarView
```

2. **Add to Scaffold:**
```kotlin
Scaffold(
    bottomBar = {
        BottomSearchBarView(
            searchText = searchQuery,
            onSearchTextChange = { /* handle */ },
            placeholder = "Search..."
        )
    }
)
```

3. **Optional: Add Custom Icon:**
```kotlin
// Using Material Icon
BottomSearchBarView(
    searchText = searchQuery,
    onSearchTextChange = { /* handle */ },
    rightIconVector = Icons.Default.Download,
    onRightIconClick = { /* handle click */ }
)

// Or using drawable resource
BottomSearchBarView(
    searchText = searchQuery,
    onSearchTextChange = { /* handle */ },
    rightIconRes = R.drawable.ic_download,
    onRightIconClick = { /* handle click */ }
)
```

## Icons Used

| Icon | Type | Usage | Location |
|------|------|-------|----------|
| Search | Drawable (ic_search.xml) | Left side of search field | core/designsystem/src/main/res/drawable/ |
| Close | Drawable (ic_close.xml) | Clear button when text entered | core/designsystem/src/main/res/drawable/ |
| Filter | Material Icon (Icons.Default.FilterList) | Optional right button (Syllabus screen) | Material Icons |

## Benefits

✅ **Better UX** - Search bar always visible at bottom
✅ **More Space** - Removed clutter from top bar
✅ **Thumb-Friendly** - Bottom position easier to reach
✅ **Visual Feedback** - Clear button and shadow effects
✅ **Flexible** - Optional filter/action button
✅ **Common Component** - Shared across all features in designsystem module
✅ **Reusable** - Can be used in any screen
✅ **Consistent** - Same look and feel everywhere
✅ **Theme-Aware** - Automatically adapts to light/dark themes
✅ **Well-Documented** - Comprehensive usage guide with previews

## Known Limitations

⚠️ Filter button functionality not implemented in Syllabus screen (TODO)

## Future Enhancements

- [ ] Implement filter/sort functionality in Syllabus screen
- [ ] Add search history dropdown (optional enhancement)
- [ ] Add voice search option (optional enhancement)
- [ ] Add search suggestions (optional enhancement)
- [ ] Animate search bar expand/collapse (optional enhancement)
- [ ] Add haptic feedback on clear (optional enhancement)

---

**Status:** ✅ Complete and Using Common Component
**Date:** December 2024
**Component Location:** `core/designsystem/src/main/java/com/app/ecarepro/designsystem/core/component/`
**Based on:** BottomSearchBarView SwiftUI design documentation

## Component Files

1. **BottomSearchBarView.kt** - Main wrapper component with shadow and optional icon
2. **SearchBar.kt** - Internal search input component
3. **ic_close.xml** - Close/clear icon drawable
4. **BOTTOM_SEARCH_BAR_USAGE.md** - Comprehensive usage documentation with examples
