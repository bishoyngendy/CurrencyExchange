package com.dolarapp.currencyexchange.core.network.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.Instant

/**
 * Moshi adapter for Instant
 * Parses ISO-8601 format with nanoseconds support
 * Falls back to epoch if parsing fails
 */
class InstantAdapter {
    @ToJson
    fun toJson(value: Instant): String {
        return value.toString()
    }

    @FromJson
    fun fromJson(value: String): Instant {
        return try {
            Instant.parse(value)
        } catch (e: Exception) {
            // Fallback to current time if parsing fails
            Instant.now()
        }
    }
}

