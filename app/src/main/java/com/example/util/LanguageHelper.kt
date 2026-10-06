package com.example.util

data class LanguageOption(
    val code: String,
    val nameNative: String,
    val nameAr: String,
    val nameEn: String,
    val isRtl: Boolean = false,
    val flagEmoji: String,
    val isAvailable: Boolean = true
)

object LanguageRepository {

    val supportedLanguages: List<LanguageOption> = listOf(
        // Arabic & Middle East
        LanguageOption("ar", "العربية", "العربية", "Arabic", isRtl = true, flagEmoji = "🇸🇦"),
        LanguageOption("en", "English", "الإنجليزية", "English", isRtl = false, flagEmoji = "🇺🇸"),
        LanguageOption("es", "Español", "الإسبانية", "Spanish", isRtl = false, flagEmoji = "🇪🇸"),
        LanguageOption("fr", "Français", "الفرنسية", "French", isRtl = false, flagEmoji = "🇫🇷"),
        LanguageOption("de", "Deutsch", "الألمانية", "German", isRtl = false, flagEmoji = "🇩🇪"),
        LanguageOption("it", "Italiano", "الإيطالية", "Italian", isRtl = false, flagEmoji = "🇮🇹"),
        LanguageOption("pt", "Português", "البرتغالية", "Portuguese", isRtl = false, flagEmoji = "🇵🇹"),
        LanguageOption("ru", "Русский", "الروسية", "Russian", isRtl = false, flagEmoji = "🇷🇺"),
        LanguageOption("zh", "简体中文", "الصينية (المبسطة)", "Chinese (Simplified)", isRtl = false, flagEmoji = "🇨🇳"),
        LanguageOption("zh-TW", "繁體中文", "الصينية (التقليدية)", "Chinese (Traditional)", isRtl = false, flagEmoji = "🇹🇼"),
        LanguageOption("ja", "日本語", "اليابانية", "Japanese", isRtl = false, flagEmoji = "🇯🇵"),
        LanguageOption("ko", "한국어", "الكورية", "Korean", isRtl = false, flagEmoji = "🇰🇷"),
        LanguageOption("tr", "Türkçe", "التركية", "Turkish", isRtl = false, flagEmoji = "🇹🇷"),
        LanguageOption("ur", "اردو", "الأوردية", "Urdu", isRtl = true, flagEmoji = "🇵🇰"),
        LanguageOption("fa", "فارسی", "الفارسية", "Persian", isRtl = true, flagEmoji = "🇮🇷"),
        LanguageOption("hi", "हिन्दी", "الهندية", "Hindi", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("bn", "বাংলা", "البنغالية", "Bengali", isRtl = false, flagEmoji = "🇧🇩"),
        LanguageOption("id", "Bahasa Indonesia", "الإندونيسية", "Indonesian", isRtl = false, flagEmoji = "🇮🇩"),
        LanguageOption("ms", "Bahasa Melayu", "الماليزية", "Malay", isRtl = false, flagEmoji = "🇲🇾"),
        LanguageOption("nl", "Nederlands", "الهولندية", "Dutch", isRtl = false, flagEmoji = "🇳🇱"),
        LanguageOption("pl", "Polski", "البولندية", "Polish", isRtl = false, flagEmoji = "🇵🇱"),
        LanguageOption("uk", "Українська", "الأوكرانية", "Ukrainian", isRtl = false, flagEmoji = "🇺🇦"),
        LanguageOption("vi", "Tiếng Việt", "الفيتنامية", "Vietnamese", isRtl = false, flagEmoji = "🇻🇳"),
        LanguageOption("th", "ไทย", "التايلاندية", "Thai", isRtl = false, flagEmoji = "🇹🇭"),
        LanguageOption("sv", "Svenska", "السويدية", "Swedish", isRtl = false, flagEmoji = "🇸🇪"),
        LanguageOption("no", "Norsk", "النرويجية", "Norwegian", isRtl = false, flagEmoji = "🇳🇴"),
        LanguageOption("da", "Dansk", "الدنماركية", "Danish", isRtl = false, flagEmoji = "🇩🇰"),
        LanguageOption("fi", "Suomi", "الفنلندية", "Finnish", isRtl = false, flagEmoji = "🇫🇮"),
        LanguageOption("el", "Ελληνικά", "اليونانية", "Greek", isRtl = false, flagEmoji = "🇬🇷"),
        LanguageOption("he", "עברית", "العبرية", "Hebrew", isRtl = true, flagEmoji = "🇮🇱"),
        LanguageOption("cs", "Čeština", "التشيكية", "Czech", isRtl = false, flagEmoji = "🇨🇿"),
        LanguageOption("hu", "Magyar", "الهنغارية", "Hungarian", isRtl = false, flagEmoji = "🇭🇺"),
        LanguageOption("ro", "Română", "الرومانية", "Romanian", isRtl = false, flagEmoji = "🇷🇴"),
        LanguageOption("bg", "Български", "البلغارية", "Bulgarian", isRtl = false, flagEmoji = "🇧🇬"),
        LanguageOption("sk", "Slovenčina", "السلوفاكية", "Slovak", isRtl = false, flagEmoji = "🇸🇰"),
        LanguageOption("hr", "Hrvatski", "الكرواتية", "Croatian", isRtl = false, flagEmoji = "🇭🇷"),
        LanguageOption("sr", "Српски", "الصربية", "Serbian", isRtl = false, flagEmoji = "🇷🇸"),
        LanguageOption("bs", "Bosanski", "البوسنية", "Bosnian", isRtl = false, flagEmoji = "🇧🇦"),
        LanguageOption("sq", "Shqip", "الألبانية", "Albanian", isRtl = false, flagEmoji = "🇦🇱"),
        LanguageOption("sl", "Slovenščina", "السلوفينية", "Slovenian", isRtl = false, flagEmoji = "🇸🇮"),
        LanguageOption("lt", "Lietuvių", "الليتوانية", "Lithuanian", isRtl = false, flagEmoji = "🇱🇹"),
        LanguageOption("lv", "Latviešu", "اللاتفية", "Latvian", isRtl = false, flagEmoji = "🇱🇻"),
        LanguageOption("et", "Eesti", "الإستونية", "Estonian", isRtl = false, flagEmoji = "🇪🇪"),
        LanguageOption("tl", "Filipino", "الفلبينية", "Filipino / Tagalog", isRtl = false, flagEmoji = "🇵🇭"),
        LanguageOption("sw", "Kiswahili", "السواحيلية", "Swahili", isRtl = false, flagEmoji = "🇰🇪"),
        LanguageOption("am", "አማርኛ", "الأمهرية", "Amharic", isRtl = false, flagEmoji = "🇪🇹"),
        LanguageOption("so", "Soomaali", "الصومالية", "Somali", isRtl = false, flagEmoji = "🇸🇴"),
        LanguageOption("ha", "Hausa", "الهوسا", "Hausa", isRtl = false, flagEmoji = "🇳🇬"),
        LanguageOption("yo", "Yorùbá", "اليوروبا", "Yoruba", isRtl = false, flagEmoji = "🇳🇬"),
        LanguageOption("ig", "Asụsụ Igbo", "الإيغبو", "Igbo", isRtl = false, flagEmoji = "🇳🇬"),
        LanguageOption("zu", "isiZulu", "الزولو", "Zulu", isRtl = false, flagEmoji = "🇿🇦"),
        LanguageOption("af", "Afrikaans", "الأفريقانية", "Afrikaans", isRtl = false, flagEmoji = "🇿🇦"),
        LanguageOption("ta", "தமிழ்", "التاميلية", "Tamil", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("te", "తెలుగు", "التيلوغوية", "Telugu", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("mr", "मराठी", "الماراثية", "Marathi", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("gu", "ગુજરાતી", "الغوجاراتية", "Gujarati", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("kn", "ಕನ್ನಡ", "الكانادا", "Kannada", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("ml", "മലയാളം", "المالايالامية", "Malayalam", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("pa", "ਪੰਜਾਬੀ", "البنجابية", "Punjabi", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("ne", "नेपाली", "النيبالية", "Nepali", isRtl = false, flagEmoji = "🇳🇵"),
        LanguageOption("si", "සිංහල", "السنهالية", "Sinhala", isRtl = false, flagEmoji = "🇱🇰"),
        LanguageOption("my", "မြန်မာဘာသာ", "البورمية", "Burmese", isRtl = false, flagEmoji = "🇲🇲"),
        LanguageOption("km", "ភាសាខ្មែរ", "الخميرية", "Khmer", isRtl = false, flagEmoji = "🇰🇭"),
        LanguageOption("lo", "ພາສາລາວ", "اللاوية", "Lao", isRtl = false, flagEmoji = "🇱🇦"),
        LanguageOption("ka", "ქართული", "الجورجية", "Georgian", isRtl = false, flagEmoji = "🇬🇪"),
        LanguageOption("hy", "Հայերեն", "الأرمنية", "Armenian", isRtl = false, flagEmoji = "🇦🇲"),
        LanguageOption("az", "Azərbaycan dili", "الأذربيجانية", "Azerbaijani", isRtl = false, flagEmoji = "🇦🇿"),
        LanguageOption("kk", "Қазақ тілі", "الكازاخستانية", "Kazakh", isRtl = false, flagEmoji = "🇰🇿"),
        LanguageOption("uz", "Oʻzbekcha", "الأوزبكية", "Uzbek", isRtl = false, flagEmoji = "🇺🇿"),
        LanguageOption("tk", "Türkmençe", "التركمانية", "Turkmen", isRtl = false, flagEmoji = "🇹🇲"),
        LanguageOption("ky", "Кыргызча", "القيرغيزية", "Kyrgyz", isRtl = false, flagEmoji = "🇰🇬"),
        LanguageOption("tg", "Тоҷикӣ", "الطاجيكية", "Tajik", isRtl = false, flagEmoji = "🇹🇯"),
        LanguageOption("ps", "پښتو", "البشتو", "Pashto", isRtl = true, flagEmoji = "🇦🇫"),
        LanguageOption("ku", "Kurdî / کوردی", "الكردية", "Kurdish", isRtl = true, flagEmoji = "🇮🇶"),
        LanguageOption("sd", "سنڌي", "السندية", "Sindhi", isRtl = true, flagEmoji = "🇵🇰"),
        LanguageOption("ug", "ئۇيغۇرچە", "الأويغورية", "Uyghur", isRtl = true, flagEmoji = "🇨🇳"),
        LanguageOption("mn", "Монгол хэл", "المنغولية", "Mongolian", isRtl = false, flagEmoji = "🇲🇳"),
        LanguageOption("is", "Íslenska", "الآيسلندية", "Icelandic", isRtl = false, flagEmoji = "🇮🇸"),
        LanguageOption("ga", "Gaeilge", "الأيرلندية", "Irish", isRtl = false, flagEmoji = "🇮🇪"),
        LanguageOption("cy", "Cymraeg", "الويلزية", "Welsh", isRtl = false, flagEmoji = "🇬🇧"),
        LanguageOption("eu", "Euskara", "الباسكية", "Basque", isRtl = false, flagEmoji = "🇪🇸"),
        LanguageOption("ca", "Català", "الكتالونية", "Catalan", isRtl = false, flagEmoji = "🇪🇸"),
        LanguageOption("gl", "Galego", "الجاليكية", "Galician", isRtl = false, flagEmoji = "🇪🇸"),
        LanguageOption("mt", "Malti", "المالطية", "Maltese", isRtl = false, flagEmoji = "🇲🇹"),
        LanguageOption("mk", "Македонски", "المقدونية", "Macedonian", isRtl = false, flagEmoji = "🇲🇰"),
        LanguageOption("be", "Беларуская", "البيلاروسية", "Belarusian", isRtl = false, flagEmoji = "🇧🇾"),
        LanguageOption("ceb", "Sinugboanon", "السيبوانية", "Cebuano", isRtl = false, flagEmoji = "🇵🇭"),
        LanguageOption("jv", "Basa Jawa", "الجاوية", "Javanese", isRtl = false, flagEmoji = "🇮🇩"),
        LanguageOption("su", "Basa Sunda", "السوندية", "Sundanese", isRtl = false, flagEmoji = "🇮🇩"),
        LanguageOption("mg", "Malagasy", "الملغاشية", "Malagasy", isRtl = false, flagEmoji = "🇲🇬"),
        LanguageOption("eo", "Esperanto", "الإسبرانتو", "Esperanto", isRtl = false, flagEmoji = "🌐"),
        LanguageOption("la", "Latina", "اللاتينية", "Latin", isRtl = false, flagEmoji = "🇻🇦"),
        LanguageOption("haw", "ʻŌlelo Hawaiʻi", "الهاوايية", "Hawaiian", isRtl = false, flagEmoji = "🇺🇸"),
        LanguageOption("mi", "Te Reo Māori", "الماورية", "Maori", isRtl = false, flagEmoji = "🇳🇿"),
        LanguageOption("sm", "Gagana Sāmoa", "الساموية", "Samoan", isRtl = false, flagEmoji = "🇼🇸"),
        LanguageOption("to", "Lea Fakatonga", "التونغية", "Tongan", isRtl = false, flagEmoji = "🇹🇴"),
        LanguageOption("fj", "Na Vosa Vakaviti", "الفيجية", "Fijian", isRtl = false, flagEmoji = "🇫🇯"),
        LanguageOption("rw", "Ikinyarwanda", "الكينيارواندية", "Kinyarwanda", isRtl = false, flagEmoji = "🇷🇼"),
        LanguageOption("sn", "chiShona", "الشونا", "Shona", isRtl = false, flagEmoji = "🇿🇼"),
        LanguageOption("xh", "isiXhosa", "الخوسا", "Xhosa", isRtl = false, flagEmoji = "🇿🇦"),
        LanguageOption("st", "Sesotho", "السوتو", "Southern Sotho", isRtl = false, flagEmoji = "🇱🇸"),
        LanguageOption("tn", "Setswana", "التسوانية", "Tswana", isRtl = false, flagEmoji = "🇧🇼"),
        LanguageOption("ts", "Xitsonga", "التسونجا", "Tsonga", isRtl = false, flagEmoji = "🇿🇦"),
        LanguageOption("ss", "SiSwati", "السوازي", "Swati", isRtl = false, flagEmoji = "🇸🇿"),
        LanguageOption("ve", "Tshivenḓa", "الفيندا", "Venda", isRtl = false, flagEmoji = "🇿🇦"),
        LanguageOption("nr", "isiNdebele", "النديبيلية", "Southern Ndebele", isRtl = false, flagEmoji = "🇿🇦"),
        LanguageOption("ny", "ChiChewa", "الشيشيوا", "Chewa", isRtl = false, flagEmoji = "🇲🇼"),
        LanguageOption("lg", "Oluganda", "اللوغندا", "Ganda", isRtl = false, flagEmoji = "🇺🇬"),
        LanguageOption("wo", "Wolof", "الولوفية", "Wolof", isRtl = false, flagEmoji = "🇸🇳"),
        LanguageOption("ff", "Fulfulde / Pulaar", "الفولانية", "Fula", isRtl = false, flagEmoji = "🇸🇳"),
        LanguageOption("bm", "Bamanankan", "البمبرية", "Bambara", isRtl = false, flagEmoji = "🇲🇱"),
        LanguageOption("ti", "ትግርኛ", "التغرينية", "Tigrinya", isRtl = false, flagEmoji = "🇪🇷"),
        LanguageOption("om", "Afaan Oromoo", "الأورومية", "Oromo", isRtl = false, flagEmoji = "🇪🇹"),
        LanguageOption("as", "অসমীয়া", "الآسامية", "Assamese", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("or", "ଓଡ଼ିଆ", "الأودية", "Odia", isRtl = false, flagEmoji = "🇮🇳"),
        LanguageOption("bo", "བོད་སྐད་", "التبتية", "Tibetan", isRtl = false, flagEmoji = "🇨🇳"),
        LanguageOption("dz", "རྫོང་ཁ", "الدزونكية", "Dzongkha", isRtl = false, flagEmoji = "🇧🇹"),
        LanguageOption("dv", "ދިވެހި", "الديفيهية", "Divehi", isRtl = true, flagEmoji = "🇲🇻"),
        LanguageOption("kmr", "Kurmancî", "الكورمانجية", "Kurmanji", isRtl = false, flagEmoji = "🇹🇷"),
        LanguageOption("ckb", "سۆرانی", "السورانية", "Sorani", isRtl = true, flagEmoji = "🇮🇶"),
        LanguageOption("ber", "Tamaziɣt / ⵜⴰⵎⴰⵣⵉⵖⵜ", "الأمازيغية", "Berber / Tamazight", isRtl = false, flagEmoji = "🇲🇦"),
        LanguageOption("luo", "Dholuo", "اللوو", "Luo", isRtl = false, flagEmoji = "🇰🇪"),
        LanguageOption("kik", "Gĩkũyũ", "الكيكويو", "Kikuyu", isRtl = false, flagEmoji = "🇰🇪"),
        LanguageOption("ln", "Lingála", "اللينغالا", "Lingala", isRtl = false, flagEmoji = "🇨🇩"),
        LanguageOption("kg", "Kikongo", "الكونغو", "Kongo", isRtl = false, flagEmoji = "🇨🇩"),
        LanguageOption("lua", "Tshiluba", "التشيلوبا", "Luba-Kasai", isRtl = false, flagEmoji = "🇨🇩")
    )

    fun searchLanguages(query: String): List<LanguageOption> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return supportedLanguages
        return supportedLanguages.filter { lang ->
            lang.nameNative.contains(trimmed, ignoreCase = true) ||
            lang.nameAr.contains(trimmed, ignoreCase = true) ||
            lang.nameEn.contains(trimmed, ignoreCase = true) ||
            lang.code.equals(trimmed, ignoreCase = true)
        }
    }

    fun findByCodeOrName(codeOrName: String): LanguageOption? {
        val trimmed = codeOrName.trim()
        return supportedLanguages.firstOrNull {
            it.code.equals(trimmed, ignoreCase = true) ||
            it.nameNative.equals(trimmed, ignoreCase = true) ||
            it.nameAr.equals(trimmed, ignoreCase = true) ||
            it.nameEn.equals(trimmed, ignoreCase = true) ||
            trimmed.contains(it.nameNative)
        }
    }

    fun isRtlLanguage(languageNameOrCode: String): Boolean {
        val found = findByCodeOrName(languageNameOrCode)
        if (found != null) return found.isRtl
        val trimmed = languageNameOrCode.trim().lowercase()
        return trimmed.startsWith("ar") || trimmed.contains("عرب") ||
               trimmed.startsWith("ur") || trimmed.contains("اردو") ||
               trimmed.startsWith("fa") || trimmed.contains("فارس") ||
               trimmed.startsWith("he") || trimmed.contains("عبر") ||
               trimmed.startsWith("ps") || trimmed.startsWith("ku") ||
               trimmed.startsWith("sd") || trimmed.startsWith("ug")
    }

    fun getDisplayName(codeOrName: String): String {
        return findByCodeOrName(codeOrName)?.nameNative ?: "العربية"
    }

    fun getFlagEmoji(codeOrName: String): String {
        return findByCodeOrName(codeOrName)?.flagEmoji ?: "🇸🇦"
    }
}
