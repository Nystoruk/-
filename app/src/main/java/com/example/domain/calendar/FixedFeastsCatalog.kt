package com.example.domain.calendar

import com.example.domain.model.FeastRank

data class FixedFeastEntry(
    val julianMonth: Int,
    val julianDay: Int,
    val rank: FeastRank,
    val title: String,
    val saints: List<String>,
    val readingRef: String,
    val reflection: String,
    val allowsFishInFast: Boolean = false,
    val isStrictOneDayFast: Boolean = false
)

/**
 * Catalog of fixed liturgical feasts and commemorations of the Ukrainian Orthodox Church (УПЦ),
 * keyed by Julian (Old Style) month and day so all dates map accurately to Gregorian civil dates.
 */
object FixedFeastsCatalog {

    private val entries: Map<Pair<Int, Int>, FixedFeastEntry> by lazy {
        allEntries.associateBy { it.julianMonth to it.julianDay }
    }

    fun findByJulian(month: Int, day: Int): FixedFeastEntry? = entries[month to day]

    fun all(): List<FixedFeastEntry> = allEntries

    private val allEntries = listOf(
        // ==================== СІЧЕНЬ (ст. ст. — Грудень / Січень) ====================
        FixedFeastEntry(
            julianMonth = 12, julianDay = 19,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Іллі Муромця, Печерського",
            saints = listOf(
                "Прп. Іллі Муромця, Печерського (бл. 1188)",
                "Мч. Боніфатія Тарсійського",
                "Свт. Боніфатія Милостивого"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Преподобний Ілля Муромець — подвижник Києво-Печерської лаври, приклад мужності, вірності та смиренного служіння ближнім."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 20,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Ігнатія Богоносця; свт. Філогонія",
            saints = listOf(
                "Сщмч. Ігнатія Богоносця, єп. Антіохійського",
                "Прп. Ігнатія Печерського",
                "Свт. Даниїла II, архієп. Сербського"
            ),
            readingRef = "Євр. 4:14–5:6; Мк. 9:33–41",
            reflection = "Передсвяття Різдва Христового: Церква готує серця вірних до зустрічі з Богомладенцем Хрестом."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 24,
            rank = FeastRank.MAJOR_SAINT,
            title = "Навечір'я Різдва Христового (Різдвяний святвечір)",
            saints = listOf(
                "Прмц. Євгенії Римської та з нею мчч.",
                "Прп. Кукші Одеського (пам'ять)"
            ),
            readingRef = "Євр. 1:1–12; Лк. 2:1–20",
            reflection = "Святвечір — день суворого посту до першої зірки та урочистих Царських часів перед Великою ніччю Різдва.",
            isStrictOneDayFast = true
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 25,
            rank = FeastRank.TWELVE_GREAT,
            title = "Різдво Господа Бога і Спаса нашого Ісуса Христа",
            saints = listOf(
                "Різдво Христове — Дванадесяте свято",
                "Поклоніння волхвів та пастухів"
            ),
            readingRef = "Гал. 4:4–7; Мф. 2:1–12",
            reflection = "«Христос народжується — славіте! Христос з небес — зустрічайте!» Небо і земля з'єдналися у Вифлеємському вертепі."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 26,
            rank = FeastRank.MAJOR_SAINT,
            title = "Собор Пресвятої Богородиці",
            saints = listOf(
                "Собор Пресвятої Богородиці",
                "Прав. Іосифа Обручника, Давида царя і Якова, брата Господнього"
            ),
            readingRef = "Євр. 2:11–18; Мф. 2:13–23",
            reflection = "Другий день Різдвяних свят: прославлення Пречистої Діви Марії, яка послужила таємниці Боговтілення."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 27,
            rank = FeastRank.MAJOR_SAINT,
            title = "Ап., першомученика і архідиякона Стефана",
            saints = listOf(
                "Ап., першомуч. і архідиякона Стефана",
                "Прп. Феодора Начертаного"
            ),
            readingRef = "Діян. 6:8–7:5, 47–60; Мф. 21:33–42",
            reflection = "Першомученик Стефан показав велич християнської любові, молячись за своїх гонителів."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 29,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Марка Гробокопателя, Феофіла та Іоанна Печерських",
            saints = listOf(
                "14 000 немовлят, за Христа у Вифлеємі вбитих",
                "Прп. Марка Гробокопателя, Печерського"
            ),
            readingRef = "2 Кор. 5:15–21; Мф. 2:13–23",
            reflection = "Молитовна пам'ять про перших невинних мучеників за Христа та преподобних отців Києво-Печерських."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 1,
            rank = FeastRank.GREAT,
            title = "Обрізання Господнє. Свт. Василія Великого",
            saints = listOf(
                "Обрізання Господа нашого Ісуса Христа",
                "Свт. Василія Великого, архієп. Кесарії Каппадокійської"
            ),
            readingRef = "Кол. 2:8–12; Лк. 2:20–21, 40–52",
            reflection = "Великий учитель Церкви святитель Василій Великий залишив безцінні богословські творіння та Божественну літургію."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 2,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Серафима Саровського; свт. Сильвестра",
            saints = listOf(
                "Прп. Серафима, Саровського чудотворця",
                "Свт. Сильвестра, папи Римського",
                "Прав. Іуліанії Лазаревської"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "«Здобудь дух мирний, і навколо тебе спасуться тисячі», — настанова преподобного Серафима Саровського."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 5,
            rank = FeastRank.MAJOR_SAINT,
            title = "Навечір'я Богоявлення (Хрещенський святвечір)",
            saints = listOf(
                "Навечір'я Богоявлення — день суворого посту",
                "Сщмч. Феопемпта і мч. Феони",
                "Прп. Синклітикиї Олександрійської"
            ),
            readingRef = "1 Кор. 9:19–27; Лк. 3:1–18",
            reflection = "У Хрещенський святвечір звершується Велике освячення води та суворий піст напередодні свята Богоявлення.",
            isStrictOneDayFast = true
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 6,
            rank = FeastRank.TWELVE_GREAT,
            title = "Хрещення Господнє. Святе Богоявлення",
            saints = listOf(
                "Святе Богоявлення — Хрещення Господа Ісуса Христа в Йордані",
                "Велике освячення води"
            ),
            readingRef = "Тит. 2:11–14, 3:4–7; Мф. 3:13–17",
            reflection = "У Хрещенні Господньому явилась світу Пресвята Трійця, і освятилося єство всіх вод земних."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 7,
            rank = FeastRank.MAJOR_SAINT,
            title = "Собор Предтечі і Хрестителя Господнього Іоанна",
            saints = listOf(
                "Собор Пророка, Предтечі і Хрестителя Господнього Іоанна"
            ),
            readingRef = "Діян. 19:1–8; Ін. 1:29–34",
            reflection = "Церква вшановує найбільшого серед пророків — святого Іоанна Предтечу, який сподобився хрестити Спасителя."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 12,
            rank = FeastRank.MAJOR_SAINT,
            title = "Мц. Татіани Римської; свт. Сави Сербського",
            saints = listOf(
                "Мц. Татіани Римської",
                "Свт. Сави I, архієп. Сербського",
                "Прп. Мартиніана Білозерського"
            ),
            readingRef = "2 Кор. 6:16–7:1; Мф. 10:32–33, 37–38",
            reflection = "Свята мучениця Татіана прославилась мужністю віри та милосердною опікою над бідними."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 17,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Антонія Великого",
            saints = listOf(
                "Прп. Антонія Великого — засновника чернецтва"
            ),
            readingRef = "Євр. 13:17–21; Лк. 6:17–23",
            reflection = "Преподобний Антоній Великий — духовний наставник і отець пустельників, приклад духовної боротьби і молитви."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 19,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Макарія Великого; свт. Марка Ефеського",
            saints = listOf(
                "Прп. Макарія Великого, Єгипетського",
                "Свт. Марка, архієп. Ефеського"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Преподобний Макарій Великий навчав безперервній молитві серця і лагідному терпінню."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 25,
            rank = FeastRank.MAJOR_SAINT,
            title = "Свт. Григорія Богослова; мц. Феліцати",
            saints = listOf(
                "Свт. Григорія Богослова, архієп. Константинопольського",
                "Ікони Божої Матері «Утамуй мої печалі»"
            ),
            readingRef = "1 Кор. 12:7–11; Ін. 10:9–16",
            reflection = "Святитель Григорій Богослов увійшов в історію Церкви глибиною тринітарного богослов'я та чистотою слова."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 27,
            rank = FeastRank.MAJOR_SAINT,
            title = "Перенесення мощей свт. Іоанна Золотоустого",
            saints = listOf(
                "Перенесення мощей свт. Іоанна Золотоустого (438)"
            ),
            readingRef = "Євр. 7:26–8:2; Ін. 10:9–16",
            reflection = "Тріумфальне повернення чесних мощей великого вселенського учителя до Константинополя."
        ),
        FixedFeastEntry(
            julianMonth = 1, julianDay = 30,
            rank = FeastRank.MAJOR_SAINT,
            title = "Собор Трьох Святителів: Василія, Григорія та Іоанна",
            saints = listOf(
                "Свт. Василія Великого, Григорія Богослова та Іоанна Золотоустого"
            ),
            readingRef = "Євр. 13:7–16; Мф. 5:14–19",
            reflection = "Троє великих вселенських учителів рівні перед Богом у своїй ревності за істину та любові до пастви."
        ),

        // ==================== ЛЮТИЙ ====================
        FixedFeastEntry(
            julianMonth = 2, julianDay = 2,
            rank = FeastRank.TWELVE_GREAT,
            title = "Стрітення Господнє",
            saints = listOf(
                "Стрітення Господа Бога і Спаса нашого Ісуса Христа",
                "Прав. Симеона Богоприємця і прор. Анни"
            ),
            readingRef = "Євр. 7:7–17; Лк. 2:22–40",
            reflection = "Зустріч Старого й Нового Завітів: праведний Симеон приймає на руки Творця неба і землі.",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 2, julianDay = 11,
            rank = FeastRank.MAJOR_SAINT,
            title = "Свт. Феодосія, архієп. Чернігівського; прп. Прохора",
            saints = listOf(
                "Прп. Прохора Лободника, Печерського",
                "Свт. Феодосія Чернігівського (знайдення мощей)"
            ),
            readingRef = "Євр. 13:7–16; Мф. 5:14–19",
            reflection = "Преподобний Прохор Печерський прославився суворим утриманням і милосердям до голодних."
        ),
        FixedFeastEntry(
            julianMonth = 2, julianDay = 12,
            rank = FeastRank.MAJOR_SAINT,
            title = "Свт. Олексія, митрополита Київського і всієї Русі",
            saints = listOf(
                "Свт. Олексія, митр. Київського, чудотворця",
                "Іверської ікони Божої Матері"
            ),
            readingRef = "Євр. 7:26–8:2; Ін. 10:9–16",
            reflection = "Молитовне шанування святителя Олексія та чудотворної Іверської ікони Пресвятої Богородиці."
        ),
        FixedFeastEntry(
            julianMonth = 2, julianDay = 24,
            rank = FeastRank.MAJOR_SAINT,
            title = "Перше і друге знайдення глави Іоанна Предтечі",
            saints = listOf(
                "Перше і друге знайдення глави Пророка, Предтечі і Хрестителя Іоанна",
                "Прп. Еразма Печерського"
            ),
            readingRef = "2 Кор. 4:6–15; Мф. 11:2–15",
            reflection = "Святиня Предтечі нагадує про безкомпромісність совісті, покаяння та духовну пильність."
        ),

        // ==================== БЕРЕЗЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 3, julianDay = 9,
            rank = FeastRank.MAJOR_SAINT,
            title = "40 мучеників, у Севастійському озері замучених",
            saints = listOf(
                "Свв. 40 мучеників Севастійських (320)"
            ),
            readingRef = "Євр. 12:1–10; Мф. 20:1–16",
            reflection = "Воїни-християни засвідчили непохитну вірність Христу, згуртувавшись у спільній молитві серед лютої стужі."
        ),
        FixedFeastEntry(
            julianMonth = 3, julianDay = 17,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Олексія, чоловіка Божого",
            saints = listOf(
                "Прп. Олексія, чоловіка Божого",
                "Прп. Макарія Калязинського"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Святий Олексій залишив земні багатства і в тихому смиренні присвятив усе життя Богові."
        ),
        FixedFeastEntry(
            julianMonth = 3, julianDay = 25,
            rank = FeastRank.TWELVE_GREAT,
            title = "Благовіщення Пресвятої Богородиці",
            saints = listOf(
                "Благовіщення Пресвятої Владичиці нашої Богородиці і Приснодіви Марії"
            ),
            readingRef = "Євр. 2:11–18; Лк. 1:24–38",
            reflection = "Архангел Гавриїл приносить Пречистій Діві благу вість про спасіння світу. Дозволяється риба (крім Страсної седмиці).",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 3, julianDay = 26,
            rank = FeastRank.MAJOR_SAINT,
            title = "Собор Архангела Гавриїла",
            saints = listOf(
                "Собор Архангела Гавриїла",
                "Прп. Василія Нового"
            ),
            readingRef = "Євр. 2:2–10; Лк. 10:16–21",
            reflection = "Вшанування небесного вісника таємниць Божих — святого Архангела Гавриїла."
        ),

        // ==================== КВІТЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 4, julianDay = 1,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Марії Єгипетської",
            saints = listOf(
                "Прп. Марії Єгипетської (522)",
                "Прп. Геронтія, канонарха Печерського"
            ),
            readingRef = "Гал. 3:23–29; Лк. 7:36–50",
            reflection = "Великий образ щирого покаяння і духовної переміни, здатний підняти людину з будь-якої прірви."
        ),
        FixedFeastEntry(
            julianMonth = 4, julianDay = 23,
            rank = FeastRank.GREAT,
            title = "Вмч. Георгія Побідоносця",
            saints = listOf(
                "Великомученика Георгія Побідоносця (303)",
                "Мц. цариці Олександри"
            ),
            readingRef = "Діян. 12:1–11; Ін. 15:17–16:2",
            reflection = "Мужній воїн Христовий, який переміг страх смерті силою віри та заступництвом Божим."
        ),
        FixedFeastEntry(
            julianMonth = 4, julianDay = 25,
            rank = FeastRank.MAJOR_SAINT,
            title = "Апостола і євангеліста Марка",
            saints = listOf(
                "Апостола і євангеліста Марка (63)"
            ),
            readingRef = "1 Петр. 5:6–14; Мк. 6:7–13",
            reflection = "Учень апостола Петра, автор другого Євангелія та засновник Олександрійської Церкви."
        ),

        // ==================== ТРАВЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 5, julianDay = 3,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Феодосія, ігумена Києво-Печерського",
            saints = listOf(
                "Прп. Феодосія, ігумена Києво-Печерського (1074)",
                "Прп. Петра Чудотворця"
            ),
            readingRef = "Євр. 13:7–16; Мф. 11:27–30",
            reflection = "Преподобний Феодосій Печерський утвердив у Києві спільножитний чернечий устав, любов до бідних і глибоке смирення."
        ),
        FixedFeastEntry(
            julianMonth = 5, julianDay = 6,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прав. Іова Багатостраждального; прп. Іова Почаївського",
            saints = listOf(
                "Прав. Іова Багатостраждального",
                "Прп. Іова, ігумена Почаївського (знайдення мощей, 1659)"
            ),
            readingRef = "Іак. 5:10–20; Лк. 6:17–23",
            reflection = "Преподобний Іов Почаївський — великий захисник православ'я на Волині та невпинний молитовник Почаївської гори."
        ),
        FixedFeastEntry(
            julianMonth = 5, julianDay = 8,
            rank = FeastRank.GREAT,
            title = "Апостола і євангеліста Іоанна Богослова",
            saints = listOf(
                "Ап. і єванг. Іоанна Богослова",
                "Прп. Арсенія Великого"
            ),
            readingRef = "1 Ін. 1:1–7; Ін. 19:25–27, 21:24–25",
            reflection = "Улюблений учень Христа, апостол любові, який звістив світу найвищу тайну Божого Слова."
        ),
        FixedFeastEntry(
            julianMonth = 5, julianDay = 9,
            rank = FeastRank.GREAT,
            title = "Перенесення мощей свт. Миколая з Мир Лікійських у Бар",
            saints = listOf(
                "Перенесення мощей святителя Миколая Чудотворця (1087)",
                "Пророка Ісаї"
            ),
            readingRef = "Євр. 13:17–21; Лк. 6:17–23",
            reflection = "Народно шанований «Микола Весняний» — швидкий помічник у бідах і захисник мандрівників та нужденних."
        ),
        FixedFeastEntry(
            julianMonth = 5, julianDay = 11,
            rank = FeastRank.MAJOR_SAINT,
            title = "Рівноапостольних Кирила і Мефодія",
            saints = listOf(
                "Свв. рівноапостольних Кирила і Мефодія, учителів слов'янських"
            ),
            readingRef = "Євр. 7:26–8:2; Ін. 10:9–16",
            reflection = "Просвітителі слов'янських народів, які створили писемність і відкрили нам Слово Боже зрозумілою мовою."
        ),
        FixedFeastEntry(
            julianMonth = 5, julianDay = 21,
            rank = FeastRank.MAJOR_SAINT,
            title = "Рівноап. царя Костянтина і матері його цариці Олени",
            saints = listOf(
                "Рівноапп. Костянтина та Олени",
                "Володимирської ікони Божої Матері"
            ),
            readingRef = "Діян. 26:1, 12–20; Ін. 10:1–9",
            reflection = "Рівноапостольні правителі дали свободу християнській Церкві та віднайшли Чесний Хрест Господній."
        ),

        // ==================== ЧЕРВЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 6, julianDay = 11,
            rank = FeastRank.MAJOR_SAINT,
            title = "Свт. Луки (Войно-Ясенецького), сповідника; апп. Варфоломія і Варнави",
            saints = listOf(
                "Свт. Луки, архієп. Кримського, сповідника і хірурга",
                "Апп. Варфоломія і Варнави"
            ),
            readingRef = "Діян. 11:19–26, 29–30; Лк. 10:16–21",
            reflection = "Святитель Лука поєднав дар видатного лікаря-хірурга з непохитним архіпастирським сповідництвом."
        ),
        FixedFeastEntry(
            julianMonth = 6, julianDay = 24,
            rank = FeastRank.GREAT,
            title = "Різдво чесного славного Пророка, Предтечі Іоанна",
            saints = listOf(
                "Різдво Іоанна Предтечі і Хрестителя Господнього"
            ),
            readingRef = "Рим. 13:11–14:4; Лк. 1:1–25, 57–68, 76, 80",
            reflection = "Дивовижне народження Предтечі у праведних Захарії та Єлисавети звістило наближення приходу Месії.",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 6, julianDay = 29,
            rank = FeastRank.GREAT,
            title = "Славних і всехвальних первоверховних апостолів Петра і Павла",
            saints = listOf(
                "Свв. первоверховних апостолів Петра і Павла"
            ),
            readingRef = "2 Кор. 11:21–12:9; Мф. 16:13–19",
            reflection = "Завершення Петрового посту. Святі Петро і Павло — непорушні стовпи Вселенської Церкви.",
            allowsFishInFast = true
        ),

        // ==================== ЛИПЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 7, julianDay = 10,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Антонія Києво-Печерського",
            saints = listOf(
                "Прп. Антонія Печерського, начальника всіх руських ченців (1073)"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Засновник Києво-Печерської лаври, який приніс на схили Дніпра благословення Святої Гори Афон."
        ),
        FixedFeastEntry(
            julianMonth = 7, julianDay = 11,
            rank = FeastRank.MAJOR_SAINT,
            title = "Рівноапостольної великої княгині Ольги",
            saints = listOf(
                "Рівноапостольної Ольги, великої княгині Київської (969)"
            ),
            readingRef = "2 Кор. 6:1–10; Мф. 25:1–13",
            reflection = "«Начальниця віри в землі нашій» — свята княгиня Ольга першою серед правителів Київської Русі прийняла Христа."
        ),
        FixedFeastEntry(
            julianMonth = 7, julianDay = 15,
            rank = FeastRank.GREAT,
            title = "Рівноап. великого князя Володимира. День Хрещення Русі",
            saints = listOf(
                "Рівноапостольного великого князя Володимира (у хрещенні Василія, 1015)",
                "День Хрещення Київської Русі (988)"
            ),
            readingRef = "Гал. 1:11–19; Ін. 10:1–9",
            reflection = "Князь Володимир увів наш народ у сім'ю християнських народів, преобразивши землю світлом Євангелія."
        ),
        FixedFeastEntry(
            julianMonth = 7, julianDay = 20,
            rank = FeastRank.GREAT,
            title = "Святого славного пророка Іллі",
            saints = listOf(
                "Пророка Іллі (IX ст. до Р. Х.)"
            ),
            readingRef = "Іак. 5:10–20; Лк. 4:22–30",
            reflection = "Полум'яний ревнитель слави Божої та єдинобожжя, піднесений на небо у вогняній колісниці."
        ),
        FixedFeastEntry(
            julianMonth = 7, julianDay = 23,
            rank = FeastRank.MAJOR_SAINT,
            title = "Почаївської ікони Божої Матері",
            saints = listOf(
                "Почаївської ікони Божої Матері",
                "Свв. мчч. Трофима і Феофіла"
            ),
            readingRef = "Флп. 2:5–11; Лк. 10:38–42, 11:27–28",
            reflection = "Одна з найбільших святинь українського православ'я — чудотворна Почаївська ікона Пресвятої Богородиці."
        ),
        FixedFeastEntry(
            julianMonth = 7, julianDay = 24,
            rank = FeastRank.MAJOR_SAINT,
            title = "Мчч. благовірних князів Бориса і Гліба",
            saints = listOf(
                "Свв. страстотерпців Бориса і Гліба (у хрещенні Романа і Давида, 1015)"
            ),
            readingRef = "Рим. 8:28–39; Ін. 15:17–16:2",
            reflection = "Перші святі канонізовані на нашій землі, які віддали перевагу християнській любові перед братовбивством."
        ),
        FixedFeastEntry(
            julianMonth = 7, julianDay = 27,
            rank = FeastRank.MAJOR_SAINT,
            title = "Великомученика і цілителя Пантелеймона",
            saints = listOf(
                "Вмч. і цілителя Пантелеймона (305)"
            ),
            readingRef = "2 Тим. 2:1–10; Ін. 15:17–16:2",
            reflection = "Безкорисливий лікар тіл і душ, до якого віруючі звертаються з молитвами про зцілення."
        ),

        // ==================== СЕРПЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 8, julianDay = 1,
            rank = FeastRank.MAJOR_SAINT,
            title = "Походження чесних древ Животворчого Хреста Господнього",
            saints = listOf(
                "Походження (Винесення) чесних древ Животворчого Хреста (Медовий Спас)",
                "Свв. семи мучеників Маккавеїв",
                "Початок Успенського посту"
            ),
            readingRef = "1 Кор. 1:18–24; Ін. 19:6–11, 13–20, 25–28, 30–35",
            reflection = "Освячення води, нового меду та трав. Початок благодатного двотижневого Успенського посту."
        ),
        FixedFeastEntry(
            julianMonth = 8, julianDay = 6,
            rank = FeastRank.TWELVE_GREAT,
            title = "Преображення Господа Бога і Спаса нашого Ісуса Христа",
            saints = listOf(
                "Преображення Господнє на горі Фавор (Яблучний Спас)"
            ),
            readingRef = "2 Петр. 1:10–19; Мф. 17:1–9",
            reflection = "Господь явив Своїм учням Божественну славу Фаворського світла. Освячуються плоди нового врожаю, в трапезі дозволяється риба.",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 8, julianDay = 15,
            rank = FeastRank.TWELVE_GREAT,
            title = "Успіння Пресвятої Владичиці нашої Богородиці і Приснодіви Марії",
            saints = listOf(
                "Успіння Пресвятої Богородиці — Дванадесяте свято"
            ),
            readingRef = "Флп. 2:5–11; Лк. 10:38–42, 11:27–28",
            reflection = "«В різдві дівство зберегла єси, в успінні світу не залишила єси, Богородице...» Пречиста не залишає нас у Своєму заступництві.",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 8, julianDay = 16,
            rank = FeastRank.MAJOR_SAINT,
            title = "Перенесення Нерукотворного Образу Господа Ісуса Христа",
            saints = listOf(
                "Нерукотворний Образ Спасителя (Горіховий / Хлібний Спас)"
            ),
            readingRef = "Кол. 1:12–18; Лк. 9:51–56, 10:22–24",
            reflection = "Прославлення іконописної традиції та істини Боговтілення — Бог став видимим у Христі."
        ),
        FixedFeastEntry(
            julianMonth = 8, julianDay = 28,
            rank = FeastRank.MAJOR_SAINT,
            title = "Собор преподобних отців Києво-Печерських, у Дальніх печерах",
            saints = listOf(
                "Собор преподобних отців Дальніх (Феодосієвих) печер",
                "Прп. Іова Почаївського (успіння, 1651)"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Велика соборна молитва до сотні подвижників святих Києво-Печерських печер."
        ),
        FixedFeastEntry(
            julianMonth = 8, julianDay = 29,
            rank = FeastRank.GREAT,
            title = "Усікновення глави Пророка, Предтечі і Хрестителя Іоанна",
            saints = listOf(
                "Усікновення глави Іоанна Предтечі — день суворого посту"
            ),
            readingRef = "Діян. 13:25–33; Мк. 6:14–30",
            reflection = "День суворого посту та скорботи про мученицьку кончину святого Іоанна Предтечі за правду Божу.",
            isStrictOneDayFast = true
        ),

        // ==================== ВЕРЕСЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 9, julianDay = 8,
            rank = FeastRank.TWELVE_GREAT,
            title = "Різдво Пресвятої Владичиці нашої Богородиці і Приснодіви Марії",
            saints = listOf(
                "Різдво Пресвятої Богородиці",
                "Почаївської ікони Божої Матері"
            ),
            readingRef = "Флп. 2:5–11; Лк. 10:38–42, 11:27–28",
            reflection = "Перше дванадесяте свято церковного року: радість народження Матері Спасителя.",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 9, julianDay = 14,
            rank = FeastRank.TWELVE_GREAT,
            title = "Воздвиження Чесного і Животворчого Хреста Господнього",
            saints = listOf(
                "Воздвиження Чесного і Животворчого Хреста Господнього",
                "Успіння свт. Іоанна Золотоустого"
            ),
            readingRef = "1 Кор. 1:18–24; Ін. 19:6–11, 13–20, 25–28, 30–35",
            reflection = "День суворого посту і поклоніння Хресту — знаменню перемоги любові над смертю.",
            isStrictOneDayFast = true
        ),
        FixedFeastEntry(
            julianMonth = 9, julianDay = 17,
            rank = FeastRank.MAJOR_SAINT,
            title = "Мучениць Віри, Надії, Любові та матері їх Софії",
            saints = listOf(
                "Свв. мучениць Віри, Надії, Любові та Софії (137)"
            ),
            readingRef = "2 Кор. 6:1–10; Лк. 7:36–50",
            reflection = "Мудра мати виховала доньок у трьох головних християнських чеснотах, які сильніші за будь-які випробування."
        ),
        FixedFeastEntry(
            julianMonth = 9, julianDay = 25,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Сергія Радонезького; прп. Євфросинії",
            saints = listOf(
                "Прп. Сергія, ігумена Радонезького",
                "Прп. Євфросинії Олександрійської"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Смиренна молитва і любов примиряють ворогуючих і творять духовну єдність."
        ),
        FixedFeastEntry(
            julianMonth = 9, julianDay = 26,
            rank = FeastRank.GREAT,
            title = "Преставлення ап. і єванг. Іоанна Богослова",
            saints = listOf(
                "Ап. і єванг. Іоанна Богослова (початок II ст.)"
            ),
            readingRef = "1 Ін. 4:12–19; Ін. 19:25–27, 21:24–25",
            reflection = "«Діти, любіть один одного», — головний заповіт апостола Іоанна Богослова."
        ),
        FixedFeastEntry(
            julianMonth = 9, julianDay = 28,
            rank = FeastRank.MAJOR_SAINT,
            title = "Собор прпп. отців Києво-Печерських, у Ближніх печерах",
            saints = listOf(
                "Собор преподобних отців Ближніх (Антонієвих) печер",
                "Прп. Харитона Сповідника"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Молитовна пам'ять святих подвижників Ближніх печер Києво-Печерської лаври."
        ),

        // ==================== ЖОВТЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 10, julianDay = 1,
            rank = FeastRank.GREAT,
            title = "Покрова Пресвятої Владичиці нашої Богородиці",
            saints = listOf(
                "Покрова Пресвятої Богородиці",
                "Ап. Ананії",
                "Прп. Романа Солодкоспівця"
            ),
            readingRef = "Євр. 9:1–7; Лк. 10:38–42, 11:27–28",
            reflection = "Особливо шановане в Україні свято материнського заступництва Божої Матері, Яка простягає Свій чесний омофор над усіма, хто просить Її допомоги.",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 10, julianDay = 14,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Параскеви Сербської; мчч. Назарія, Гервасія",
            saints = listOf(
                "Прп. Параскеви Сербської (XI ст.)",
                "Прп. Миколи Святоші, князя Чернігівського, Печерського"
            ),
            readingRef = "2 Кор. 6:1–10; Мф. 25:1–13",
            reflection = "Князь Микола Святоша залишив княжий престол заради простого послуху братаря в Києво-Печерській лаврі."
        ),
        FixedFeastEntry(
            julianMonth = 10, julianDay = 18,
            rank = FeastRank.MAJOR_SAINT,
            title = "Апостола і євангеліста Луки",
            saints = listOf(
                "Ап. і єванг. Луки — першого іконописця",
                "Прп. Юліана"
            ),
            readingRef = "Кол. 4:5–9, 14, 18; Лк. 10:16–21",
            reflection = "Святий Лука — лікар, сподвижник апостола Павла, автор Євангелія і Діянь святих апостолів."
        ),
        FixedFeastEntry(
            julianMonth = 10, julianDay = 26,
            rank = FeastRank.GREAT,
            title = "Великомученика Димитрія Солунського",
            saints = listOf(
                "Вмч. Димитрія Солунського (бл. 306)",
                "Прп. Афанасія Мідікійського"
            ),
            readingRef = "2 Тим. 2:1–10; Ін. 15:17–16:2",
            reflection = "Небесний покровитель Солуня, незламний свідок Христової правди."
        ),
        FixedFeastEntry(
            julianMonth = 10, julianDay = 27,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Нестора Літописця, Печерського",
            saints = listOf(
                "Прп. Нестора Літописця, Печерського (бл. 1114)",
                "Мц. Капітоліни"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Преподобний Нестор зберіг для нащадків духовну та історичну пам'ять Київської Русі у «Повісті временних літ»."
        ),

        // ==================== ЛИСТОПАД ====================
        FixedFeastEntry(
            julianMonth = 11, julianDay = 1,
            rank = FeastRank.MAJOR_SAINT,
            title = "Безсрібників і чудотворців Косми і Даміана",
            saints = listOf(
                "Безсрібників Косми і Даміана Асійських",
                "Прп. Іакова Железноборовського"
            ),
            readingRef = "1 Кор. 12:27–13:8; Мф. 10:1, 5–8",
            reflection = "«Даром отримали — даром давайте»: святі брати-цілителі лікували недужих любов'ю і безкорисливістю."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 8,
            rank = FeastRank.GREAT,
            title = "Собор Архістратига Михаїла та інших Небесних Сил безплотних",
            saints = listOf(
                "Собор Архістратига Божого Михаїла та всіх безплотних небесних воїнств"
            ),
            readingRef = "Євр. 2:2–10; Лк. 10:16–21",
            reflection = "Прославлення ангельських чинів на чолі з Архістратигом Михаїлом — захисником віри та покровителем Києва."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 12,
            rank = FeastRank.MAJOR_SAINT,
            title = "Свт. Іоанна Милостивого; прп. Ніла Постника",
            saints = listOf(
                "Свт. Іоанна Милостивого, патріарха Олександрійського",
                "Прп. Ніла Постника"
            ),
            readingRef = "2 Кор. 9:6–11; Мф. 5:42–48",
            reflection = "Патріарх Іоанн називав бідних «своїми владиками», бо через милостиню ми служимо Самому Христу."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 13,
            rank = FeastRank.GREAT,
            title = "Свт. Іоанна Золотоустого, архієпископа Константинопольського",
            saints = listOf(
                "Свт. Іоанна Золотоустого, архієп. Константинопольського (407)",
                "Мчч. Антоніна, Никифора і Германа",
                "Мц. Манефи"
            ),
            readingRef = "Євр. 7:26–8:2; Ін. 10:9–16",
            reflection = "Великий учитель покаяння, любові до Святого Письма і милосердя; укладач Божественної літургії, що звершується щонеділі."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 14,
            rank = FeastRank.MAJOR_SAINT,
            title = "Апостола Пилипа (Заговини на Різдвяний піст)",
            saints = listOf(
                "Ап. Пилипа з дванадцяти",
                "Свт. Григорія Палами, архієп. Фессалонітського",
                "Заговини на Різдвяний (Пилипів) піст"
            ),
            readingRef = "1 Кор. 4:9–16; Ін. 1:43–51",
            reflection = "Переддень Різдвяного посту — останній день перед Пилипівкою для мирного входження у благодатний подвиг."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 15,
            rank = FeastRank.ORDINARY,
            title = "Початок Різдвяного посту (Пилипівки)",
            saints = listOf(
                "Мчч. і сповв. Гурія, Самона і Авіва",
                "Прп. Паїсія Величковського"
            ),
            readingRef = "2 Тим. 1:1–2, 8–18; Лк. 16:15–18",
            reflection = "Розпочинається 40-денний Різдвяний піст, що веде нас до світлої радості Вифлеємської ночі."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 16,
            rank = FeastRank.MAJOR_SAINT,
            title = "Апостола і євангеліста Матфея",
            saints = listOf(
                "Ап. і єванг. Матфея (60)",
                "Прав. Фулвіана, князя Ефіопського"
            ),
            readingRef = "1 Кор. 4:9–16; Мф. 9:9–13",
            reflection = "Колишній митар, який на один поклик Христа залишив усе і написав перше Євангеліє."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 21,
            rank = FeastRank.TWELVE_GREAT,
            title = "Введення в храм Пресвятої Богородиці",
            saints = listOf(
                "Введення в храм Пресвятої Владичиці нашої Богородиці і Приснодіви Марії"
            ),
            readingRef = "Євр. 9:1–7; Лк. 10:38–42, 11:27–28",
            reflection = "Трьохрічна Діва Марія входить у Святая Святих Єрусалимського храму. У трапезі Різдвяного посту дозволяється риба.",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 24,
            rank = FeastRank.MAJOR_SAINT,
            title = "Великомучениці Катерини; вмч. Меркурія",
            saints = listOf(
                "Вмц. Катерини Олександрійської (305–313)",
                "Вмч. Меркурія"
            ),
            readingRef = "Гал. 3:23–29; Мк. 5:24–34",
            reflection = "Мудра і високоосвічена свята Катерина віддала всі свої таланти на свідчення про Істинного Бога."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 27,
            rank = FeastRank.MAJOR_SAINT,
            title = "Ікони Божої Матері «Знамення»; вмч. Якова Персянина",
            saints = listOf(
                "Ікони Божої Матері «Знамення»",
                "Вмч. Якова Персянина"
            ),
            readingRef = "Євр. 9:1–7; Лк. 10:38–42",
            reflection = "Молитовна пам'ять про заступництво Богородиці перед Її чудотворним образом «Знамення»."
        ),
        FixedFeastEntry(
            julianMonth = 11, julianDay = 30,
            rank = FeastRank.GREAT,
            title = "Апостола Андрія Первозванного",
            saints = listOf(
                "Ап. Андрія Первозванного (62)",
                "Свт. Фрументія, архієп. Індійського"
            ),
            readingRef = "1 Кор. 4:9–16; Ін. 1:35–51",
            reflection = "Первозванний апостол благословив Київські гори, пророкуючи, що на них засяє благодать Божа і постане велике місто."
        ),

        // ==================== ГРУДЕНЬ ====================
        FixedFeastEntry(
            julianMonth = 12, julianDay = 4,
            rank = FeastRank.GREAT,
            title = "Великомучениці Варвари; прп. Іоанна Дамаскіна",
            saints = listOf(
                "Вмц. Варвари Іліопольської (бл. 306)",
                "Прп. Іоанна Дамаскіна"
            ),
            readingRef = "Гал. 3:23–29; Мк. 5:24–34",
            reflection = "Мощі святої великомучениці Варвари спочивають у Києві. До неї особливо моляться про мирну християнську кончину й Причастя."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 5,
            rank = FeastRank.MAJOR_SAINT,
            title = "Прп. Сави Освяченого",
            saints = listOf(
                "Прп. Сави Освяченого (532)",
                "Прп. Нектарія Печерського"
            ),
            readingRef = "Гал. 5:22–6:2; Лк. 6:17–23",
            reflection = "Укладач Єрусалимського богослужбового уставу та засновник Великої лаври в Палестині."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 6,
            rank = FeastRank.GREAT,
            title = "Святителя Миколая, архієпископа Мир Лікійських, чудотворця",
            saints = listOf(
                "Свт. Миколая, архієп. Мир Лікійських, чудотворця (бл. 345)"
            ),
            readingRef = "Євр. 13:17–21; Лк. 6:17–23",
            reflection = "«Правило віри і образ лагідності» — найулюбленіший святий, швидкий помічник у скорботах і покровитель милосердя.",
            allowsFishInFast = true
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 9,
            rank = FeastRank.MAJOR_SAINT,
            title = "Зачаття праведною Анною Пресвятої Богородиці",
            saints = listOf(
                "Зачаття прав. Анною Пресвятої Богородиці",
                "Прор. Анни, матері прор. Самуїла"
            ),
            readingRef = "Гал. 4:22–31; Лк. 8:16–21",
            reflection = "Через багаторічну молитву праведних Іоакима й Анни починається земна історія Пресвятої Діви."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 12,
            rank = FeastRank.MAJOR_SAINT,
            title = "Свт. Спиридона, єпископа Триміфунтського, чудотворця",
            saints = listOf(
                "Свт. Спиридона Триміфунтського (бл. 348)"
            ),
            readingRef = "Еф. 5:8–19; Ін. 10:9–16",
            reflection = "Простий серцем пастир, учасник I Вселенського Собору, який допомагає людям у життєвих і матеріальних потребах."
        ),
        FixedFeastEntry(
            julianMonth = 12, julianDay = 18,
            rank = FeastRank.MAJOR_SAINT,
            title = "Свт. Петра Могили, митрополита Київського і Галицького",
            saints = listOf(
                "Свт. Петра Могили, митр. Київського (1647)",
                "Мч. Севастіана і дружини його"
            ),
            readingRef = "Євр. 7:26–8:2; Ін. 10:9–16",
            reflection = "Святитель Петро Могила — великий ревнитель православної освіти, духовного відродження Києва і Києво-Могилянської колегії."
        )
    )
}
