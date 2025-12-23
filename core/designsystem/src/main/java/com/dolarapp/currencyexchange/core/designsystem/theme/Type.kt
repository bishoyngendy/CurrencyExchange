package com.dolarapp.currencyexchange.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material 3 typography system
 * Uses system fonts for optimal performance
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Custom typography styles for Currency Exchange app
 * Note: Messina Sans Narrow font family should be added as a custom font resource
 * For now using FontFamily.Default (system font)
 */

/**
 * Title style for Exchange calculator
 * Font: Messina Sans Narrow (using Default for now)
 * Weight: 700 (Bold)
 * Size: 30px
 * Line height: 33px
 * Letter spacing: -2%
 */
val TitleTextStyle = TextStyle(
    fontFamily = FontFamily.Default, // TODO: Replace with Messina Sans Narrow when font is added
    fontWeight = FontWeight.Bold, // 700
    fontSize = 30.sp,
    lineHeight = 33.sp,
    letterSpacing = (-0.6).sp // -2% of 30px = -0.6sp
)

/**
 * Exchange rate subtitle style
 * Weight: 600 (SemiBold)
 * Size: 16px
 * Line height: 20px
 * Letter spacing: 2%
 */
val ExchangeRateTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.32.sp // 2% of 16px = 0.32sp
)

/**
 * Currency amount text style for input fields and display
 * Weight: 700 (Bold)
 * Size: 16px
 * Line height: 20px
 * Letter spacing: 2%
 * Alignment: Right, Middle
 */
val CurrencyAmountTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold, // 700
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.32.sp // 2% of 16px = 0.32sp
)

/**
 * Currency name text style for currency code display
 * Weight: 600 (SemiBold)
 * Size: 16px
 * Line height: 20px
 * Letter spacing: 2%
 * Vertical alignment: Middle
 */
val CurrencyNameTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.32.sp // 2% of 16px = 0.32sp
)

