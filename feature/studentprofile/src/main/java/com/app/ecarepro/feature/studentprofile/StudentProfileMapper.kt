package com.app.ecarepro.feature.studentprofile

import com.app.ecarepro.core.domain.model.StudentProfile
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonType

/**
 * Extension function to convert StudentProfile to PersonPresentation
 */
fun StudentProfile.toPresentation(): PersonPresentation {
    val fullClassName = if (section != null) {
        "$classSTD-$section"
    } else {
        classSTD
    }

    return PersonPresentation(
        id = id,
        name = name ?: "",
        subtitle = "Class: $fullClassName | Roll: ${rollNumber ?: "N/A"}",
        detail = "Adm No: ${admissionNumber ?: "N/A"}",
        profileImageURL = photo,
        gender = Gender.fromString(gender ?: ""),
        personType = PersonType.STUDENT
    )
}
