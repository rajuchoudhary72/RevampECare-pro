package com.app.ecarepro.designsystem.core.component.personlist

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.ecarepro.designsystem.core.theme.EcareProTheme
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography

/**
 * PersonRowView - List item for person list
 *
 * Design specs:
 * - 48x48dp circular profile image
 * - Name: SemiBold 16sp
 * - Subtitle & Detail: Regular 14sp, gray
 * - Horizontal padding: 16dp, Vertical padding: 12dp
 * - Avatar-text spacing: 16dp
 */
@Composable
fun PersonRowView(
    person: PersonPresentation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        if (person.profileImageURL != null) {
            AsyncImage(
                model = person.profileImageURL,
                contentDescription = person.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.appColors.border, CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.appColors.border, CircleShape)
                    .padding(12.dp),
                tint = MaterialTheme.appColors.textSecondary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Text(
                text = person.name,
                style = MaterialTheme.appTypography.interSemiBold14px,
                color = MaterialTheme.appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = person.subtitle,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Detail
            Text(
                text = person.detail,
                style = MaterialTheme.appTypography.interRegular14px,
                color = MaterialTheme.appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonRowViewPreview() {
    EcareProTheme {
        PersonRowView(
            person = PersonPresentation(
                id = "1",
                name = "Aastha Saini",
                subtitle = "Class: 11 A | Roll: 1",
                detail = "Adm No: AB123",
                profileImageURL = null,
                gender = Gender.FEMALE,
                personType = PersonType.STUDENT
            ),
            onClick = {}
        )
    }
}
