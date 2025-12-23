package com.dolarapp.currencyexchange.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

/**
 * Maps currency codes to flag emojis
 * Returns a flag emoji based on currency code
 */
fun getCurrencyFlag(currencyCode: String): String {
    val upperCode = currencyCode.uppercase()
    return when (upperCode) {
        "USD", "USDC" -> "🇺🇸"
        "MXN" -> "🇲🇽"
        "ARS" -> "🇦🇷"
        "BRL" -> "🇧🇷"
        "COP" -> "🇨🇴"
        "EUR", "EURC" -> "🇪🇺"
        "GBP" -> "🇬🇧"
        "JPY" -> "🇯🇵"
        "CAD" -> "🇨🇦"
        "AUD" -> "🇦🇺"
        "CHF" -> "🇨🇭"
        "CNY" -> "🇨🇳"
        "INR" -> "🇮🇳"
        "NZD" -> "🇳🇿"
        "SGD" -> "🇸🇬"
        "HKD" -> "🇭🇰"
        "KRW" -> "🇰🇷"
        "TRY" -> "🇹🇷"
        "RUB" -> "🇷🇺"
        "ZAR" -> "🇿🇦"
        else -> "🌍" // Default flag
    }
}

/**
 * Flag icon component
 */
@Composable
fun FlagIcon(
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = getCurrencyFlag(currencyCode),
        fontSize = 16.sp,
        modifier = modifier
    )
}
