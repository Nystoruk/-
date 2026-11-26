package com.example.domain.calendar

import com.example.domain.model.CalendarDate
import com.example.domain.model.FastingPeriod
import com.example.domain.model.FastingPeriodSummary
import com.example.domain.model.FastingType
import com.example.domain.model.FeastRank
import com.example.domain.model.LiturgicalDay
import java.util.concurrent.ConcurrentHashMap

class OrthodoxCalendarRepository {

    private val movableCache = ConcurrentHashMap<Int, MovableCalendarInfo>()

    fun getMovableYear(year: Int): MovableCalendarInfo =
        movableCache.getOrPut(year) { PaschaliaCalculator.calculateMovableYear(year) }

    fun getLiturgicalDay(date: CalendarDate): LiturgicalDay {
        val movable = getMovableYear(date.year)
        val julian = date.toJulianDate()
        val fixed = FixedFeastsCatalog.findByJulian(julian.month, julian.day)
        val tone = PaschaliaCalculator.calculateTone(date)
        val demetriusSat = PaschaliaCalculator.calculateDemetriusSaturday(date.year)

        // 1. Determine movable feast overlay (if any)
        val movableEvent = resolveMovableEvent(date, movable, demetriusSat)

        // 2. Determine fasting period and daily fasting rule
        val (fastingPeriod, fastingType, fastingNote) = resolveFasting(date, julian, movable, fixed)

        // 3. Merge feast rank, title, saints, reading, reflection
        val finalRank = when {
            movableEvent != null && fixed != null -> {
                if (movableEvent.rank.ordinal <= fixed.rank.ordinal) movableEvent.rank else fixed.rank
            }
            movableEvent != null -> movableEvent.rank
            fixed != null -> fixed.rank
            date.isSunday -> FeastRank.SUNDAY
            else -> FeastRank.ORDINARY
        }

        val finalTitle = when {
            movableEvent != null && fixed != null -> {
                if (movableEvent.rank.ordinal <= fixed.rank.ordinal) movableEvent.title else fixed.title
            }
            movableEvent != null -> movableEvent.title
            fixed != null -> fixed.title
            date.isSunday -> "Неділя ${describeWeekAfterPentecost(date, movable)}"
            else -> defaultWeekdayTitle(date, fastingPeriod)
        }

        val secondaryTitle = when {
            movableEvent != null && fixed != null -> {
                if (movableEvent.rank.ordinal <= fixed.rank.ordinal) fixed.title else movableEvent.title
            }
            else -> null
        }

        val saints = buildList {
            if (movableEvent != null) add(movableEvent.subtitle)
            if (fixed != null) addAll(fixed.saints)
            if (movableEvent == null && fixed == null) {
                add(defaultSaintMemory(date))
            }
        }

        val weekDescription = describeLiturgicalWeek(date, movable, tone)

        val readingRef = movableEvent?.readingRef
            ?: fixed?.readingRef
            ?: defaultReadingRef(date, tone)

        val reflection = movableEvent?.reflection
            ?: fixed?.reflection
            ?: defaultReflection(date, fastingPeriod, fastingType)

        return LiturgicalDay(
            date = date,
            julianDate = julian,
            feastRank = finalRank,
            feastTitle = finalTitle,
            secondaryTitle = secondaryTitle,
            saints = saints,
            fastingType = fastingType,
            fastingPeriod = fastingPeriod,
            fastingNote = fastingNote,
            liturgicalTone = tone,
            weekDescription = weekDescription,
            readingRef = readingRef,
            reflection = reflection
        )
    }

    fun getMonthDays(year: Int, month: Int): List<LiturgicalDay> {
        val count = CalendarDate.lengthOfMonth(year, month)
        return (1..count).map { day -> getLiturgicalDay(CalendarDate(year, month, day)) }
    }

    fun getYearMajorFeasts(year: Int): List<LiturgicalDay> {
        val result = mutableListOf<LiturgicalDay>()
        for (month in 1..12) {
            val days = CalendarDate.lengthOfMonth(year, month)
            for (day in 1..days) {
                val lit = getLiturgicalDay(CalendarDate(year, month, day))
                if (lit.isMajorFeast) {
                    result.add(lit)
                }
            }
        }
        return result
    }

    fun getYearMemorialDays(year: Int): List<LiturgicalDay> {
        val m = getMovableYear(year)
        val demetriusSat = PaschaliaCalculator.calculateDemetriusSaturday(year)
        val dates = listOf(
            m.meatfareSaturday,
            m.lentenSaturday2,
            m.lentenSaturday3,
            m.lentenSaturday4,
            m.radonitsa,
            m.trinitySaturday,
            demetriusSat
        )
        return dates.distinct().sorted().map { getLiturgicalDay(it) }
    }

    fun getYearFastingPeriods(year: Int): List<FastingPeriodSummary> {
        val m = getMovableYear(year)
        // Nativity fast in a given calendar year has two spans: Jan 1-6 (end of previous year's fast) and Nov 28 - Jan 6 next year
        val nativityStart = CalendarDate(year, 11, 28)
        val nativityEnd = CalendarDate(year + 1, 1, 6)
        val dormitionStart = CalendarDate(year, 8, 14)
        val dormitionEnd = CalendarDate(year, 8, 27)

        val list = mutableListOf<FastingPeriodSummary>()

        list.add(
            FastingPeriodSummary(
                id = "great_lent_$year",
                title = "Великий піст (Свята Чотиридесятниця і Страсна седмиця)",
                subtitle = "Головний піст перед Світлим Христовим Воскресінням",
                startDate = m.greatLentStart,
                endDate = m.pascha.minusDays(1),
                description = "48 днів поглибленої молитви, покаяння та підготовки до Пасхи Господньої, включаючи Страсну седмицю.",
                dietarySummary = "Без м'яса, молочного, яєць та риби; у будні без олії, у вихідні з олією. Риба дозволяється на Благовіщення та Вербну неділю.",
                periodType = FastingPeriod.GREAT_LENT
            )
        )

        if (m.apostlesFastStart <= m.apostlesFastEnd) {
            val daysCount = m.apostlesFastStart.daysUntil(m.apostlesFastEnd) + 1
            list.add(
                FastingPeriodSummary(
                    id = "apostles_fast_$year",
                    title = "Петрів (Апостольський) піст",
                    subtitle = "Тривалість у $year році: $daysCount днів",
                    startDate = m.apostlesFastStart,
                    endDate = m.apostlesFastEnd,
                    description = "Піст на честь святих первоверховних апостолів Петра і Павла; його тривалість залежить від дати Пасхи.",
                    dietarySummary = "Середа і п'ятниця — без олії; в інші дні дозволяється їжа з олією, у суботу, неділю та великі свята — риба.",
                    periodType = FastingPeriod.APOSTLES_FAST
                )
            )
        }

        list.add(
            FastingPeriodSummary(
                id = "dormition_fast_$year",
                title = "Успенський піст (Спасівка)",
                subtitle = "Двотижневий піст перед Успінням Пресвятої Богородиці",
                startDate = dormitionStart,
                endDate = dormitionEnd,
                description = "Суворий літній піст, що починається Медовим Спасом і завершується святом Успіння Божої Матері.",
                dietarySummary = "За строгістю близький до Великого посту: у будні без олії, у суботу та неділю з олією. На Преображення (19 серпня) дозволяється риба.",
                periodType = FastingPeriod.DORMITION_FAST
            )
        )

        list.add(
            FastingPeriodSummary(
                id = "nativity_fast_$year",
                title = "Різдвяний піст (Пилипівка)",
                subtitle = "Сорокаденний піст перед Різдвом Христовим",
                startDate = nativityStart,
                endDate = nativityEnd,
                description = "Духовна підготовка до зустрічі Богомладенця Христа. Завершується суворим Різдвяним святвечором 6 січня.",
                dietarySummary = "До 19 грудня у вихідні та великі свята дозволяється риба; після 20 грудня устав посилюється, а 6 січня — піст до першої зірки.",
                periodType = FastingPeriod.NATIVITY_FAST
            )
        )

        return list
    }

    fun getUpcomingHighlights(from: CalendarDate): UpcomingHighlights {
        var nextFeast: LiturgicalDay? = null
        var nextFastStart: LiturgicalDay? = null
        var nextMemorial: LiturgicalDay? = null

        val todayLit = getLiturgicalDay(from)

        for (offset in 1..370) {
            val d = from.plusDays(offset)
            val lit = getLiturgicalDay(d)
            if (nextFeast == null && lit.isMajorFeast) {
                nextFeast = lit
            }
            if (nextMemorial == null && lit.isMemorialDay) {
                nextMemorial = lit
            }
            if (nextFastStart == null && lit.isFastDay && !todayLit.isFastDay) {
                nextFastStart = lit
            } else if (nextFastStart == null && todayLit.isFastDay) {
                // Find next fast-free / feast break or next multi-day fast start
                val prev = getLiturgicalDay(d.minusDays(1))
                if (lit.isFastDay && !prev.isFastDay) {
                    nextFastStart = lit
                }
            }
            if (nextFeast != null && nextFastStart != null && nextMemorial != null) break
        }

        return UpcomingHighlights(
            nextMajorFeast = nextFeast,
            nextFastDay = nextFastStart,
            nextMemorialDay = nextMemorial
        )
    }

    fun searchEvents(query: String, year: Int): List<LiturgicalDay> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        val results = mutableListOf<LiturgicalDay>()
        for (month in 1..12) {
            val days = CalendarDate.lengthOfMonth(year, month)
            for (day in 1..days) {
                val lit = getLiturgicalDay(CalendarDate(year, month, day))
                val matches = lit.feastTitle.lowercase().contains(q) ||
                    (lit.secondaryTitle?.lowercase()?.contains(q) == true) ||
                    lit.saints.any { it.lowercase().contains(q) } ||
                    lit.fastingPeriod.titleUk.lowercase().contains(q)
                if (matches && (lit.feastRank != FeastRank.ORDINARY || lit.saints.any { it.lowercase().contains(q) })) {
                    results.add(lit)
                }
            }
        }
        return results
    }

    private data class MovableEvent(
        val rank: FeastRank,
        val title: String,
        val subtitle: String,
        val readingRef: String,
        val reflection: String
    )

    private fun resolveMovableEvent(
        date: CalendarDate,
        m: MovableCalendarInfo,
        demetriusSat: CalendarDate
    ): MovableEvent? = when (date) {
        m.pascha -> MovableEvent(
            rank = FeastRank.PASCHA,
            title = "Світле Христове Воскресіння. Пасха",
            subtitle = "Великдень — свято над святами і торжество над торжествами",
            readingRef = "Діян. 1:1–8; Ін. 1:1–17",
            reflection = "Христос Воскрес! Світлом Свого Воскресіння Господь переміг смерть і дарував нам радість вічного життя."
        )
        m.publicanPhariseeSunday -> MovableEvent(
            rank = FeastRank.SUNDAY,
            title = "Неділя про митаря і фарисея",
            subtitle = "Початок підготовчих тижнів до Великого посту (Тріодь Посна)",
            readingRef = "2 Тим. 3:10–15; Лк. 18:10–14",
            reflection = "Притча про смиренну молитву митаря навчає нас уникати гордості та осуду ближніх."
        )
        m.prodigalSonSunday -> MovableEvent(
            rank = FeastRank.SUNDAY,
            title = "Неділя про блудного сина",
            subtitle = "Друга підготовча неділя до Великого посту",
            readingRef = "1 Кор. 6:12–20; Лк. 15:11–32",
            reflection = "Безмежна любов Отця Небесного завжди чекає на покаянне повернення кожної душі."
        )
        m.meatfareSaturday -> MovableEvent(
            rank = FeastRank.MEMORIAL,
            title = "Вселенська батьківська (м'ясопусна) субота",
            subtitle = "Загальне церковне поминання всіх від віку спочилих православних християн",
            readingRef = "1 Кор. 10:23–28; Лк. 21:8–9, 25–27, 33–36",
            reflection = "Напередодні спогаду про Страшний суд Церква соборно молиться за всіх спочилих."
        )
        m.meatfareSunday -> MovableEvent(
            rank = FeastRank.SUNDAY,
            title = "Неділя м'ясопусна, про Страшний суд",
            subtitle = "Заговини на м'ясо перед Великим постом",
            readingRef = "1 Кор. 8:8–9:2; Мф. 25:31–46",
            reflection = "На Страшному суді Господь запитає нас насамперед про діла милосердної любові до ближніх."
        )
        m.cheesefareSunday -> MovableEvent(
            rank = FeastRank.SUNDAY,
            title = "Прощена неділя (Сиропусна)",
            subtitle = "Спогад Адамового вигнання. Чин прощення перед Великим постом",
            readingRef = "Рим. 13:11–14:4; Мф. 6:14–21",
            reflection = "Щоб увійти у Великий піст із чистим серцем, просімо прощення в усіх і самі щиро прощаймо."
        )
        m.greatLentStart -> MovableEvent(
            rank = FeastRank.MAJOR_SAINT,
            title = "Чистий понеділок. Початок Великого посту",
            subtitle = "Перший день Святої Чотиридесятниці. Великий канон прп. Андрія Критського",
            readingRef = "Іс. 1:1–20; Бут. 1:1–13",
            reflection = "Благословенний початок весни духовної — час тиші, покаяння та оновлення серця."
        )
        m.triumphOfOrthodoxy -> MovableEvent(
            rank = FeastRank.MAJOR_SAINT,
            title = "Торжество Православ'я (1-ша неділя Великого посту)",
            subtitle = "Спогад відновлення іконошанування (843)",
            readingRef = "Євр. 11:24–26, 32–12:2; Ін. 1:43–51",
            reflection = "Перемога православної віри та істинного свідчення про Боговтілення."
        )
        m.lentenSaturday2, m.lentenSaturday3, m.lentenSaturday4 -> MovableEvent(
            rank = FeastRank.MEMORIAL,
            title = "Батьківська поминальна субота Великого посту",
            subtitle = "Поминання спочилих родичів і всіх православних християн",
            readingRef = "Євр. 10:32–38; Мк. 2:14–17",
            reflection = "Молитовна пам'ять про спочилих не переривається і в дні Чотиридесятниці."
        )
        m.crossVenerationSunday -> MovableEvent(
            rank = FeastRank.MAJOR_SAINT,
            title = "Хрестопоклонна неділя (3-тя неділя Великого посту)",
            subtitle = "Винесення Чесного і Животворчого Хреста Господнього",
            readingRef = "Євр. 4:14–5:6; Мк. 8:34–9:1",
            reflection = "Середина Великого посту: Хрест Господній укріплює вірних у духовному подвигу."
        )
        m.laudationSaturday -> MovableEvent(
            rank = FeastRank.MAJOR_SAINT,
            title = "Похвала Пресвятої Богородиці (Субота Акафіста)",
            subtitle = "Урочисте читання Акафіста Божій Матері",
            readingRef = "Євр. 9:1–7; Лк. 10:38–42, 11:27–28",
            reflection = "Подячний спів Заступниці нашій Пресвятій Богородиці на шляху до Пасхи."
        )
        m.lazarusSaturday -> MovableEvent(
            rank = FeastRank.MAJOR_SAINT,
            title = "Лазарева субота",
            subtitle = "Воскрешення праведного Лазаря Четвероденного",
            readingRef = "Євр. 12:28–13:8; Ін. 11:1–45",
            reflection = "Знамення прийдешнього загального воскресіння і перемоги Христа над смертю."
        )
        m.palmSunday -> MovableEvent(
            rank = FeastRank.TWELVE_GREAT,
            title = "Вхід Господній в Єрусалим (Вербна неділя)",
            subtitle = "Дванадесяте перехідне свято. Освячення верби",
            readingRef = "Флп. 4:4–9; Ін. 12:1–18",
            reflection = "Зустрічаймо Господа не лише гілками верби, а й чистотою серця, готового йти за Ним."
        )
        m.pascha.minusDays(2) -> MovableEvent(
            rank = FeastRank.GREAT,
            title = "Велика П'ятниця. Спогад святих спасительних Страстей Господніх",
            subtitle = "Винесення Святої Плащаниці. День найсуворішого посту",
            readingRef = "1 Кор. 1:18–2:2; Мф. 27:1–61",
            reflection = "Тиша благоговіння перед Хресною жертвою Спасителя заради нашого спасіння."
        )
        m.pascha.minusDays(1) -> MovableEvent(
            rank = FeastRank.GREAT,
            title = "Велика Субота",
            subtitle = "Погребіння Господа Ісуса Христа і зішестя в ад",
            readingRef = "Рим. 6:3–11; Мф. 28:1–20",
            reflection = "Благословенна тиша напередодні Світлої Пасхальної ночі."
        )
        m.radonitsa -> MovableEvent(
            rank = FeastRank.MEMORIAL,
            title = "Радониця. Особливе поминання спочилих",
            subtitle = "Пасхальне поминання померлих (вівторок 2-го тижня після Пасхи)",
            readingRef = "Діян. 4:1–10; Ін. 3:16–21",
            reflection = "Ділимося світлою радістю Воскресіння Христового з нашими спочилими близькими."
        )
        m.ascension -> MovableEvent(
            rank = FeastRank.TWELVE_GREAT,
            title = "Вознесіння Господнє",
            subtitle = "Дванадесяте перехідне свято (40-й день після Пасхи)",
            readingRef = "Діян. 1:1–12; Лк. 24:36–53",
            reflection = "Господь возніс людське єство праворуч Отця і послав благословення учням."
        )
        m.trinitySaturday -> MovableEvent(
            rank = FeastRank.MEMORIAL,
            title = "Троїцька вселенська батьківська субота",
            subtitle = "Поминання всіх від віку спочилих православних християн перед Трійцею",
            readingRef = "Діян. 28:1–31; Ін. 21:15–25",
            reflection = "Напередодні П'ятидесятниці молимося, щоб Дух Святий упокоїв душі наших спочилих."
        )
        m.pentecost -> MovableEvent(
            rank = FeastRank.TWELVE_GREAT,
            title = "День Святої Трійці. П'ятидесятниця",
            subtitle = "Дванадесяте свято. Зішестя Святого Духа на апостолів",
            readingRef = "Діян. 2:1–11; Ін. 7:37–52, 8:12",
            reflection = "День народження Церкви Христової: благодать Святого Духа оновлює весь світ."
        )
        m.holySpiritDay -> MovableEvent(
            rank = FeastRank.GREAT,
            title = "День Святого Духа",
            subtitle = "Понеділок після П'ятидесятниці",
            readingRef = "Еф. 5:8–19; Мф. 18:10–20",
            reflection = "Особливе прославлення Третьої Особи Пресвятої Трійці — Духа Істини і Утішителя."
        )
        m.allSaintsSunday -> MovableEvent(
            rank = FeastRank.MAJOR_SAINT,
            title = "Неділя Всіх Святих",
            subtitle = "Заговини на Петрів (Апостольський) піст",
            readingRef = "Євр. 11:33–12:2; Мф. 10:32–33, 37–38, 19:27–30",
            reflection = "Прославлення всіх угодників Божих, які стали плодом сходження Святого Духа."
        )
        demetriusSat -> MovableEvent(
            rank = FeastRank.MEMORIAL,
            title = "Димитрівська батьківська субота",
            subtitle = "Осіннє поминання спочилих родичів, воїнів і всіх православних",
            readingRef = "1 Сол. 4:13–17; Ін. 5:24–30",
            reflection = "Церковна молитва за спочилих перед днем пам'яті великомученика Димитрія Солунського."
        )
        else -> null
    }

    private fun resolveFasting(
        date: CalendarDate,
        julian: CalendarDate,
        m: MovableCalendarInfo,
        fixed: FixedFeastEntry?
    ): Triple<FastingPeriod, FastingType, String> {
        // Check Nativity Svyatki (Dec 25 Julian .. Jan 4 Julian => Jan 7 .. Jan 17 Gregorian)
        val isSvyatki = (julian.month == 12 && julian.day >= 25) || (julian.month == 1 && julian.day <= 4)
        if (isSvyatki) {
            return Triple(
                FastingPeriod.FAST_FREE_WEEK,
                FastingType.FAST_FREE,
                "Різдвяні святки — суцільна седмиця, посту немає в усі дні."
            )
        }

        // Publican & Pharisee fast-free week
        if (date in m.publicanPhariseeSunday.plusDays(1)..m.publicanPhariseeSunday.plusDays(6)) {
            return Triple(
                FastingPeriod.FAST_FREE_WEEK,
                FastingType.FAST_FREE,
                "Суцільна седмиця про митаря і фарисея — посту в середу та п'ятницю немає."
            )
        }

        // Cheesefare (Maslenitsa / Syropusna) week
        if (date in m.meatfareSunday.plusDays(1)..m.cheesefareSunday) {
            return Triple(
                FastingPeriod.CHEESE_WEEK,
                FastingType.DAIRY_ALLOWED,
                "Сирна седмиця (Масляна): м'ясо вже не вживається, молочні продукти, яйця та риба дозволені щодня."
            )
        }

        // Passion week
        if (date in m.passionWeekStart..m.pascha.minusDays(1)) {
            val type = when (date) {
                m.pascha.minusDays(2) -> FastingType.STRICT_DRY
                m.pascha.minusDays(3) -> FastingType.WITH_OIL
                else -> FastingType.NO_OIL
            }
            val note = when (date) {
                m.pascha.minusDays(2) -> "Велика П'ятниця — день найсуворішого посту (за традицією повне утримання до виносу Плащаниці)."
                m.pascha.minusDays(3) -> "Великий Четвер — дозволяється варена рослинна їжа з олією."
                else -> "Страсна седмиця — суворий піст напередодні Пасхи Господньої."
            }
            return Triple(FastingPeriod.PASSION_WEEK, type, note)
        }

        // Great Lent
        if (date in m.greatLentStart..m.palmSunday) {
            val type = when {
                date == m.palmSunday -> FastingType.FISH_ALLOWED
                fixed?.allowsFishInFast == true -> FastingType.FISH_ALLOWED
                date == m.greatLentStart -> FastingType.STRICT_DRY
                date.isSaturday || date.isSunday -> FastingType.WITH_OIL
                else -> FastingType.NO_OIL
            }
            val note = when {
                date == m.palmSunday -> "Вербна неділя: послаблення посту — дозволяється риба та олія."
                fixed?.allowsFishInFast == true -> "Благовіщення Пресвятої Богородиці: дозволяється риба та олія."
                date == m.lazarusSaturday -> "Лазарева субота: дозволяється їжа з олією та риб'яча ікра."
                date.isSaturday || date.isSunday -> "Великий піст (вихідний день): дозволяється гаряча їжа з олією."
                else -> "Великий піст (будній день): рослинна їжа без олії."
            }
            return Triple(FastingPeriod.GREAT_LENT, type, note)
        }

        // Bright Week
        if (date in m.pascha..m.brightWeekEnd) {
            return Triple(
                FastingPeriod.FAST_FREE_WEEK,
                FastingType.FAST_FREE,
                "Світла Пасхальна седмиця — суцільний тиждень пасхальної радості, посту немає."
            )
        }

        // Trinity Week
        if (date in m.holySpiritDay..m.trinityWeekEnd) {
            return Triple(
                FastingPeriod.FAST_FREE_WEEK,
                FastingType.FAST_FREE,
                "Троїцька седмиця — суцільна, посту в середу та п'ятницю немає."
            )
        }

        // Apostles' Fast
        if (m.apostlesFastStart <= m.apostlesFastEnd && date in m.apostlesFastStart..m.apostlesFastEnd) {
            val type = when {
                date.isWednesdayOrFriday && fixed?.allowsFishInFast != true -> FastingType.NO_OIL
                date.isSaturday || date.isSunday || fixed?.allowsFishInFast == true -> FastingType.FISH_ALLOWED
                else -> FastingType.WITH_OIL
            }
            val note = when (type) {
                FastingType.FISH_ALLOWED -> "Петрів піст: дозволяється риба та їжа з олією."
                FastingType.NO_OIL -> "Петрів піст (середа/п'ятниця): рослинна їжа без олії."
                else -> "Петрів піст: рослинна їжа з олією."
            }
            return Triple(FastingPeriod.APOSTLES_FAST, type, note)
        }

        // Dormition Fast (Julian Aug 1..14 => Gregorian Aug 14..27)
        if (julian.month == 8 && julian.day in 1..14) {
            val type = when {
                fixed?.allowsFishInFast == true -> FastingType.FISH_ALLOWED
                date.isSaturday || date.isSunday -> FastingType.WITH_OIL
                else -> FastingType.NO_OIL
            }
            val note = when (type) {
                FastingType.FISH_ALLOWED -> "Преображення Господнє в Успенський піст: дозволяється риба та олія."
                FastingType.WITH_OIL -> "Успенський піст (вихідний): дозволяється гаряча їжа з олією."
                else -> "Успенський піст (суворий): рослинна їжа без олії."
            }
            return Triple(FastingPeriod.DORMITION_FAST, type, note)
        }

        // Nativity Fast (Julian Nov 15..Dec 24 => Gregorian Nov 28..Jan 6)
        val isNativityFast = (julian.month == 11 && julian.day >= 15) || (julian.month == 12 && julian.day <= 24)
        if (isNativityFast) {
            val isEve = julian.month == 12 && julian.day == 24
            val isForefeast = julian.month == 12 && julian.day in 20..23
            val type = when {
                isEve -> FastingType.STRICT_DRY
                fixed?.allowsFishInFast == true -> FastingType.FISH_ALLOWED
                isForefeast -> if (date.isSaturday || date.isSunday) FastingType.WITH_OIL else FastingType.NO_OIL
                date.isWednesdayOrFriday -> FastingType.NO_OIL
                date.isSaturday || date.isSunday -> FastingType.FISH_ALLOWED
                else -> FastingType.WITH_OIL
            }
            val note = when {
                isEve -> "Різдвяний святвечір: суворий піст до першої зірки (традиційно кутя та узвар)."
                fixed?.allowsFishInFast == true -> "Велике свято у Різдвяний піст: дозволяється риба та олія."
                isForefeast -> "Передсвяття Різдва Христового: посилений піст, риба не вживається."
                type == FastingType.FISH_ALLOWED -> "Різдвяний піст (вихідний): дозволяється риба та олія."
                type == FastingType.NO_OIL -> "Різдвяний піст (середа/п'ятниця): рослинна їжа без олії."
                else -> "Різдвяний піст: рослинна їжа з олією."
            }
            return Triple(FastingPeriod.NATIVITY_FAST, type, note)
        }

        // Strict one-day fasts (Epiphany Eve, Beheading of John, Exaltation of Cross)
        if (fixed?.isStrictOneDayFast == true) {
            return Triple(
                FastingPeriod.ONE_DAY_FAST,
                FastingType.STRICT_DRY,
                "Одноденний суворий піст (без м'яса, молочного, яєць і риби; рослинна їжа)."
            )
        }

        // Regular Wednesdays & Fridays
        if (date.isWednesdayOrFriday) {
            if (fixed?.allowsFishInFast == true || fixed?.rank == FeastRank.TWELVE_GREAT || fixed?.rank == FeastRank.GREAT) {
                return Triple(
                    FastingPeriod.ONE_DAY_FAST,
                    FastingType.FISH_ALLOWED,
                    "Пісний день з послабленням заради свята: дозволяється риба та олія."
                )
            }
            return Triple(
                FastingPeriod.ONE_DAY_FAST,
                FastingType.WITH_OIL,
                "Одноденний піст (середа/п'ятниця): утримання від м'яса, молочних продуктів, яєць та риби; дозволяється олія."
            )
        }

        return Triple(
            FastingPeriod.ORDINARY,
            FastingType.NONE,
            "Звичайний день без посту."
        )
    }

    private fun describeWeekAfterPentecost(date: CalendarDate, m: MovableCalendarInfo): String {
        val pentecost = m.pentecost
        return if (date > pentecost) {
            val weeks = (pentecost.daysUntil(date)) / 7
            if (weeks >= 1) "$weeks-та після П'ятидесятниці" else ""
        } else {
            "після Богоявлення"
        }
    }

    private fun describeLiturgicalWeek(
        date: CalendarDate,
        m: MovableCalendarInfo,
        tone: Int
    ): String {
        val periodLabel = when {
            date in m.greatLentStart..m.palmSunday -> "Великий піст"
            date in m.passionWeekStart..m.pascha.minusDays(1) -> "Страсна седмиця"
            date in m.pascha..m.brightWeekEnd -> "Світла седмиця"
            date in m.pascha..m.pentecost -> {
                val w = (m.pascha.daysUntil(date) / 7) + 1
                "$w-ма седмиця після Пасхи"
            }
            date > m.pentecost -> {
                val w = (m.pentecost.daysUntil(date) / 7) + 1
                "$w-ма седмиця після П'ятидесятниці"
            }
            else -> {
                val prevPentecost = getMovableYear(date.year - 1).pentecost
                val w = (prevPentecost.daysUntil(date) / 7) + 1
                "$w-ма седмиця після П'ятидесятниці"
            }
        }
        return "$periodLabel · Глас $tone"
    }

    private fun defaultWeekdayTitle(date: CalendarDate, period: FastingPeriod): String {
        return when (period) {
            FastingPeriod.GREAT_LENT -> "День Великого посту"
            FastingPeriod.PASSION_WEEK -> "День Страсної седмиці"
            FastingPeriod.NATIVITY_FAST -> "День Різдвяного посту"
            FastingPeriod.DORMITION_FAST -> "День Успенського посту"
            FastingPeriod.APOSTLES_FAST -> "День Петрового посту"
            else -> "Седмичний день (${date.weekdayName().lowercase()})"
        }
    }

    private fun defaultSaintMemory(date: CalendarDate): String {
        val julian = date.toJulianDate()
        return "Пам'ять святих дня (${julian.day} ${CalendarDate.monthGenitive(julian.month)} за ст. ст.)"
    }

    private fun defaultReadingRef(date: CalendarDate, tone: Int): String = when (date.dayOfWeek) {
        1 -> "Тропар безплотних сил · Глас $tone"
        2 -> "Тропар Іоанна Предтечі · Глас $tone"
        3 -> "Тропар Чесного Хреста · Глас $tone"
        4 -> "Тропар святих апостолів і свт. Миколая · Глас $tone"
        5 -> "Тропар Чесного Хреста · Глас $tone"
        6 -> "Тропар усіх святих і заупокійна молитва · Глас $tone"
        else -> "Недільне читання Євангелія · Глас $tone"
    }

    private fun defaultReflection(
        date: CalendarDate,
        period: FastingPeriod,
        fastingType: FastingType
    ): String {
        return when {
            period == FastingPeriod.GREAT_LENT ->
                "У дні Великого посту головним подвигом є стриманість не лише в їжі, а й у словах, осуді та гніві."
            period == FastingPeriod.NATIVITY_FAST ->
                "Різдвяний піст вчить тиші серця, добрим справам милосердя та вдячності Богові."
            date.isWednesdayOrFriday && fastingType != FastingType.FAST_FREE ->
                "Традиційний пісний день тижня: у середу згадуємо зраду Спасителя, а в п'ятницю — Його Хресні страждання."
            date.isSunday ->
                "Мала Пасха — недільний день, присвячений молитві, Божественній літургії та ділам любові."
            else ->
                "Кожен день церковного року освячується ранковою та вечірньою молитвою, вдячністю та добрим словом."
        }
    }
}

data class UpcomingHighlights(
    val nextMajorFeast: LiturgicalDay?,
    val nextFastDay: LiturgicalDay?,
    val nextMemorialDay: LiturgicalDay?
)
