package com.app.ecarepro.ui.studentProfile.share_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.ecarepro.model.Profile
import com.app.ecarepro.model.SiblingDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModelProfile @Inject constructor() : ViewModel() {
    private val profile = MutableLiveData<Profile>()
    private val siblingDetails = MutableLiveData<List<SiblingDetails>>()

    fun getProfile(): LiveData<Profile> {
        return profile
    }

    fun setProfile(profile: Profile) {
        this.profile.value = profile
    }

    fun getSiblingDetails(): LiveData<List<SiblingDetails>> {
        return siblingDetails
    }

    fun setSiblingDetails(siblingDetails: List<SiblingDetails>) {
        this.siblingDetails.value = siblingDetails
    }
}

