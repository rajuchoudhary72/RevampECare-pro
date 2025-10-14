package com.app.ecarepro.feature.schoolcode.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.app.ecarepro.core.ui.UiState
import com.app.ecarepro.core.ui.UiStateHandler
import com.app.ecarepro.designsystem.core.component.AppAsyncImage
import com.app.ecarepro.designsystem.core.component.shimmer
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

    Scaffold(
        topBar = {
            SearchBar(searchQuery, viewModel)
        }, containerColor = MaterialTheme.appColors.background
    ) { paddingValues ->
        SearchSchoolCodeScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            uiState = uiState,
            handleIntent = { intent ->
                viewModel.handleIntent(intent)
            }
        )
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    viewModel: SchoolSearchViewModel,
) {
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
            .background(White)
            .systemBarsPadding()
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
}

@Composable
private fun SearchSchoolCodeScreenContent(
    modifier: Modifier = Modifier,
    uiState: UiState<List<School>>,
    handleIntent: (SearchSchoolCodeIntent) -> Unit,
) {
    UiStateHandler(
        state = uiState,
        loadingContent = { SearchSchoolShimmer(modifier) },
        onRetry = { handleIntent(SearchSchoolCodeIntent.RefetchSchools) }
    ) { schools ->
        LazyColumn(
            modifier = modifier
                .padding(vertical = 10.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(schools, key = { it.schoolCode }) { school ->
                SchoolListItem(
                    school = school,
                    onCopySchoolCode = { schoolCode ->
                        handleIntent(
                            SearchSchoolCodeIntent.CopySchoolCode(schoolCode)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SchoolListItem(
    modifier: Modifier = Modifier,
    school: School,
    onCopySchoolCode: (String) -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                        text = school.address.orEmpty(),
                        style = MaterialTheme.appTypography.interRegular12px
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 8.dp)
                    .clickable {
                        onCopySchoolCode(school.schoolCode)
                    },

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

@Composable
fun SearchSchoolShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(7) {
            SchoolListShimmerItem()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchSchoolShimmerPreview() {
    EcareProTheme() {
        SearchSchoolShimmer()
    }
}

@Composable
fun SchoolListShimmerItem() {
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
                    imageUrl = "",
                    contentDescription = "School Logo",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.appColors.background)
                        .shimmer(cornerRadius = 12.dp)
                        .padding(6.dp),
                    contentScale = ContentScale.Inside
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shimmer(),
                        text = "",
                        style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 16.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .shimmer(),
                        text = "",
                        style = MaterialTheme.appTypography.interRegular12px
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
                        .shimmer(cornerRadius = 6.dp)
                        .background(MaterialTheme.appColors.background)
                        .padding(6.dp),
                    text = "",
                    style = MaterialTheme.appTypography.interSemiBold14px.copy(fontSize = 12.sp),
                    color = MaterialTheme.appColors.textPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    modifier = Modifier.shimmer(),
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
fun SearchSchoolCodeScreenContentPreview() {
    EcareProTheme {
        SearchSchoolCodeScreenContent(
            uiState = UiState.Success(
                listOf(
                    School(
                        address = "Vill & Post - Bhikkawala P.O Afzalgarh, Tehsil - Dharampur",
                        city = "",
                        logo = "",
                        name = "St. Mary’s School, Bhikkawala",
                        schoolCode = "12A345",
                        state = ""
                    ),
                    School(
                        address = "Vill & Post - Bhikkawala P.O Afzalgarh, Tehsil - Dharampur",
                        city = "",
                        logo = "",
                        name = "St. Mary’s School, Bhikkawala",
                        schoolCode = "12Aa45",
                        state = ""
                    ),
                    School(
                        address = "Vill & Post - Bhikkawala P.O Afzalgarh, Tehsil - Dharampur",
                        city = "",
                        logo = "",
                        name = "St. Mary’s School, Bhikkawala",
                        schoolCode = "12Ae45",
                        state = ""
                    ),
                )
            ),
            handleIntent = {}
        )
    }
}