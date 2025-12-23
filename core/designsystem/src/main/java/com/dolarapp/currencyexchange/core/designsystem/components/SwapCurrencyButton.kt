package com.dolarapp.currencyexchange.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dolarapp.currencyexchange.core.designsystem.R
import com.dolarapp.currencyexchange.core.designsystem.theme.ExchangeRateTextColor
import com.dolarapp.currencyexchange.core.designsystem.theme.ScreenBackground

/**
 * Swap currency button component
 * Circular button with swap icon
 */
@Composable
fun SwapCurrencyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp) // 24dp button + 6dp border on each side
            .clip(CircleShape)
            .background(ScreenBackground)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(ExchangeRateTextColor)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "Swap currencies",
                tint = Color.White,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

