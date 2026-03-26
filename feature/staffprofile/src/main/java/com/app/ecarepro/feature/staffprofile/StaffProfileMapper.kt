package com.app.ecarepro.feature.staffprofile

import com.app.ecarepro.core.domain.model.StaffProfile
import com.app.ecarepro.designsystem.core.component.personlist.Gender
import com.app.ecarepro.designsystem.core.component.personlist.PersonPresentation
import com.app.ecarepro.designsystem.core.component.personlist.PersonType

/**
 * Extension function to convert StaffProfile to PersonPresentation
 */
fun StaffProfile.toPresentation(): PersonPresentation {
    return PersonPresentation(
        id = id,
        name = displayName,
        subtitle = "Designation: $designation",
        detail = "DOJ: 01 July 2012", // TODO: Add DOJ field to API when available
        profileImageURL = photo,
        gender = Gender.fromString(gender ?: ""),
        personType = PersonType.STAFF
    )
}
