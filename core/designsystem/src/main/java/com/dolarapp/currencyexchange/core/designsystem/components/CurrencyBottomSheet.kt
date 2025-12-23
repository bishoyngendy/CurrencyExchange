package com.dolarapp.currencyexchange.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dolarapp.currencyexchange.core.designsystem.theme.ScreenBackground
import com.dolarapp.currencyexchange.core.designsystem.theme.Spacing

/**
 * Currency Bottom Sheet component following Material 3 design
 * Height: 428dp as per Figma design
 * Background: #F8F8F8
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    title: String = "Choose currency",
    modifier: Modifier = Modifier,
    sheetState: SheetState? = null,
    content: @Composable () -> Unit
) {
    val defaultSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )
    val finalSheetState = sheetState ?: defaultSheetState

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = finalSheetState,
            containerColor = ScreenBackground,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(428.dp)
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "✕",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(Spacing.xs)
                    )
                }
                
                // Content
                content()
            }
        }
    }
}

