# EcarePro Top Bar Components

Two comprehensive top bar/app bar implementations for the EcarePro application, designed to maintain consistency across all screens.

## Components

### 1. `EcareProTopBar` - Custom Implementation
A fully custom navigation bar following the iOS NavigationBarView pattern with precise control over styling.

### 2. `EcareProAppBar` - Material3 Integration
Built on Material3's TopAppBar for better integration with Material Design components and automatic system UI handling.

---

## EcareProTopBar (Custom)

### When to Use
- Need exact pixel-perfect design matching iOS
- Custom spacing and padding requirements
- Full control over shadow and elevation
- Simpler component hierarchy

### Basic Usage

```kotlin
import com.app.ecarepro.designsystem.core.component.EcareProTopBar

@Composable
fun MyScreen() {
    Scaffold(
        topBar = {
            EcareProTopBar(
                title = "Leave Report",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        // Screen content
    }
}
```

### With Trailing Content

```kotlin
EcareProTopBar(
    title = "Documents",
    onBackClick = { navController.popBackStack() }
) {
    // Single icon
    Icon(
        imageVector = Icons.Default.Download,
        contentDescription = "Download",
        modifier = Modifier
            .size(20.dp)
            .clickable { handleDownload() }
    )
}
```

### With Multiple Actions

```kotlin
EcareProTopBar(
    title = "Messages",
    onBackClick = { navController.popBackStack() }
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        IconButton(onClick = { /* Search */ }) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
        IconButton(onClick = { /* Filter */ }) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter")
        }
    }
}
```

### With Text Button

```kotlin
EcareProTopBar(
    title = "Edit Profile",
    onBackClick = { navController.popBackStack() }
) {
    Text(
        text = "Save",
        color = MaterialTheme.appColors.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clickable { handleSave() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
```

### Without Back Button

```kotlin
EcareProTopBar(
    title = "Dashboard",
    onBackClick = {},
    showBackButton = false
) {
    IconButton(onClick = { /* Settings */ }) {
        Icon(Icons.Default.Settings, contentDescription = "Settings")
    }
}
```

### Alternative: EcareProTopBarNoBack

For screens without a back button (simpler API):

```kotlin
EcareProTopBarNoBack(
    title = "Home"
) {
    IconButton(onClick = { /* Notifications */ }) {
        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
    }
}
```

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | String | Required | Title text to display |
| `onBackClick` | () -> Unit | Required | Back button click handler |
| `modifier` | Modifier | Modifier | Optional modifier |
| `showBackButton` | Boolean | true | Show/hide back button |
| `backgroundColor` | Color | White | Background color |
| `contentColor` | Color | theme.textPrimary | Title and icon color |
| `elevation` | Dp | 4.dp | Shadow elevation |
| `trailingContent` | @Composable | {} | Trailing content lambda |

---

## EcareProAppBar (Material3)

### When to Use
- Need Material3 integration
- Automatic WindowInsets handling
- ScrollBehavior support
- Standard Material Design patterns

### Basic Usage

```kotlin
import com.app.ecarepro.designsystem.core.component.EcareProAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen() {
    Scaffold(
        topBar = {
            EcareProAppBar(
                title = "Leave Report",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        // Screen content
    }
}
```

### With Actions

```kotlin
EcareProAppBar(
    title = "Messages",
    onBackClick = { navController.popBackStack() },
    actions = {
        IconButton(onClick = { /* Search */ }) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
        IconButton(onClick = { /* More */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
    }
)
```

### With Text Action

```kotlin
EcareProAppBar(
    title = "Edit Profile",
    onBackClick = { navController.popBackStack() },
    actions = {
        TextButton(onClick = { handleSave() }) {
            Text(
                text = "Save",
                color = MaterialTheme.appColors.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
)
```

### Without Back Button

```kotlin
EcareProAppBar(
    title = "Home",
    onBackClick = null, // Pass null to hide back button
    actions = {
        IconButton(onClick = { /* Settings */ }) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
)
```

### Centered Title Variant

Use for modal screens or centered title layouts:

```kotlin
EcareProCenteredAppBar(
    title = "Settings",
    onBackClick = { navController.popBackStack() },
    actions = {
        IconButton(onClick = { /* Done */ }) {
            Icon(Icons.Default.Check, contentDescription = "Done")
        }
    }
)
```

### Large Title Variant

Use for main screens with prominent titles:

```kotlin
EcareProLargeAppBar(
    title = "Dashboard",
    actions = {
        IconButton(onClick = { /* Profile */ }) {
            Icon(Icons.Default.Person, contentDescription = "Profile")
        }
    }
)
```

### Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `title` | String | Required | Title text to display |
| `onBackClick` | (() -> Unit)? | Required | Back button handler (null hides button) |
| `modifier` | Modifier | Modifier | Optional modifier |
| `backgroundColor` | Color | White | Background color |
| `contentColor` | Color | theme.textPrimary | Title and icon color |
| `elevation` | Dp | 4.dp | Shadow elevation |
| `actions` | @Composable | {} | Actions lambda |

---

## Complete Screen Examples

### Example 1: Basic Screen with TopBar

```kotlin
@Composable
fun LeaveReportScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            EcareProTopBar(
                title = "Leave Report",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Screen content
        }
    }
}
```

### Example 2: Screen with Actions

```kotlin
@Composable
fun MessagesScreen(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Scaffold(
        topBar = {
            EcareProAppBar(
                title = "Messages",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onFilterClick) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Screen content
    }
}
```

### Example 3: Edit Screen with Save

```kotlin
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        topBar = {
            EcareProTopBar(
                title = "Edit Profile",
                onBackClick = onBackClick
            ) {
                TextButton(onClick = onSaveClick) {
                    Text(
                        text = "Save",
                        color = MaterialTheme.appColors.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) { paddingValues ->
        // Form content
    }
}
```

### Example 4: Bottom Sheet with TopBar

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    onDismiss: () -> Unit
) {
    Column {
        EcareProCenteredAppBar(
            title = "Filter Options",
            onBackClick = onDismiss
        )

        // Bottom sheet content
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Filter options
        }
    }
}
```

---

## Design Specifications

### Spacing
- **Leading padding**: 24dp (custom) / 8dp (Material3)
- **Trailing padding**: 20dp (custom) / 8dp (Material3)
- **Vertical padding**: 20dp
- **Horizontal spacing**: 16dp between elements

### Typography
- **Font Family**: Inter
- **Font Weight**: Medium
- **Font Size**: 16sp
- **Max Lines**: 1

### Icons
- **Back Icon Size**: 20dp
- **Action Icon Size**: 20dp (recommended)

### Shadow
- **Elevation**: 4dp
- **Shadow Color**: Black with 6% opacity
- **Shadow Direction**: Bottom only

### Colors
- **Background**: White (customizable)
- **Content**: Theme's textPrimary (customizable)

---

## Choosing Between Components

| Feature | EcareProTopBar | EcareProAppBar |
|---------|----------------|----------------|
| **Custom styling** | ✅ Full control | ⚠️ Limited |
| **Material3 integration** | ❌ | ✅ |
| **WindowInsets handling** | Manual | ✅ Automatic |
| **ScrollBehavior support** | ❌ | ✅ |
| **Centered title variant** | Manual | ✅ Built-in |
| **Large title variant** | Manual | ✅ Built-in |
| **Complexity** | Simple | More features |
| **File size** | Smaller | Larger |

### Recommendation
- Use **EcareProTopBar** for simple screens with custom requirements
- Use **EcareProAppBar** for complex screens needing Material3 features

---

## Migration from Existing TopAppBars

### Before
```kotlin
TopAppBar(
    title = { Text("My Screen") },
    navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
        }
    }
)
```

### After
```kotlin
EcareProAppBar(
    title = "My Screen",
    onBackClick = { navController.popBackStack() }
)
```

---

## Accessibility

Both components include:
- ✅ Proper content descriptions for screen readers
- ✅ Semantic roles for clickable elements
- ✅ Ripple effects for touch feedback
- ✅ Minimum touch target sizes (48dp for Material3)

---

## Testing

### Preview in Android Studio
Both components include comprehensive preview functions. Use them to:
1. View different configurations
2. Test with different content
3. Check dark theme support
4. Validate spacing and alignment

### UI Tests
```kotlin
@Test
fun testTopBarDisplaysTitle() {
    composeTestRule.setContent {
        EcareProTopBar(
            title = "Test Title",
            onBackClick = {}
        )
    }

    composeTestRule.onNodeWithText("Test Title").assertIsDisplayed()
}
```

---

## Common Patterns

### Pattern 1: With Menu Overflow
```kotlin
EcareProAppBar(
    title = "Settings",
    onBackClick = { navController.popBackStack() },
    actions = {
        var expanded by remember { mutableStateOf(false) }

        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, "More options")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Option 1") },
                onClick = { /* Handle */ }
            )
            DropdownMenuItem(
                text = { Text("Option 2") },
                onClick = { /* Handle */ }
            )
        }
    }
)
```

### Pattern 2: With Badge
```kotlin
EcareProTopBar(
    title = "Notifications",
    onBackClick = { navController.popBackStack() }
) {
    BadgedBox(
        badge = {
            Badge { Text("3") }
        }
    ) {
        Icon(Icons.Default.Notifications, "Notifications")
    }
}
```

### Pattern 3: Conditional Actions
```kotlin
EcareProAppBar(
    title = "Edit Mode",
    onBackClick = { navController.popBackStack() },
    actions = {
        if (isEditMode) {
            TextButton(onClick = { saveChanges() }) {
                Text("Save")
            }
            IconButton(onClick = { cancelEdit() }) {
                Icon(Icons.Default.Close, "Cancel")
            }
        } else {
            IconButton(onClick = { enableEditMode() }) {
                Icon(Icons.Default.Edit, "Edit")
            }
        }
    }
)
```

---

## Troubleshooting

### Issue: Shadow not visible
**Solution**: Ensure the topBar is wrapped in a Scaffold or has a contrasting background.

### Issue: Content clipped
**Solution**: Ensure proper padding is applied using the `paddingValues` from Scaffold.

### Issue: Actions not clickable
**Solution**: Make sure IconButton or clickable modifiers are used, not just Icons alone.

### Issue: Back button not working
**Solution**: Verify `onBackClick` is properly connected to your navigation controller.

---

## Support

For questions or issues with these components, refer to:
- Component source code with inline documentation
- Preview functions for visual reference
- This README for usage patterns
