package com.app.ecarepro.feature.schoolcode.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.ecarepro.core.domain.model.School
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.AppAsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.White
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.schoolcode.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolSearchScreen(
    viewModel: SchoolSearchViewModel = hiltViewModel(),
    onSchoolCodeSelect: (String) -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvent.collectLatest { event ->
            when (event) {
                is SearchSchoolCodeEvent.NavigateToBack -> {
                    onSchoolCodeSelect(event.schoolCode)
                }
            }
        }
    }

    UiStateHandler(
        state = uiState,
        onRetry = { viewModel.handleIntent(SearchSchoolCodeIntent.RefetchSchools) }
    ) { schools ->

        Scaffold(
            topBar = {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.handleIntent(
                            SearchSchoolCodeIntent.SearchQueryChanged(
                                it
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .background(White)
                        .padding(16.dp),
                    placeholder = {
                        Text(
                            text = "Search your school",
                            style = MaterialTheme.appTypography.interRegular12px,
                            color = MaterialTheme.appColors.textSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = "Search Icon"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.handleIntent(SearchSchoolCodeIntent.ClearQueryChanged) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = "Clear Search"
                                )
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    textStyle = MaterialTheme.appTypography.interMedium16px
                )
            }, containerColor = MaterialTheme.appColors.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(schools, key = {it.schoolCode}) { school ->
                    SchoolListItem(school = school)
                }


            }
        }
    }


}

@Composable
fun SchoolListItem(school: School) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {

        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .fillMaxWidth(), verticalAlignment = Alignment.Top
            ) {

                AppAsyncImage(
                    imageUrl = school.logo,
                    contentDescription = "School Logo",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.appColors.background)
                        .padding(6.dp),
                    contentScale = ContentScale.Inside
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = school.name.orEmpty(),
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
                    )
                    Text(
                        text = school.address.orEmpty(), style = MaterialTheme.appTypography.interRegular12px
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 8.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .size(width = 64.dp, height = 25.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.appColors.background)
                        .padding(6.dp),
                    text = school.schoolCode,
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Tap to copy",
                    color = MaterialTheme.appColors.textPrimary,
                    style = MaterialTheme.appTypography.interRegular12px,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SchoolSearchScreenPreview() {
    EcareProTheme {
        SchoolSearchScreen {

        }
    }
}