package com.app.ecarepro.ui.institutioncode

import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InstitutionCodeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

}