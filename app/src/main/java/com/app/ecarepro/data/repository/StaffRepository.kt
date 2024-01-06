package com.app.ecarepro.data.repository

import com.app.ecarepro.model.MyClasse
import dagger.Provides



interface StaffRepository {

    suspend fun getMyClass(subID: Int, iD: Int): List<MyClasse>

}