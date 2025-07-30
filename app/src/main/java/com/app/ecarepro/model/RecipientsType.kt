package com.app.ecarepro.model

import java.io.Serializable

enum class RecipientsType(val title: String, val id: Int) {
    PARENTS("Parent", 2),
    STUDENTS("Student", 1),
    STAFFS("Staff", 0);


    companion object : Serializable {
        fun getRecipientsType(title: String): RecipientsType {
            return values().firstOrNull { it.title == title } ?: PARENTS
        }
        fun getRecipientTypes(userType: Int): List<RecipientsType> {
            return if (userType == 3) {
                values().toList()
            } else {
                listOf(STAFFS)
            }
        }
    }
}