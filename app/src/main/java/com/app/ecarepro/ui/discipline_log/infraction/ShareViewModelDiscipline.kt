package com.app.ecarepro.ui.discipline_log.infraction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.ecarepro.data.network.model.NetworkStudentProfile
import com.app.ecarepro.model.RecentInfraction
import javax.inject.Inject

class ShareViewModelDiscipline  @Inject constructor() : ViewModel() {

    private val recentInfraction = MutableLiveData<RecentInfraction>()

    fun getRecentInfraction(): MutableLiveData<RecentInfraction> {
        return recentInfraction
    }

    fun setRecentInfraction(recentInfraction: RecentInfraction) {
        this.recentInfraction.value = recentInfraction
    }


}