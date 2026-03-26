package com.app.ecarepro.feature.update_record.navigation

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.app.ecarepro.feature.update_record.class_promotion.ClassPromotionScreen
import com.app.ecarepro.feature.update_record.manage_roll_number.ManageRollNumberScreen
import com.app.ecarepro.feature.update_record.update_house.UpdateHouseScreen
import com.app.ecarepro.feature.update_record.update_profile_picture.UpdateProfilePictureScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface UpdateRecordNavGraph : NavKey {

    @Serializable
    data object ClassPromotion : UpdateRecordNavGraph

    @Serializable
    data object ManageRollNumber : UpdateRecordNavGraph

    @Serializable
    data object UpdateHouse : UpdateRecordNavGraph

    @Serializable
    data object UpdateProfilePicture : UpdateRecordNavGraph
}

fun EntryProviderBuilder<NavKey>.entryUpdateRecordNavigation(
    navigateBack: () -> Unit,
) {
    entry<UpdateRecordNavGraph.ClassPromotion> {
        ClassPromotionScreen(navigateBack = navigateBack)
    }
    entry<UpdateRecordNavGraph.ManageRollNumber> {
        ManageRollNumberScreen(navigateBack = navigateBack)
    }
    entry<UpdateRecordNavGraph.UpdateHouse> {
        UpdateHouseScreen(navigateBack = navigateBack)
    }
    entry<UpdateRecordNavGraph.UpdateProfilePicture> {
        UpdateProfilePictureScreen(navigateBack = navigateBack)
    }
}
