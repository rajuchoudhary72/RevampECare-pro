package com.app.ecarepro.core.data.repository

import com.app.ecarepro.core.database.dao.SchoolDao
import com.app.ecarepro.core.database.dao.UserDao
import com.app.ecarepro.core.domain.ext.asResultFlow
import com.app.ecarepro.core.domain.repository.MarkManagerRepository
import com.app.ecarepro.core.network.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import java.net.URLEncoder
import javax.inject.Inject

internal class MarkManagerRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao,
    private val schoolDao: SchoolDao,
) : MarkManagerRepository {

    /**
     * Combines the school's marks-entry base URL (from the local DB) with a freshly
     * generated SSO token (from the API) and returns the ready-to-open URL.
     *
     * The token is URL-encoded so that the `+` and `=` characters in the Base64
     * string are transmitted correctly as query-parameter values.
     *
     * Example result:
     * https://demo.franciscansolutions.info/MarksManager/Login/EcareLogin.aspx
     *   ?token=UyEknqj8JqYvLD2i9R9VlPTN0Rg7kWZQ56xS5xXURf8XRC4ZxaWQ1G4XW%2Bbzu7H...
     */
    override fun getMarkManagerUrl(): Flow<Result<String>> = asResultFlow {
        // 1. Active user → school code
        val user = userDao.getActiveUser()
            ?: error("No active user found. Please log in again.")

        // 2. School record → marksEntryURL
        val schools = schoolDao.getAllSchools()
        val school = schools.firstOrNull { it.schoolCode == user.schoolCode }
            ?: error("School record not found for code: ${user.schoolCode}")

        val baseUrl = school.marksEntryURL
            ?: error("Marks entry is not configured for your school.")

        // 3. Generate SSO token from API (Device = 1 for Android)
        val tokenKey = userRemoteDataSource.generateToken(device = 1)

        // 4. URL-encode the token (Base64 contains +, /, = which must be percent-encoded)
        val encodedToken = URLEncoder.encode(tokenKey, "UTF-8")

        "$baseUrl?token=$encodedToken"
    }

    override fun getSchoolWebsiteUrl(): Flow<Result<String>> = asResultFlow {
        val user = userDao.getActiveUser()
            ?: error("No active user found. Please log in again.")

        val schools = schoolDao.getAllSchools()
        val school = schools.firstOrNull { it.schoolCode == user.schoolCode }
            ?: error("School record not found for code: ${user.schoolCode}")

        val raw = school.webSite
            ?: error("School website is not configured for your school.")

        // Bare domains like "www.demoschool.com" need a scheme; default to https.
        if (raw.startsWith("http://") || raw.startsWith("https://")) raw
        else "https://$raw"
    }
}
