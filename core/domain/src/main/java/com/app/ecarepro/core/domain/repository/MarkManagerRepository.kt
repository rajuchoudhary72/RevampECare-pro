package com.app.ecarepro.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Provides the fully-formed Mark Manager URL that includes the SSO token so the
 * WebView can log in automatically.
 *
 * Flow:
 *  1. Read the active user's school code from the local database.
 *  2. Look up `marksEntryURL` for that school from the local database.
 *  3. Call GET /User/GenerateToken?Device=1 to obtain a short-lived SSO token.
 *  4. Return `<marksEntryURL>?token=<urlEncodedToken>`.
 */
interface MarkManagerRepository {
    /** Builds the Mark Manager SSO URL (requires GenerateToken API call). */
    fun getMarkManagerUrl(): Flow<Result<String>>

    /** Returns the school's website URL directly from the local DB (no API call). */
    fun getSchoolWebsiteUrl(): Flow<Result<String>>
}
