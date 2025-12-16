package com.app.ecarepro.feature.questionner.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.feature.questionner.QuestionnaireTab

@Composable
fun QuestionnaireHeader(
    selectedTab: QuestionnaireTab,
    onTabSelected: (QuestionnaireTab) -> Unit,
    onBackClick: () -> Unit,
    onCreateNewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.appColors.surface)
    ) {
        // Top bar with back button, title, and add button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.appColors.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Questionnaire",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.appColors.textPrimary
            )
        }

        // Tab row
        TabRow(
            selectedTabIndex = if (selectedTab == QuestionnaireTab.ALL) 0 else 1,
            containerColor = MaterialTheme.appColors.surface,
            contentColor = MaterialTheme.appColors.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[if (selectedTab == QuestionnaireTab.ALL) 0 else 1])
                        .padding(horizontal = 32.dp),
                    color = MaterialTheme.appColors.primary,
                    height = 3.dp
                )
            },
            divider = {
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.appColors.divider)
                )
            }
        ) {
            Tab(
                selected = selectedTab == QuestionnaireTab.ALL,
                onClick = { onTabSelected(QuestionnaireTab.ALL) },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Forum,
                        contentDescription = "All",
                        tint = if (selectedTab == QuestionnaireTab.ALL) MaterialTheme.appColors.primary else MaterialTheme.appColors.textSecondary
                    )
                },
                text = {
                    Text(
                        text = "All",
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == QuestionnaireTab.ALL) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == QuestionnaireTab.ALL)
                            MaterialTheme.appColors.primary
                        else
                            MaterialTheme.appColors.textSecondary
                    )
                }
            )
            Tab(
                selected = selectedTab == QuestionnaireTab.CREATED_BY_ME,
                onClick = { onTabSelected(QuestionnaireTab.CREATED_BY_ME) },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Created by me",
                        tint = if (selectedTab == QuestionnaireTab.CREATED_BY_ME) MaterialTheme.appColors.primary else MaterialTheme.appColors.textSecondary
                    )
                },
                text = {
                    Text(
                        text = "Created by me",
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == QuestionnaireTab.CREATED_BY_ME) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == QuestionnaireTab.CREATED_BY_ME)
                            MaterialTheme.appColors.primary
                        else
                            MaterialTheme.appColors.textSecondary
                    )
                }
            )
            Tab(
                selected = false,
                onClick = onCreateNewClick,
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create new",
                            tint = MaterialTheme.appColors.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create New",
                            fontSize = 12.sp,
                            color = MaterialTheme.appColors.textSecondary
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionnaireHeaderPreview() {
    EcareProTheme {
        QuestionnaireHeader(
            selectedTab = QuestionnaireTab.ALL,
            onTabSelected = {},
            onBackClick = {},
            onCreateNewClick = {}
        )
    }
}
