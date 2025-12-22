package com.dolarapp.currencyexchange.feature.currency.api.domain

import java.math.BigDecimal
import java.time.Instant

/**
 * Domain model for currency ticker
 * Represents exchange rate information for a currency pair
 */
data class CurrencyTicker(
    val currencyCode: String,
    val bid: BigDecimal,
    val ask: BigDecimal,
    val lastUpdated: Instant
)

