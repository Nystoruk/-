package com.example.domain.calendar

import com.example.domain.model.CalendarDate

data class MovableCalendarInfo(
    val pascha: CalendarDate,
    val publicanPhariseeSunday: CalendarDate,
    val prodigalSonSunday: CalendarDate,
    val meatfareSaturday: CalendarDate,
    val meatfareSunday: CalendarDate,
    val cheesefareSunday: CalendarDate,
    val greatLentStart: CalendarDate,
    val triumphOfOrthodoxy: CalendarDate,
    val lentenSaturday2: CalendarDate,
    val lentenSaturday3: CalendarDate,
    val lentenSaturday4: CalendarDate,
    val crossVenerationSunday: CalendarDate,
    val laudationSaturday: CalendarDate,
    val lazarusSaturday: CalendarDate,
    val palmSunday: CalendarDate,
    val passionWeekStart: CalendarDate,
    val brightWeekEnd: CalendarDate,
    val radonitsa: CalendarDate,
    val midPentecost: CalendarDate,
    val ascension: CalendarDate,
    val trinitySaturday: CalendarDate,
    val pentecost: CalendarDate,
    val holySpiritDay: CalendarDate,
    val trinityWeekEnd: CalendarDate,
    val allSaintsSunday: CalendarDate,
    val apostlesFastStart: CalendarDate,
    val apostlesFastEnd: CalendarDate
)

object PaschaliaCalculator {

    /**
     * Exact Meeus Julian Easter algorithm, converted to Gregorian CalendarDate.
     * Valid for modern liturgical years (1900–2099 offset = 13 days, generalized via Julian->Gregorian epoch).
     */
    fun calculateOrthodoxPascha(year: Int): CalendarDate {
        val a = year % 4
        val b = year % 7
        val c = year % 19
        val d = (19 * c + 15) % 30
        val e = (2 * a + 4 * b - d + 34) % 7
        val julianMonth = (d + e + 114) / 31 // 3 = March, 4 = April
        val julianDay = ((d + e + 114) % 31) + 1

        // Convert Julian date (year, julianMonth, julianDay) to Gregorian CalendarDate
        val gregorianOffset = (year / 100) - (year / 400) - 2
        // Note: For 1900-2099, 20 - 5 - 2 = 13 days.
        // Build approximate Gregorian date by interpreting Julian day in Gregorian and adding offset:
        // Wait: to be exact around leap centuries, convert using Julian epoch formula:
        val julianEpochDay = julianToEpochDay(year, julianMonth, julianDay)
        return CalendarDate.fromEpochDay(julianEpochDay)
    }

    private fun julianToEpochDay(year: Int, month: Int, day: Int): Long {
        val y = year.toLong() - if (month <= 2) 1L else 0L
        val era = (if (y >= 0) y else y - 3) / 4
        val yoe = y - era * 4
        val m = month.toLong()
        val doy = (153 * (m + if (m > 2) -3 else 9) + 2) / 5 + day - 1
        val doe = yoe * 365 + doy
        return era * 1461 + doe - 719470
    }

    fun calculateMovableYear(year: Int): MovableCalendarInfo {
        val pascha = calculateOrthodoxPascha(year)
        val apostlesFastStart = pascha.plusDays(57) // Monday after All Saints Sunday
        val apostlesFastEnd = CalendarDate(year, 7, 11) // Eve of Sts. Peter and Paul (12 July)

        return MovableCalendarInfo(
            pascha = pascha,
            publicanPhariseeSunday = pascha.minusDays(70),
            prodigalSonSunday = pascha.minusDays(63),
            meatfareSaturday = pascha.minusDays(57),
            meatfareSunday = pascha.minusDays(56),
            cheesefareSunday = pascha.minusDays(49),
            greatLentStart = pascha.minusDays(48),
            triumphOfOrthodoxy = pascha.minusDays(42),
            lentenSaturday2 = pascha.minusDays(36),
            lentenSaturday3 = pascha.minusDays(29),
            lentenSaturday4 = pascha.minusDays(22),
            crossVenerationSunday = pascha.minusDays(28),
            laudationSaturday = pascha.minusDays(15),
            lazarusSaturday = pascha.minusDays(8),
            palmSunday = pascha.minusDays(7),
            passionWeekStart = pascha.minusDays(6),
            brightWeekEnd = pascha.plusDays(6),
            radonitsa = pascha.plusDays(9),
            midPentecost = pascha.plusDays(24),
            ascension = pascha.plusDays(39),
            trinitySaturday = pascha.plusDays(48),
            pentecost = pascha.plusDays(49),
            holySpiritDay = pascha.plusDays(50),
            trinityWeekEnd = pascha.plusDays(55),
            allSaintsSunday = pascha.plusDays(56),
            apostlesFastStart = apostlesFastStart,
            apostlesFastEnd = apostlesFastEnd
        )
    }

    /**
     * Demetrius Memorial Saturday (Димитрівська батьківська субота):
     * Saturday immediately preceding November 8 (St. Demetrius of Thessaloniki).
     */
    fun calculateDemetriusSaturday(year: Int): CalendarDate {
        val stDemetrius = CalendarDate(year, 11, 8)
        var d = stDemetrius.minusDays(1)
        while (!d.isSaturday) {
            d = d.minusDays(1)
        }
        return d
    }

    /**
     * Computes liturgical Tone (Глас 1..8) for a given date.
     * On Pascha week & St. Thomas week, tones cycle; St. Thomas Sunday is Tone 1, etc.
     */
    fun calculateTone(date: CalendarDate): Int {
        val paschaThisYear = calculateOrthodoxPascha(date.year)
        val referencePascha = if (date >= paschaThisYear) {
            paschaThisYear
        } else {
            calculateOrthodoxPascha(date.year - 1)
        }
        val daysSincePascha = referencePascha.daysUntil(date)
        val weeksSincePascha = daysSincePascha / 7
        // Bright Week (week 0): all 8 tones sung daily; Thomas Sunday (week 1) begins Tone 1.
        if (weeksSincePascha == 0) {
            return (daysSincePascha % 8) + 1
        }
        return ((weeksSincePascha - 1) % 8) + 1
    }
}
