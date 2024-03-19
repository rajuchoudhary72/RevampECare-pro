package com.app.ecarepro.model

import java.io.Serializable

enum class RecipientsType(val title: String) {
    PARENTS("Parent's"),
    STUDENTS("Student's"),
    STAFFS("Staff's");


    companion object : Serializable {
        fun getRecipientsType(title: String): RecipientsType {
            return values().firstOrNull { it.title == title } ?: PARENTS
        }
    }
}