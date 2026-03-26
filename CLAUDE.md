# ECarePro-Revamp — Claude Code Reference

## Project Overview

ECarePro is a multi-module Android application for school management (students, staff, teachers). It is built with Kotlin, Jetpack Compose, and a strict MVI architecture across all feature modules.

---

## Module Structure

```
ECarePro-Revamp/
├── app/                        # Application entry point, NavDisplay host
├── build-logic/                # Gradle convention plugins
├── core/
│   ├── data/                   # Repository implementations (@Binds in DataModule)
│   ├── database/               # Room database
│   ├── designsystem/           # ALL reusable UI components + theme system
│   ├── domain/                 # Domain models, repository interfaces, extensions
│   ├── download/               # File download utilities
│   ├── location/               # Location services
│   ├── mylibrary/              # Shared utilities
│   ├── navigation/             # Shared navigation utilities
│   ├── network/                # Retrofit services, network models, remote data sources
│   └── ui/                     # BaseViewModel, UiState, AssistedViewModelFactory
└── feature/
    ├── announcement/           # School/staff/class notices, circulars
    ├── assignment/             # Assignment management
    ├── dashboard/              # Main dashboard
    ├── discipline/             # Infraction & appreciation records
    ├── docviewer/              # In-app document viewer
    ├── feed/                   # Activity feed/timeline
    ├── gallery/                # Photo albums, video albums, kids corner, favorites
    ├── home/                   # Home screen
    ├── homeselection/          # Home selection flow
    ├── leave/                  # Leave management (apply, view, approve)
    ├── login/                  # Authentication
    ├── message/                # Messaging
    ├── onboarding/             # Onboarding flow
    ├── questionner/            # Questionnaire
    ├── schoolcode/             # School code entry
    ├── splash/                 # Splash screen
    ├── staffprofile/           # Staff profiles
    ├── studentprofile/         # Student profiles
    ├── syllabus/               # Syllabus display
    ├── taskmanger/             # Task manager
    ├── testingmenu/            # Debug menu — connects all modules for testing
    ├── timetable/              # Timetable display
    ├── transport_att/          # Transport attendance
    └── update_record/          # Class promotion, roll number, house, profile picture
        ├── class_promotion/
        ├── manage_roll_number/
        ├── update_house/
        ├── update_profile_picture/
        └── navigation/
```

---

## Gradle / Build System

**Convention Plugins** (via `build-logic/`):

| Plugin alias | Use on |
|---|---|
| `ecarepro.android.feature` | Every feature module |
| `ecarepro.android.library.compose` | Modules with Compose UI |
| `ecarepro.android.androidLibrary` | Pure Android library modules |
| `ecarepro.android.application.compose` | The `:app` module |
| `ecarepro.hilt` | Any module needing Hilt |
| `ecarepro.android.room` | Database module |

**Key versions** (from `gradle/libs.versions.toml`):

| Library | Version |
|---|---|
| Kotlin | 2.1.10 |
| AGP | 8.13.0 |
| Compose BOM | 2025.09.01 |
| Hilt | 2.57.1 |
| Navigation3 | 1.0.0-alpha10 |
| Retrofit | 3.0.0 |
| OkHttp | 5.0.0-alpha.9 |
| kotlinx-serialization | 1.8.0 |
| Coil | 3.3.0 |
| Room | 2.7.2 |
| Lifecycle | 2.9.4 |

---

## Architecture: MVI + Clean Architecture

### Pattern

**MVI (Model-View-Intent)** with a repository-backed domain layer. Every screen follows the same structure:

```
Screen (Composable)
  └── ViewModel (BaseViewModel<Intent, Event>)
        └── Repository (domain interface)
              └── RepositoryImpl (core:data)
                    └── RemoteDataSource (interface)
                          └── RemoteDataSourceImpl (core:network)
                                └── RetrofitService
```

### BaseViewModel

`core/ui/src/main/java/com/app/ecarepro/core/ui/viewmodel/BaseViewModel.kt`

```kotlin
abstract class BaseViewModel<Intent, Event> : ViewModel() {
    private val _screenEvent = Channel<Event>(Channel.BUFFERED)
    val screenEvent = _screenEvent.receiveAsFlow()

    abstract fun handleIntent(intent: Intent)

    protected fun sendEvent(event: Event) {
        viewModelScope.launch { _screenEvent.send(event) }
    }
}
```

### UiState Sealed Interface

`core/ui/src/main/java/com/app/ecarepro/core/ui/UiState.kt`

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

### Per-Screen Structure (in every ViewModel file)

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val repository: ExampleRepository,
) : BaseViewModel<ExampleIntent, ExampleEvent>() {

    private val _uiState = MutableStateFlow(ExampleUiState())
    val uiState = _uiState.asStateFlow()

    override fun handleIntent(intent: ExampleIntent) { ... }
}

@Immutable
data class ExampleUiState(...)

sealed interface ExampleIntent { ... }
sealed interface ExampleEvent { ... }
```

> **Note**: Some screens use a flat `MutableStateFlow(ExampleUiState())` (most update_record screens), while others use `MutableStateFlow<UiState<T>>(UiState.Loading)` for the loading/success/error tri-state. Both patterns exist — match whichever the surrounding feature uses.

---

## Dependency Injection — Hilt

### Module locations

| Module | File | Pattern |
|---|---|---|
| Repository bindings | `core/data/.../di/DataModule.kt` | `@Binds` abstract functions |
| Remote data source bindings | `core/network/.../di/DataSourceModule.kt` | `@Binds` abstract functions |
| Retrofit / Services | `core/network/.../di/NetworkModule.kt` | `@Provides` singleton functions |

### DataModule pattern (`@Binds`)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds internal abstract fun bindsExampleRepository(impl: ExampleRepositoryImpl): ExampleRepository
}
```

### NetworkModule pattern (`@Provides`)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit { ... }

    @Provides @Singleton
    fun provideExampleService(retrofit: Retrofit): ExampleService =
        retrofit.create(ExampleService::class.java)
}
```

### AssistedViewModel (for NavKey-parametrized screens)

When a screen's ViewModel needs a `NavKey` parameter (e.g., album ID), use `@AssistedInject`:

```kotlin
@HiltViewModel(assistedFactory = PhotoDetailViewModel.Factory::class)
class PhotoDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: GalleryNavGraph.PhotoAlbumDetail,
    private val repository: GalleryRepository,
) : BaseViewModel<...>() {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<GalleryNavGraph.PhotoAlbumDetail, PhotoDetailViewModel> {
        override fun create(param: GalleryNavGraph.PhotoAlbumDetail): PhotoDetailViewModel
    }
}
```

In the navigation entry, create it with:
```kotlin
val viewModel: PhotoDetailViewModel = navKeyViewModel(key)
```

`AssistedViewModelFactory` is defined in `core/ui/.../viewmodel/AssistedViewModelFactory.kt`.

---

## Networking Stack

**Retrofit 3 + OkHttp 5 + kotlinx.serialization**

### OkHttpClient config

- Connect / Read / Write timeout: **60 seconds**
- Interceptors: `AuthTokenInterceptor` (adds auth header), `HttpLoggingInterceptor`
- Converter: `kotlinx.serialization` via `Json.asConverterFactory`

### NetworkResponse interface

`core/network/.../model/NetworkResponse.kt`

Every API response model implements this:
```kotlin
interface NetworkResponse {
    val errorCode: Int
    val message: String
    val status: String
}
```

Use `unwrapPayload { }` to extract the data payload and throw on error:
```kotlin
academicService.getTimeline().unwrapPayload { this }
```

### CommonNetworkResponse

For endpoints that only return a success/error message (no payload):
```kotlin
@Serializable
data class CommonNetworkResponse(
    @SerialName("errorCode") override val errorCode: Int,
    @SerialName("message") override val message: String,
    @SerialName("status") override val status: String,
) : NetworkResponse
```

### asResultFlow

`core/domain/.../ext/ResultExt.kt`

Wraps any suspending network call into `Flow<Result<T>>`:
```kotlin
fun <T> asResultFlow(block: suspend () -> T): Flow<Result<T>> = flow {
    emit(Result.success(block()))
}.catch { emit(Result.failure(it)) }
```

### Full network layer example

```kotlin
// 1. Retrofit Service
interface AdminService {
    @GET("Admin/StudentList")
    suspend fun getStudents(@Query("ID") classId: String): NetworkStudentList

    @POST("Admin/AssignHouse")
    suspend fun assignHouses(@Body body: List<NetworkAssignHouseItem>): CommonNetworkResponse
}

// 2. Network Model
@Serializable
data class NetworkStudent(
    @SerialName("stID") val stID: Int,
    @SerialName("studentName") val studentName: String,
)
fun NetworkStudent.toDomainModel() = Student(id = stID, name = studentName)

// 3. Remote Data Source
interface AdminRemoteDataSource {
    suspend fun getStudents(classId: String): List<NetworkStudent>
}
class AdminRemoteDataSourceImpl @Inject constructor(
    private val service: AdminService
) : AdminRemoteDataSource {
    override suspend fun getStudents(classId: String) =
        service.getStudents(classId).unwrapPayload { students }
}

// 4. Repository (domain interface in core:domain, impl in core:data)
interface ExampleRepository {
    fun getStudents(classId: String): Flow<Result<List<Student>>>
}
class ExampleRepositoryImpl @Inject constructor(
    private val dataSource: AdminRemoteDataSource
) : ExampleRepository {
    override fun getStudents(classId: String) = asResultFlow {
        dataSource.getStudents(classId).map { it.toDomainModel() }
    }
}
```

### Import aliases for toDomainModel collisions

When a repository uses both `admin` and `staff` network models that both define `toDomainModel()`:

```kotlin
import com.app.ecarepro.core.network.model.admin.toDomainModel as toAdminDomain
import com.app.ecarepro.core.network.model.staff.toDomainModel as toStaffDomain
```

---

## Navigation — Navigation3 (alpha)

**Library**: `androidx.navigation3` (`1.0.0-alpha10`)
**NOT** the standard `androidx.navigation` Compose library.

### NavKey pattern

Every feature defines a `@Serializable sealed interface` that implements `NavKey`:

```kotlin
@Serializable
sealed interface UpdateRecordNavGraph : NavKey {
    @Serializable data object ClassPromotion : UpdateRecordNavGraph
    @Serializable data object ManageRollNumber : UpdateRecordNavGraph
    @Serializable data class PhotoAlbumDetail(val albumId: String) : UpdateRecordNavGraph
}
```

### Entry provider pattern

Navigation entries are registered as extension functions on `EntryProviderBuilder<NavKey>`:

```kotlin
fun EntryProviderBuilder<NavKey>.entryUpdateRecordNavigation(
    navigateBack: () -> Unit,
) {
    entry<UpdateRecordNavGraph.ClassPromotion> {
        ClassPromotionScreen(navigateBack = navigateBack)
    }
    entry<UpdateRecordNavGraph.PhotoAlbumDetail> { key ->
        val viewModel: PhotoDetailViewModel = navKeyViewModel(key)
        PhotoDetailScreen(viewModel = viewModel, navigateBack = navigateBack)
    }
}
```

### NavDisplay host

`app/.../navigation/EcareProNavDisplay.kt` — the single `NavDisplay` with a `backStack: SnapshotStateList<NavKey>`. All feature navigation entries are registered here. Navigation is done by:
- Push: `backStack.add(SomeNavGraph.Destination)`
- Pop: `backStack.removeLastOrNull()`

### Adding a new feature module to navigation

1. Define `@Serializable sealed interface YourNavGraph : NavKey` in the feature's `navigation/` package
2. Write `fun EntryProviderBuilder<NavKey>.entryYourNavigation(...)` extension
3. Register in `EcareProNavDisplay.kt`
4. Add `implementation(projects.feature.yourModule)` to `app/build.gradle.kts`
5. Add entries to `ModuleRegistry` in `TestingMenuScreen.kt` + add dependency to `testingmenu/build.gradle.kts`

---

## State Management

### UI state

`MutableStateFlow` + `_uiState.update { }` in ViewModel, collected with `collectAsStateWithLifecycle()` in Compose:

```kotlin
// ViewModel
private val _uiState = MutableStateFlow(MyUiState())
val uiState = _uiState.asStateFlow()
_uiState.update { it.copy(isLoading = true) }

// Composable
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

### One-time events

Use `sendEvent()` in ViewModel → collected with `LaunchedEffect(Unit)` in composable:

```kotlin
// ViewModel
sendEvent(MyEvent.NavigateBack)
sendEvent(MyEvent.ShowMessage(SnackbarMessage("Saved", MessageType.SUCCESS)))

// Composable
LaunchedEffect(Unit) {
    viewModel.screenEvent.collect { event ->
        when (event) {
            is MyEvent.NavigateBack -> navigateBack()
            is MyEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message.text)
        }
    }
}
```

### SnackbarMessage

```kotlin
data class SnackbarMessage(val text: String, val type: MessageType)
enum class MessageType { INFO, SUCCESS, WARNING, ERROR }
```

Pass `snackbarHostState` and `snackbarMessage` to `EcareProScaffold` — it renders the styled snackbar automatically.

---

## Theming System

### Access theme values

```kotlin
MaterialTheme.appColors.primary        // AppColors extension on MaterialTheme
MaterialTheme.appColors.textPrimary
MaterialTheme.appColors.textSecondary
MaterialTheme.appColors.background
MaterialTheme.appColors.error
MaterialTheme.appColors.success

MaterialTheme.appTypography.interSemiBold14px
MaterialTheme.appTypography.interRegular14px
MaterialTheme.appTypography.interMedium16px
```

### Color palette highlights

- **Primary**: EmeraldGreen `#4CAF50` (light) / LimeGreen `#66BB6A` (dark)
- **Background**: OffWhite `#F9F9F9` (light) / JetBlack `#121212` (dark)
- **Divider**: LightGrey `#E0E0E0` / `Color(0xFFEEEEEE)` used inline
- **Text Primary**: CharcoalText `#1C1C1C`
- **Text Secondary**: LightGreyText `#757575`
- **Error**: TomatoRed
- **Success**: FreshGreen

### Typography

Two font families — **Inter** (primary) and **Nunito** (accent). All TextStyles follow the naming pattern:
`inter{Weight}{Size}px` e.g. `interSemiBold14px`, `interRegular12px`, `interMedium16px`

### Wrap all previews and tests with

```kotlin
EcareProTheme { ... }
```

---

## Design System — Reusable Components

All components are in:
`core/designsystem/src/main/java/com/app/ecarepro/designsystem/core/component/`

### Screen scaffold

```kotlin
EcareProScaffold(
    topBar = { EcareProTopAppBar(title = "...", onNavigationClicked = { ... }) },
    bottomBar = { BottomSearchBarView(...) },
    snackbarHostState = snackbarHostState,
    snackbarMessage = snackbarMessage,
    onSnackbarDismissed = { snackbarMessage = null },
    containerColor = White,
) { paddingValues -> ... }
```

### Top app bar

```kotlin
EcareProTopAppBar(
    title = "Screen Title",
    onNavigationClicked = { handleIntent(MyIntent.OnBackClicked) },
    // optional: actions = { ... }
)
```

### Class/category tabs (scrollable)

```kotlin
EcareProClassTabs(
    selectedTabIndex = uiState.selectedClassIndex,
    tabs = uiState.classes.map { it.className ?: "" },
    onTabClick = { handleIntent(MyIntent.SelectClass(it)) },
    applyOrdinalTransform = true,  // converts "1" → "1st", "2" → "2nd", etc.
)
```

### Bottom search bar

```kotlin
BottomSearchBarView(
    searchText = uiState.searchQuery,
    onSearchTextChange = { handleIntent(MyIntent.OnSearchQueryChanged(it)) },
    placeholder = "Search by name...",
    showSortButton = true,
    onSortClick = { handleIntent(MyIntent.OnSortClick) },
)
```

### Sort bottom sheet

```kotlin
if (uiState.showSortSheet) {
    SortBottomSheet(
        currentSortConfig = uiState.sortConfig,
        onSortSelected = { handleIntent(MyIntent.OnSortSelected(it)) },
        onDismiss = { handleIntent(MyIntent.DismissSortSheet) },
        availableOptions = listOf(SortOption.ROLL_NUMBER, SortOption.ADMISSION_NUMBER, SortOption.NAME),
    )
}
```

`SortConfig(option: SortOption, direction: SortDirection)` — default direction is ASCENDING.

### Selection bottom sheet (radio / checkbox)

```kotlin
EcareProSelectionBottomSheet(
    isVisible = uiState.showSheet,
    title = "Select House",
    options = uiState.houses.map { SelectionOption(id = it.houseID, label = it.houseName) },
    selectedId = uiState.selectedHouseId,
    onSelected = { handleIntent(MyIntent.OnHouseSelected(it)) },
    onDismiss = { handleIntent(MyIntent.DismissSheet) },
)
```

### File upload bottom sheet

```kotlin
EcareProFileUploadBottomSheet(
    isVisible = uiState.showFilePicker,
    onDismiss = { handleIntent(MyIntent.DismissPicker) },
    allowedOptions = listOf(UploadOption.CAMERA, UploadOption.GALLERY),  // or add UploadOption.DOCUMENT
    maxFileSizeInMb = 5,
    onFilesSelected = { files ->
        files.firstOrNull()?.let { handleIntent(MyIntent.OnFileSelected(it)) }
    },
)
// SelectedFileDetails has: file: File, mimeType: String
```

### Async image

```kotlin
EcareProAsyncImage(
    imageUrl = student.photo,
    contentDescription = student.name,
    modifier = Modifier.size(48.dp).clip(CircleShape),
)
```

### Empty state

```kotlin
EcareProEmptyState(message = "No students found")
```

### Input field

```kotlin
EcareProInputField(
    value = uiState.text,
    onValueChange = { handleIntent(MyIntent.OnTextChanged(it)) },
    label = "Roll Number",
    placeholder = "Enter roll number",
)
```

---

## Feature Module Conventions

### Folder structure per feature

Each feature module follows this internal package structure:

```
feature/my_feature/
└── src/main/java/com/app/ecarepro/feature/my_feature/
    ├── navigation/
    │   └── MyFeatureNavigation.kt       ← NavGraph sealed interface + entryMyFeatureNavigation()
    ├── screen_one/
    │   ├── ScreenOneViewModel.kt         ← ViewModel + UiState + Intent + Event
    │   ├── ScreenOneScreen.kt            ← Composable screen + @Preview
    │   └── component/                   ← Sub-composables specific to this screen
    └── screen_two/
        ├── ScreenTwoViewModel.kt
        └── ScreenTwoScreen.kt
```

### Screen file convention

Each screen file contains (in order):
1. The public `@Composable fun MyScreen(viewModel, navigate*)` — connects ViewModel + events
2. A private `@Composable fun Content(uiState, handleIntent, ...)` — pure, previewable
3. Private sub-composable functions (item rows, cards, etc.)
4. `@Preview` composables at the bottom calling `Content` directly with mock data, wrapped in `EcareProTheme`

### ViewModel file convention

Each ViewModel file contains (in order):
1. `@HiltViewModel class MyViewModel` with all business logic
2. `@Immutable data class MyUiState(...)` — annotated with `@Immutable`
3. `sealed interface MyIntent { ... }`
4. `sealed interface MyEvent { ... }`

---

## Existing Domain Models

Key models in `core/domain/src/main/java/com/app/ecarepro/core/domain/model/`:

- `Class` — `id: String`, `className: String?` (used for class tabs across all features)
- `ProfilePictureStudent` — `stID`, `name`, `rollNumber`, `admissionNumber`, `photo`, `fatherName`
- `HouseItem` — `houseID: Int`, `houseName: String`
- `HouseStudent` / `HouseStudentData`
- `User` — authenticated user model

---

## Testing Menu

`feature/testingmenu/src/main/java/com/app/ecarepro/feature/testingmenu/TestingMenuScreen.kt`

`ModuleRegistry.screens` maps display names to `NavKey` destinations. Every new feature screen must be added here for manual testing. Also add the feature dependency to `testingmenu/build.gradle.kts`.

---

## Key Reminders

- **Never use `!!`** — use safe calls, elvis operator, or early returns
- **Never block the main thread** — all network/disk work via `Dispatchers.IO`
- **Always `throw` `CancellationException`** — never swallow it in catch blocks
- **`toDomainModel()`** must be defined as an extension on the network model, not inside the class
- **Import aliases** when two packages both define `toDomainModel()` in the same file:
  ```kotlin
  import com.app.ecarepro.core.network.model.admin.toDomainModel as toAdminDomain
  import com.app.ecarepro.core.network.model.staff.toDomainModel as toStaffDomain
  ```
- **`applyOrdinalTransform = false`** when class names are not numeric (e.g., "10-A", "KG")
- **`HorizontalDivider`** between list items uses `color = Color(0xFFEEEEEE)`, `thickness = 0.5.dp`
- **Bottom sheet dismiss pattern**: use `rememberModalBottomSheetState(skipPartiallyExpanded = true)` + coroutine scope to hide before calling `onDismiss`
- **Scroll-based FAB/button visibility**: detect scroll direction via `snapshotFlow { firstVisibleItemIndex to firstVisibleItemScrollOffset }` comparing composite scroll position
