# Feature: Know Your Teacher

## Overview

Displays a list of all teachers in the school. Users can search by name or designation, filter by designation, sort by name, and toggle between grid and list view.

---

## API

| Method | Endpoint         | Description              |
|--------|------------------|--------------------------|
| GET    | `Staff/List`     | Returns list of all staff/teachers |

### Sample Response

```json
{
  "errorCode": 0,
  "status": "success",
  "message": "",
  "staff": [
    {
      "userID": 101,
      "staffName": "John Doe",
      "designation": "PGT",
      "teachersSubject": "Mathematics",
      "photo": "https://example.com/photo.jpg",
      "gender": "Male",
      "mobile": "9876543210"
    }
  ]
}
```

### UI Fields Used

| API Field          | Displayed As       |
|--------------------|--------------------|
| `staffName`        | Name (card title)  |
| `designation`      | Subtitle           |
| `teachersSubject`  | Detail (e.g. `Subject: Mathematics`) |
| `photo`            | Profile image      |
| `gender`           | Used for stats count (Male/Female) |

---

## Module Structure

```
feature/knowyourteacher/
└── src/main/java/com/app/ecarepro/feature/knowyourteacher/
    ├── navigation/
    │   └── KnowYourTeacherNavigation.kt   ← NavGraph sealed interface + entry function
    ├── KnowYourTeacherScreen.kt            ← Composable screen
    ├── KnowYourTeacherViewModel.kt         ← ViewModel + UiState + Intent + Event
    └── KnowYourTeacherMapper.kt            ← StaffProfile → PersonPresentation
```

---

## Architecture

```
KnowYourTeacherScreen
  └── KnowYourTeacherViewModel
        └── KnowYourTeacherRepository (domain interface)
              └── KnowYourTeacherRepositoryImpl (core:data)
                    └── StaffRemoteDataSource (interface)
                          └── StaffRemoteDataSourceImpl (core:network)
                                └── StaffService → GET Staff/List
```

---

## Navigation

**NavGraph key:** `KnowYourTeacherNavGraph`

```kotlin
@Serializable
sealed interface KnowYourTeacherNavGraph : NavKey {
    @Serializable
    data object KnowYourTeacherList : KnowYourTeacherNavGraph
}
```

**Entry registered in:** `app/.../navigation/EcareProNavDisplay.kt`

**Menu ID:** `64` (mapped in `EcareProNavDisplay.kt`)

**Testing menu entry:** `"Know Your Teacher"` in `TestingMenuScreen.kt`

---

## UiState

```kotlin
data class KnowYourTeacherUiState(
    val isRefreshing: Boolean,
    val searchQuery: String,
    val filteredTeachers: List<PersonPresentation>,
    val stats: ListStatsPresentation,          // total / male / female count
    val viewMode: ListViewMode,                // GRID or LIST
    val sortConfig: SortConfig,
    val selectedFilters: Map<String, Set<String>>,
    val filterSections: List<FilterSection>,   // populated with "Designation" options
)
```

---

## Intents

| Intent                  | Description                                 |
|-------------------------|---------------------------------------------|
| `OnBackClicked`         | Navigate back                               |
| `OnSearchQueryChanged`  | Filter list by name / designation / mobile  |
| `OnRefresh`             | Pull-to-refresh reload                      |
| `OnViewModeToggle`      | Switch between grid and list view           |
| `OnSortSelected`        | Apply sort config (Name A→Z / Z→A)          |
| `OnFiltersApplied`      | Apply designation filter from filter sheet  |

---

## Events

| Event          | Description                           |
|----------------|---------------------------------------|
| `NavigateBack` | Sent when back is pressed             |
| `ShowMessage`  | Show snackbar (e.g. on refresh error) |

---

## UI Features

- **Grid / List toggle** — `ViewModeToggleButton` in top app bar actions (shared from `core/designsystem`)
- **Stats header** — shows total, male, and female teacher counts below the top app bar
- **Bottom search bar** — search + sort + filter buttons
- **Designation filter** — `PersonFilterSheet` with dynamically populated designations from API data
- **Sort** — by name ascending/descending via `SortBottomSheet`
- **Pull-to-refresh** — shows current data with loading indicator, shows snackbar on refresh error
- **Empty state** — shown when no results match the current search/filter

---

## Mapper

`KnowYourTeacherMapper.kt` — `StaffProfile.toPresentation(): PersonPresentation`

```kotlin
PersonPresentation(
    id          = id,
    name        = displayName,
    subtitle    = designation,
    detail      = "Subject: <teachersSubject>"   // empty string if null/blank
    profileImageURL = photo,
    gender      = Gender.fromString(gender ?: ""),
    personType  = PersonType.STAFF
)
```

---

## Dependency Graph

**Reuses existing network layer** — `StaffService` and `NetworkStaffProfile` (same models used by `staffprofile` feature for `Staff/List`).

| Layer        | Class                          | Location         |
|--------------|--------------------------------|------------------|
| Retrofit API | `StaffService`                 | `core:network`   |
| Network model| `NetworkStaffProfile`          | `core:network`   |
| Remote DS    | `StaffRemoteDataSource(Impl)`  | `core:network`   |
| Domain model | `StaffProfile`                 | `core:domain`    |
| Repository   | `KnowYourTeacherRepository(Impl)` | `core:domain` / `core:data` |
| Feature VM   | `KnowYourTeacherViewModel`     | `feature:knowyourteacher` |

---

## DI Bindings

**`DataModule.kt`** (core:data):
```kotlin
@Binds
abstract fun bindsKnowYourTeacherRepository(
    impl: KnowYourTeacherRepositoryImpl
): KnowYourTeacherRepository
```

`StaffRemoteDataSource` binding already exists (shared with staffprofile).
