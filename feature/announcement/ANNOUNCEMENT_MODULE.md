# Announcement Module — Implementation Reference

This document describes the complete Announcement module of the ECarePro app. It covers all screens, navigation, APIs, data models, and business logic needed to implement equivalent functionality on iOS (Swift/SwiftUI).

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [Navigation Flow](#2-navigation-flow)
3. [Screen 1 — Notice List](#3-screen-1--notice-list)
4. [Screen 2 — Notice Detail](#4-screen-2--notice-detail)
5. [Screen 3 — Circular List](#5-screen-3--circular-list)
6. [Screen 4 — Circular Detail](#6-screen-4--circular-detail)
7. [Screen 5 — Create Circular (Admin only)](#7-screen-5--create-circular-admin-only)
8. [API Reference](#8-api-reference)
9. [Data Models](#9-data-models)
10. [Business Logic Summary](#10-business-logic-summary)

---

## 1. Module Overview

The Announcement module has two sub-features:
- **Notices** — School-wide, Staff-only, or Class-specific notices. Read-only for users.
- **Circulars** — School circulars with academic year filtering. Admins can create circulars.

### Notice Types
| Type | Description |
|------|-------------|
| `SCHOOL` | All school notices (no extra params) |
| `STAFF` | Staff-only notices (`isStaffNotice=true`) |
| `CLASS` | Class-specific notices (requires `ClassID`) |

---

## 2. Navigation Flow

```
NoticeList(noticeTypeOrdinal)
    └──> NoticeDetail(noticeId, noticeTypeOrdinal)
             └──> [Attachment Viewer — handled by shared module]

Circular
    ├──> CircularDetail(circularId)
    │        └──> [Attachment Viewer — handled by shared module]
    └──> CreateCircular (admin only)
```

---

## 3. Screen 1 — Notice List

### UI Layout
- **Top Bar**: Back arrow + title (`"All Notices"` / `"Staff Notices"` / `"Class Notices"`)
  - For `CLASS` type: a dropdown field below the top bar to select the class
- **Body**: Lazy scrollable list, items grouped by notice date (sticky date headers)
  - Each row shows: notice heading, updated-on timestamp, attachment indicator (paperclip icon + "1 Attachment"), "New" green badge if `isNew=true`, green dot on right if `isRead=false`
  - Unread notices use **SemiBold** font weight; read notices use Regular
- **Bottom Bar**: Search field (`"Search by title"`) + Filter icon button (right)
- **Pull-to-refresh**: supported

### Filters (bottom sheet)
Three options: **All** / **Unread** / **Read**

### State
| Field | Type | Description |
|-------|------|-------------|
| `isRefreshing` | Bool | Pull-to-refresh active |
| `isNoticesLoading` | Bool | Loading class notices specifically |
| `searchQuery` | String | Live search text |
| `selectedFilter` | Enum (ALL/UNREAD/READ) | Active filter |
| `isFilterVisible` | Bool | Show filter bottom sheet |
| `allNotices` | [Notice] | All fetched notices |
| `filteredNotices` | [Notice] | After search + filter applied |
| `classes` | [Class] | Available classes (CLASS type only) |
| `selectedClass` | Class? | Currently selected class |
| `isClassPickerVisible` | Bool | Show class picker bottom sheet |

### Filtering Logic (client-side)
```
filteredNotices = allNotices
  .filter { selectedFilter == ALL || (selectedFilter == UNREAD && !isRead) || (selectedFilter == READ && isRead) }
  .filter { searchQuery.isBlank() || heading.contains(searchQuery, ignoreCase: true) }
```

### API Calls
- On load (SCHOOL): `GET School/Notices?pg=1`
- On load (STAFF): `GET School/Notices?pg=1&isStaffNotice=true`
- On load (CLASS): First `GET [classes API]`, then `GET School/Notices?pg=1&ClassID={id}`
- On tap notice: navigate to NoticeDetail

---

## 4. Screen 2 — Notice Detail

### UI Layout
- **Top Bar**: Back arrow + `"Notice details"` title
- **Body** (scrollable):
  - Heading (SemiBold)
  - Body text (HTML stripped — remove all `<tag>` patterns, trim)
  - Horizontal divider
  - Row: Calendar icon + `"Added"` label + `noticeDate` value
  - Horizontal divider
  - Row: Clock icon + `"Updated on"` label + `updatedOn` value (first 16 chars)
  - If `hasAttachment == true`:
    - Horizontal divider
    - Attachment row: paperclip icon + `"Attachment"` label + `[View]` button + `|` + `[Download]` button
    - Preview box: colored box showing `"PDF"` (red tint) or `"File"` (primary color tint) + file size

### Actions
- **View**: Opens attachment in in-app viewer (shared viewer screen)
- **Download**: Downloads file to device (shows "Download started" snackbar)

### API Call
- `GET School/NoticeDTL?ID={noticeId}`

---

## 5. Screen 3 — Circular List

### UI Layout
- **Top Bar**: Back arrow + `"All Circular"` + **Add** icon button (top right, navigates to Create Circular)
  - Below top bar: Academic year dropdown selector
- **Body**: Lazy scrollable list, items grouped by circular date (sticky date headers)
  - Each row shows: title, updated-on, attachment indicator, "New" badge if `isNew`, unread dot if `!isRead`, "Must Read" indicator if `mustRead=true`
- **Bottom Bar**: Search field (`"Search by title"`) + Filter icon button
- **Pull-to-refresh**: supported

### Filters
Same as Notice List: **All** / **Unread** / **Read**

### State
| Field | Type | Description |
|-------|------|-------------|
| `isRefreshing` | Bool | Pull-to-refresh active |
| `searchQuery` | String | Live search |
| `selectedFilter` | Enum | Active filter |
| `isFilterVisible` | Bool | Show filter bottom sheet |
| `allCirculars` | [Circular] | All fetched circulars |
| `filteredCirculars` | [Circular] | After search + filter |
| `academicYears` | [AcademicYear] | Year options |
| `selectedYear` | AcademicYear? | Selected year (default = current year where `isCur=true`) |
| `isYearPickerVisible` | Bool | Show year picker bottom sheet |

### Filtering Logic (client-side)
```
filteredCirculars = allCirculars
  .filter { selectedFilter == ALL || (selectedFilter == UNREAD && !isRead) || (selectedFilter == READ && isRead) }
  .filter { searchQuery.isBlank() || title.contains(searchQuery, ignoreCase: true) }
```

### API Calls
- On load: `GET School/Circulars?pg=1` (returns both circulars and academic years)
- On year change: `GET School/Circulars?pg=1&YrID={yrID}`

---

## 6. Screen 4 — Circular Detail

### UI Layout
- **Top Bar**: Back arrow + circular title
- **Body** (scrollable):
  - Heading (SemiBold)
  - Body text (HTML stripped)
  - Divider
  - Row: Calendar icon + `"Added"` + `cirDate`
  - Divider
  - Row: Clock icon + `"Updated on"` + `updatedOn` (first 16 chars)
  - If `hasAttachment == true`: same attachment row as Notice Detail

### Actions
- **View attachment**: opens in shared viewer
- **Download**: downloads file

### API Call
- `GET School/CircularDTL?ID={circularId}`

---

## 7. Screen 5 — Create Circular (Admin only)

### UI Layout
A form screen with:
1. **Title** — text input (required)
2. **Description** — multiline text input (optional)
3. **Date** — date picker field (default = today, format `dd MMMM yyyy` for display, `yyyy-MM-dd` for API)
4. **Recipient Type** — dropdown selector (see options below)
5. **Conditional fields** (appear based on recipient type):
   - `STAFF_TYPE`: Staff Type multi-selector → then Staff multi-selector (loads after staff types selected)
   - `ALL_CLASSES` / `STUDENTS_PARENTS`: Class multi-selector
   - `STUDENTS_PARENTS` + classes selected: Scholar Type selector (All / Boarding / Day Scholar) + Student/Parent multi-selector
6. **Active** — checkbox/toggle (default `true`)
7. **Must Read** — checkbox/toggle (default `false`)
8. **File attachment** — optional file picker

### Recipient Types
| Value | Label |
|-------|-------|
| `"0"` | All User |
| `"1"` | All students/parents |
| `"3"` | All Staff |
| `"6"` | Staff type |
| `"4"` | Classes |
| `"5"` | Students/Parents |

### Scholar Types
| Value | Label |
|-------|-------|
| `"2"` | All |
| `"1"` | Boarding |
| `"0"` | Day Scholar |

### API Calls
1. On load: `GET Admin/CreateCircular` — returns form reference data (staff types, classes, `isBoardingSchool`)
2. When staff types selected: `GET Message/StaffContact?StaffTypeIDs={id1,id2,...}`
3. When classes selected (for STUDENTS_PARENTS): `GET Message/StudentParentContact?OfUserType=1&ClassIDs={id1,id2,...}&ScholarType={value}`
4. On submit: `POST Admin/SaveCircular` with JSON body (see request model below)

### Submit Logic
- Validate title is not blank
- Convert file to base64 before sending
- Build `classIDs` as comma-separated string of classID values
- Build `siDs` (staff IDs) as comma-separated string of receiverID values (for `STAFF_TYPE` only)
- Build `stIDs` (student/parent) as comma-separated `"{classID}|{receiverID}"` pairs (for `STUDENTS_PARENTS` only)

---

## 8. API Reference

**Base URL**: configured per environment (append endpoints to base URL)

All responses include:
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "..."
}
```
`errorCode == 0` means success. Any non-zero value is an error — display `message` to user.

---

### 8.1 Get Notices

**Request**
```
GET School/Notices
Query params:
  pg             Int     Page number (default 1)
  isStaffNotice  Bool?   true for staff notices (omit for school)
  ClassID        Int?    Class ID for class notices (omit otherwise)
```

**Response**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "Notices",
  "totalNotice": 25,
  "unreadNotice": 5,
  "noticeList": [
    {
      "id": "abc123",
      "ntID": 101,
      "heading": "Holiday Notice",
      "detail": "<p>School holiday on Friday.</p>",
      "noticeDate": "21-Jan-2025",
      "updatedOn": "2025-01-21T14:34:00",
      "isNew": true,
      "isRead": false,
      "hasAttachment": true,
      "filePath": "https://cdn.example.com/notice.pdf",
      "fileSize": "348.75 KB"
    }
  ]
}
```

---

### 8.2 Get Notice Detail

**Request**
```
GET School/NoticeDTL
Query params:
  ID  String  Notice ID
```

**Response**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "Notice",
  "notice": {
    "id": "abc123",
    "ntID": 101,
    "heading": "Holiday Notice",
    "detail": "<p>School holiday on Friday.</p>",
    "noticeDate": "21-Jan-2025",
    "updatedOn": "2025-01-21T14:34:00",
    "isNew": false,
    "isRead": true,
    "hasAttachment": true,
    "filePath": "https://cdn.example.com/notice.pdf",
    "fileSize": "348.75 KB"
  }
}
```

---

### 8.3 Get Circulars

**Request**
```
GET School/Circulars
Query params:
  pg     Int     Page number (default 1)
  title  String  Search by title (default "")
  date   String  Filter by date (default "")
  YrID   Int     Academic year ID (0 = all years)
```

**Response**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "Circulars",
  "totalCirculer": 15,
  "unreadCirculer": 3,
  "enableCreate": true,
  "circularList": [
    {
      "cirID": 332,
      "title": "Testing new circular",
      "message": "<p>HOLIDAY</p>",
      "cirDate": "10-Feb-2026",
      "updatedOn": "10-Feb-2026 17:23 PM",
      "isNew": true,
      "isRead": false,
      "hasAttachment": true,
      "filePath": null,
      "fileSize": null,
      "isEditable": false,
      "postedBy": 5,
      "userDTL": {
        "name": "John Doe",
        "photo": "https://cdn.example.com/photo.jpg",
        "userID": 5,
        "userType": 2,
        "otherInfo": "Principal",
        "childName": null
      },
      "id": "9ue6TTKZNYEOWjmngRdrNw==",
      "mustRead": true,
      "readBy": 10,
      "sentTo": "All",
      "classes": null,
      "status": "Active"
    }
  ],
  "academicYears": [
    {
      "yrID": 9,
      "session": "2025-2026",
      "isCur": true,
      "startDate": "01-Apr-2025",
      "endDate": "31-Mar-2026"
    },
    {
      "yrID": 8,
      "session": "2024-2025",
      "isCur": false,
      "startDate": "01-Apr-2024",
      "endDate": "31-Mar-2025"
    }
  ]
}
```

---

### 8.4 Get Circular Detail

**Request**
```
GET School/CircularDTL
Query params:
  ID  String  Circular ID
```

**Response**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "CircularDTL",
  "circuler": {
    "cirID": 332,
    "title": "Testing new circular",
    "message": "<p>HOLIDAY</p>",
    "cirDate": "10-Feb-2026",
    "updatedOn": "10-Feb-2026 17:23 PM",
    "isNew": false,
    "isRead": true,
    "hasAttachment": true,
    "filePath": "https://cdn.example.com/circular.pdf",
    "fileSize": "512 KB",
    "isEditable": false,
    "postedBy": 5,
    "userDTL": {
      "name": "John Doe",
      "photo": "https://cdn.example.com/photo.jpg",
      "userID": 5,
      "userType": 2,
      "otherInfo": "Principal",
      "childName": null
    },
    "id": "9ue6TTKZNYEOWjmngRdrNw==",
    "mustRead": true,
    "readBy": 10,
    "sentTo": "All",
    "classes": null,
    "status": "Active"
  }
}
```

---

### 8.5 Get Create Circular Form Data

**Request**
```
GET Admin/CreateCircular
(no query params)
```

**Response**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "CreateCircular",
  "isBoardingSchool": false,
  "staffTypes": [
    { "staffTypeID": 1, "staff_Type": "Teaching" },
    { "staffTypeID": 2, "staff_Type": "Non-Teaching" }
  ],
  "classes": [
    { "classID": 10, "className": "Class 1", "id": "abc==", "isSelect": false },
    { "classID": 11, "className": "Class 2", "id": "def==", "isSelect": false }
  ]
}
```

---

### 8.6 Get Staff Contacts

**Request**
```
GET Message/StaffContact
Query params:
  StaffTypeIDs  String  Comma-separated staff type IDs (e.g. "1,2")
```

**Response**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "StaffContact",
  "contacts": [
    {
      "receiverID": 101,
      "receiverType": 2,
      "name": "Jane Smith",
      "designation": "Math Teacher",
      "staffTypeID": 1,
      "photo": "https://cdn.example.com/staff.jpg"
    }
  ]
}
```

---

### 8.7 Get Student/Parent Contacts

**Request**
```
GET Message/StudentParentContact
Query params:
  OfUserType   Int     Always 1
  ClassIDs     String  Comma-separated class IDs (e.g. "10,11")
  ScholarType  Int     0=Day Scholar, 1=Boarding, 2=All
```

**Response**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "StudentParentContact",
  "contacts": [
    {
      "receiverID": 201,
      "receiverType": 3,
      "name": "Alice Johnson",
      "classID": 10,
      "className": "Class 1",
      "childName": "Bob Johnson",
      "rollNumber": "01",
      "photo": null
    }
  ]
}
```

---

### 8.8 Save Circular

**Request**
```
POST Admin/SaveCircular
Content-Type: application/json
```

**Request Body**
```json
{
  "title": "Circular Title",
  "details": "<p>Circular description</p>\n",
  "releadOn": "2025-02-10",
  "recipientType": "0",
  "status": true,
  "mustRead": false,
  "scholarType": "2",
  "siDs": null,
  "classIDs": null,
  "stIDs": null,
  "browsedFile": {
    "attachment": "base64encodedfilecontenthere...",
    "fileExt": "pdf"
  }
}
```

> Note: `browsedFile` is `null` when no file is attached.

**Field Reference**

| Field | Type | Description |
|-------|------|-------------|
| `title` | String | Required. Circular title |
| `details` | String? | HTML-wrapped body text. Null if empty |
| `releadOn` | String | Date in `yyyy-MM-dd` format |
| `recipientType` | String | `"0"` All, `"1"` Students/Parents, `"3"` All Staff, `"4"` Classes, `"5"` Students/Parents by class, `"6"` Staff type |
| `status` | Bool | `true` = active |
| `mustRead` | Bool | Mark as must-read |
| `scholarType` | String | `"0"` Day Scholar, `"1"` Boarding, `"2"` All |
| `siDs` | String? | Staff IDs (for recipientType `"6"`). Format: `"101,102,103"` |
| `classIDs` | String? | Class IDs (for recipientType `"4"` or `"5"`). Format: `"10,11"` |
| `stIDs` | String? | Student IDs (for recipientType `"5"`). Format: `"{classID}\|{receiverID},{classID}\|{receiverID}"` |
| `browsedFile` | Object? | File attachment or null |

**Response**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "Circular saved successfully"
}
```

---

## 9. Data Models

### Notice
```swift
struct Notice {
    let id: String         // Prefer `id` field; fallback to ntID.toString()
    let ntID: Int
    let heading: String
    let detail: String?    // HTML content — strip tags for display
    let noticeDate: String?
    let updatedOn: String?
    let isNew: Bool
    let isRead: Bool
    let hasAttachment: Bool
    let filePath: String?
    let fileSize: String?
}
```

### Circular
```swift
struct Circular {
    let cirID: Int
    let title: String
    let message: String?   // HTML content — strip tags for display
    let cirDate: String?
    let updatedOn: String?
    let isNew: Bool
    let isRead: Bool
    let hasAttachment: Bool
    let filePath: String?
    let fileSize: String?
    let mustRead: Bool
    let id: String         // Prefer `id` field; fallback to cirID.toString()
    let postedByName: String?   // From userDTL.name
    let postedByPhoto: String?  // From userDTL.photo
    let postedByRole: String?   // From userDTL.otherInfo
}
```

### AcademicYear
```swift
struct AcademicYear {
    let yrID: Int
    let session: String    // e.g. "2025-2026"
    let isCur: Bool        // true = current year
    let startDate: String
    let endDate: String
}
```

### Class
```swift
struct Class {
    let classID: Int?
    let className: String?
    let id: String
    let isSelect: Bool
}
```

### StaffType
```swift
struct StaffType {
    let staffTypeID: Int
    let staffType: String  // Note: API key is "staff_Type"
}
```

### StaffContact
```swift
struct StaffContact {
    let receiverID: Int
    let receiverType: Int
    let name: String
    let designation: String?
    let staffTypeID: Int?
    let photo: String?
}
```

### StudentParentContact
```swift
struct StudentParentContact {
    let receiverID: Int
    let receiverType: Int
    let name: String
    let classID: Int?
    let className: String?
    let childName: String?
    let rollNumber: String?
    let photo: String?
}
```

### CreateCircularData
```swift
struct CreateCircularData {
    let isBoardingSchool: Bool
    let staffTypes: [StaffType]
    let classes: [Class]
}
```

### SaveCircularRequest
```swift
struct SaveCircularRequest {
    let title: String
    let details: String?
    let releadOn: String           // yyyy-MM-dd
    let recipientType: String
    let status: Bool
    let mustRead: Bool
    let scholarType: String
    let siDs: String?              // Staff IDs, comma-separated
    let classIDs: String?          // Class IDs, comma-separated
    let stIDs: String?             // "{classID}|{receiverID}" pairs, comma-separated
    let browsedFile: CircularAttachment?
}

struct CircularAttachment {
    let attachment: String   // base64 encoded file content
    let fileExt: String      // e.g. "pdf", "jpg"
}
```

---

## 10. Business Logic Summary

### Notice List
1. Call API based on notice type on screen load
2. For CLASS type: load classes first, auto-select first class, then fetch notices for that class
3. Apply client-side filter (ALL/UNREAD/READ) and search on every change
4. Pull-to-refresh reloads the current set
5. Group notices by `noticeDate` for display

### Circular List
1. Load circulars on screen load (`YrID=0` = all)
2. Academic years come back in the same response — auto-select `isCur=true` year
3. When user picks a different year, re-fetch with new `YrID`
4. Apply client-side filter + search on every change
5. Group circulars by `cirDate` for display

### Notice/Circular Detail
1. Fetch detail by ID on screen load
2. Strip HTML tags from `detail`/`message` for plain text display
3. Attachment "View": open in app's attachment viewer
4. Attachment "Download": trigger system file download

### Create Circular
1. Load form reference data (staff types, classes) on screen load
2. When recipient type changes: clear previously selected sub-fields
3. When staff types selected: fetch staff contacts for those types
4. When classes selected (for STUDENTS_PARENTS): fetch student/parent contacts
5. On submit:
   - Validate title is not blank
   - Convert file to base64 string (background thread)
   - Build request payload with conditional ID fields
   - On success: navigate back
   - On failure: show error snackbar

### HTML Stripping
Replace all occurrences of `<[^>]*>` regex with `""`, then trim whitespace. Also decode `&nbsp;` → `" "` for display.

### Date Formats
- **Display**: `"dd-MMM-yyyy"` (e.g. `"21-Jan-2025"`)
- **API submit**: `"yyyy-MM-dd"` (e.g. `"2025-01-21"`)
- **Updated on display**: take first 16 characters of the raw `updatedOn` string

---

*Document generated from Android implementation. For questions, contact the Android team.*
