package com.app.ecarepro.feature.dashboard.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.component.EcareProAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.dashboard.R

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedDestination: Int,
    profilePhotoUrl: String? = null,
    onDestinationSelected: (Destination) -> Unit,
) {

    NavigationBar(
        modifier = modifier,
        containerColor = White,
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            val isSelected = selectedDestination == index
            NavigationBarItem(
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = MaterialTheme.appColors.primary,
                    selectedTextColor = MaterialTheme.appColors.primary,
                    unselectedIconColor = MaterialTheme.appColors.textPrimary,
                    unselectedTextColor = MaterialTheme.appColors.textPrimary,
                ),
                onClick = {
                    onDestinationSelected(destination)
                },
                icon = {
                    when (destination.icon) {
                        is DestinationIcon.Icon -> {
                            Icon(
                                painterResource(destination.icon.defaultIcon),
                                contentDescription = stringResource(destination.contentDescription)
                            )
                        }

                        is DestinationIcon.Url -> {
                            EcareProAsyncImage(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(
                                        width = 0.6.dp,
                                        color = if (isSelected) MaterialTheme.appColors.primary else MaterialTheme.appColors.border,
                                        shape = CircleShape
                                    )
                                    .clip(CircleShape),
                                placeholder = painterResource(destination.icon.placeholder),
                                error = painterResource(destination.icon.placeholder),
                                imageUrl = if (destination == Destination.Profile) profilePhotoUrl else destination.icon.url,
                                contentDescription = stringResource(destination.contentDescription),
                            )
                        }

                    }

                },
                label = {
                    Text(
                        text = stringResource(destination.label),
                        style = MaterialTheme.appTypography.nunitoMedium12px.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                    )
                }
            )

        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    EcareProTheme {
        BottomNavigationBar(
            selectedDestination = 1,
            onDestinationSelected = {}
        )
    }
}


enum class Destination(
    val icon: DestinationIcon,
    val route: String,
    @param:StringRes val label: Int,
    @param:StringRes val contentDescription: Int,
) {

    MENU(
        icon = DestinationIcon.Icon(
            defaultIcon = R.drawable.ic_menu,
            selectedIcon = R.drawable.ic_menu,
        ),
        route = "menu",
        label = R.string.feature_dashboard_menu,
        contentDescription = R.string.feature_dashboard_menu,
    ),
    HOME(
        icon = DestinationIcon.Icon(
            defaultIcon = R.drawable.ic_menu_home,
            selectedIcon = R.drawable.ic_menu_home,
        ),
        route = "home",
        label = R.string.feature_dashboard_home,
        contentDescription = R.string.feature_dashboard_home,
    ),
    MESSAGE(
        icon = DestinationIcon.Icon(
            defaultIcon = R.drawable.ic_message,
            selectedIcon = R.drawable.ic_message,
        ),
        route = "message",
        label = R.string.feature_dashboard_message,
        contentDescription = R.string.feature_dashboard_message,
    ),
    Profile(
        icon = DestinationIcon.Url(
            url = "https://images.ctfassets.net/h6goo9gw1hh6/2sNZtFAWOdP1lmQ33VwRN3/24e953b920a9cd0ff2e1d587742a2472/1-intro-photo-final.jpg?w=1200&h=992&fl=progressive&q=70&fm=jpg"
        ),
        route = "profile",
        label = R.string.feature_dashboard_profile,
        contentDescription = R.string.feature_dashboard_profile,
    ),
}

sealed interface DestinationIcon {
    data class Icon(
        @param:DrawableRes val defaultIcon: Int,
        @param:DrawableRes val selectedIcon: Int,
    ) : DestinationIcon

    data class Url(val url: String, val placeholder: Int = R.drawable.ic_profile) : DestinationIcon
}
