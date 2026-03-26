# Feature: Class Teacher

## Overview

Displays a list of class teachers assigned to each class in the school. Users can search by teacher name, class, or designation, sort by name or class, and toggle between grid and list view.

---

## API

| Method | Endpoint              | Description                        |
|--------|-----------------------|------------------------------------|
| GET    | `Report/Classteacher` | Returns list of class teachers     |

### Sample Response

```json
{
  "errorCode": 0,
  "status": "success",
  "message": "",
  "teachers": [
    {
      "userID": 202,
      "name": "Jane Smith",
      "designation": "PRT",
      "class": "2-A",
      "photo": "https://example.com/photo.jpg"
    }
  ]
}
```

### UI Fields Used

| API Field     | Displayed As                   |
|---------------|--------------------------------|
| `name`        | Name (card title)              |
| `designation` | Subtitle                       |
| `class`       | Detail (e.g. `Class: 2-A`)     |
| `photo`       | Profile image                  |

---

## Module Structure

```
feature/classteacher/
└── src/main/java/com/app/ecarepro/feature/classteacher/
    ├── navigation/
    │   └── ClassTeacherNavigation.kt   ← NavGraph sealed interface + entry function
    ├── ClassTeacherScreen.kt            ← Composable screen
    ├── ClassTeacherViewModel.kt         ← ViewModel + UiState + Intent + Event
    └── ClassTeacherMapper.kt            ← ClassTeacher → PersonPresentation
```

---

## Architecture

```
ClassTeacherScreen
  └── ClassTeacherViewModel
        └── ClassTeacherRepository (domain interface)
              └── ClassTeacherRepositoryImpl (core:data)
                    └── ClassTeacherRemoteDataSource (interface)
                          └── ClassTeacherRemoteDataSourceImpl (core:network)
                                └── ReportService → GET Report/Classteacher
```

---

## Navigation

**NavGraph key:** `ClassTeacherNavGraph`

```kotlin
@Serializable
sealed interface ClassTeacherNavGraph : NavKey {
    @Serializable
    data object ClassTeacherList : ClassTeacherNavGraph
}
```

**Entry registered in:** `app/.../navigation/EcareProNavDisplay.kt`

**Menu ID:** `66` (mapped in `EcareProNavDisplay.kt`)

**Testing menu entry:** `"Class Teacher"` in `TestingMenuScreen.kt`

---

## UiState

```kotlin
data class ClassTeacherUiState(
    val isRefreshing: Boolean,
    val searchQuery: String,
    val filteredTeachers: List<PersonPresentation>,
    val viewMode: ListViewMode,    // GRID or LIST
    val sortConfig: SortConfig,
)
```

---

## Intents

| Intent                  | Description                                        |
|-------------------------|----------------------------------------------------|
| `OnBackClicked`         | Navigate back                                      |
| `OnSearchQueryChanged`  | Filter list by name / designation / class name     |
| `OnRefresh`             | Pull-to-refresh reload                             |
| `OnViewModeToggle`      | Switch between grid and list view                  |
| `OnSortSelected`        | Apply sort config (Name or Class, A→Z / Z→A)       |

---

## Events

| Event          | Description                           |
|----------------|---------------------------------------|
| `NavigateBack` | Sent when back is pressed             |
| `ShowMessage`  | Show snackbar (e.g. on refresh error) |

---

## UI Features

- **Grid / List toggle** — `ViewModeToggleButton` in top app bar actions (shared from `core/designsystem`)
- **Bottom search bar** — search + sort buttons
- **Sort** — by name or by class name, ascending/descending via `SortBottomSheet`
- **Pull-to-refresh** — shows current data with loading indicator, shows snackbar on refresh error
- **Empty state** — shown when no results match the current search

---

## Sort Behaviour

| Sort Option | Sort Key         |
|-------------|------------------|
| `NAME`      | `ClassTeacher.name` (alphabetical) |
| Other       | `ClassTeacher.className` (alphabetical) |

Both options support ascending and descending direction.

---

## Mapper

`ClassTeacherMapper.kt` — `ClassTeacher.toPresentation(): PersonPresentation`

```kotlin
PersonPresentation(
    id          = id.toString(),
    name        = name,
    subtitle    = designation,
    detail      = "Class: <className>"    // empty string if className is blank
    profileImageURL = photo,
    gender      = Gender.OTHER,
    personType  = PersonType.STAFF
)
```

> Gender is not provided by the `Report/Classteacher` API, so it defaults to `Gender.OTHER`.

---

## Dependency Graph

Uses a **dedicated network layer** separate from `staffprofile`. The `ReportService` is shared with other report features (e.g. Birthday report, SMS report).

| Layer        | Class                             | Location           |
|--------------|-----------------------------------|--------------------|
| Retrofit API | `ReportService`                   | `core:network`     |
| Network model| `NetworkClassTeacher`             | `core:network`     |
| Remote DS    | `ClassTeacherRemoteDataSource(Impl)` | `core:network`  |
| Domain model | `ClassTeacher`                    | `core:domain`      |
| Repository   | `ClassTeacherRepository(Impl)`    | `core:domain` / `core:data` |
| Feature VM   | `ClassTeacherViewModel`           | `feature:classteacher` |

---

## DI Bindings

**`DataSourceModule.kt`** (core:network):
```kotlin
@Binds
abstract fun bindsClassTeacherRemoteDataSource(
    impl: ClassTeacherRemoteDataSourceImpl
): ClassTeacherRemoteDataSource
```

**`DataModule.kt`** (core:data):
```kotlin
@Binds
abstract fun bindsClassTeacherRepository(
    impl: ClassTeacherRepositoryImpl
): ClassTeacherRepository
```

`ReportService` is already provided via `NetworkModule` (shared with other report features).
