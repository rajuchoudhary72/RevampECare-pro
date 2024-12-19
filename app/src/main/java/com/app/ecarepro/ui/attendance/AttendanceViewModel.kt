package com.app.ecarepro.ui.attendance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.app.ecarepro.data.datastore.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.math.log
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsConstants
import com.app.ecarepro.ui.firebaseAnalytics.AnalyticsManager
@HiltViewModel
class AttendanceViewModel @Inject constructor(
    userDataStore: UserDataStore,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {

    private val sortBy = MutableStateFlow<SortBy>(SortBy.AllClasses)

    private val attendanceSummary = userDataStore.getDashboardData().map { it?.attendanceSummary }

    val classes = combine(
        flow = sortBy,
        flow2 = attendanceSummary
    ) { sortBy, attendanceSummary ->
        when (sortBy) {
            SortBy.AllClasses -> attendanceSummary?.classSummary ?: emptyList()
            SortBy.MarkedClasses -> attendanceSummary?.classSummary?.filter { it.isMarked == true }
            SortBy.NotMarkedClasses -> attendanceSummary?.classSummary?.filter { it.isMarked == false }
        }
    }

    val presentCount =
        attendanceSummary.map {
            Log.e("TAG", "${it?.totalStudent()} -> ${it?.totalPresent} " )
            "${it?.totalPresent} (${(it?.totalPresent ?: 0) percentOf (it?.totalStudent() ?: 0)}%)"
        }.asLiveData()
    val absentCount =
        attendanceSummary.map { "${it?.totalAbsent} (${(it?.totalAbsent ?: 0) percentOf (it?.totalStudent() ?: 0)}%)" }.asLiveData()
    val leaveCount =
        attendanceSummary.map { "${it?.totalLeave} (${(it?.totalLeave ?: 0) percentOf (it?.totalStudent() ?: 0)}%)" }.asLiveData()
    val lateCount =
        attendanceSummary.map { "${it?.totalLate} (${(it?.totalLate ?: 0) percentOf (it?.totalStudent() ?: 0)}%)" }.asLiveData()

    val sortOptions = attendanceSummary.map {
        listOf(
            Pair(SortBy.AllClasses, "All Classes (${it?.classSummary?.size})"),
            Pair(SortBy.MarkedClasses, "Marked Classes (${
                it?.classSummary?.count
                { it.isMarked == true }
            })"
            ),
            Pair(SortBy.NotMarkedClasses, "Not Marked Classes (${
                it?.classSummary?.count
                { it.isMarked == false }
            })"
            )
        )
    }

    fun sortBy(sortBy: SortBy) {
        this.sortBy.update { sortBy }
    }
    fun sendScreenEvent() {
        analyticsManager.trackScreen(AnalyticsConstants.Screens.ATTENDANCE_TAB)
    }
}

infix fun Int.percentOf(value: Int): String {
    return if (this == 0) "0.0"
    else String.format("%.2f", (this.toDouble() / value) * 100)
}

enum class SortBy() {
    AllClasses, MarkedClasses, NotMarkedClasses
}