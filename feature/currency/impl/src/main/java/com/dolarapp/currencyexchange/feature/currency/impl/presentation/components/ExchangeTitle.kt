package com.dolarapp.currencyexchange.feature.currency.impl.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dolarapp.currencyexchange.core.designsystem.theme.TitleTextStyle
import com.dolarapp.currencyexchange.core.designsystem.theme.TitleTextColor

/**
 * Exchange title component
 */
@Composable
fun ExchangeTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = TitleTextStyle,
        color = TitleTextColor,
        modifier = modifier
    )
}

