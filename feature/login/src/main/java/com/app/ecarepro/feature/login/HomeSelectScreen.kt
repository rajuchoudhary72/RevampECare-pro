package com.app.ecarepro.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.component.ButtonComponent
import com.app.ecarepro.designsystem.core.component.TextComponent
import com.app.ecarepro.designsystem.core.component.TextComponentStyle
import com.app.ecarepro.designsystem.core.component.TextComponentView
import com.app.ecarepro.designsystem.core.theme.EcareProTypography


data class StartOption(
    val title: String,
    val description: String,
    val icon: Int,
    val backgroundColor: Color
)

@Composable
fun HomeSelectScreen() {
    var selectedOption by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()


    val options = listOf(
        StartOption(
            title = "Dashboard view",
            description = "Get a quick overview of everything important — best for tracking and managing at a glance.",
            icon = R.drawable.dashboard_holder_icon,
            backgroundColor = Color(0xFFE8F5E9)
        ),
        StartOption(
            title = "Timeline/Feed",
            description = "See real-time updates and announcements — best for staying up to date with what's happening.",
            icon = R.drawable.time_line_logo,
            backgroundColor = Color(0xFFE1F5FE)
        ),
        StartOption(
            title = "Bookmarks",
            description = "Access your pinned modules instantly — best for quick, everyday tasks you use the most.",
            icon = R.drawable.bookmark,
            backgroundColor = Color(0xFFFCE4EC)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    )
    {
        Box {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFFAFAFA)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.img_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop, // fill the entire background
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Logo
                    Box(
                        modifier = Modifier
                            .size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.school_logo),
                            contentDescription = null,
                            contentScale = ContentScale.Fit, // fill the entire background
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))


                    TextComponentView(
                        component = TextComponent(
                            text = "How Do You Want to Start?",
                        )
                    )



                    Spacer(modifier = Modifier.height(12.dp))

                    TextComponentView(
                        component = TextComponent(
                            text = "Begin with Dashboard, Feeds, or Favorites — whichever suits you best. You can update this later in Settings.",
                            style = TextComponentStyle(
                                textStyle = EcareProTypography.nunitoBold12px,
                                foregroundColor = Color(0xFF757575),
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Options List
                    options.forEachIndexed { index, option ->
                        SelectableCard(
                            option = option,
                            isSelected = selectedOption == index,
                            onClick = { selectedOption = index }
                        )

                        if (index < options.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    // Footer text
                    Text(
                        text = "You can switch anytime later from settings",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ButtonComponent(text = "Set as Home") {}

                }
            }
        }
    }
}

@Composable
fun SelectableCard(
    option: StartOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF66BB6A) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .size(109.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center

            ) {
                Image(
                    painter = painterResource(option.icon),
                    contentDescription = null,
                    contentScale = ContentScale.Fit, // fill the entire background
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {

                TextComponentView(
                    component = TextComponent(
                        text = option.title,
                        style = TextComponentStyle(
                            textStyle = EcareProTypography.interSemiBold14px
                        )
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))


                TextComponentView(
                    component = TextComponent(
                        text = option.description,
                        style = TextComponentStyle(
                            textStyle = EcareProTypography.interRegular12px,
                            foregroundColor = Color(0xFF616161),
                        )
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeSelectPreview() {
    HomeSelectScreen()
}













