# Gallery Module — iOS Implementation Reference

Complete specification for implementing the Gallery module on iOS. Covers all 3 screens, 5 API endpoints, data models, ViewModels, and business logic.

---

## Module Overview

The Gallery module allows users to browse photo albums organized by category, view individual album photos in a grid, and interact with photos via a full-screen slider supporting like, bookmark, and share actions.

**Navigation flow:**
```
Photo Album List Screen
    └── tap album → Album Detail Screen
                        └── tap photo → Photo Slider Screen (horizontal / vertical toggle)
```

---

## Navigation Routes

| Route | Parameters | Description |
|-------|-----------|-------------|
| `PhotoAlbumList` | none | Album list with category filter chips |
| `PhotoAlbumDetail` | `albumId: String`, `albumTitle: String` | Photo grid for a single album |
| `PhotoSlider` | `albumId: String`, `albumTitle: String`, `initialPhotoIndex: Int` | Full-screen photo viewer |

---

## Domain Models

```swift
struct AlbumType {
    let typeID: Int
    let typeName: String
}

struct Album {
    let id: String
    let title: String
    let description: String
    let totalPhotos: Int
    let fileName: String    // thumbnail image URL
    let eventDate: String   // display string e.g. "15 Jan 2025"
}

struct Photo {
    let id: String
    let title: String
    let description: String?
    let photoPath: String   // full image URL
    let likes: Int
    let isLike: Bool
    let isFavourite: Bool
}

struct PhotoSetting {
    let isLikeEnabled: Bool
    let isShareEnabled: Bool
    let isAddFavouriteEnabled: Bool
}

struct AlbumDetail {
    let title: String
    let description: String     // may contain HTML tags — strip before display
    let eventDate: String
    let totalPhotos: Int
    let setting: PhotoSetting
    let photos: [Photo]
}
```

---

## API Endpoints

### 1. GET `Gallery/PhotoAlbumTypes`

Returns all available album category filter types.

**Request:** No parameters

**Response:**
```json
{
  "errorCode": 0,
  "status": "Success",
  "message": "Album types fetched successfully",
  "albumTypes": [
    { "typeID": 1, "typeName": "Sports" },
    { "typeID": 2, "typeName": "Cultural" },
    { "typeID": 3, "typeName": "Academic" }
  ]
}
```

**Business logic:**
- Prepend a virtual "All" type (`typeID: 0, typeName: "All"`) to the beginning of the list
- "All" is selected by default on load
- When "All" is selected, call `Gallery/PhotoAlbums` with `typeID=0`

---

### 2. GET `Gallery/PhotoAlbums`

Returns albums filtered by category type, with pagination.

**Request Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `typeID` | Int | `0` | Album type filter (0 = all) |
| `pg` | Int | `1` | Page number |

**Example:** `Gallery/PhotoAlbums?typeID=0&pg=1`

**Response:**
```json
{
  "errorCode": 0,
  "status": "Success",
  "message": "Albums fetched successfully",
  "albums": [
    {
      "id": "kqlZ3H32mteJKLrBwNFNhg==",
      "title": "Annual Sports Day 2024",
      "description": "Highlights from the annual sports meet",
      "totalPhotos": 24,
      "fileName": "https://example.com/images/sports_thumb.jpg",
      "eventDate": "15 Jan 2025"
    },
    {
      "id": "AbCdEfGhIjKlMnOpQrStUg==",
      "title": "Cultural Festival",
      "description": "",
      "totalPhotos": 18,
      "fileName": "https://example.com/images/cultural_thumb.jpg",
      "eventDate": "10 Feb 2025"
    }
  ]
}
```

**Business logic:**
- Reload albums whenever the selected type chip changes
- Client-side search: filter `albums` list by `title.lowercased().contains(query.lowercased())`
- Search bar is at the bottom of the screen — filter applies instantly as user types

---

### 3. GET `Gallery/PhotoAlbumDTL`

Returns full album detail including all photo items and per-album feature settings.

**Request Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `ID` | String | required | Album ID (base64 encoded) |
| `pg` | Int | `1` | Page number |

**Example:** `Gallery/PhotoAlbumDTL?ID=kqlZ3H32mteJKLrBwNFNhg==&pg=1`

**Response:**
```json
{
  "errorCode": 0,
  "status": "Success",
  "message": "Album detail fetched",
  "title": "Annual Sports Day 2024",
  "description": "<p>Highlights from the annual sports meet held on <strong>15 Jan 2025</strong>.</p>",
  "eventDate": "15 Jan 2025",
  "totalPhotos": 3,
  "setting": {
    "isLikeEnabled": true,
    "isShareEnabled": true,
    "isAddFavouriteEnabled": true
  },
  "photos": [
    {
      "id": "IRTkbd95i5HEwmcIabYfOg==",
      "title": "Opening Ceremony",
      "description": null,
      "photoPath": "https://example.com/photos/img1.jpg",
      "likes": 12,
      "isLike": false,
      "isFavourite": false
    },
    {
      "id": "XyZaBcDeFgHiJkLmNoPqRs==",
      "title": "100m Race",
      "description": "Finals",
      "photoPath": "https://example.com/photos/img2.jpg",
      "likes": 25,
      "isLike": true,
      "isFavourite": true
    }
  ]
}
```

**Business logic:**
- The `description` field may contain HTML — strip all HTML tags and decode `&nbsp;` as a space before displaying
- Show description as expandable text (collapsed: 3 lines max, then "view more..." tappable label)
- If description length > 150 chars, show the expand/collapse toggle
- `setting` controls which action buttons appear in the slider for each photo
- This same API is also called by the Photo Slider screen (to get the photos list)

---

### 4. GET `Gallery/Like`

Toggles like/unlike for a specific photo.

**Request Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `ID` | String | required | Photo ID |
| `GalleryType` | Int | `1` | Always send `1` |
| `like` | Boolean | required | `true` = like, `false` = unlike |

**Like:** `Gallery/Like?ID=IRTkbd95i5HEwmcIabYfOg==&GalleryType=1&like=true`
**Unlike:** `Gallery/Like?ID=IRTkbd95i5HEwmcIabYfOg==&GalleryType=1&like=false`

**Response:**
```json
{
  "errorCode": 0,
  "status": "Success",
  "message": "Like updated",
  "totalLikes": 13
}
```

**Business logic:**
- On success, update the photo in the list: flip `isLike` and set `likes = totalLikes` from response
- On failure, show error snackbar/toast — do NOT optimistically update
- Like icon: filled heart (red) when `isLike == true`, outline heart (white) when `false`
- Label: `"Like (13)"` when `likes > 0`, `"Like"` when 0

---

### 5. GET `Gallery/ManageFavorites`

Adds or removes a photo from the user's favourites (bookmark).

**Request Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `ID` | String | required | Photo ID |
| `GalleryType` | Int | `1` | Always send `1` |
| `Action` | String | required | `"add"` or `"remove"` |

**Bookmark:** `Gallery/ManageFavorites?ID=IRTkbd95i5HEwmcIabYfOg==&GalleryType=1&Action=add`
**Unbookmark:** `Gallery/ManageFavorites?ID=IRTkbd95i5HEwmcIabYfOg==&GalleryType=1&Action=remove`

**Response:**
```json
{
  "errorCode": 0,
  "status": "Success",
  "message": "Favourite updated"
}
```

**Business logic:**
- If `isFavourite == true`, send `Action=remove`; if `false`, send `Action=add`
- On success, flip `isFavourite` on the photo in the list
- Bookmark icon: filled bookmark (primary color) when `isFavourite == true`, outline bookmark (white) when `false`

---

## Screen 1 — Photo Album List

### UI Layout

```
┌─────────────────────────────────────────┐
│  ← Photo Album                          │  ← Navigation bar
├─────────────────────────────────────────┤
│  [All] [Sports] [Cultural] [Academic]   │  ← Horizontal scrollable filter chips
├─────────────────────────────────────────┤
│  ┌─────────┐  ┌─────────┐              │
│  │  thumb  │  │  thumb  │              │  ← 2-column album grid
│  │  image  │  │  image  │              │
│  │ 1:1 ratio│  │ 1:1 ratio│            │
│  ├─────────┤  ├─────────┤              │
│  │Album Name│  │Album Name│            │
│  │15 Jan 25 24 items│                  │
│  └─────────┘  └─────────┘              │
│                                         │
│  ...more albums...                      │
├─────────────────────────────────────────┤
│  🔍 Search by album name                │  ← Bottom search bar (fixed)
└─────────────────────────────────────────┘
```

### State Fields

```swift
struct GalleryListUiState {
    var isRefreshing: Bool = false
    var albumTypes: [AlbumType] = []           // includes prepended "All"
    var selectedAlbumType: AlbumType = AlbumType(typeID: 0, typeName: "All")
    var allAlbums: [Album] = []                // raw API result
    var filteredAlbums: [Album] = []           // filtered by search query
    var searchQuery: String = ""
}
```

### User Actions → Events

| Action | Result |
|--------|--------|
| Tap back | Navigate back |
| Tap filter chip | Select type, reload albums, clear search |
| Tap album card | Navigate to Album Detail |
| Type in search bar | Filter `allAlbums` by title (client-side) |
| Pull to refresh | Reload albums for selected type |

### Album Card Layout
- Thumbnail image: square (1:1 aspect ratio), rounded corners (12dp), full width of column
- Title: semibold 14px, 1 line max with ellipsis
- Bottom row (space between): event date (left) | "N items" (right), regular 12px, secondary text color

### Filter Chips
- Horizontal scroll, no wrapping
- Selected chip: primary color background, white label
- Unselected chip: transparent background, outlined border

---

## Screen 2 — Album Detail

### UI Layout

```
┌─────────────────────────────────────────┐
│  ← [Album Title]                        │  ← Navigation bar
├─────────────────────────────────────────┤
│  Description text here (max 3 lines)    │  ← Only shown if description not empty
│  view more...                           │  ← Toggle (only if text > 150 chars)
├─────────────────────────────────────────┤
│  ┌───────┐ ┌───────┐ ┌───────┐         │
│  │ photo │ │ photo │ │ photo │         │  ← 3-column photo grid
│  └───────┘ └───────┘ └───────┘         │
│  ┌───────┐ ┌───────┐ ┌───────┐         │
│  │ photo │ │ photo │ │ photo │         │
│  └───────┘ └───────┘ └───────┘         │
└─────────────────────────────────────────┘
```

### State Fields

```swift
struct GalleryDetailUiState {
    var albumDetail: AlbumDetail
    var isDescriptionExpanded: Bool = false
    var isRefreshing: Bool = false
}
```

### User Actions → Events

| Action | Result |
|--------|--------|
| Tap back | Navigate back |
| Tap "view more / view less" | Toggle description expansion |
| Tap photo | Navigate to Photo Slider with tapped photo index |
| Pull to refresh | Reload album detail |

### Description Handling
- Strip all HTML tags: `<[^>]*>` → `""`
- Replace `&nbsp;` → `" "`
- Collapsed: max 3 lines with ellipsis
- Expanded: full text
- Show toggle only if plain text length > 150 characters
- Toggle label: `"view more..."` (collapsed) / `"view less"` (expanded), primary color, semibold

### Photo Grid
- 3 columns, 4dp spacing between items
- Each photo: square (1:1 aspect), rounded corners (4dp)
- Tap to open slider at tapped index

---

## Screen 3 — Photo Slider

### UI Layout — Horizontal Mode (default)

```
┌─────────────────────────────────────────┐
│  ←  [Album Title]              [⊞ grid] │  ← Top bar, grid icon toggles to list mode
├─────────────────────────────────────────┤
│                                         │
│         ← swipe photo right →          │  ← Full-screen photo (ContentScale.Fit)
│                                         │
│                                         │
│                                         │
├─────────────────────────────────────────┤
│  [🔖 Bookmark] [❤ Like (12)] [↗ Share] │  ← Action bar (semi-transparent black bg)
└─────────────────────────────────────────┘
```

### UI Layout — Vertical Mode (list)

```
┌─────────────────────────────────────────┐
│  ←  [Album Title]              [☰ list] │  ← List icon toggles back to horizontal
├─────────────────────────────────────────┤
│  ┌───────────────────────────────────┐  │
│  │           photo 1                 │  │  ← Fixed height 280dp, ContentScale.FillWidth
│  └───────────────────────────────────┘  │
│  [🔖 Bookmark] [❤ Like (12)] [↗ Share] │
│  ─────────────────────────────────────  │
│  ┌───────────────────────────────────┐  │
│  │           photo 2                 │  │
│  └───────────────────────────────────┘  │
│  [🔖 Bookmark] [❤ Like (25)] [↗ Share] │
└─────────────────────────────────────────┘
```

### State Fields

```swift
struct GallerySliderUiState {
    var photos: [Photo] = []
    var setting: PhotoSetting
    var currentIndex: Int = 0
    var isVerticalLayout: Bool = false   // false = horizontal pager, true = vertical list
}
```

### User Actions → Events

| Action | Result |
|--------|--------|
| Tap back | Navigate back |
| Tap grid/list icon | Toggle `isVerticalLayout` |
| Swipe left/right (horizontal mode) | Update `currentIndex` |
| Tap like button | Call `Gallery/Like` API, update photo in list |
| Tap bookmark button | Call `Gallery/ManageFavorites` API, update photo in list |
| Tap share button | Show native share sheet with photo URL (plain text) |

### Action Bar Rules
- Only show each button if the corresponding setting flag is `true`:
  - **Bookmark**: show if `setting.isAddFavouriteEnabled == true`
  - **Like**: show if `setting.isLikeEnabled == true`
  - **Share**: show if `setting.isShareEnabled == true`
- Each action button: icon (24dp) above label text (regular 12px, white)
- Background: `Color.Black.copy(alpha: 0.6)` in horizontal mode, `0.8` in vertical mode

### Photo Action Button States

**Like button:**
- `isLike == true`: filled heart icon, red tint
- `isLike == false`: outline heart icon, white tint
- Label: `"Like (N)"` if `likes > 0`, else `"Like"`

**Bookmark button:**
- `isFavourite == true`: filled bookmark icon, primary color tint
- `isFavourite == false`: outline bookmark icon, white tint
- Label: `"Bookmark"`

**Share button:**
- Icon: share icon, white tint
- Label: `"Share"`
- On tap: share the `photo.photoPath` URL as plain text via system share sheet

### Layout Toggle Icon
- When `isVerticalLayout == false` (horizontal pager): show **grid icon** (tap to switch to vertical list)
- When `isVerticalLayout == true` (vertical list): show **list icon** (tap to switch back to horizontal pager)

### Data Loading
- The slider fetches `Gallery/PhotoAlbumDTL` independently (same endpoint as Album Detail screen)
- Opens at `initialPhotoIndex` passed from the Album Detail screen
- `initialPhotoIndex` is clamped to valid range: `max(0, min(initialPhotoIndex, photos.count - 1))`

---

## Error Handling

All API calls follow the same pattern:
- `errorCode == 0`: success
- `errorCode != 0`: throw error with `message` from response

**Screen-level errors** (initial load failure): replace entire screen with error state + retry button

**In-flight errors** (refresh, like, bookmark failure): show snackbar/toast with error message, do NOT replace screen content

---

## Summary Table

| Screen | APIs Called | Navigation Out |
|--------|-------------|---------------|
| Photo Album List | `Gallery/PhotoAlbumTypes`, `Gallery/PhotoAlbums` | → Album Detail |
| Album Detail | `Gallery/PhotoAlbumDTL` | → Photo Slider |
| Photo Slider | `Gallery/PhotoAlbumDTL`, `Gallery/Like`, `Gallery/ManageFavorites` | Back only |
