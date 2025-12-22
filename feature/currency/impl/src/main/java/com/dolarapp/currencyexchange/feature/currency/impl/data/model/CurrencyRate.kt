package com.dolarapp.currencyexchange.feature.currency.impl.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Currency rate data model
 */
@JsonClass(generateAdapter = true)
data class CurrencyRate(
    @Json(name = "code")
    val code: String,
    @Json(name = "rate")
    val rate: Double
)

/**
 * Currency exchange response model
 */
@JsonClass(generateAdapter = true)
data class ExchangeRatesResponse(
    @Json(name = "base")
    val base: String,
    @Json(name = "rates")
    val rates: Map<String, Double>
)

