package v2.navigation

import android.os.Bundle
import com.app.ecarepro.R


const val EXTRA_LEGACY_FLOW = "legacyFlow"
const val EXTRA_DESTINATION_ID = "destinationId"
const val EXTRA_EXTRAS = "extras"

sealed class LegacyNavigationDestination {
    abstract val destinationId: Int
    open val extras: Bundle? = null


    data object AddSchool : LegacyNavigationDestination() {
        override val destinationId: Int = R.id.schoolCodeFragment
    }

   /* data class UserProfile(val userId: String) : ExistingNavigationDestination() {
        override val destinationId: Int = R.id.userProfileFragment // Replace with your actual ID
        override val extras: Bundle
            get() = Bundle().apply {
                putString("USER_ID_KEY", userId)
            }
    }*/

}