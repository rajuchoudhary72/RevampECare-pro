# Transport Attendance Module - iOS Implementation Guide

## Table of Contents
1. [Overview](#overview)
2. [Navigation & Screen Flow](#navigation--screen-flow)
3. [API Endpoints](#api-endpoints)
4. [Data Models](#data-models)
5. [Constants](#constants)
6. [Screen 1: Transport Attendance (Main)](#screen-1-transport-attendance-main)
7. [Screen 2: Mark Attendance](#screen-2-mark-attendance)
8. [Screen 3: View Attendance Report](#screen-3-view-attendance-report)
9. [Screen 4: Out Pass](#screen-4-out-pass)
10. [UI Components](#ui-components)
11. [Business Logic Rules](#business-logic-rules)

---

## Overview

The Transport Attendance module handles bus transport attendance for students. It allows staff to:
- **Mark** pickup/drop attendance for students on bus routes
- **View** attendance reports filtered by date, route, and stops
- **Manage** Out Passes (OP) for students
- **Drop** individual students from the transport

---

## Navigation & Screen Flow

```
TransportAttendance (Main Screen)
  |
  |-- [Mark attendance button] --> Mark Attendance Screen (inline, same ViewModel)
  |-- [View attendance button] --> View Attendance Screen (separate ViewModel)
  |-- [View Out Pass button]   --> Out Pass Screen (separate ViewModel)
```

- **TransportAttendance** and **MarkAttendance** share the same ViewModel (`TransportAttViewModel`)
- **ViewAttendance** has its own `ViewAttendanceViewModel`
- **OutPass** has its own `OutPassViewModel`

---

## API Endpoints

Base URL: `https://apiuat.franciscanecare.net/`

### 1. GET Routes List
```
GET Transport/Routes
```
**Response:**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "Routes",
  "routeLST": [
    { "routeID": 239, "routeName": "Route 1 - Kashipur" }
  ]
}
```

---

### 2. GET Stoppage List
```
GET Transport/Stoppage?RouteIDs={routeIDs}&Trip={trip}
```
| Param    | Type   | Description                              |
|----------|--------|------------------------------------------|
| RouteIDs | String | Route ID                                 |
| Trip     | Int    | Trip type (0 for DROP, 1=UP, 2=DOWN)     |

**Response:**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "Stoppage",
  "stopLST": [
    { "stopID": 483, "stopName": "Corbett Sun City" },
    { "stopID": 484, "stopName": "Kashipur" }
  ]
}
```

---

### 3. GET Students to Mark Transport Attendance
```
GET Transport/StudentToMarkTransAttendane?RouteID={routeID}&StopID={stopID}&Trip={trip}&AttDate={attDate}&StopIDs={stopIDs}
```
| Param   | Type   | Description                                          |
|---------|--------|------------------------------------------------------|
| RouteID | String | Route ID                                             |
| StopID  | Int    | Always pass `0`                                      |
| Trip    | Int    | Trip type (1=UP, 2=DOWN)                             |
| AttDate | String | Date in `yyyy-MM-dd` format                         |
| StopIDs | String | Comma-separated stop IDs (e.g. `"483,484"`) URL-encoded |

**Response:**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "StudentToMarkTransAttendane",
  "freezDrop": false,
  "freezPickup": false,
  "stuLst": [
    {
      "stID": 3129,
      "stName": "DAKSHITH SINGH SAINI",
      "className": "6-D",
      "admissionNo": "8000",
      "rollNo": "8",
      "photo": "https://s3-noi.aces3.ai/.../3129.jpg",
      "route": null,
      "stop": null,
      "stopID": 0,
      "pickupStatus": 0,
      "dropStatus": 0,
      "pickupAtt": "Absent",
      "pickupTime": null,
      "dropAtt": "Absent",
      "dropTime": null,
      "isdropped": false,
      "isConstant": false
    }
  ]
}
```

> **Important:** `route`, `stop`, `pickupTime`, `dropTime`, `pickupAtt`, `dropAtt` can be `null`. Handle accordingly.

---

### 4. GET Students to Drop
```
GET Transport/StudentToDrop?RouteID={routeID}&StopID={stopID}&AttDate={attDate}
```
| Param   | Type   | Description                  |
|---------|--------|------------------------------|
| RouteID | Int    | Route ID                     |
| StopID  | Int    | Single stop ID               |
| AttDate | String | Date in `yyyy-MM-dd` format  |

**Response:** Same structure as endpoint #3.

---

### 5. POST Mark Attendance
```
POST Transport/PostTransAttendance
Content-Type: application/json
```
**Request Body:**
```json
{
  "attDate": "2026-02-13",
  "routeID": 239,
  "stopID": 0,
  "trip": 1,
  "stuAtt": [
    { "stID": 3129, "status": 1, "stopID": 483 },
    { "stID": 2805, "status": 0, "stopID": 484 }
  ]
}
```

| Field  | Type | Description                                      |
|--------|------|--------------------------------------------------|
| status | Int  | `0` = Absent, `1` = Present, `2` = OP (OutPass)  |
| trip   | Int  | `1` = UP (to school), `2` = DOWN (from school)    |

**Response:**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "Attendance marked successfully"
}
```

---

### 6. GET Drop a Student
```
GET Transport/DropToStudent?StID={stID}&AttDate={attDate}&HasDropped={hasDropped}
```
| Param      | Type    | Description                 |
|------------|---------|-----------------------------|
| StID       | Int     | Student ID                  |
| AttDate    | String  | Date in `yyyy-MM-dd` format |
| HasDropped | Boolean | Always `true` when dropping |

**Response:**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "Updated successfully"
}
```

---

### 7. GET Attendance Report
```
GET Transport/TransAttendanceReport?RouteID={routeID}&StopIDs={stopIDs}&AttDate={attDate}
```
| Param   | Type   | Description                             |
|---------|--------|-----------------------------------------|
| RouteID | Int    | Route ID                                |
| StopIDs | String | Comma-separated stop IDs, URL-encoded   |
| AttDate | String | Date in `yyyy-MM-dd` format             |

**Response:**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "TransAttendanceReport",
  "stopLST": [
    {
      "stopID": 483,
      "stopName": "Corbett Sun City",
      "stuLst": [
        {
          "stID": 3129,
          "stName": "DAKSHITH SINGH SAINI",
          "className": "6-D",
          "admissionNo": "8000",
          "rollNo": "8",
          "photo": "https://...",
          "route": null,
          "stop": null,
          "stopID": 0,
          "pickupStatus": 0,
          "dropStatus": 0,
          "pickupAtt": "Absent",
          "pickupTime": null,
          "dropAtt": "Absent",
          "dropTime": null,
          "isdropped": false,
          "isConstant": false
        }
      ]
    }
  ]
}
```

---

### 8. GET Out Pass Report
```
GET Transport/OutPassReport?AttDate={attDate}
```
| Param   | Type   | Description                 |
|---------|--------|-----------------------------|
| AttDate | String | Date in `yyyy-MM-dd` format |

**Response:**
```json
{
  "errorCode": 0,
  "status": "ok",
  "message": "OutPassReport",
  "stuLst": [
    {
      "stID": 2805,
      "stName": "VIDAAD KHAN",
      "className": "4-C",
      "admissionNo": "6736",
      "rollNo": "20",
      "photo": "https://...",
      "route": "Route 1 - Kashipur",
      "stop": "Kashipur",
      "stopID": 484,
      "pickupStatus": 0,
      "dropStatus": 0,
      "pickupAtt": "Present",
      "pickupTime": "02:13 PM",
      "dropAtt": "Absent",
      "dropTime": null,
      "isdropped": false,
      "isConstant": false
    }
  ]
}
```

---

## Data Models

### Route
```
Route {
  routeID: Int
  routeName: String
}
```

### Stop
```
Stop {
  stopID: Int
  stopName: String
  students: [TransportStudent]   // populated in report APIs, empty from stoppage API
  checked: Boolean               // local UI state for checkbox selection
}
```

### TransportStudent
```
TransportStudent {
  stID: Int
  stName: String
  className: String
  admissionNo: String
  rollNo: String
  photo: String                  // URL to student photo thumbnail
  route: String?                 // nullable - can be null from API
  stop: String?                  // nullable - can be null from API
  stopID: Int
  pickupStatus: Int              // 0=Absent, 1=Present (used for marking)
  dropStatus: Int                // 0=Absent, 1=Present, 2=OP (used for marking)
  pickupAtt: String              // "Present" / "Absent" (display text from server)
  pickupTime: String?            // nullable - e.g. "02:13 PM" or null
  dropAtt: String                // "Present" / "Absent" (display text from server)
  dropTime: String?              // nullable
  isDropped: Boolean             // JSON key: "isdropped"
  isConstant: Boolean
  isSelected: Boolean            // local UI state (not from API)
}
```

### StudentAttendanceRequest (for POST)
```
StudentAttendanceRequest {
  stID: Int
  status: Int      // 0=Absent, 1=Present, 2=OP
  stopID: Int
}
```

### MarkTransportAttendanceResult
```
MarkTransportAttendanceResult {
  students: [TransportStudent]
  freezDrop: Boolean             // if true, drop attendance is locked
  freezPickup: Boolean           // if true, pickup attendance is locked
}
```

---

## Constants

```
// Attendance status
PRESENT = 1
ABSENT = 0
OP = 2             // Out Pass (only for DOWN trip)
DROP_CONFORM = 5

// Trip types
UP_TRIP = 1             // "To the school"
DOWN_TRIP = 2           // "From the school"
DROP_STUDENT_TRIP = 3   // "Drop student"
```

---

## Screen 1: Transport Attendance (Main)

### Top Bar
- Title: "Transport attendance"
- Back button (left)
- "View Out Pass" text button (right) --> navigates to Out Pass screen

### Form Fields (top to bottom)
1. **Date selector** (left half) - Opens date picker, default = today
   - Display format: `dd MMM yyyy` (e.g. "13 Feb 2026")
   - System format: `yyyy-MM-dd` (for API calls)
2. **Trip type selector** (right half) - Opens bottom sheet with options:
   - "To the school" (UP_TRIP = 1)
   - "From the school" (DOWN_TRIP = 2)
   - "Drop student" (DROP_STUDENT_TRIP = 3)
3. **Route selector** - Opens bottom sheet with route list (fetched on init)
4. **Stop list** - Shown after route + trip type selected (fetched via API)
   - Checkboxes for each stop
   - "Select all" checkbox (HIDDEN for DROP_STUDENT_TRIP)
   - **DROP_STUDENT_TRIP**: Only single stop selection allowed (radio behavior)
   - **UP/DOWN_TRIP**: Multiple stop selection allowed

### Bottom Buttons
1. **"Mark attendance"** (primary filled) - Validates selections, fetches students, shows Mark Attendance screen
2. **"View attendance"** (outlined) - Navigates to View Attendance screen

### Data Flow
1. On screen load: Fetch routes via `GET Transport/Routes`
2. When route + trip type selected: Fetch stops via `GET Transport/Stoppage`
3. When "Mark attendance" clicked: Fetch students via:
   - `GET Transport/StudentToMarkTransAttendane` (for UP/DOWN trips)
   - `GET Transport/StudentToDrop` (for DROP trip)

### UI State
```
TransportAttUiState {
  selectedDate: String               // display format
  selectedDateSystem: String         // yyyy-MM-dd
  routeList: [Route]
  stopList: [Stop]
  studentList: [TransportStudent]
  selectedRoute: Route?
  selectedStops: [Stop]
  tripType: Int                      // 0=none, 1=UP, 2=DOWN, 3=DROP
  routeSelected: Boolean
  stoppersSelected: Boolean
  selectAllStops: Boolean
  busCount: Int
  freezDrop: Boolean
  freezPickup: Boolean
  isLoading: Boolean
  showStudentList: Boolean
  isDatePickerVisible: Boolean
  isTripTypeSheetVisible: Boolean
  isRouteSheetVisible: Boolean
  isConfirmDialogVisible: Boolean
  isDropConfirmVisible: Boolean
  droppingStudentIndex: Int?
  presentCount: Int
  absentCount: Int
  leaveCount: Int
}
```

---

## Screen 2: Mark Attendance

Shown inline (replaces the main screen content when `showStudentList = true`).

### Header Row
- **Bus count badge** (circle with number) - Students currently in the bus
- Route name + date + trip type label
- **Close button** (X icon) - Returns to main screen

### Student List (LazyColumn / UITableView)
Each student card shows:
- Student photo (circular, 48dp)
- Name + Class (e.g. "VIDAAD KHAN, 4-C")
- Stop name
- Admission number

**Attendance buttons vary by trip type:**

| Trip Type | Buttons Shown | Behavior |
|-----------|---------------|----------|
| UP_TRIP   | **P** (Present), **A** (Absent) | Sets `pickupStatus` |
| DOWN_TRIP | **OP** (OutPass), **A** (Absent), **P** (Present) | Sets `dropStatus` |
| DROP_STUDENT_TRIP | **"Mark as Drop"** button / **"Dropped"** label | Calls drop API |

### Attendance Button Colors
| Button | Selected BG    | Unselected BG | Selected Text | Unselected Text |
|--------|----------------|---------------|---------------|-----------------|
| P      | `#4CAF50` (green) | `#F5F5F5`  | White         | `#9E9E9E`       |
| A      | `#EF5350` (red)   | `#F5F5F5`  | White         | `#9E9E9E`       |
| OP     | `#FFA726` (orange)| `#F5F5F5`  | White         | `#9E9E9E`       |

Button size: 36x36dp, corner radius: 8dp

### Bottom Button
- **"Mark attendance"** (primary filled) - Only shown for UP/DOWN trips (NOT for DROP)
- Opens **Confirm Attendance Dialog**

### Confirm Attendance Dialog
Shows summary before saving:
- Title: "Confirm attendance"
- Subtitle: "Marked on {date}"
- Stat boxes: Present count (green), Absent count (red)
- OP count shown only for DOWN_TRIP when count > 0 (orange)
- Cancel + Confirm buttons
- On confirm: `POST Transport/PostTransAttendance`

### Drop Confirm Dialog
When "Mark as Drop" is tapped:
- Title: "Drop {studentName}"
- Subtitle: "on {date}"
- Cancel + Confirm buttons
- On confirm: `GET Transport/DropToStudent`
- On success: Update student's `isDropped = true` locally

### Bus Count Calculation Logic
```
busCount = 0
for each student:
  if pickupStatus == PRESENT: busCount++
  if tripType == DOWN_TRIP:
    if dropStatus == PRESENT or dropStatus == OP:
      if busCount > 0: busCount--
return busCount
```

### Business Rules
- If `freezPickup == true`: Disable all UP_TRIP attendance buttons
- If `freezDrop == true`: Disable all DOWN_TRIP attendance buttons
- For DOWN_TRIP: If student's `pickupStatus != PRESENT`, cannot set drop status to Present or OP (only Absent allowed). Show error: "Absent student status cannot be changed"

---

## Screen 3: View Attendance Report

### Top Bar
- Title: "View Attendance"
- Back button

### Selection View (default)
1. **Date selector** - Date picker, no default date
2. **Route selector** - Bottom sheet with routes (fetched on init)
3. **Stop list** - Checkboxes, "Select all" checkbox, multiple selection
4. **"View attendance"** button - Fetches report

### Report View (after fetch)
Shown when `showReport == true`, replaces selection view.

**Header:**
- Student count badge (circle) - Total students across all stops
- Route name
- Date + "To the school" label
- Close button (X) - Returns to selection view

**Report body (scrollable list):**
For each stop:
- **Stop header row**: Red dot + Stop name + "PICKUP" + "DROP" labels
- **Student cards** under each stop:
  - Student photo, name, class, admission number
  - Pickup chip: shows pickup time or "--" (green if Present, red if Absent)
  - Drop chip: shows drop time or "--" (green if Present, red if Absent)

### Status Chip Colors
| Status  | Color          |
|---------|----------------|
| Present | `#4CAF50` (green) |
| Absent  | `#EF5350` (red)   |
| Other   | `#FFA726` (orange) |

Chip: rounded 6dp, padding 10h/4v, white text, 11sp bold

### UI State
```
ViewAttendanceUiState {
  selectedDate: String
  selectedDateSystem: String
  routeList: [Route]
  stopList: [Stop]
  reportData: [Stop]           // stops with nested students
  selectedRoute: Route?
  routeSelected: Boolean
  stoppersSelected: Boolean
  selectAllStops: Boolean
  isLoading: Boolean
  showReport: Boolean
  isDatePickerVisible: Boolean
  isRouteSheetVisible: Boolean
}
```

---

## Screen 4: Out Pass

### Top Bar
- Title: "Out Passes"
- Back button

### Content
1. **Date selector row** - Calendar icon + date text + dropdown arrow
   - Tapping opens date picker
   - No default date (user must select)
2. **Student list** (shown after date selected and API returns data)
   - Each card: Photo + "Name, Class" + "Stop: {stop}" + "Route: {route}"
3. **Empty state** - "No out pass records found" when no data

### Data Flow
1. User selects date
2. Fetch `GET Transport/OutPassReport?AttDate={date}`
3. Display student list or empty state

### UI State
```
OutPassUiState {
  selectedDate: String
  selectedDateSystem: String
  studentList: [TransportStudent]
  isLoading: Boolean
  showReport: Boolean
  isDatePickerVisible: Boolean
  isDropConfirmVisible: Boolean
  droppingStudentIndex: Int?
}
```

---

## UI Components

### DropdownSelector
- Card with border (`#E0E0E0`), rounded 8dp
- Text + dropdown arrow icon on the right
- Full width, padding 12h/14v

### RouteBottomSheet
- Modal bottom sheet (skip partial expand)
- Scrollable list of routes with radio buttons
- Selected route highlighted

### TripTypeBottomSheet
- Modal bottom sheet
- 3 radio options: "To the school", "From the school", "Drop student"

### StudentAttendanceItem (Mark Attendance card)
- Card: white bg, rounded 12dp, 1dp elevation
- Row: Photo (48dp circle) + Info column + Action buttons
- Info: "Name, Class" (bold) / "Stop: {stop}" / "Admission no: {number}"

### StudentReportItem (View Attendance card)
- Card: white bg, rounded 12dp, 1dp elevation
- Row: Photo (48dp circle) + Info column + Status chips
- Info: Name (bold) / "Class: {class}" / "Admission no: {number}"
- Chips: Pickup time chip + Drop time chip (horizontally spaced 6dp)

### OutPassStudentItem
- Card: white bg, rounded 12dp, 1dp elevation
- Row: Photo (48dp circle) + Info column
- Info: "Name, Class" (bold) / "Stop: {stop}" / "{route}"

### EcareProDatePicker
- Standard date picker component
- Returns date string in `yyyy-MM-dd` format
- Converted to display format `dd MMM yyyy` for UI

---

## Business Logic Rules

1. **Route fetch**: Always fetch routes on screen init (for both main and view attendance screens)
2. **Stoppage fetch**: Triggered when both route AND trip type are selected
   - For DROP_STUDENT_TRIP: Pass `trip=0` to stoppage API
   - For UP/DOWN: Pass actual trip type
3. **Student fetch**: Triggered when stops are selected and "Mark attendance" is tapped
4. **Validation before marking**:
   - Route must be selected
   - Trip type must be selected (non-zero)
   - At least one stop must be checked
5. **Freeze behavior**: Server returns `freezPickup`/`freezDrop` flags - when true, attendance buttons are disabled (attendance already submitted)
6. **DOWN trip restriction**: Cannot mark a student as Present/OP for drop if their `pickupStatus != PRESENT`
7. **DROP trip stop selection**: Only single stop selection (radio behavior, not checkbox)
8. **After saving attendance**: Re-fetch students to get updated status from server
9. **Date formats**:
   - Display: `dd MMM yyyy` (e.g. "13 Feb 2026")
   - API: `yyyy-MM-dd` (e.g. "2026-02-13")
