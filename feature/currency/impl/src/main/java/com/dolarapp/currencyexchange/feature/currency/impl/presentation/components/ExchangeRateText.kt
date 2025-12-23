package com.dolarapp.currencyexchange.feature.currency.impl.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dolarapp.currencyexchange.core.designsystem.theme.ExchangeRateTextStyle
import com.dolarapp.currencyexchange.core.designsystem.theme.ExchangeRateTextColor

/**
 * Exchange rate text component
 */
@Composable
fun ExchangeRateText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = ExchangeRateTextStyle,
        color = ExchangeRateTextColor,
        modifier = modifier
    )
}

