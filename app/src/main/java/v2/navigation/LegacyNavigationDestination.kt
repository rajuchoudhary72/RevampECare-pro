package v2.navigation

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.R
import com.app.ecarepro.core.domain.model.User
import com.app.ecarepro.feature.timetable.navigation.TimetableNavigationGraph
import kotlinx.parcelize.Parcelize


const val EXTRA_LEGACY_FLOW = "legacyFlow"
const val EXTRA_DESTINATION_ID = "destinationId"
const val EXTRA_EXTRAS = "extras"

sealed class LegacyNavigationDestination {
    abstract val destinationId: Int
    open val extras: Bundle? = null


    data object AddSchool : LegacyNavigationDestination() {
        override val destinationId: Int = R.id.schoolCodeFragment
    }

    data class Main(val user: User) : LegacyNavigationDestination() {
        override val destinationId: Int = R.id.homeFragment
        override val extras: Bundle
            get() = bundleOf(
                "user" to user
            )

    }


}


/**
 * A type-safe representation of all possible entry points into the new Compose flow.
 * Each object/class corresponds to a specific starting screen or flow.
 */
sealed interface ComposeNavigationDestination : Parcelable {
    /**
     * The initial NavKey that the back stack should be populated with.
     */
    val startKey: NavKey

    @Parcelize
    data object Timetable : ComposeNavigationDestination {
        // The onboarding flow starts with the Onboarding screen.
        override val startKey: NavKey = TimetableNavigationGraph.Timetable
    }
}