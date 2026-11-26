package com.example

import com.example.domain.calendar.OrthodoxCalendarRepository
import com.example.domain.calendar.PaschaliaCalculator
import com.example.domain.model.CalendarDate
import com.example.domain.model.FastingPeriod
import com.example.domain.model.FastingType
import com.example.domain.model.FeastRank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OrthodoxCalendarTest {

    private val repo = OrthodoxCalendarRepository()

    @Test
    fun paschaDatesAreAccurate() {
        assertEquals(CalendarDate(2025, 4, 20), PaschaliaCalculator.calculateOrthodoxPascha(2025))
        assertEquals(CalendarDate(2026, 4, 12), PaschaliaCalculator.calculateOrthodoxPascha(2026))
        assertEquals(CalendarDate(2027, 5, 2), PaschaliaCalculator.calculateOrthodoxPascha(2027))
        assertEquals(CalendarDate(2028, 4, 16), PaschaliaCalculator.calculateOrthodoxPascha(2028))
    }

    @Test
    fun julianConversionAndFixedFeastsWork() {
        val nov26 = CalendarDate(2026, 11, 26)
        assertEquals(CalendarDate(2026, 11, 13), nov26.toJulianDate())
        val chrysostom = repo.getLiturgicalDay(nov26)
        assertEquals(FeastRank.GREAT, chrysostom.feastRank)
        assertTrue(chrysostom.feastTitle.contains("Іоанна Золотоустого"))

        val christmas = repo.getLiturgicalDay(CalendarDate(2026, 1, 7))
        assertEquals(FeastRank.TWELVE_GREAT, christmas.feastRank)
        assertEquals(FastingType.FAST_FREE, christmas.fastingType)
        assertTrue(christmas.feastTitle.contains("Різдво"))
    }

    @Test
    fun fastingPeriodsAreAccurate() {
        // Nativity fast begins Nov 28
        val nov28 = repo.getLiturgicalDay(CalendarDate(2026, 11, 28))
        assertEquals(FastingPeriod.NATIVITY_FAST, nov28.fastingPeriod)
        assertTrue(nov28.isFastDay)

        // Bright Week after 2026 Pascha (April 15 is Wednesday of Bright Week - fast free!)
        val brightWed = repo.getLiturgicalDay(CalendarDate(2026, 4, 15))
        assertEquals(FastingType.FAST_FREE, brightWed.fastingType)
        assertFalse(brightWed.isFastDay)

        // Great Friday 2026 (April 10)
        val goodFriday = repo.getLiturgicalDay(CalendarDate(2026, 4, 10))
        assertEquals(FastingPeriod.PASSION_WEEK, goodFriday.fastingPeriod)
        assertEquals(FastingType.STRICT_DRY, goodFriday.fastingType)
    }

    @Test
    fun searchFindsFeasts() {
        val results = repo.searchEvents("Миколая", 2026)
        assertTrue(results.any { it.date == CalendarDate(2026, 12, 19) })
        assertTrue(results.any { it.date == CalendarDate(2026, 5, 22) })
    }
}
