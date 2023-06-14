package com.app.ecarepro.ui.searchinstitution

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchInstitutionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

}