package com.dolarapp.currencyexchange.core.network.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal

/**
 * Moshi adapter for BigDecimal
 * Preserves precision by parsing from String
 */
class BigDecimalAdapter {
    @ToJson
    fun toJson(value: BigDecimal): String {
        return value.toPlainString()
    }

    @FromJson
    fun fromJson(value: String): BigDecimal {
        return try {
            BigDecimal(value)
        } catch (e: NumberFormatException) {
            BigDecimal.ZERO
        }
    }
}

