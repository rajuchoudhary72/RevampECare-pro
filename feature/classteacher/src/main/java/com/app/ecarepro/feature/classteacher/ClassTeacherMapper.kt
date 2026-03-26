package com.app.ecarepro.feature.classteacher

import com.app.ecarepro.core.domain.model.ClassTeacher
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonType

/**
 * Converts a [ClassTeacher] domain model to a [PersonPresentation] for the list UI.
 *
 * Card shows:
 *  - name        → teacher name
 *  - subtitle    → designation (e.g. "PRT", "Officer")
 *  - detail      → class assigned (e.g. "Class: 2-A")
 *  - photo       → profile image
 */

fun ClassTeacher.toPresentation(): PersonPresentation = PersonPresentation(
    id = id.toString(),
    name = name,
    subtitle = designation,
    detail = if (className.isNotBlank()) "Class: $className" else "",
    profileImageURL = photo,
    gender = Gender.OTHER,
    personType = PersonType.STAFF,
)
