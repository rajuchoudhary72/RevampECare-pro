package com.app.ecarepro.feature.knowyourteacher

import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonType

/**
 * Extension function to convert StaffProfile to PersonPresentation for Know Your Teacher.
 */
fun StaffProfile.toPresentation(): PersonPresentation {
    val subjectDetail = teachersSubject?.takeIf { it.isNotBlank() }?.let { "Subject: $it" } ?: ""
    return PersonPresentation(
        id = id,
        name = displayName,
        subtitle = designation,
        detail = subjectDetail,
        profileImageURL = photo,
        gender = Gender.fromString(gender ?: ""),
        personType = PersonType.STAFF
    )
}
