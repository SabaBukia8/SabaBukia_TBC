package com.example.sababukia_tbc.presentation.mapper

import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormatterTest {

    @Test
    fun `formatEpochToDate formats timestamp correctly`() {
        // January 1, 2020 00:00:00 UTC
        val timestamp = 1577836800L

        val result = DateFormatter.formatEpochToDate(timestamp)

        assertEquals("Jan 01, 2020", result)
    }

    @Test
    fun `formatEpochToDate formats different date correctly`() {
        // December 25, 2023 12:00:00 UTC
        val timestamp = 1703505600L

        val result = DateFormatter.formatEpochToDate(timestamp)

        assertEquals("Dec 25, 2023", result)
    }

    @Test
    fun `formatEpochToDate handles epoch zero`() {
        val timestamp = 0L

        val result = DateFormatter.formatEpochToDate(timestamp)

        assertEquals("Jan 01, 1970", result)
    }

    @Test
    fun `formatEpochToDate formats February date correctly`() {
        // February 14, 2024 00:00:00 UTC
        val timestamp = 1707868800L

        val result = DateFormatter.formatEpochToDate(timestamp)

        assertEquals("Feb 14, 2024", result)
    }
}
