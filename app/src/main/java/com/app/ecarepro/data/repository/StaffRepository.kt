package com.app.ecarepro.data.repository

import com.app.ecarepro.model.MyClasse

interface StaffRepository {

    suspend fun getMyClass(subID: Int, iD: Int): List<MyClasse>

}