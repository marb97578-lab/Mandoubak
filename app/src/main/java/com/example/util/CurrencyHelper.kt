package com.example.util

import com.example.data.entity.CurrencySettingEntity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class SymbolPlacement(val titleAr: String, val titleEn: String) {
    AFTER_AMOUNT("بعد المبلغ (مثال: 125.50 ﷼)", "After Amount (e.g. 125.50 YER)"),
    BEFORE_AMOUNT("قبل المبلغ (مثال: ﷼ 125.50)", "Before Amount (e.g. YER 125.50)")
}

data class CurrencyItem(
    val code: String,
    val defaultSymbol: String,
    val nameAr: String,
    val nameEn: String,
    val countryAr: String,
    val flagEmoji: String,
    val regionAr: String = "العملات العالمية",
    val countryEn: String = ""
) {
    val displayCountry: String
        get() = if (countryEn.isNotBlank()) "$countryAr — $countryEn" else countryAr

    val displayName: String
        get() = "$nameAr ($nameEn)"
}

data class CurrencyConfig(
    val selectedCode: String = "YER",
    val customSymbol: String = "﷼",
    val symbolPlacement: SymbolPlacement = SymbolPlacement.AFTER_AMOUNT,
    val decimalPlaces: Int = 2,
    val useGrouping: Boolean = true
) {
    fun toEntity(): CurrencySettingEntity {
        val item = CurrencyRepository.findByCode(selectedCode)
        return CurrencySettingEntity(
            id = 1,
            selectedCode = selectedCode,
            customSymbol = customSymbol,
            currencyNameAr = item?.nameAr ?: "ريال يمني",
            currencyNameEn = item?.nameEn ?: "Yemeni Rial",
            countryNameAr = item?.countryAr ?: "اليمن",
            symbolPlacement = symbolPlacement.name,
            decimalPlaces = decimalPlaces,
            useGrouping = useGrouping,
            updatedAtMillis = System.currentTimeMillis()
        )
    }

    companion object {
        fun fromEntity(entity: CurrencySettingEntity): CurrencyConfig {
            val placement = try {
                SymbolPlacement.valueOf(entity.symbolPlacement)
            } catch (_: Exception) {
                SymbolPlacement.AFTER_AMOUNT
            }
            return CurrencyConfig(
                selectedCode = entity.selectedCode,
                customSymbol = entity.customSymbol,
                symbolPlacement = placement,
                decimalPlaces = entity.decimalPlaces,
                useGrouping = entity.useGrouping
            )
        }
    }
}

object CurrencyRepository {

    /**
     * Complete international official currencies list.
     * Starts with: Yemen — Yemeni Rial (YER) — ﷼
     */
    val supportedCurrencies: List<CurrencyItem> = listOf(
        // 1. Yemen — Yemeni Rial (Always at the top)
        CurrencyItem("YER", "﷼", "ريال يمني", "Yemeni Rial", "اليمن", "🇾🇪", "الدول العربية", "Yemen"),

        // Arab World Currencies (الوطن العربي والخليج)
        CurrencyItem("SAR", "ر.س", "ريال سعودي", "Saudi Riyal", "المملكة العربية السعودية", "🇸🇦", "الدول العربية", "Saudi Arabia"),
        CurrencyItem("AED", "د.إ", "درهم إماراتي", "UAE Dirham", "الإمارات العربية المتحدة", "🇦🇪", "الدول العربية", "United Arab Emirates"),
        CurrencyItem("KWD", "د.ك", "دينار كويتي", "Kuwaiti Dinar", "الكويت", "🇰🇼", "الدول العربية", "Kuwait"),
        CurrencyItem("QAR", "ر.ق", "ريال قطري", "Qatari Riyal", "قطر", "🇶🇦", "الدول العربية", "Qatar"),
        CurrencyItem("BHD", "د.ب", "دينار بحريني", "Bahraini Dinar", "البحرين", "🇧🇭", "الدول العربية", "Bahrain"),
        CurrencyItem("OMR", "ر.ع", "ريال عماني", "Omani Rial", "سلطنة عمان", "🇴🇲", "الدول العربية", "Oman"),
        CurrencyItem("EGP", "ج.م", "جنيه مصري", "Egyptian Pound", "مصر", "🇪🇬", "الدول العربية", "Egypt"),
        CurrencyItem("JOD", "د.أ", "دينار أردني", "Jordanian Dinar", "الأردن", "🇯🇴", "الدول العربية", "Jordan"),
        CurrencyItem("IQD", "د.ع", "دينار عراقي", "Iraqi Dinar", "العراق", "🇮🇶", "الدول العربية", "Iraq"),
        CurrencyItem("SYP", "ل.س", "ليرة سورية", "Syrian Pound", "سوريا", "🇸🇾", "الدول العربية", "Syria"),
        CurrencyItem("LBP", "ل.ل", "ليرة لبنانية", "Lebanese Pound", "لبنان", "🇱🇧", "الدول العربية", "Lebanon"),
        CurrencyItem("LYD", "د.ل", "دينار ليبي", "Libyan Dinar", "ليبيا", "🇱🇾", "الدول العربية", "Libya"),
        CurrencyItem("SDG", "ج.س", "جنيه سوداني", "Sudanese Pound", "السودان", "🇸🇩", "الدول العربية", "Sudan"),
        CurrencyItem("MAD", "د.م", "درهم مغربي", "Moroccan Dirham", "المغرب", "🇲🇦", "الدول العربية", "Morocco"),
        CurrencyItem("DZD", "د.ج", "دينار جزائري", "Algerian Dinar", "الجزائر", "🇩🇿", "الدول العربية", "Algeria"),
        CurrencyItem("TND", "د.ت", "دينار تونسي", "Tunisian Dinar", "تونس", "🇹🇳", "الدول العربية", "Tunisia"),
        CurrencyItem("MRU", "أ.م", "أوقية موريتانية", "Mauritanian Ouguiya", "موريتانيا", "🇲🇷", "الدول العربية", "Mauritania"),
        CurrencyItem("SOS", "ش.ص", "شلن صومالي", "Somali Shilling", "الصومال", "🇸🇴", "الدول العربية", "Somalia"),
        CurrencyItem("DJF", "ف.ج", "فرنك جيبوتي", "Djiboutian Franc", "جيبوتي", "🇩🇯", "الدول العربية", "Djibouti"),
        CurrencyItem("KMF", "ف.ق", "فرنك قمري", "Comorian Franc", "جزر القمر", "🇰🇲", "الدول العربية", "Comoros"),
        CurrencyItem("ILS", "₪", "شيكل جديد", "New Israeli Shekel", "فلسطين والمنطقة", "🇵🇸", "الدول العربية", "Palestine"),

        // Major Global Currencies (العملات العالمية الكبرى)
        CurrencyItem("USD", "$", "دولار أمريكي", "US Dollar", "الولايات المتحدة", "🇺🇸", "العملات العالمية", "United States"),
        CurrencyItem("EUR", "€", "يورو أوروبي", "Euro", "الاتحاد الأوروبي", "🇪🇺", "العملات العالمية", "European Union"),
        CurrencyItem("GBP", "£", "جنيه إسترليني", "British Pound", "المملكة المتحدة", "🇬🇧", "العملات العالمية", "United Kingdom"),
        CurrencyItem("JPY", "¥", "ين ياباني", "Japanese Yen", "اليابان", "🇯🇵", "العملات العالمية", "Japan"),
        CurrencyItem("CHF", "CHF", "فرنك سويسري", "Swiss Franc", "سويسرا", "🇨🇭", "العملات العالمية", "Switzerland"),
        CurrencyItem("CAD", "C$", "دولار كندي", "Canadian Dollar", "كندا", "🇨🇦", "العملات العالمية", "Canada"),
        CurrencyItem("AUD", "A$", "دولار أسترالي", "Australian Dollar", "أستراليا", "🇦🇺", "العملات العالمية", "Australia"),
        CurrencyItem("CNY", "¥", "يوان صيني", "Chinese Yuan", "الصين", "🇨🇳", "العملات العالمية", "China"),

        // Asia & Middle East (آسيا والشرق الأوسط)
        CurrencyItem("TRY", "₺", "ليرة تركية", "Turkish Lira", "تركيا", "🇹🇷", "آسيا", "Turkey"),
        CurrencyItem("INR", "₹", "روبية هندية", "Indian Rupee", "الهند", "🇮🇳", "آسيا", "India"),
        CurrencyItem("KRW", "₩", "وون كوري جنوبي", "South Korean Won", "كوريا الجنوبية", "🇰🇷", "آسيا", "South Korea"),
        CurrencyItem("SGD", "S$", "دولار سنغافوري", "Singapore Dollar", "سنغافورة", "🇸🇬", "آسيا", "Singapore"),
        CurrencyItem("MYR", "RM", "رينغيت ماليزي", "Malaysian Ringgit", "ماليزيا", "🇲🇾", "آسيا", "Malaysia"),
        CurrencyItem("IDR", "Rp", "روبية إندونيسية", "Indonesian Rupiah", "إندونيسيا", "🇮🇩", "آسيا", "Indonesia"),
        CurrencyItem("THB", "฿", "بات تايلاندي", "Thai Baht", "تايلاند", "🇹🇭", "آسيا", "Thailand"),
        CurrencyItem("PHP", "₱", "بيزو فلبيني", "Philippine Peso", "الفلبين", "🇵🇭", "آسيا", "Philippines"),
        CurrencyItem("VND", "₫", "دونغ فيتنامي", "Vietnamese Dong", "فيتنام", "🇻🇳", "آسيا", "Vietnam"),
        CurrencyItem("PKR", "₨", "روبية باكستانية", "Pakistani Rupee", "باكستان", "🇵🇰", "آسيا", "Pakistan"),
        CurrencyItem("BDT", "৳", "تاكا بنغلاديشية", "Bangladeshi Taka", "بنغلاديش", "🇧🇩", "آسيا", "Bangladesh"),
        CurrencyItem("LKR", "Rs", "روبية سريلانكية", "Sri Lankan Rupee", "سريلانكا", "🇱🇰", "آسيا", "Sri Lanka"),
        CurrencyItem("NPR", "Rs", "روبية نيبالية", "Nepalese Rupee", "نيبال", "🇳🇵", "آسيا", "Nepal"),
        CurrencyItem("AFN", "؋", "أفغاني", "Afghan Afghani", "أفغانستان", "🇦🇫", "آسيا", "Afghanistan"),
        CurrencyItem("IRR", "﷼", "ريال إيراني", "Iranian Rial", "إيران", "🇮🇷", "آسيا", "Iran"),
        CurrencyItem("KZT", "₸", "تينغ كازاخستاني", "Kazakhstani Tenge", "كازاخستان", "🇰🇿", "آسيا", "Kazakhstan"),
        CurrencyItem("UZS", "so'm", "سوم أوزبكستاني", "Uzbekistani Som", "أوزبكستان", "🇺🇿", "آسيا", "Uzbekistan"),
        CurrencyItem("TMT", "T", "مانات تركمانستاني", "Turkmenistani Manat", "تركمانستان", "🇹🇲", "آسيا", "Turkmenistan"),
        CurrencyItem("KGS", "с", "سوم قيرغيزستاني", "Kyrgyzstani Som", "قيرغيزستان", "🇰🇬", "آسيا", "Kyrgyzstan"),
        CurrencyItem("TJS", "SM", "ساماني طاجيكي", "Tajikistani Somoni", "طاجيكستان", "🇹🇯", "آسيا", "Tajikistan"),
        CurrencyItem("MNT", "₮", "توغروغ منغولي", "Mongolian Tugrik", "منغوليا", "🇲🇳", "آسيا", "Mongolia"),
        CurrencyItem("MMK", "K", "كيات ميانماري", "Myanmar Kyat", "ميانمار", "🇲🇲", "آسيا", "Myanmar"),
        CurrencyItem("KHR", "៛", "ريال كمبودي", "Cambodian Riel", "كمبوديا", "🇰🇭", "آسيا", "Cambodia"),
        CurrencyItem("LAK", "₭", "كيب لاوسي", "Lao Kip", "لاوس", "🇱🇦", "آسيا", "Laos"),
        CurrencyItem("BND", "B$", "دولار بروني", "Brunei Dollar", "بروناي", "🇧🇳", "آسيا", "Brunei"),
        CurrencyItem("MVR", "Rf", "روفيا مالديفية", "Maldivian Rufiyaa", "المالديف", "🇲🇻", "آسيا", "Maldives"),
        CurrencyItem("HKD", "HK$", "دولار هونغ كونغ", "Hong Kong Dollar", "هونغ كونغ", "🇭🇰", "آسيا", "Hong Kong"),
        CurrencyItem("TWD", "NT$", "دولار تايواني جديد", "New Taiwan Dollar", "تايوان", "🇹🇼", "آسيا", "Taiwan"),

        // Europe (أوروبا)
        CurrencyItem("RUB", "₽", "روبل روسي", "Russian Ruble", "روسيا", "🇷🇺", "أوروبا", "Russia"),
        CurrencyItem("SEK", "kr", "كرونة سويدية", "Swedish Krona", "السويد", "🇸🇪", "أوروبا", "Sweden"),
        CurrencyItem("NOK", "kr", "كرونة نرويجية", "Norwegian Krone", "النرويج", "🇳🇴", "أوروبا", "Norway"),
        CurrencyItem("DKK", "kr", "كرونة دنماركية", "Danish Krone", "الدنمارك", "🇩🇰", "أوروبا", "Denmark"),
        CurrencyItem("PLN", "zł", "زلوتي بولندي", "Polish Zloty", "بولندا", "🇵🇱", "أوروبا", "Poland"),
        CurrencyItem("CZK", "Kč", "كرونة تشيكية", "Czech Koruna", "التشيك", "🇨🇿", "أوروبا", "Czech Republic"),
        CurrencyItem("HUF", "Ft", "فورنت مجري", "Hungarian Forint", "المجر", "🇭🇺", "أوروبا", "Hungary"),
        CurrencyItem("RON", "lei", "ليو روماني", "Romanian Leu", "رومانيا", "🇷🇴", "أوروبا", "Romania"),
        CurrencyItem("BGN", "лв", "ليف بلغاري", "Bulgarian Lev", "بلغاريا", "🇧🇬", "أوروبا", "Bulgaria"),
        CurrencyItem("RSD", "din", "دينار صربي", "Serbian Dinar", "صربيا", "🇷🇸", "أوروبا", "Serbia"),
        CurrencyItem("UAH", "₴", "هريفنيا أوكرانية", "Ukrainian Hryvnia", "أوكرانيا", "🇺🇦", "أوروبا", "Ukraine"),
        CurrencyItem("ISK", "kr", "كرونة آيسلندية", "Icelandic Krona", "آيسلندا", "🇮🇸", "أوروبا", "Iceland"),
        CurrencyItem("ALL", "L", "ليك ألباني", "Albanian Lek", "ألبانيا", "🇦🇱", "أوروبا", "Albania"),
        CurrencyItem("BAM", "KM", "مارك بوسني قابل للتحويل", "Bosnian Mark", "البوسنة والهرسك", "🇧🇦", "أوروبا", "Bosnia and Herzegovina"),
        CurrencyItem("MKD", "den", "دينار مقدوني", "Macedonian Denar", "مقدونيا الشمالية", "🇲🇰", "أوروبا", "North Macedonia"),
        CurrencyItem("GEL", "₾", "لاري جورجي", "Georgian Lari", "جورجيا", "🇬🇪", "أوروبا", "Georgia"),
        CurrencyItem("AMD", "֏", "درام أرميني", "Armenian Dram", "أرمينيا", "🇦🇲", "أوروبا", "Armenia"),
        CurrencyItem("AZN", "₼", "مانات أذربيجاني", "Azerbaijani Manat", "أذربيجان", "🇦🇿", "أوروبا", "Azerbaijan"),
        CurrencyItem("BYN", "Br", "روبل بيلاروسي", "Belarusian Ruble", "بيلاروسيا", "🇧🇾", "أوروبا", "Belarus"),
        CurrencyItem("MDL", "L", "ليو مولدوفي", "Moldovan Leu", "مولدوفا", "🇲🇩", "أوروبا", "Moldova"),

        // Americas (الأمريكتين)
        CurrencyItem("BRL", "R$", "ريال برازيلي", "Brazilian Real", "البرازيل", "🇧🇷", "الأمريكتين", "Brazil"),
        CurrencyItem("MXN", "Mex$", "بيزو مكسيكي", "Mexican Peso", "المكسيك", "🇲🇽", "الأمريكتين", "Mexico"),
        CurrencyItem("ARS", "$", "بيزو أرجنتيني", "Argentine Peso", "الأرجنتين", "🇦🇷", "الأمريكتين", "Argentina"),
        CurrencyItem("CLP", "CLP$", "بيزو تشيلي", "Chilean Peso", "تشيلي", "🇨🇱", "الأمريكتين", "Chile"),
        CurrencyItem("COP", "COL$", "بيزو كولومبي", "Colombian Peso", "كولومبيا", "🇨🇴", "الأمريكتين", "Colombia"),
        CurrencyItem("PEN", "S/.", "سول بيروفي", "Peruvian Sol", "بيرو", "🇵🇪", "الأمريكتين", "Peru"),
        CurrencyItem("UYU", "\$U", "بيزو أوروغواياني", "Uruguayan Peso", "الأوروغواي", "🇺🇾", "الأمريكتين", "Uruguay"),
        CurrencyItem("BOB", "Bs.", "بوليفيانو بوليفي", "Bolivian Boliviano", "بوليفيا", "🇧🇴", "الأمريكتين", "Bolivia"),
        CurrencyItem("PYG", "₲", "غواراني باراغواياني", "Paraguayan Guarani", "باراغواي", "🇵🇾", "الأمريكتين", "Paraguay"),
        CurrencyItem("VES", "Bs.", "بوليفار فنزويلي", "Venezuelan Bolivar", "فنزويلا", "🇻🇪", "الأمريكتين", "Venezuela"),
        CurrencyItem("CRC", "₡", "كولون كوستاريكي", "Costa Rican Colon", "كوستاريكا", "🇨🇷", "الأمريكتين", "Costa Rica"),
        CurrencyItem("DOP", "RD$", "بيزو دومينيكاني", "Dominican Peso", "جمهورية الدومينيكان", "🇩🇴", "الأمريكتين", "Dominican Republic"),
        CurrencyItem("GTQ", "Q", "كيتزال غواتيمالي", "Guatemalan Quetzal", "غواتيمالا", "🇬🇹", "الأمريكتين", "Guatemala"),
        CurrencyItem("HNL", "L", "ليمبيرا هندوراسية", "Honduran Lempira", "هندوراس", "🇭🇳", "الأمريكتين", "Honduras"),
        CurrencyItem("NIO", "C$", "كوردوبا نيكاراغوية", "Nicaraguan Cordoba", "نيكاراغوا", "🇳🇮", "الأمريكتين", "Nicaragua"),
        CurrencyItem("PAB", "B/.", "بالبوا بنمي", "Panamanian Balboa", "بنما", "🇵🇦", "الأمريكتين", "Panama"),
        CurrencyItem("JMD", "J$", "دولار جامايكي", "Jamaican Dollar", "جامايكا", "🇯🇲", "الأمريكتين", "Jamaica"),
        CurrencyItem("TTD", "TT$", "دولار ترينيداد وتوباغو", "Trinidad Dollar", "ترينيداد وتوباغو", "🇹🇹", "الأمريكتين", "Trinidad and Tobago"),
        CurrencyItem("BBD", "Bds$", "دولار بربادوسي", "Barbadian Dollar", "باربادوس", "🇧🇧", "الأمريكتين", "Barbados"),
        CurrencyItem("BSD", "B$", "دولار بهامي", "Bahamian Dollar", "الباهاماس", "🇧🇸", "الأمريكتين", "Bahamas"),

        // Africa (أفريقيا)
        CurrencyItem("ZAR", "R", "راند جنوب أفريقي", "South African Rand", "جنوب أفريقيا", "🇿🇦", "أفريقيا", "South Africa"),
        CurrencyItem("NGN", "₦", "نايرا نيجيرية", "Nigerian Naira", "نيجيريا", "🇳🇬", "أفريقيا", "Nigeria"),
        CurrencyItem("KES", "KSh", "شلن كيني", "Kenyan Shilling", "كينيا", "🇰🇪", "أفريقيا", "Kenya"),
        CurrencyItem("GHS", "GH₵", "سيدي غاني", "Ghanaian Cedi", "غانا", "🇬🇭", "أفريقيا", "Ghana"),
        CurrencyItem("ETB", "Br", "بير إثيوبي", "Ethiopian Birr", "إثيوبيا", "🇪🇹", "أفريقيا", "Ethiopia"),
        CurrencyItem("TZS", "TSh", "شلن تنزاني", "Tanzanian Shilling", "تنزانيا", "🇹🇿", "أفريقيا", "Tanzania"),
        CurrencyItem("UGX", "USh", "شلن أوغندي", "Ugandan Shilling", "أوغندا", "🇺🇬", "أفريقيا", "Uganda"),
        CurrencyItem("RWF", "FRw", "فرنك رواندي", "Rwandan Franc", "رواندا", "🇷🇼", "أفريقيا", "Rwanda"),
        CurrencyItem("ZMW", "ZK", "كواشا زامبية", "Zambian Kwacha", "زامبيا", "🇿🇲", "أفريقيا", "Zambia"),
        CurrencyItem("AOA", "Kz", "كوانزا أنغولية", "Angolan Kwanza", "أنغولا", "🇦🇴", "أفريقيا", "Angola"),
        CurrencyItem("MZN", "MT", "متكال موزمبيقي", "Mozambican Metical", "موزمبيق", "🇲🇿", "أفريقيا", "Mozambique"),
        CurrencyItem("BWP", "P", "بولا بوتسوانية", "Botswana Pula", "بوتسوانا", "🇧🇼", "أفريقيا", "Botswana"),
        CurrencyItem("NAD", "N$", "دولار ناميبي", "Namibian Dollar", "ناميبيا", "🇳🇦", "أفريقيا", "Namibia"),
        CurrencyItem("XOF", "CFA", "فرنك غرب أفريقيا", "West African CFA Franc", "دول غرب أفريقيا", "🌍", "أفريقيا", "West African States"),
        CurrencyItem("XAF", "FCFA", "فرنك وسط أفريقيا", "Central African CFA Franc", "دول وسط أفريقيا", "🌍", "أفريقيا", "Central African States"),
        CurrencyItem("GMD", "D", "دالاسي غامبي", "Gambian Dalasi", "غامبيا", "🇬🇲", "أفريقيا", "Gambia"),
        CurrencyItem("SLL", "Le", "ليون سيراليوني", "Sierra Leonean Leone", "سيراليون", "🇸🇱", "أفريقيا", "Sierra Leone"),
        CurrencyItem("LRD", "L$", "دولار ليبيري", "Liberian Dollar", "ليبيريا", "🇱🇷", "أفريقيا", "Liberia"),
        CurrencyItem("GNF", "FG", "فرنك غيني", "Guinean Franc", "غينيا", "🇬🇳", "أفريقيا", "Guinea"),
        CurrencyItem("MWK", "MK", "كواشا ملاوية", "Malawian Kwacha", "ملاوي", "🇲🇼", "أفريقيا", "Malawi"),
        CurrencyItem("MGA", "Ar", "أرياري مدغشقري", "Malagasy Ariary", "مدغشقر", "🇲🇬", "أفريقيا", "Madagascar"),
        CurrencyItem("SCR", "SR", "روبية سيشيلية", "Seychellois Rupee", "سيشل", "🇸🇨", "أفريقيا", "Seychelles"),
        CurrencyItem("MUR", "Rs", "روبية موريشيوسية", "Mauritian Rupee", "موريشيوس", "🇲🇺", "أفريقيا", "Mauritius"),

        // Oceania (أوقيانوسيا)
        CurrencyItem("NZD", "NZ$", "دولار نيوزيلندي", "New Zealand Dollar", "نيوزيلندا", "🇳🇿", "أوقيانوسيا", "New Zealand"),
        CurrencyItem("FJD", "FJ$", "دولار فيجي", "Fijian Dollar", "فيجي", "🇫🇯", "أوقيانوسيا", "Fiji"),
        CurrencyItem("PGK", "K", "كينا بابوا غينيا", "Papua New Guinean Kina", "بابوا غينيا الجديدة", "🇵🇬", "أوقيانوسيا", "Papua New Guinea"),
        CurrencyItem("WST", "WS$", "تالا ساموية", "Samoan Tala", "ساموا", "🇼🇸", "أوقيانوسيا", "Samoa"),
        CurrencyItem("TOP", "T$", "بانغا تونغية", "Tongan Paʻanga", "تونغا", "🇹🇴", "أوقيانوسيا", "Tonga"),
        CurrencyItem("SBD", "SI$", "دولار جزر سليمان", "Solomon Islands Dollar", "جزر سليمان", "🇸🇧", "أوقيانوسيا", "Solomon Islands"),
        CurrencyItem("VUV", "VT", "فاتو فانواتي", "Vanuatu Vatu", "فانواتو", "🇻🇺", "أوقيانوسيا", "Vanuatu")
    )

    fun findByCode(code: String): CurrencyItem? {
        return supportedCurrencies.find { it.code.equals(code, ignoreCase = true) }
    }

    fun findBySymbol(symbol: String): CurrencyItem? {
        return supportedCurrencies.find { it.defaultSymbol == symbol || it.code.equals(symbol, ignoreCase = true) }
    }

    fun searchCurrencies(query: String, regionFilter: String = "ALL"): List<CurrencyItem> {
        val trimmed = query.trim()
        val baseList = if (regionFilter == "ALL") {
            supportedCurrencies
        } else {
            supportedCurrencies.filter { it.regionAr == regionFilter }
        }

        if (trimmed.isEmpty()) return baseList

        return baseList.filter {
            it.code.contains(trimmed, ignoreCase = true) ||
            it.nameAr.contains(trimmed, ignoreCase = true) ||
            it.nameEn.contains(trimmed, ignoreCase = true) ||
            it.countryAr.contains(trimmed, ignoreCase = true) ||
            it.countryEn.contains(trimmed, ignoreCase = true) ||
            it.defaultSymbol.contains(trimmed, ignoreCase = true)
        }
    }

    fun format(amount: Double, config: CurrencyConfig): String {
        val pattern = buildString {
            if (config.useGrouping) append("#,##0") else append("0")
            if (config.decimalPlaces > 0) {
                append(".")
                repeat(config.decimalPlaces) { append("0") }
            }
        }
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        val formatter = DecimalFormat(pattern, symbols)
        val formattedNum = formatter.format(amount)

        val symbol = config.customSymbol.trim()
        return if (config.symbolPlacement == SymbolPlacement.BEFORE_AMOUNT) {
            "$symbol $formattedNum"
        } else {
            "$formattedNum $symbol"
        }
    }

    fun formatSimple(amount: Double, symbol: String, decimals: Int = 2): String {
        return format(amount, CurrencyConfig(customSymbol = symbol, decimalPlaces = decimals))
    }
}
