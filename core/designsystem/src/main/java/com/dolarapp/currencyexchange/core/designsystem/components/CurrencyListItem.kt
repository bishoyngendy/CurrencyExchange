package com.dolarapp.currencyexchange.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dolarapp.currencyexchange.core.designsystem.components.getCurrencyFlag
import com.dolarapp.currencyexchange.core.designsystem.theme.ExchangeRateGreen
import com.dolarapp.currencyexchange.core.designsystem.theme.Spacing

/**
 * Currency list item for bottom sheet with flag, currency code, and radio button
 * Matches the design from Figma
 */
@Composable
fun CurrencyListItem(
    currencyCode: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable { onClick() }
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flag icon - using same font size as currency code for proper alignment
        Text(
            text = getCurrencyFlag(currencyCode),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.size(Spacing.md))
        
        // Currency code
        Text(
            text = currencyCode,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        // Radio button (green circle with checkmark when selected) - add padding to prevent cropping
        Box(
            modifier = Modifier
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                .size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) ExchangeRateGreen else Color.Transparent
                    )
                    .then(
                        if (!isSelected) {
                            Modifier.border(1.dp, Color.Gray, CircleShape)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

