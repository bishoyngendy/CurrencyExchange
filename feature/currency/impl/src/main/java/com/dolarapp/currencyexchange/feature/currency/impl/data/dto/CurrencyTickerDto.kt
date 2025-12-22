package com.dolarapp.currencyexchange.feature.currency.impl.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO for currency ticker response from API
 */
@JsonClass(generateAdapter = true)
data class CurrencyTickerDto(
    @Json(name = "ask")
    val ask: String,
    
    @Json(name = "bid")
    val bid: String,
    
    @Json(name = "book")
    val book: String,
    
    @Json(name = "date")
    val date: String
)

