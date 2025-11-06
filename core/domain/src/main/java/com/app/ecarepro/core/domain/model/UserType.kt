package com.app.ecarepro.core.domain.model

import androidx.annotation.StringRes
import com.app.ecarepro.core.domain.R

enum class UserType(
    @param:StringRes val stringResId: Int,
    val id: Int,
) {
    PARENT(R.string.core_domain_parent, 2),
    STUDENT(R.string.core_domain_student, 1),
    STAFF(R.string.core_domain_staff, 3)
}