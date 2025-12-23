package com.dolarapp.currencyexchange.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dolarapp.currencyexchange.core.designsystem.components.getCurrencyFlag
import com.dolarapp.currencyexchange.core.designsystem.theme.CurrencyAmountTextStyle
import com.dolarapp.currencyexchange.core.designsystem.theme.CurrencyCardBackground
import com.dolarapp.currencyexchange.core.designsystem.theme.CurrencyNameTextStyle
import com.dolarapp.currencyexchange.core.designsystem.theme.Spacing
import com.dolarapp.currencyexchange.core.designsystem.theme.TitleTextColor
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Currency card component for displaying currency selection and amount
 * Matches the design from Figma
 */
@Composable
fun CurrencyCard(
    currencyCode: String,
    amount: String,
    isEditable: Boolean,
    onCurrencyClick: () -> Unit,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isCurrencySelectable: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CurrencyCardBackground)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag icon and currency code area - fully clickable when selectable
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = if (isCurrencySelectable) {
                    Modifier
                        .clickable { onCurrencyClick() }
                        .padding(vertical = Spacing.sm, horizontal = Spacing.xs)
                } else {
                    Modifier
                }
            ) {
                // Flag icon - using same font size as currency code
                Text(
                    text = getCurrencyFlag(currencyCode),
                    style = CurrencyNameTextStyle.copy(
                        color = TitleTextColor
                    )
                )
                
                Spacer(modifier = Modifier.width(Spacing.sm))
                
                // Currency code with dropdown indicator
                Text(
                    text = currencyCode,
                    style = CurrencyNameTextStyle.copy(
                        color = TitleTextColor
                    )
                )
                if (isCurrencySelectable) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            // Amount display/input
            if (isEditable) {
                // Format amount with commas and add dollar sign
                val formattedAmount = if (amount.isEmpty()) "" else "$${formatNumberWithCommas(amount)}"
                var textFieldValue by remember { mutableStateOf(TextFieldValue(formattedAmount)) }
                
                // Update text field value when amount changes from parent (e.g., when swapped or calculated)
                LaunchedEffect(amount) {
                    val newFormatted = if (amount.isEmpty()) "" else "$${formatNumberWithCommas(amount)}"
                    textFieldValue = TextFieldValue(
                        text = newFormatted,
                        selection = TextRange(newFormatted.length)
                    )
                }
                
                TextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue // Update immediately for responsive typing
                        
                        // Strip any "$" signs and commas that might be entered
                        var cleanedValue = newValue.text.replace("$", "").replace(",", "").trim()
                        
                        // If user deletes everything, show placeholder "$0"
                        if (cleanedValue.isEmpty() || cleanedValue == "0") {
                            onAmountChange("")
                            // Update text field to show empty (which will show placeholder)
                            textFieldValue = TextFieldValue(
                                text = "",
                                selection = TextRange(0)
                            )
                        } else {
                            // Validate it's a valid number before passing to callback
                            try {
                                BigDecimal(cleanedValue)
                                onAmountChange(cleanedValue)
                                // Format with $ and commas, then move cursor to end
                                val formatted = "$${formatNumberWithCommas(cleanedValue)}"
                                val cursorPosition = formatted.length
                                textFieldValue = TextFieldValue(
                                    text = formatted,
                                    selection = TextRange(cursorPosition)
                                )
                            } catch (e: Exception) {
                                // Invalid number, revert to previous valid value
                                val previousFormatted = if (amount.isEmpty()) "" else "$${formatNumberWithCommas(amount)}"
                                textFieldValue = TextFieldValue(
                                    text = previousFormatted,
                                    selection = TextRange(previousFormatted.length)
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            text = "$0",
                            style = CurrencyAmountTextStyle.copy(
                                color = TitleTextColor.copy(alpha = 0.6f),
                                textAlign = TextAlign.End
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    textStyle = CurrencyAmountTextStyle.copy(
                        color = TitleTextColor,
                        textAlign = TextAlign.End
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = TitleTextColor,
                        unfocusedTextColor = TitleTextColor,
                        focusedPlaceholderColor = TitleTextColor.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = TitleTextColor.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Spacing.xs),
                    singleLine = true
                )
            } else {
                // Format amount with commas for display
                val formattedAmount = formatNumberWithCommas(amount)
                Text(
                    text = formattedAmount,
                    style = CurrencyAmountTextStyle.copy(
                        color = TitleTextColor,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Formats a number string with commas every 3 digits before the decimal point
 * Removes trailing zeros after the decimal point
 * Example: "1234.56" -> "1,234.56", "1234.00" -> "1,234", "1234567" -> "1,234,567"
 */
private fun formatNumberWithCommas(numberString: String): String {
    if (numberString.isEmpty()) return ""
    
    return try {
        val decimal = BigDecimal(numberString)
        // Strip trailing zeros
        val stripped = decimal.stripTrailingZeros()
        val plainString = stripped.toPlainString()
        
        if (plainString.contains(".")) {
            val parts = plainString.split(".")
            val integerPart = parts[0]
            val decimalPart = parts[1]
            // Remove trailing zeros from decimal part
            val trimmedDecimal = decimalPart.trimEnd('0')
            if (trimmedDecimal.isEmpty()) {
                addCommasToInteger(integerPart)
            } else {
                "${addCommasToInteger(integerPart)}.$trimmedDecimal"
            }
        } else {
            addCommasToInteger(plainString)
        }
    } catch (e: Exception) {
        // If parsing fails, try to add commas manually for simple integer cases
        if (numberString.contains(".")) {
            val parts = numberString.split(".")
            val integerPart = parts[0]
            val decimalPart = parts[1].trimEnd('0')
            if (decimalPart.isEmpty()) {
                addCommasToInteger(integerPart)
            } else {
                "${addCommasToInteger(integerPart)}.$decimalPart"
            }
        } else {
            addCommasToInteger(numberString)
        }
    }
}

/**
 * Adds commas to an integer string every 3 digits from right to left
 */
private fun addCommasToInteger(integerString: String): String {
    if (integerString.isEmpty()) return ""
    
    val isNegative = integerString.startsWith("-")
    val digits = if (isNegative) integerString.substring(1) else integerString
    
    val result = StringBuilder()
    var count = 0
    
    for (i in digits.length - 1 downTo 0) {
        if (count > 0 && count % 3 == 0) {
            result.insert(0, ",")
        }
        result.insert(0, digits[i])
        count++
    }
    
    return if (isNegative) "-$result" else result.toString()
}

