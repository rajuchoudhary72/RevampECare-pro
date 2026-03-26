package com.app.ecarepro.feature.discipline

enum class DisciplineUserType {
    STUDENT,
    STAFF;

    val uTypeValue: Int
        get() = when (this) {
            STUDENT -> 1
            STAFF -> 3
        }

    val complianceUType: Int
        get() = when (this) {
            STUDENT -> 1
            STAFF -> 2
        }
}

enum class DisciplineModuleType {
    INFRACTION,
    APPRECIATION,
}
