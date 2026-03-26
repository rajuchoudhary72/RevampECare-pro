# Logout Flow — ECarePro Legacy Reference

> Source: `MainActivity.kt` (line 1723), `SystemViewModel.kt`, `UserRepositoryImpl.kt`, `AppSessionManager.kt`
> Target: Reuse in new Jetpack Compose + Clean Architecture project

---

## Overview

Logout has **two modes**:

| Mode | Trigger | Behaviour |
|---|---|---|
| **Normal** | User taps Logout button | Show confirmation dialog → on Yes → call API → clear all data → restart |
| **Force** | Session expired / 401 / kicked by server | Skip dialog → call API immediately → clear all data → restart |

---

## Full Execution Flow

```
logout(forceLogout)
    │
    ├─ forceLogout = false → show MaterialAlertDialog (Yes/No)
    │       └─ Yes clicked
    │
    └─ forceLogout = true  → skip dialog
            │
            ▼
    SystemViewModel.logout(onDataClear)
            │
            ├─ 1. Call API:  GET User/LogOut
            │       params: DeviceType=1, DeviceID=ANDROID_ID, SessionID
            │       → on errorCode == 0 → success
            │
            ├─ 2. sendLogoutEvent()         ← Analytics / tracking
            │
            ├─ 3. userDataStore.clear()
            │       ├─ DataStore Preferences → edit { clear() }
            │       ├─ Room: userDao.nukeTable()
            │       └─ Room: schoolDao.nukeTable()
            │
            └─ 4. onDataClear callback → MainActivity.clearAppData()
                    │
                    ├─ 4a. Delete all Room databases
                    │       for (dbName in databaseList()) deleteDatabase(dbName)
                    │
                    ├─ 4b. Clear SharedPreferences
                    │       getSharedPreferences("SHARED_PREF_NAME_PROMPT", MODE_PRIVATE)
                    │           .edit().clear().apply()
                    │
                    └─ 4c. clearAppCache()
                            cacheDir.deleteRecursively()
                            │
                            └─ appOut()  ← restart app
                                    startActivity(MainActivity, FLAG_NEW_TASK | CLEAR_TASK)
                                    Runtime.getRuntime().exit(0)
```

---

## Step-by-Step Details

### Step 1 — Logout API Call

```kotlin
// Retrofit service
@GET("User/LogOut")
suspend fun logout(
    @Query("DeviceType") deviceType: Int = 1,         // always 1 for Android
    @Query("deviceID")   deviceID: String,             // Settings.Secure.ANDROID_ID
    @Query("SessionID")  sessionID: String,            // from DataStore / DB
): CommonResponse

// Repository
override suspend fun logout(): Flow<Result<Boolean>> = flow {
    val response = userService.logout(
        deviceID  = Secure.getString(context.contentResolver, Secure.ANDROID_ID),
        sessionID = userDataStore.getUserSessionId().orEmpty()
    )
    if (response.errorCode == 0) emit(Result.success(true))
    else emit(Result.failure(IllegalArgumentException(response.message)))
}
```

> **New project note:**
> Map to your own `/User/LogOut` or equivalent. Always send `DeviceType`, `DeviceID`, `SessionID`.
> If the server returns a non-zero `errorCode`, still proceed with local data clear — never block logout on an API failure.

---

### Step 2 — Analytics Event (optional)

```kotlin
suspend fun sendLogoutEvent() {
    analyticsManager.trackEvent(
        event = "logout",
        params = mapOf(
            "user_id"     to currentUser.userId,
            "user_type"   to currentUser.userType,
            "school_code" to schoolData.schoolCode,
        )
    )
}
```

> Fire-and-forget. Wrap in `try/catch` so it never blocks the logout.

---

### Step 3 — DataStore / Preferences Clear

```kotlin
// DataStore (Jetpack)
context.dataStore.edit { it.clear() }

// Room tables (nuke)
userDao.nukeTable()     // DELETE FROM users
schoolDao.nukeTable()   // DELETE FROM schools
```

> In the new project: call `userDao.deleteAll()` and any other DAOs holding user-scoped data.

---

### Step 4a — Delete Room Database Files

```kotlin
val databases = databaseList()          // returns all DB file names for this app
for (dbName in databases) {
    deleteDatabase(dbName)              // deletes .db, .db-shm, .db-wal files
}
```

> This physically removes database files from disk. Useful as a hard reset.
> In the new project: call this from the `Activity` context after coroutine completes.

---

### Step 4b — Clear SharedPreferences

```kotlin
getSharedPreferences("SHARED_PREF_NAME_PROMPT", Context.MODE_PRIVATE)
    .edit()
    .clear()
    .apply()
```

> Replace `"SHARED_PREF_NAME_PROMPT"` with your own prefs file name.
> If your app uses **multiple** SharedPreferences files, clear each one.
> To clear ALL prefs at once:
> ```kotlin
> File(applicationInfo.dataDir, "shared_prefs")
>     .listFiles()
>     ?.forEach { file ->
>         getSharedPreferences(file.nameWithoutExtension, MODE_PRIVATE)
>             .edit().clear().apply()
>     }
> ```

---

### Step 4c — Clear App Cache

```kotlin
fun clearAppCache() {
    try {
        cacheDir.deleteRecursively()    // internal cache: /data/data/<pkg>/cache
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

> For **external** cache too (optional):
> ```kotlin
> externalCacheDir?.deleteRecursively()
> ```

---

### Step 5 — App Restart

```kotlin
private fun appOut() {
    val intent = Intent(this, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
    startActivity(intent)
    Runtime.getRuntime().exit(0)   // kills the process completely
}
```

> `FLAG_ACTIVITY_CLEAR_TASK` destroys all activities in the back stack.
> `Runtime.getRuntime().exit(0)` kills the process — forces all ViewModels, singletons, and in-memory state to be destroyed.
> In a Compose-only app, this is the correct approach (vs `recreate()` which retains `@HiltViewModel` instances).

---

## Multi-Account Variant (`AppSessionManager`)

When the app supports multiple saved accounts, logout works differently:

```kotlin
private suspend fun clearUserSession(context: Context, force: Boolean) {
    val currentUsers = dataStore.getUsersFlow().first().sortedBy { it.id }

    if (force.not() && currentUsers.size > 1) {
        // Still has other accounts — just delete current user row and switch
        database.deleteUserById(dataStore.getCurrentUserId()!!)
        dataStore.setCurrentUserId(
            currentUsers.first { it.id != dataStore.getCurrentUserId()!! }.id
        )
        // No app restart needed — just reload the UI for the new active user
    } else {
        // Last account or force logout — wipe everything
        dataStore.clear()
        context.databaseList()?.forEach { context.deleteDatabase(it) }
        context.getSharedPreferences("SHARED_PREF_NAME_PROMPT", Context.MODE_PRIVATE)
            .edit().clear().apply()
        context.cacheDir?.deleteRecursively()
        // then restart app
    }
}
```

> **For the new project's multi-account logout:**
> - If other accounts exist: delete the active user row → set another user as active → restart app via `FLAG_NEW_TASK or CLEAR_TASK`
> - If it's the last account: full wipe (DataStore + DB + SharedPrefs + Cache) → restart to login screen

---

## New Project — Clean Architecture Implementation

```
logout trigger (UI Intent)
    └── LogoutUseCase (domain)
            ├── userRepository.callLogoutApi()       ← Step 1
            ├── userRepository.clearActiveSession()  ← Step 3 (DataStore + Room)
            └── emit Result.success
                    └── ViewModel receives success
                            └── sendEvent(LogoutEvent.RestartApp)
                                    └── Activity: startActivity(FLAG_NEW_TASK|CLEAR_TASK) + finish()
                                                  + deleteDatabase() + clearSharedPrefs() + clearCache()
```

### Checklist for new project logout

- [ ] Call `User/LogOut` API with `DeviceID` + `SessionID`
- [ ] Even on API failure → still clear local data (never block logout)
- [ ] Clear DataStore / Preferences keys
- [ ] `userDao.deleteAll()` (or just the active user row for multi-account)
- [ ] `databaseList().forEach { deleteDatabase(it) }` for hard reset
- [ ] Clear SharedPreferences file(s)
- [ ] `cacheDir.deleteRecursively()`
- [ ] Restart with `FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK` + `finish()`
- [ ] Do NOT use `activity.recreate()` — it retains Hilt ViewModels

---

## Key Constants

| Item | Value |
|---|---|
| Logout API endpoint | `GET User/LogOut` |
| DeviceType (Android) | `1` |
| DeviceID source | `Settings.Secure.ANDROID_ID` |
| SharedPrefs name | `"SHARED_PREF_NAME_PROMPT"` |
| Restart flags | `FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK` |
