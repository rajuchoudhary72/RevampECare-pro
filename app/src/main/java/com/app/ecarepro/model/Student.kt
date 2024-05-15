package com.app.ecarepro.model

import java.util.jar.Attributes.Name

data class Student(
    val admissionNumber: String,
    val `class`: String,
    val contactMob: String,
    val contactPerson: String,
    val dob: String,
    val fatherName: String,
    val isSelected: Boolean,
    val name: String,
    val photo: String,
    val rollNumber: String,
    val stID: Int
){
    override fun toString(): String {
        return "$name $rollNumber $admissionNumber"
    }
}