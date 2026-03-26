package com.app.ecarepro.feature.leave.applyleave.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.ecarepro.designsystem.core.theme.appColors
import com.app.ecarepro.designsystem.core.theme.appTypography
import com.app.ecarepro.feature.leave.R
import com.app.ecarepro.feature.leave.applyleave.data.LeaveBalancePresentation

@Composable
fun LeaveBalanceView(
    balance: LeaveBalancePresentation,
    error: String?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.feature_leave_balance_title),
            style = MaterialTheme.appTypography.interMedium16px,
            color = MaterialTheme.appColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.appColors.primary.copy(alpha = 0.05f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.feature_leave_balance_available, balance.available.toInt().toString()),
                        style = MaterialTheme.appTypography.interSemiBold14px,
                        color = MaterialTheme.appColors.textPrimary
                    )
                    Text(
                        text = stringResource(R.string.feature_leave_balance_total, balance.total.toInt().toString()),
                        style = MaterialTheme.appTypography.interRegular14px,
                        color = MaterialTheme.appColors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { balance.progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.appColors.primary,
                    trackColor = MaterialTheme.appColors.divider
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.feature_leave_balance_used, balance.taken.toInt().toString()),
                    style = MaterialTheme.appTypography.interRegular12px,
                    color = MaterialTheme.appColors.textSecondary
                )
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.appTypography.interRegular12px,
                color = MaterialTheme.appColors.error
            )
        }
    }
}
