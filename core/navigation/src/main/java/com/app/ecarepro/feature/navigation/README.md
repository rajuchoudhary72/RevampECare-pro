

# ECare Pro Navigation System

Comprehensive navigation layer for Android using Jetpack Compose, based on iOS NavigationManager architecture.

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Core Components](#core-components)
3. [Setup](#setup)
4. [Usage Examples](#usage-examples)
5. [Bottom Sheets](#bottom-sheets)
6. [Tab Navigation](#tab-navigation)
7. [Modal Presentations](#modal-presentations)
8. [Best Practices](#best-practices)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         AppContent                                   │
│  ┌────────────────────────────────────────────────────────────────┐ │
│  │                  BottomSheetOverlay()                           │ │
│  │  ┌──────────────────────────────────────────────────────────┐  │ │
│  │  │                    MainContent                            │  │ │
│  │  │  ┌────────────────────────────────────────────────────┐  │  │ │
│  │  │  │        TabContainer (Logged In)                    │  │  │ │
│  │  │  │  ┌──────────────────────────────────────────────┐  │  │  │ │
│  │  │  │  │    ECNavigationAttacher (per tab)            │  │  │  │ │
│  │  │  │  │  ┌────────────────────────────────────────┐  │  │  │  │ │
│  │  │  │  │  │         NavigationHost                 │  │  │  │  │ │
│  │  │  │  │  │    (Router manages stack)              │  │  │  │  │ │
│  │  │  │  │  └────────────────────────────────────────┘  │  │  │  │ │
│  │  │  │  └──────────────────────────────────────────────┘  │  │  │ │
│  │  │  └────────────────────────────────────────────────────┘  │  │ │
│  │  └──────────────────────────────────────────────────────────┘  │ │
│  └────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       NavigationManager (Singleton)                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐  │
│  │   Tab Routers   │  │  Bottom Sheet   │  │  Modal Presentation │  │
│  │   Dictionary    │  │     State       │  │       State         │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Core Components

### 1. NavigationManager (Singleton)

Central hub for all navigation operations.

```kotlin
object NavigationManager {
    // State flows
    val isKeyboardVisible: StateFlow<Boolean>
    val isBottomSheetPresented: StateFlow<Boolean>
    val selectedTab: StateFlow<TabbarItem>

    // Methods
    fun navigateToRoute(route: RouteType, type: NavigationType)
    fun navigateBack()
    fun popToRoot()
    fun switchToTab(tab: TabbarItem)
    fun presentBottomSheet(...)
    fun dismissBottomSheet()
}
```

### 2. Router

Manages navigation stack for a specific context (tab or modal).

```kotlin
class Router(
    var associatedTab: TabbarItem? = null,
    var isPresentationRouter: Boolean = false
) {
    val navStack: List<RouteType>
    fun navigateTo(route: RouteType)
    fun navigateBack(): Boolean
    fun popToRoot()
}
```

### 3. RouteType (Sealed Class)

Type-safe navigation destinations.

```kotlin
sealed class RouteType : Parcelable {
    object Home : RouteType()
    object Dashboard : RouteType()
    data class AssignmentDetail(val id: Int) : RouteType()
    // ... all app screens
}
```

### 4. NavigationType

Defines how navigation is performed.

```kotlin
sealed class NavigationType {
    object Push : NavigationType()
    sealed class Present : NavigationType() {
        object Sheet : Present()
        object FullScreen : Present()
    }
}
```

---

## Setup

### Step 1: Add to Root Composable

```kotlin
@Composable
fun App() {
    EcareProTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Your main content
            MainContent()

            // Add bottom sheet overlay at root level
            BottomSheetOverlay()
        }
    }
}
```

### Step 2: Setup Tab Navigation

```kotlin
@Composable
fun MainContent() {
    val authState by AuthHandler.authStatus.collectAsState()

    when (authState) {
        AuthStatus.LoggedIn -> TabContainer()
        AuthStatus.LoggedOut -> OnboardingFlow()
    }
}

@Composable
fun TabContainer() {
    val selectedTab by NavigationManager.selectedTab.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                TabbarItem.visibleTabs.forEach { tab ->
                    BottomNavigationItem(
                        icon = { Icon(tab.icon, tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTab == tab,
                        onClick = { NavigationManager.switchToTab(tab) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            TabbarItem.visibleTabs.forEach { tab ->
                if (selectedTab == tab) {
                    TabContent(tab = tab)
                }
            }
        }
    }
}
```

### Step 3: Setup Tab Content with Navigation

```kotlin
@Composable
fun TabContent(tab: TabbarItem) {
    val router = remember {
        Router(associatedTab = tab).also {
            NavigationManager.registerRouter(it, tab)
        }
    }

    ECNavigationAttacher(router = router) {
        tab.Content() // Root screen for this tab
    }
}
```

---

## Usage Examples

### 1. Push Navigation

```kotlin
// Simple push
NavigationManager.navigateToRoute(RouteType.Assignment)

// Push with data
NavigationManager.navigateToRoute(
    RouteType.AssignmentDetail(assignmentId = 123)
)

// Using router directly
NavigationManager.router?.navigateTo(RouteType.LeaveReport)
```

### 2. Back Navigation

```kotlin
// Navigate back
NavigationManager.navigateBack()

// Pop to root
NavigationManager.popToRoot()

// Pop to specific route
NavigationManager.router?.popTo(RouteType.Home)
```

### 3. Modal Presentations

```kotlin
// Sheet presentation
NavigationManager.navigateToRoute(
    RouteType.AddAssignment(null),
    NavigationType.Present.Sheet
)

// Full screen modal
NavigationManager.navigateToRoute(
    RouteType.EditProfile,
    NavigationType.Present.FullScreen
)
```

---

## Bottom Sheets

### Basic Bottom Sheet

```kotlin
NavigationManager.presentBottomSheet(
    content = {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Bottom Sheet Content")
            Button(onClick = { NavigationManager.dismissBottomSheet() }) {
                Text("Close")
            }
        }
    },
    title = "My Bottom Sheet"
)
```

### Selection List Bottom Sheet

```kotlin
data class Subject(val id: Int, val name: String)

fun showSubjectPicker(
    subjects: List<Subject>,
    onSelect: (Subject) -> Unit
) {
    NavigationManager.presentBottomSheet(
        content = {
            SelectionList(
                items = subjects,
                selectedItem = null,
                getTitle = { it.name },
                getId = { it.id },
                onSelect = { subject ->
                    onSelect(subject)
                    NavigationManager.dismissBottomSheet()
                }
            )
        },
        title = "Select Subject",
        heightConfig = BottomSheetHeightConfig.Dynamic
    )
}
```

### Date Picker Bottom Sheet

```kotlin
fun showDatePicker(
    currentDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    NavigationManager.presentBottomSheet(
        content = {
            DatePickerSheet(
                selectedDate = currentDate,
                onDateSelected = { date ->
                    onDateSelected(date)
                    NavigationManager.dismissBottomSheet()
                }
            )
        },
        title = "Select Date",
        heightConfig = BottomSheetHeightConfig.Dynamic
    )
}
```

### Custom Height Bottom Sheet

```kotlin
NavigationManager.presentBottomSheet(
    content = { /* Your content */ },
    title = "Student List",
    heightConfig = BottomSheetHeightConfig.BelowStatusBar, // Nearly full screen
    isDismissible = true,
    style = BottomSheetStyle(
        background = MaterialTheme.appColors.surface,
        isDraggable = true
    )
)
```

### Non-Dismissible Bottom Sheet (Forms)

```kotlin
NavigationManager.presentBottomSheet(
    content = {
        AddAssignmentForm(
            onSave = {
                // Save logic
                NavigationManager.dismissBottomSheet()
            },
            onCancel = {
                NavigationManager.dismissBottomSheet()
            }
        )
    },
    title = "Add Assignment",
    heightConfig = BottomSheetHeightConfig.BelowStatusBar,
    isDismissible = false, // Prevent accidental dismissal
    showHeaderContentSeparator = true
)
```

---

## Tab Navigation

### Switch Tabs Programmatically

```kotlin
// Switch to home tab
NavigationManager.switchToTab(TabbarItem.HOME)

// Switch to messages
NavigationManager.switchToTab(TabbarItem.MESSAGE)

// Get current tab
val currentTab = NavigationManager.selectedTab.collectAsState().value
```

### Deep Linking to Tab + Screen

```kotlin
fun navigateToNotificationDetail(notificationId: Int) {
    // Switch to notifications tab
    NavigationManager.switchToTab(TabbarItem.NOTIFICATION)

    // Navigate to detail screen
    NavigationManager.navigateToRoute(
        RouteType.NotificationDetail(notificationId)
    )
}
```

### Tab-Specific Navigation

```kotlin
// Each tab maintains its own navigations stack
// Switching between tabs preserves their individual stacks

// User flow example:
// 1. On Home tab, navigate to Assignment -> AssignmentDetail
// 2. Switch to Messages tab
// 3. Switch back to Home tab
// Result: User is still on AssignmentDetail screen (stack preserved)
```

---

## Modal Presentations

### Sheet with Navigation

Sheets can have their own navigation stack:

```kotlin
NavigationManager.navigateToRoute(
    RouteType.AddAssignment(null),
    NavigationType.Present.Sheet
)

// Inside AddAssignmentScreen, you can navigate further:
NavigationManager.navigateToRoute(RouteType.SelectStudents)
```

### Full Screen Modal

```kotlin
NavigationManager.navigateToRoute(
    RouteType.VideoPlayer(videoUrl),
    NavigationType.Present.FullScreen
)
```

### Dismissing Modals

```kotlin
// From within a modal screen
NavigationManager.navigateBack() // Pops one screen
NavigationManager.popToRoot() // Closes modal completely
```

---

## Best Practices

### 1. Route Organization

```kotlin
// Group related routes together
sealed class RouteType {
    // Auth routes
    object Login : RouteType()
    object Register : RouteType()

    // Feature routes
    sealed class Assignment : RouteType() {
        object List : Assignment()
        data class Detail(val id: Int) : Assignment()
        data class Add(val templateId: Int?) : Assignment()
    }
}
```

### 2. Navigation from ViewModels

```kotlin
class AssignmentViewModel : ViewModel() {
    fun navigateToDetail(id: Int) {
        NavigationManager.navigateToRoute(
            RouteType.AssignmentDetail(id)
        )
    }

    fun showSubjectPicker() {
        NavigationManager.presentBottomSheet(
            content = { SubjectPickerContent() },
            title = "Select Subject"
        )
    }
}
```

### 3. Handle Back Press

```kotlin
@Composable
fun MyScreen() {
    BackHandler {
        NavigationManager.navigateBack()
    }

    // Screen content
}
```

### 4. Navigation Interceptors

```kotlin
// Log navigations events
NavigationManager.router?.navStack?.let { stack ->
    LaunchedEffect(stack.size) {
        Log.d("Navigation", "Stack size: ${stack.size}")
        stack.lastOrNull()?.let { route ->
            Analytics.logScreenView(route.getDisplayName())
        }
    }
}
```

### 5. Clear State on Logout

```kotlin
fun logout() {
    // Clear all navigations state
    NavigationManager.clearAllState()

    // Navigate to login
    NavigationManager.navigateToRoute(RouteType.Login)
}
```

---

## Implementation Components

### ECNavigationAttacher (To Be Implemented)

```kotlin
@Composable
fun ECNavigationAttacher(
    router: Router,
    content: @Composable () -> Unit
) {
    // Implements navigations host that observes router stack
    // and renders appropriate screens
    // See iOS ECNavigationAttacherView equivalent
}
```

### BottomSheetOverlay (To Be Implemented)

```kotlin
@Composable
fun BottomSheetOverlay() {
    // Observes NavigationManager.isBottomSheetPresented
    // Shows ModalBottomSheet with content
    // Handles animations and dismissal
    // See iOS BottomSheetOverlayView equivalent
}
```

### Selection List Component

```kotlin
@Composable
fun <T> SelectionList(
    items: List<T>,
    selectedItem: T?,
    getTitle: (T) -> String,
    getId: (T) -> Any,
    onSelect: (T) -> Unit
) {
    // Generic selection list for bottom sheets
    // Shows items with checkmark on selected
    // See iOS SelectionListSheet equivalent
}
```

---

## Migration Guide

### From Fragment Navigation

**Before:**
```kotlin
findNavController().navigate(
    R.id.action_home_to_detail,
    bundleOf("id" to assignmentId)
)
```

**After:**
```kotlin
NavigationManager.navigateToRoute(
    RouteType.AssignmentDetail(assignmentId)
)
```

### From DialogFragment

**Before:**
```kotlin
val dialog = MyDialogFragment()
dialog.show(childFragmentManager, "dialog")
```

**After:**
```kotlin
NavigationManager.presentBottomSheet(
    content = { MyDialogContent() },
    title = "Dialog Title"
)
```

---

## Testing

### Unit Testing Navigation

```kotlin
@Test
fun `test navigation to detail screen`() {
    val router = Router(associatedTab = TabbarItem.HOME)

    router.navigateTo(RouteType.AssignmentDetail(123))

    assertEquals(1, router.stackSize)
    assertTrue(router.currentRoute is RouteType.AssignmentDetail)
}
```

### UI Testing

```kotlin
@Test
fun `test bottom sheet presentation`() {
    composeTestRule.setContent {
        BottomSheetOverlay()
    }

    NavigationManager.presentBottomSheet(
        content = { Text("Test Content") },
        title = "Test Sheet"
    )

    composeTestRule.onNodeWithText("Test Content").assertIsDisplayed()
}
```

---

## Troubleshooting

### Issue: Navigation not working
**Solution**: Ensure NavigationManager.registerRouter() is called for each tab

### Issue: Bottom sheet not showing
**Solution**: Verify BottomSheetOverlay() is added at root level

### Issue: Back button doesn't work
**Solution**: Check that router is properly set for current context

### Issue: Tab navigation clears stack
**Solution**: Ensure each tab has its own Router instance

---

## API Reference

See individual component files for detailed API documentation:
- `NavigationManager.kt` - Central navigation manager
- `Router.kt` - Navigation stack manager
- `RouteType.kt` - All navigation destinations
- `NavigationType.kt` - Navigation styles
- `BottomSheetConfiguration.kt` - Bottom sheet configuration
- `TabbarItem.kt` - Tab definitions

---

## Support

For issues or questions:
1. Check this README
2. Review iOS documentation reference
3. Check component source code comments
4. Consult the team

---

**Version**: 1.0.0
**Last Updated**: 2025-12-02
**Based On**: iOS NavigationManager v2.0
