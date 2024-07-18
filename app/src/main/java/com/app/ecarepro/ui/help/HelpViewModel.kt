package com.app.ecarepro.ui.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.app.ecarepro.data.database.databases.SchoolDatabase
import com.app.ecarepro.data.database.model.SchoolEntity
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val schoolDatabase: SchoolDatabase
) : ViewModel() {
    val school = schoolDatabase.getSchoolsFlow().map { it.lastOrNull() }.asLiveData()
}