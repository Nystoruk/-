package com.example.domain.model

import kotlinx.serialization.Serializable

enum class FeastRank(val labelUk: String) {
    PASCHA("Світле Христове Воскресіння"),
    TWELVE_GREAT("Дванадесяте свято"),
    GREAT("Велике свято"),
    MAJOR_SAINT("Значне церковне свято"),
    MEMORIAL("Поминальний день"),
    SUNDAY("Недільний день"),
    ORDINARY("Церковний день")
}

enum class FastingType(
    val titleUk: String,
    val shortUk: String,
    val dietaryRuleUk: String
) {
    NONE(
        titleUk = "Посту немає",
        shortUk = "Без посту",
        dietaryRuleUk = "Дозволяється скоромна їжа."
    ),
    FAST_FREE(
        titleUk = "Суцільна седмиця (посту немає)",
        shortUk = "Суцільна",
        dietaryRuleUk = "Посту немає протягом усього тижня, включно з середою та п'ятницею."
    ),
    DAIRY_ALLOWED(
        titleUk = "Сирна седмиця (Масляна)",
        shortUk = "Без м'яса",
        dietaryRuleUk = "Утримання від м'яса; дозволяються молочні продукти, яйця та риба щодня."
    ),
    FISH_ALLOWED(
        titleUk = "Піст (дозволяється риба)",
        shortUk = "Піст · Риба",
        dietaryRuleUk = "Утримання від м'яса, молочних продуктів і яєць; дозволяється риба та олія."
    ),
    WITH_OIL(
        titleUk = "Пісний день (їжа з олією)",
        shortUk = "Піст · З олією",
        dietaryRuleUk = "Рослинна їжа з додаванням олії; без м'яса, молочного, яєць та риби."
    ),
    NO_OIL(
        titleUk = "Пісний день (без олії)",
        shortUk = "Піст · Без олії",
        dietaryRuleUk = "Гаряча рослинна їжа без олії або сухоїдіння за церковним уставом."
    ),
    STRICT_DRY(
        titleUk = "Суворий піст",
        shortUk = "Суворий піст",
        dietaryRuleUk = "Особливо суворе утримання (сухоїдіння — хліб, вода, сирі овочі та фрукти, або повне утримання до вечірньої молитви)."
    )
}

enum class FastingPeriod(val titleUk: String) {
    GREAT_LENT("Великий піст"),
    PASSION_WEEK("Страсна седмиця"),
    APOSTLES_FAST("Петрів піст"),
    DORMITION_FAST("Успенський піст"),
    NATIVITY_FAST("Різдвяний піст"),
    ONE_DAY_FAST("Одноденний піст"),
    CHEESE_WEEK("Сирна седмиця"),
    FAST_FREE_WEEK("Суцільна седмиця"),
    ORDINARY("Звичайний час")
}

data class LiturgicalDay(
    val date: CalendarDate,
    val julianDate: CalendarDate,
    val feastRank: FeastRank,
    val feastTitle: String,
    val secondaryTitle: String? = null,
    val saints: List<String>,
    val fastingType: FastingType,
    val fastingPeriod: FastingPeriod,
    val fastingNote: String,
    val liturgicalTone: Int?,
    val weekDescription: String,
    val readingRef: String,
    val reflection: String
) {
    val isFastDay: Boolean
        get() = fastingType != FastingType.NONE && fastingType != FastingType.FAST_FREE

    val isMajorFeast: Boolean
        get() = feastRank == FeastRank.PASCHA ||
            feastRank == FeastRank.TWELVE_GREAT ||
            feastRank == FeastRank.GREAT

    val isMemorialDay: Boolean
        get() = feastRank == FeastRank.MEMORIAL
}

data class FastingPeriodSummary(
    val id: String,
    val title: String,
    val subtitle: String,
    val startDate: CalendarDate,
    val endDate: CalendarDate,
    val description: String,
    val dietarySummary: String,
    val periodType: FastingPeriod
)

@Serializable
data class CustomReminder(
    val id: String,
    val isoDate: String,
    val title: String,
    val note: String = "",
    val hour: Int = 8,
    val minute: Int = 0
)

data class ReminderSettings(
    val enabled: Boolean = true,
    val notifyGreatFeasts: Boolean = true,
    val notifyMultiDayFasts: Boolean = true,
    val notifyOneDayFasts: Boolean = false,
    val notifyMemorialSaturdays: Boolean = true,
    val notifyEveningBefore: Boolean = true,
    val hour: Int = 8,
    val minute: Int = 0
)
