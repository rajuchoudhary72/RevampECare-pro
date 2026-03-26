package com.app.ecarepro.designsystem.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.ecarepro.core.domain.model.SiblingDetail
import kotlinx.serialization.InternalSerializationApi
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

@Composable
fun SiblingRowView(
    sibling: SiblingDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Photo
        if (sibling.photo.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.appColors.textSecondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile photo",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.appColors.textSecondary
                )
            }
        } else {
            EcareProAsyncImage(
                imageUrl = sibling.photo,
                contentDescription = "Profile photo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Info Grid
        val gridItems = listOf(
            InfoGridItem("Name", sibling.name ?: "NA"),
            InfoGridItem("Class", sibling.siblingClass ?: "NA"),
            InfoGridItem("Date of Birth", sibling.dob ?: "NA"),
            InfoGridItem("Roll No", sibling.rollNumber ?: "NA"),
            InfoGridItem("Adm No", sibling.admissionNumber ?: "NA")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            gridItems.chunked(2).forEach { rowItems ->
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { item ->
                        Column {
                            Text(
                                text = item.label,
                                style = MaterialTheme.appTypography.interRegular12px,
                                color = MaterialTheme.appColors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.value,
                                style = MaterialTheme.appTypography.interMedium14px,
                                color = MaterialTheme.appColors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// Preview Data
private val sampleSiblingWithPhoto = SiblingDetail(
    stID = 1001,
    name = "Jane Doe",
    gender = "Female",
    siblingClass = "Class 8-B",
    rollNumber = "12",
    admissionNumber = "ADM12346",
    dob = "20-Mar-2012",
    fatherName = "Mr. John Doe Sr.",
    contactPerson = "Mr. John Doe Sr.",
    contactMob = "+91 9876543210",
    photo = "https://example.com/photo.jpg", // URL to show photo scenario
    houseID = 1,
    houseName = "Red House",
    clubID = 1,
    clubName = "Art Club",
    fatherPhoto = null,
    motherPhoto = null,
    escortPhoto = null,
    isSelected = false
)

private val sampleSiblingWithoutPhoto = SiblingDetail(
    stID = 1002,
    name = "Jack Doe",
    gender = "Male",
    siblingClass = "Class 6-A",
    rollNumber = "8",
    admissionNumber = "ADM12347",
    dob = "10-Jul-2014",
    fatherName = "Mr. John Doe Sr.",
    contactPerson = "Mr. John Doe Sr.",
    contactMob = "+91 9876543210",
    photo = null, // No photo
    houseID = 1,
    houseName = "Red House",
    clubID = 2,
    clubName = "Sports Club",
    fatherPhoto = null,
    motherPhoto = null,
    escortPhoto = null,
    isSelected = false
)

// Preview Composables
@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Sibling Row - Without Photo")
@Composable
private fun PreviewSiblingRowViewWithoutPhoto() {
    SiblingRowView(sibling = sampleSiblingWithoutPhoto)
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Sibling Row - With Photo URL")
@Composable
private fun PreviewSiblingRowViewWithPhoto() {
    SiblingRowView(sibling = sampleSiblingWithPhoto)
}

@OptIn(InternalSerializationApi::class)
@Preview(showBackground = true, name = "Multiple Siblings")
@Composable
private fun PreviewMultipleSiblings() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SiblingRowView(sibling = sampleSiblingWithoutPhoto)
        SiblingRowView(sibling = sampleSiblingWithPhoto)
    }
}
