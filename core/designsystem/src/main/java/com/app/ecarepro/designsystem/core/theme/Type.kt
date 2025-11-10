package com.app.ecarepro.designsystem.core.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.ecarepro.core.designsystem.R
import com.app.ecarepro.designsystem.core.model.AppTypography

val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, weight = FontWeight.Normal),
    Font(R.font.inter_medium, weight = FontWeight.Medium),
    Font(R.font.inter_bold, weight = FontWeight.Bold),
    Font(R.font.inter_semi_bold, weight = FontWeight.SemiBold),
    Font(R.font.inter_light, weight = FontWeight.Light),
    Font(R.font.inter_extra_light, weight = FontWeight.ExtraLight),
    Font(R.font.inter_extra_bold, weight = FontWeight.ExtraBold),
    Font(R.font.inter_black, weight = FontWeight.Black),
)
val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_regular, weight = FontWeight.Normal),
    Font(R.font.nunito_medium, weight = FontWeight.Medium),
    Font(R.font.nunito_bold, weight = FontWeight.Bold),
    Font(R.font.nunito_semi_bold, weight = FontWeight.SemiBold),
    Font(R.font.nunito_light, weight = FontWeight.Light),
    Font(R.font.nunito_extra_light, weight = FontWeight.ExtraLight),
    Font(R.font.nunito_extra_bold, weight = FontWeight.ExtraBold),
    Font(R.font.nunito_black, weight = FontWeight.Black),
)

val EcareProTypography = AppTypography(
    interRegular12px = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    interRegular14px = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    interRegular16px = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    interMedium16px = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    interSemiBold14px = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    nunitoMedium12px = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    nunitoBold12px = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    nunitoBlack34px = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp
    ),
    nunitoBlack28px = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp
    )
)