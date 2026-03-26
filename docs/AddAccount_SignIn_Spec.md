# Add Account & Sign In — Jetpack Compose Implementation Spec

> Extracted from the legacy ECarePro-Revamp app (Fragment/ViewModel/Room/DataStore pattern).
> Use this as the reference blueprint for re-implementing in the new Jetpack Compose + MVI project.

---

## 1. Complete User Flow

```
App Launch
    │
    ▼
[Onboarding / School Code Screen]
    │  User types school code (6-char OTP field)
    │  OR taps a saved school from carousel
    ▼
validateSchoolCode(code) ──► API: SchoolCode/ValidateSchoolCode?schoolCode=XXXX
    │  errorCode == 0 → school is valid
    │  School saved/updated in Room DB (schools table)
    ▼
[Sign In Screen]  ← receives schoolCode + isStudentLoginBlocked
    │
    │ ── STEP 1 ──
    │  User enters Username → tap Continue
    │  verifyUser(schoolCode, username) ──► API: User/VerifyUser
    │  Check: isUserAlreadyLogin(userId, userType) → if true, block with "User already login!"
    │  errorCode == 0 → show Password field
    │
    │ ── STEP 2 ──
    │  User enters Password → tap Continue
    │  twoFactorLogin(schoolCode, username, password) ──► API: User/TwoFactorLogin
    │
    ├── errorCode == 0, authenticated == true
    │       ├── isOTPEnabled == true → navigate to OTP Verification screen
    │       │       └── On OTP verified → launchToNextDestination()
    │       └── isOTPEnabled == false → launchToNextDestination()
    │
    ├── errorCode == 401 → "Invalid password"
    ├── errorCode == 429 → show server message (rate limit)
    └── errorCode == 404 → show server message (not found)

launchToNextDestination(response):
    ├── isAddAccount == true (came from "Add Account" nav arg)
    │       └── Find user in DB by (userId, schoolCode, userType)
    │           Set currentUserId in DataStore
    │           Restart app (FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK)
    └── isAddAccount == false (normal login)
            Register FCM token
            Navigate to Home / Dashboard
```

---

## 2. API Endpoints

### 2.1 Validate School Code
```
GET  SchoolCode/ValidateSchoolCode?schoolCode={CODE}

Response: NetworkSchool
{
  "errorCode": 0,
  "status": "0",
  "message": "",
  "schoolCode": "DEMOIN",
  "schoolName": "Demo Institution",
  "logo": "https://...",
  "city": "Mumbai",
  "state": "Maharashtra",
  "themColor": "#4CAF50",
  "isStudentLoginBlocked": false,
  "active": 1,
  "slider": [...],
  "schAdd_1": "...",
  "schAdd_2": "...",
  "contactEmail": "...",
  "webSite": "...",
  "supportPhone": "...",
  "supportEmail": "...",
  "supportHours": "...",
  "supportDays": "...",
  "eCareProSch": true,
  "feePayemtURL": null,
  "feeReportURL": null,
  "assessmentMarksURL": null,
  "marksEntryURL": null,
  "isBoardingSchool": false,
  "schUpdatedOn": "2024-01-01"
}
```

### 2.2 Verify User (Step 1 of login)
```
POST or GET  User/VerifyUser
Params: schoolCode, username

Response: NetworkUserDetailsDto
{
  "errorCode": 0,
  "message": "",
  "status": "0",
  "userID": 1234,
  "userType": 1,          // 1=Student, 2=Parent, 3+=Staff
  "name": "John Doe",
  "photo": "https://...",
  "roleName": "Student",
  "mobileNumer": "9876543210",
  "classID": "5",
  "class": "5th A",
  "stName": "John",
  "isVerified": false,
  "authToken": null,
  "sessionID": null,
  "schoolCode": "DEMOIN",
  "isUserAuthenticated": false,
  "loginTime": null,
  "id": 0
}
```

### 2.3 Two-Factor Login (Step 2 of login)
```
POST  User/TwoFactorLogin
Body: { schoolCode, userName, password }

Response: TwoFactorLoginResponseDto
{
  "errorCode": 0,
  "status": "0",
  "message": "Login successful",
  "authenticated": true,
  "isOTPEnabled": false,
  "isOTPValidated": false,
  "isDefaulter": false,
  "otpAuthKey": null,
  "otpMode": 0,
  "remainAttampts": null,
  "schCode": null,
  "userDTL": {
    "authToken": "eyJhbGci...",
    "sessionID": "abc-xyz-123",
    "authenticated": true,
    "errorCode": 0,
    "message": null,
    "status": null,
    "userID": 1234,
    "userType": 1,
    "name": "John Doe",
    "photoPath": "https://...",
    "roleName": "Student",
    "mobileNumer": "9876543210",
    "classID": 5,
    "class": "5th A",
    "stName": "John",
    "isDefaulter": false
  }
}
```

---

## 3. Local Database (Room)

### 3.1 Database Declaration
```kotlin
// DB name: "ecare-database"
// Tables: users, schools
// Version: 8

@Database(
    entities = [UserEntity::class, SchoolEntity::class],
    version = 8,
    exportSchema = true,
)
abstract class ECareProDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun schoolDao(): SchoolDao
}
```

### 3.2 UserEntity — `users` table
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   // local auto-generated row ID
    val userId: Int,                // server userID
    val name: String?,
    val photo: String?,
    val userType: Int,              // 1=Student, 2=Parent, 3+=Staff
    val authToken: String?,
    val sessionId: String?,
    val roleName: String?,
    val schoolCode: String?,        // which school this user belongs to
    val isUserAuthenticated: Boolean?,
    val isVerified: Boolean?,
    val mobileNumber: String?,
    val classID: String?,
    val loginTime: String?,
    val stName: String?,
    val className: String?,         // "5th A"
)
```

**Key rules:**
- Multiple users CAN exist in the `users` table (multi-account support)
- `OnConflictStrategy.REPLACE` on insert (same userId + schoolCode + userType replaces existing)
- The **currently active** user is identified by `currentUserId` stored in DataStore (= `UserEntity.id`, the local auto-generated PK)

### 3.3 SchoolEntity — `schools` table
```kotlin
@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey val schoolCode: String,   // PK = schoolCode (unique per school)
    val schoolName: String?,
    val logo: String?,
    val city: String?,
    val state: String?,
    val themColor: String?,               // hex color for school branding
    val active: Int?,
    val schAdd1: String?,
    val schAdd2: String?,
    val contactEmail: String?,
    val webSite: String?,
    val supportPhone: String?,
    val supportEmail: String?,
    val supportHours: String?,
    val supportDays: String?,
    val eCareProSch: Boolean?,
    val feePaymentURL: String?,
    val feeReportURL: String?,
    val assessmentMarksURL: String?,
    val marksEntryURL: String?,
    val isBoardingSchool: Boolean?,
    val schUpdatedOn: String?,
    val logoNScName: String?,
    val logoScName: String?,
    val slides: String?,                  // JSON-serialized slider list
)
```

### 3.4 UserDao
```kotlin
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long     // returns new row id

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUser(userId: Int): UserEntity

    @Query("SELECT * FROM users WHERE user_id = :userId AND schoolCode = :schoolCode AND userType = :userType LIMIT 1")
    suspend fun getUser(userId: Int, schoolCode: String, userType: Int): UserEntity

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    fun getUserFlow(userId: Int): Flow<UserEntity>

    @Query("SELECT * FROM users")
    fun getUsersFlow(): Flow<List<UserEntity>>

    @Query("DELETE FROM users WHERE user_id = :userId")
    fun deleteUser(userId: Int)

    @Delete
    fun deleteUser(userEntity: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Int)

    @Query("DELETE FROM users")
    suspend fun nukeTable()
}
```

### 3.5 SchoolDao
```kotlin
@Dao
interface SchoolDao {
    @Insert
    suspend fun insertSchool(school: SchoolEntity)

    @Update
    fun updateSchool(entity: SchoolEntity)

    @Query("SELECT * FROM schools WHERE schoolCode = :schoolCode LIMIT 1")
    suspend fun getSchool(schoolCode: String): SchoolEntity

    @Query("SELECT * FROM schools WHERE schoolCode = :schoolCode LIMIT 1")
    suspend fun getSchoolData(schoolCode: String): SchoolEntity?   // nullable — used for existence check

    @Query("SELECT * FROM schools WHERE schoolCode = :schoolCode LIMIT 1")
    fun getSchoolFlow(schoolCode: String): Flow<SchoolEntity>

    @Query("SELECT * FROM schools")
    fun getSchoolsFlow(): Flow<List<SchoolEntity>>

    @Delete
    fun deleteSchool(entity: SchoolEntity)

    @Query("DELETE FROM schools")
    suspend fun nukeTable()
}
```

---

## 4. DataStore (Preferences)

The legacy app uses a `UserDataStore` class backed by `DataPreferencesStore`. Key keys used:

| Key | Type | Purpose |
|---|---|---|
| `current_school_code` | String? | Currently active school code |
| `current_user_id` | Int | Local DB `UserEntity.id` of active user |
| `is_user_authenticated` | Boolean | Quick auth check (not per-user) |

**Key methods used during login flow:**
```kotlin
// Read
userDataStore.getCurrentSchoolCode(): String?
userDataStore.getCurrentSchoolCodeAsFlow(): Flow<String?>
userDataStore.isUserAuthenticated(): Boolean
userDataStore.getUsersFlow(): Flow<List<UserSession>>   // lightweight user list for multi-account check

// Write (after successful login)
userDataStore.setCurrentSchoolCode(schoolCode: String)
userDataStore.setCurrentUserId(id: Int)                // local DB row id, NOT server userId
```

---

## 5. Login → DB Write Sequence

This is the **exact sequence** that happens after a successful `twoFactorLogin()` response:

```
twoFactorLogin() returns TwoFactorLoginResponseDto (errorCode=0, authenticated=true)
    │
    ├── Inside UserRepository.twoFactorLogin() implementation:
    │       1. Map response.userDTL → UserEntity
    │       2. Set userEntity.schoolCode = schoolCode (from request param)
    │       3. Set userEntity.isUserAuthenticated = true
    │       4. Insert into Room: val rowId = userDao.insertUser(userEntity)
    │       5. Save school to Room: schoolDao.insertSchool(networkSchool.asSchoolEntity())
    │          (school was already fetched & saved during validateSchoolCode step)
    │
    └── Back in SignInFragment / ViewModel:
            If isAddAccount:
                val entity = userDatabase.getUser(userDTL.userID, schoolCode, userDTL.userType)
                userDataStore.setCurrentUserId(entity.id)   // local row id
                restartApp()
            If normal login:
                registerFcmToken()
                navigate to Home
```

**UserEntity mapping from TwoFactorLoginResponseDto:**
```kotlin
UserEntity(
    userId = userDTL.userID,
    name = userDTL.name,
    photo = userDTL.photoPath,
    userType = userDTL.userType,
    authToken = userDTL.authToken,
    sessionId = userDTL.sessionID,
    roleName = userDTL.roleName,
    schoolCode = schoolCode,             // from request, NOT response
    isUserAuthenticated = true,
    isVerified = true,
    mobileNumber = userDTL.mobileNumer,
    classID = userDTL.classID?.toString(),
    loginTime = <current timestamp>,
    stName = userDTL.stName,
    className = userDTL.classX,
)
```

---

## 6. Multi-Account (Add Account) Logic

### How "Add Account" is triggered
- From the dashboard/menu: navigate to SchoolCode screen with `args = bundleOf("add_account" to true)`
- In SchoolCode screen: if `add_account` arg is present AND a school code is already in DataStore → skip directly to Sign In

### Duplicate User Check (verifyUser step)
```kotlin
// Before showing password field, check if this user is already logged in
suspend fun isUserAlreadyLogin(userId: Int?, userType: Int?): Boolean {
    val user = userDataStore.getUsersFlow().map { users ->
        users.firstOrNull {
            it.userId == userId && it.schoolCode == schoolCode && it.userType == userType
        }
    }.first()
    return user != null
}
// If true → show "User already login!" and block the flow
```

### After successful add-account login
```kotlin
// Get the newly inserted UserEntity's local id
val entity = userDatabase.getUser(response.userDTL.userID, schoolCode, response.userDTL.userType)
// Set as the active user
userDataStore.setCurrentUserId(entity.id)
// Restart the entire app so it loads fresh with the new user context
val intent = Intent(context, MainActivity::class.java)
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
startActivity(intent)
Runtime.getRuntime().exit(0)
```

### Saved accounts carousel (School Code screen)
```kotlin
// Observe all schools from Room, mark the currently active one
schools = userDataStore.getCurrentSchoolCodeAsFlow().flatMapLatest { currentCode ->
    schoolDatabase.getSchoolsFlow().map { entities ->
        entities.map { entity ->
            entity.asNetworkSchool()?.copy(isSelected = entity.schoolCode == currentCode)
        }
    }
}
// Tap a saved school → setCurrentSchoolCode(school.schoolCode) → navigate to Sign In
```

---

## 7. Sign In Screen — Two-Step UX Logic

```
STATE: userNameValid = false

[Continue] tap:
    if (!userNameValid):
        Call verifyUser(username)
        → success: set userNameValid = true, show password field, disable username field
        → failure: show "Invalid username"
    else (userNameValid == true):
        Call twoFactorLogin(username, password)
        → handle response codes

[Previous/Back] tap:
    if (userNameValid):
        // Go back to username step
        hide password field
        clear password text
        re-enable username field
        set userNameValid = false
    else:
        popBackStack (go back to SchoolCode screen)
```

---

## 8. userType Values

| Value | Role |
|---|---|
| 1 | Student |
| 2 | Parent |
| 3+ | Staff / Teacher / Admin |

Used in:
- `UserEntity.userType`
- `isUserAlreadyLogin()` duplicate check
- `getUserTypeName()` → "Student" / "Parent" / "Staff"
- The new app uses `user.userType == 1` check to determine Staff vs Student for question paper class selection

---

## 9. OTP Verification (Two-Factor)

When `twoFactorLogin()` returns `isOTPEnabled == true`:
- Show message from response
- Navigate to OTP Verification screen with params:
  - `message` — instruction text
  - `userName` — the username entered
  - `schoolCode` — current school code
  - `oTPAuthKey` — key from response, used for OTP validation API

OTP APIs:
```
POST User/ResendOTP    { schoolCode, oTPAuthKey }
POST User/ValidateOTP  { schoolCode, oTPAuthKey, otp, userName }
```

After OTP validated → call `launchToNextDestination()` same as normal login.

---

## 10. New Compose Implementation — What to Build

### Screens needed
1. **SchoolCodeScreen** — OTP-style 6-char input + saved schools list + "Find School" button
2. **SignInScreen** — two-step (username → password) with back navigation between steps
3. **OtpVerificationScreen** — OTP input + resend + confirm

### Domain Models to create
```kotlin
// School code validation result
data class School(
    val schoolCode: String,
    val schoolName: String?,
    val logo: String?,
    val city: String?,
    val themColor: String?,
    val isStudentLoginBlocked: Boolean,
    // ... other fields
)

// Login result
data class LoginResult(
    val errorCode: Int,
    val authenticated: Boolean,
    val isOTPEnabled: Boolean,
    val otpAuthKey: String?,
    val message: String?,
    val user: LoggedInUser?,
)

data class LoggedInUser(
    val userId: Int,
    val userType: Int,
    val name: String?,
    val photo: String?,
    val roleName: String?,
    val authToken: String,
    val sessionId: String?,
    val schoolCode: String,
    val classID: Int?,
    val className: String?,
)
```

### Repository interfaces needed
```kotlin
interface SchoolRepository {
    suspend fun validateSchoolCode(code: String): School  // throws on error
    fun getSavedSchools(): Flow<List<School>>             // from Room
    suspend fun getCurrentSchoolCode(): String?
    suspend fun setCurrentSchoolCode(code: String)
}

interface UserRepository {
    suspend fun verifyUser(schoolCode: String, username: String): VerifyUserResult
    suspend fun login(schoolCode: String, username: String, password: String): LoginResult
    suspend fun resendOtp(schoolCode: String, otpAuthKey: String): LoginResult
    suspend fun validateOtp(schoolCode: String, otpAuthKey: String, otp: String, username: String): LoginResult
    suspend fun isUserAlreadyLoggedIn(userId: Int, schoolCode: String, userType: Int): Boolean
    suspend fun getActiveUser(): LoggedInUser?
    suspend fun setActiveUser(localRowId: Int)
    fun getActiveUserFlow(): Flow<LoggedInUser?>
}
```

### Room tables in new app
- Keep the same `users` and `schools` table structure
- Rename column `user_id` → maps to server `userID`
- The `id` (autoGenerate PK) is the **internal handle** — store this in DataStore as current user
- Use `@Upsert` (Room 2.5+) instead of `@Insert(REPLACE)` where possible

### DI bindings needed
```kotlin
// DataModule.kt
@Binds fun bindsSchoolRepository(impl: SchoolRepositoryImpl): SchoolRepository

// DataSourceModule.kt
@Binds fun bindsUserRemoteDataSource(impl: UserRemoteDataSourceImpl): UserRemoteDataSource

// NetworkModule.kt
@Provides fun provideSchoolService(retrofit: Retrofit): SchoolService
@Provides fun provideUserAuthService(retrofit: Retrofit): UserAuthService
```

### Navigation args
```kotlin
@Serializable
sealed interface LoginNavGraph : NavKey {
    @Serializable data object SchoolCode : LoginNavGraph
    @Serializable data class SignIn(
        val schoolCode: String,
        val isStudentLoginBlocked: Boolean,
        val isAddAccount: Boolean = false,
    ) : LoginNavGraph
    @Serializable data class OtpVerification(
        val schoolCode: String,
        val username: String,
        val otpAuthKey: String,
        val message: String,
        val isAddAccount: Boolean = false,
    ) : LoginNavGraph
}
```

---

## 11. Edge Cases & Business Rules

| Rule | Detail |
|---|---|
| Auto-skip SchoolCode | If `isAddAccount` arg present AND a school code is already saved in DataStore → skip directly to Sign In |
| Duplicate account block | After `verifyUser()` succeeds, check Room for existing user with same `(userId, schoolCode, userType)` — if found, show "User already login!" and block |
| School code case | Always `toUpperCase()` before calling `validateSchoolCode()` API |
| Flavor-locked codes | `MYSFHS` flavor → pre-fill "MYSFHS", disable input. `MYSFPS Play` flavor → pre-fill "MYSFPS", disable input. `Franciscan e-Care` → free input + "Find School" button visible |
| isStudentLoginBlocked | Pass from school validation response to Sign In screen; use in Forgot Password navigation |
| Add Account restart | After successful add-account login: `FLAG_ACTIVITY_NEW_TASK OR FLAG_ACTIVITY_CLEAR_TASK` + `Runtime.exit(0)` — full app restart required |
| FCM token on normal login | Register device FCM token after normal (non-add-account) login |
| OTP flow | If `isOTPEnabled == true` in login response, show OTP screen; after OTP validation succeeds, treat identically to normal login success |

---

## 12. Key File Locations in Legacy App (For Reference)

| File | Path |
|---|---|
| InstitutionCodeFragment | `ui/institutioncode/InstitutionCodeFragment.kt` |
| InstitutionCodeViewModel | `ui/institutioncode/InstitutionCodeViewModel.kt` |
| SignInFragment | `ui/signin/SignInFragment.kt` |
| SignInViewModel | `ui/signin/SignInViewModel.kt` |
| UserEntity | `data/database/model/UserEntity.kt` |
| SchoolEntity | `data/database/model/SchoolEntity.kt` |
| UserDao | `data/database/dao/UserDao.kt` |
| SchoolDao | `data/database/dao/SchoolDao.kt` |
| ECareProDatabase | `data/database/ECareProDatabase.kt` |
| UserDatabaseImpl | `data/database/UserDatabaseImpl.kt` |
| UserRepository (interface) | `data/repository/UserRepository.kt` |
| SchoolRepository (interface) | `data/repository/SchoolRepository.kt` |
| TwoFactorLoginResponseDto | `data/network/model/submit_assignment/TwoFactorLoginResponseDto.kt` |
| NetworkUserDetailsDto | `data/network/model/NetworkUserDetailsDto.kt` |
| NetworkSchool | `data/network/model/NetworkSchool.kt` |
| LoginResponseDto | `data/network/model/LoginResponseDto.kt` |
| DatabaseModule | `di/DatabaseModule.kt` |
| DatabaseBinds | `di/DatabaseBinds.kt` |
