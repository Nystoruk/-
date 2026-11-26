package com.example.domain.model

import java.util.Calendar

data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int
) : Comparable<CalendarDate> {

    init {
        require(month in 1..12) { "Invalid month: $month" }
        require(day in 1..31) { "Invalid day: $day" }
    }

    /** Epoch days since 1970-01-01 (Gregorian proleptic). */
    fun toEpochDay(): Long {
        val y = year.toLong() - if (month <= 2) 1L else 0L
        val era = (if (y >= 0) y else y - 399) / 400
        val yoe = y - era * 400
        val m = month.toLong()
        val doy = (153 * (m + if (m > 2) -3 else 9) + 2) / 5 + day - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097 + doe - 719468
    }

    /** 1 = Monday, ..., 7 = Sunday */
    val dayOfWeek: Int
        get() = ((toEpochDay() + 3).mod(7)) + 1

    val isSunday: Boolean get() = dayOfWeek == 7
    val isSaturday: Boolean get() = dayOfWeek == 6
    val isWednesdayOrFriday: Boolean get() = dayOfWeek == 3 || dayOfWeek == 5

    fun plusDays(days: Int): CalendarDate = fromEpochDay(toEpochDay() + days)
    fun minusDays(days: Int): CalendarDate = plusDays(-days)

    fun daysUntil(other: CalendarDate): Int = (other.toEpochDay() - toEpochDay()).toInt()

    fun lengthOfMonth(): Int = lengthOfMonth(year, month)

    /** Converts this Gregorian date to its corresponding Julian (Old Style) date. */
    fun toJulianDate(): CalendarDate {
        // Difference between Gregorian and Julian calendars:
        // Between 1900-03-01 and 2100-02-28 it is 13 days.
        // General formula from epochDay (Julian calendar epoch offset):
        val z = toEpochDay() + 719470
        val era = (if (z >= 0) z else z - 1460) / 1461
        val doe = z - era * 1461
        val yoe = (doe - doe / 1460) / 365
        val y = yoe + era * 4
        val doy = doe - (365 * yoe + yoe / 4)
        val mp = (5 * doy + 2) / 153
        val d = (doy - (153 * mp + 2) / 5 + 1).toInt()
        val m = (mp + if (mp < 10) 3 else -9).toInt()
        val finalYear = (y + if (m <= 2) 1 else 0).toInt()
        return CalendarDate(finalYear, m, d)
    }

    fun toIsoString(): String =
        "%04d-%02d-%02d".format(year, month, day)

    fun formatUkrainianFull(): String =
        "$day ${monthGenitive(month)} $year р."

    fun formatUkrainianShort(): String =
        "$day ${monthGenitive(month)}"

    fun formatJulianShort(): String {
        val j = toJulianDate()
        return "${j.day} ${monthGenitiveShort(j.month)} ст. ст."
    }

    fun formatJulianFull(): String {
        val j = toJulianDate()
        return "${j.day} ${monthGenitive(j.month)} (ст. ст.)"
    }

    fun weekdayName(): String = weekdayNames[dayOfWeek - 1]
    fun weekdayShort(): String = weekdayShortNames[dayOfWeek - 1]

    override fun compareTo(other: CalendarDate): Int {
        return compareValuesBy(this, other, { it.year }, { it.month }, { it.day })
    }

    companion object {
        fun today(): CalendarDate {
            val cal = Calendar.getInstance()
            return CalendarDate(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1,
                day = cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun parseIso(iso: String): CalendarDate? {
            val parts = iso.split("-")
            if (parts.size != 3) return null
            val y = parts[0].toIntOrNull() ?: return null
            val m = parts[1].toIntOrNull() ?: return null
            val d = parts[2].toIntOrNull() ?: return null
            return runCatching { CalendarDate(y, m, d) }.getOrNull()
        }

        fun fromEpochDay(epochDay: Long): CalendarDate {
            val z = epochDay + 719468
            val era = (if (z >= 0) z else z - 146096) / 146097
            val doe = z - era * 146097
            val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
            val y = yoe + era * 400
            val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
            val mp = (5 * doy + 2) / 153
            val d = (doy - (153 * mp + 2) / 5 + 1).toInt()
            val m = (mp + if (mp < 10) 3 else -9).toInt()
            val finalYear = (y + if (m <= 2) 1 else 0).toInt()
            return CalendarDate(finalYear, m, d)
        }

        fun isLeapYearGregorian(year: Int): Boolean =
            (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

        fun lengthOfMonth(year: Int, month: Int): Int = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYearGregorian(year)) 29 else 28
            else -> 30
        }

        fun monthNominative(month: Int): String = monthNominativeNames[month - 1]
        fun monthGenitive(month: Int): String = monthGenitiveNames[month - 1]
        fun monthGenitiveShort(month: Int): String = monthShortNames[month - 1]

        private val monthNominativeNames = listOf(
            "Січень", "Лютий", "Березень", "Квітень", "Травень", "Червень",
            "Липень", "Серпень", "Вересень", "Жовтень", "Листопад", "Грудень"
        )

        private val monthGenitiveNames = listOf(
            "січня", "лютого", "березня", "квітня", "травня", "червня",
            "липня", "серпня", "вересня", "жовтня", "листопада", "грудня"
        )

        private val monthShortNames = listOf(
            "січ.", "лют.", "бер.", "квіт.", "трав.", "черв.",
            "лип.", "серп.", "вер.", "жовт.", "лист.", "груд."
        )

        private val weekdayNames = listOf(
            "Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця", "Субота", "Неділя"
        )

        val weekdayShortNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд")
    }
}
