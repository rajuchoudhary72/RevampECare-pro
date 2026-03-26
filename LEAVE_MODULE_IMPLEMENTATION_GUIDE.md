# Leave Module Implementation Guide

This document provides a comprehensive guide for implementing the Leave Module based on the iOS specification.

## Implementation Status

### ✅ Completed
1. Data Models for Applied Leaves module:
   - `LeaveStatus.kt` - Enum with display colors and text
   - `AppliedLeavesScreenType.kt` - Screen type configuration
   - `Leave.kt` - Main leave data model
   - `AppliedLeavesResponse.kt` - API response model
   - `LeaveAction.kt` - Action models (approve, reject, etc.)
   - `LeaveCardPresentation.kt` - UI presentation model

2. ViewModel for Applied Leaves:
   - `AppliedLeavesViewModel.kt` - Complete business logic

3. Data Models for Apply Leave module:
   - `ApplyLeaveScreenType.kt` - Screen type and enums
   - `LeaveSettingModels.kt` - Settings and configuration
   - `ApplyLeaveRequest.kt` - Request models
   - `LeaveValidationError.kt` - Validation errors
   - `LeaveDurationCalculator.kt` - Duration calculation logic

### 🚧 Pending Implementation

The following components need to be implemented to complete the module:

## 1. Apply Leave ViewModel

Create: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/applyleave/domain/ApplyLeaveViewModel.kt`

Key features:
- Form state management (dates, sessions, reason, attachments)
- Real-time validation
- Duration calculation
- Form submission
- File attachment handling

Reference the iOS specification document sections 418-1787 for complete implementation details.

## 2. UI Components

### Applied Leaves UI
Create the following files:

1. **AppliedLeavesScreen.kt**
   - Main screen composable
   - Tab management
   - Pull-to-refresh
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/appliedleaves/ui/`

2. **LeaveCardView.kt**
   - Individual leave card
   - Action buttons (approve/reject/forward)
   - Status badges
   - Attachment indicators
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/appliedleaves/ui/components/`

3. **LeaveTimeline.kt**
   - Visual timeline component
   - From/To date display
   - Duration display
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/appliedleaves/ui/components/`

4. **HorizontalTabBar.kt**
   - Status tabs (Pending, Approved, Rejected, Cancelled)
   - Reference: `feature/timetable/components/DayTabs.kt` for pattern
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/appliedleaves/ui/components/`

5. **BottomActionBar.kt**
   - Bulk approve/reject buttons
   - Shows when items selected
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/appliedleaves/ui/components/`

### Apply Leave UI
Create the following files:

1. **ApplyLeaveScreen.kt**
   - Main form screen
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/applyleave/ui/`

2. **LeaveTypeSelector.kt**
   - Dropdown for leave type selection
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/applyleave/ui/components/`

3. **LeaveBalanceView.kt**
   - Progress bar showing leave balance
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/applyleave/ui/components/`

4. **DateSection.kt**
   - Date pickers for from/to dates
   - Session selectors (First half/Second half)
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/applyleave/ui/components/`

5. **ReasonSelector.kt**
   - Reason dropdown
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/applyleave/ui/components/`

6. **DocumentUploadSection.kt**
   - File picker
   - Upload indicator
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/applyleave/ui/components/`

7. **TermsCheckbox.kt**
   - Terms and conditions checkbox
   - Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/applyleave/ui/components/`

## 3. Repository Layer

Create repository interfaces and implementations:

1. **AppliedLeavesRepository.kt**
   ```kotlin
   interface AppliedLeavesRepository {
       val applicantType: Int
       val userTypeName: String
       suspend fun fetchLeaves(status: Int, attPer: Boolean): AppliedLeavesResponse
       suspend fun performAction(request: LeaveActionRequest): GenericResponse
   }
   ```

2. **Implementation classes:**
   - `StudentLeavesRepository` (applicantType = 1)
   - `StaffLeavesRepository` (applicantType = 3)
   - `SelfLeavesRepository` (applicantType = 0)

3. **ApplyLeaveRepository.kt**
   ```kotlin
   interface ApplyLeaveRepository {
       suspend fun fetchLeaveSettings(): LeaveSettingResponse
       suspend fun submitLeaveRequest(request: ApplyLeaveRequest): ApplyLeaveResponse
   }
   ```

Location: `feature/leave/src/main/java/com/app/ecarepro/feature/leave/data/repository/`

## 4. API Integration

Add to your API service interface:

```kotlin
interface ECareApiService {
    // Applied Leaves endpoints
    @GET("leaveReport")
    suspend fun getLeaveReport(
        @Query("applType") applType: Int,
        @Query("status") status: Int,
        @Query("attPer") attPer: Boolean
    ): AppliedLeavesResponse

    @GET("leaveStatus")
    suspend fun getLeaveStatus(): AppliedLeavesResponse

    @POST("leaveAction")
    suspend fun performLeaveAction(
        @Body request: LeaveActionRequest
    ): GenericResponse

    // Apply Leave endpoints
    @GET("leaveSetting")
    suspend fun getLeaveSetting(): LeaveSettingResponse

    @POST("applyLeave")
    suspend fun applyLeave(
        @Body request: ApplyLeaveRequest
    ): ApplyLeaveResponse
}
```

## 5. Navigation

Update navigation configuration:

```kotlin
// Add routes
sealed class LeaveRoute {
    object AppliedLeavesSelf : LeaveRoute()
    object AppliedLeavesStudent : LeaveRoute()
    object AppliedLeavesStaff : LeaveRoute()
    object ApplyLeave : LeaveRoute()
}

// Add to navigation graph
composable("applied_leaves_self") {
    AppliedLeavesScreen(
        screenType = AppliedLeavesScreenType.SELF_LEAVES,
        // ...
    )
}

composable("apply_leave") {
    ApplyLeaveScreen(
        screenType = ApplyLeaveScreenType.STUDENT, // or STAFF
        // ...
    )
}
```

## 6. Design System Integration

Use existing design system components:

### Colors
```kotlin
MaterialTheme.appColors.primary // Green #4CAF50
MaterialTheme.appColors.textPrimary // #1E1D0E
MaterialTheme.appColors.textSecondary // #9FA297
MaterialTheme.appColors.background // White
```

### Typography
```kotlin
MaterialTheme.appTypography.interSemiBold14px
MaterialTheme.appTypography.interMedium16px
// etc.
```

### Components to use
- `EcareProScaffold` - Main scaffold
- `EcareProTopAppBar` - Top app bar
- `EcareProAsyncImage` - Image loading
- `Loader` - Loading indicator
- `EcareProOutlinedTextField` - Text fields

## 7. Key Implementation Patterns

### State Management
Follow the existing pattern from TimetableViewModel:
- Use `MutableStateFlow` for state
- Use `StateFlow` for exposing to UI
- Use `collectAsStateWithLifecycle()` in composables

### Tab Navigation
Reference `TimetableScreen.kt` for tab implementation:
- Use `PrimaryScrollableTabRow`
- Use `HorizontalPager` for swiping between tabs

### List Items
Reference `TimetableItem.kt` for list item patterns:
- Use `LazyColumn` for lists
- Add `HorizontalDivider` between items

### Error Handling
```kotlin
sealed class UIState {
    object Loading : UIState()
    object Loaded : UIState()
    object Error : UIState()
    object NoResult : UIState()
}
```

## 8. Testing Checklist

- [ ] Self leaves screen loads and displays leaves
- [ ] Student leaves screen with tabs (Pending, Approved, Rejected, Cancelled)
- [ ] Staff leaves screen with forward button
- [ ] Attendance toggle functionality
- [ ] Select all / individual selection
- [ ] Approve/Reject single leave
- [ ] Approve/Reject multiple leaves
- [ ] Pull to refresh
- [ ] Empty states
- [ ] Error states
- [ ] Apply leave form validation
- [ ] Leave balance display
- [ ] Date picker constraints
- [ ] Session selection
- [ ] Duration calculation
- [ ] Attachment upload
- [ ] Terms acceptance
- [ ] Submit leave request

## 9. Migration from Old Code

Remove these old files after new implementation is complete:
- `feature/leave/src/main/java/com/app/ecarepro/feature/leave/ApplyLeaveScreen.kt`
- `feature/leave/src/main/java/com/app/ecarepro/feature/leave/ApplyLeaveViewModel.kt`
- `feature/leave/src/main/java/com/app/ecarepro/feature/leave/LeaveListScreen.kt`
- `feature/leave/src/main/java/com/app/ecarepro/feature/leave/LeaveListViewModel.kt`
- `feature/leave/src/main/java/com/app/ecarepro/feature/leave/component/LeaveCard.kt`
- `feature/leave/src/main/java/com/app/ecarepro/feature/leave/component/LeaveHeader.kt`

## 10. Next Steps

1. Implement `ApplyLeaveViewModel.kt` with full validation logic
2. Create all UI composables listed in section 2
3. Implement repository layer (section 3)
4. Integrate with API (section 4)
5. Update navigation (section 5)
6. Test all functionality (section 8)
7. Remove old code (section 9)

## Reference Files

- **iOS Specification**: `/Users/gouravbhati/Downloads/LEAVE_MODULE_ANDROID.md`
- **Timetable Reference**: `/Users/gouravbhati/StudioProjects/ECarePro-Revamp/feature/timetable/`
- **Design System**: `/Users/gouravbhati/StudioProjects/ECarePro-Revamp/core/designsystem/`

## Important Notes

1. **Hilt Injection**: Use `@HiltViewModel` and `@Inject` for all ViewModels
2. **Serialization**: Use Kotlinx Serialization (`@Serializable`) for all data models
3. **Dates**: Use `java.time.LocalDate` for date handling
4. **Images**: Use Coil's `AsyncImage` for image loading
5. **Material 3**: Use Material 3 components throughout
6. **StateFlow**: Prefer StateFlow over LiveData for Compose integration

## Contact & Support

For questions or clarifications, refer to:
- Official Android Compose Documentation
- Material Design 3 guidelines
- Existing codebase patterns in timetable module
