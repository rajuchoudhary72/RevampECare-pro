package com.app.ecarepro.model

import java.io.Serializable

enum class RecipientsType(val title: String, val id: Int) {
    PARENTS("Parent's", 2),
    STUDENTS("Student's", 1),
    STAFFS("Staff's", 0);


    companion object : Serializable {
        fun getRecipientsType(title: String): RecipientsType {
            return values().firstOrNull { it.title == title } ?: PARENTS
        }
    }
}