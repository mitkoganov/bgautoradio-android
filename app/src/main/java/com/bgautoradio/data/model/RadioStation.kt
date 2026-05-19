package com.bgautoradio.data.model

data class RadioStation(
    val id: String,
    val name: String,
    val streamUrl: String,
    val logoUrl: String?        = null,
    val country: String         = "Bulgaria",
    val city: String?           = null,
    val category: RadioCategory = RadioCategory.OTHER,
    val tags: List<String>      = emptyList(),
    val websiteUrl: String?     = null,
    val bitrate: Int?           = null,
    val codec: String?          = null,
    val isFavorite: Boolean     = false,
    val isVerified: Boolean     = false,
    val lastChecked: String?    = null,
    val source: String?         = null,
    val sortOrder: Int          = 999
) {
    val hasValidStream: Boolean get() = streamUrl.isNotBlank()
    val displayCity: String     get() = city ?: "България"
}

enum class RadioCategory(
    val key: String,
    val displayName: String,
    val emoji: String
) {
    NATIONAL  ("national",   "Национален",       "🇧🇬"),
    NEWS      ("news",       "Новини",            "📰"),
    POP       ("pop",        "Поп",               "🎵"),
    ROCK      ("rock",       "Рок",               "🎸"),
    DANCE     ("dance",      "Денс/Електронна",   "🎧"),
    FOLK      ("folk",       "Народна музика",    "🪗"),
    FOLK_POP  ("folk_pop",   "Чалга/Поп-фолк",   "🎤"),
    JAZZ      ("jazz",       "Джаз",              "🎷"),
    CLASSICAL ("classical",  "Класическа",        "🎻"),
    REGIONAL  ("regional",   "Регионален",        "📍"),
    TALK      ("talk",       "Говорен",           "🗣️"),
    RETRO     ("retro",      "Ретро",             "📻"),
    HIPHOP    ("hiphop",     "Хип-Хоп/R&B",      "🎤"),
    URBAN     ("urban",      "Урбан",             "🏙️"),
    ONLINE    ("online",     "Онлайн-only",       "🌐"),
    OTHER     ("other",      "Друго",             "🎶");

    companion object {
        fun fromKey(key: String): RadioCategory =
            entries.firstOrNull { it.key == key } ?: OTHER
    }
}
