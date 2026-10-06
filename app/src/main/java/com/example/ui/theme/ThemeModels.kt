package com.example.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ThemeCategory(val titleAr: String, val titleEn: String) {
    ROYAL_CLASSIC("الملكي والكلاسيكي", "Royal & Classic"),
    EMERALD_NATURE("الزمرد والطبيعة", "Emerald & Nature"),
    LUXURY_VIOLET("الفخامة والبنفسج", "Luxury & Violet"),
    WARMTH_SUNSET("الدفء والغروب", "Warmth & Sunset"),
    CRIMSON_RUBY("الأحمر والعنابي", "Crimson & Ruby"),
    CYAN_OCEANIC("البحري والتركواز", "Cyan & Oceanic"),
    MODERN_MONO("العصري والحيادي", "Modern & Monochromes"),
    ELEGANCE_GOLD("الأناقة والذهب", "Elegance & Gold")
}

enum class ThemeMode(val titleAr: String, val titleEn: String) {
    LIGHT("النمط النهاري", "Light Mode"),
    DARK("النمط الليلي", "Dark Mode"),
    SYSTEM("تلقائي حسب النظام", "System Default")
}

enum class CardColorStyle(val titleAr: String, val titleEn: String) {
    CLASSIC_PASTEL("الباستيل الكلاسيكي المعتمد", "Classic Pastel (Approved)"),
    CLEAN_WHITE("أبيض نقي كلاسيكي", "Pure White"),
    ACCENT_TINT("تدرج بلون الثيم المختار", "Active Theme Tint"),
    SOFT_MUTED("رمادي هادئ وأنيق", "Soft Muted Slate")
}

enum class TextColorOption(val titleAr: String, val titleEn: String, val primaryColor: Color) {
    DEFAULT_NAVY("الكحلي المعتمد (Mandoubak Navy)", "Approved Navy", MandoubakNavy),
    HIGH_CONTRAST_BLACK("أسود عالي التباين (High Contrast)", "High Contrast Black", Color(0xFF000000)),
    DEEP_SLATE("رمادي داكن نخبوي (Deep Slate)", "Deep Slate", Color(0xFF1E293B)),
    CHARCOAL("فحمي احترافي (Charcoal)", "Charcoal Modern", Color(0xFF334155))
}

data class AppColorTheme(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val category: ThemeCategory,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val darkBackground: Color = Color(0xFF0F172A),
    val darkSurface: Color = Color(0xFF1E293B)
)

data class ThemeConfig(
    val activeThemeId: Int = 1,
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val customAccentHex: String? = null,
    val cardColorStyle: CardColorStyle = CardColorStyle.CLASSIC_PASTEL,
    val textColorOption: TextColorOption = TextColorOption.DEFAULT_NAVY
)

val LocalThemeConfig = compositionLocalOf { ThemeConfig() }

object ThemeRepository {

    // Over 80 distinct curated themes (87 total)
    val allThemes: List<AppColorTheme> = listOf(
        // Category 1: ROYAL_CLASSIC (12 themes)
        AppColorTheme(1, "مندوبك الأصلي المعتمد", "Mandoubak Navy Classic", ThemeCategory.ROYAL_CLASSIC, MandoubakBlue, MandoubakCyan, MandoubakNavy),
        AppColorTheme(2, "الأزرق الملكي", "Royal Blue", ThemeCategory.ROYAL_CLASSIC, Color(0xFF2563EB), Color(0xFF38BDF8), Color(0xFF1E3A8A)),
        AppColorTheme(3, "الياقوت الإمبراطوري", "Imperial Sapphire", ThemeCategory.ROYAL_CLASSIC, Color(0xFF1D4ED8), Color(0xFF60A5FA), Color(0xFF172554)),
        AppColorTheme(4, "كحلي أوكسفورد", "Oxford Navy", ThemeCategory.ROYAL_CLASSIC, Color(0xFF1E40AF), Color(0xFF93C5FD), Color(0xFF0B192C)),
        AppColorTheme(5, "أطلسي عميق", "Deep Atlantic", ThemeCategory.ROYAL_CLASSIC, Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF0369A1)),
        AppColorTheme(6, "منتصف الليل الملكي", "Royal Midnight", ThemeCategory.ROYAL_CLASSIC, Color(0xFF3B82F6), Color(0xFF60A5FA), Color(0xFF030712)),
        AppColorTheme(7, "أزرق بروسي", "Prussian Blue", ThemeCategory.ROYAL_CLASSIC, Color(0xFF003153), Color(0xFF4A90E2), Color(0xFF001F3F)),
        AppColorTheme(8, "كوبالت نقي", "Cobalt Deep", ThemeCategory.ROYAL_CLASSIC, Color(0xFF0047AB), Color(0xFF4D94FF), Color(0xFF002244)),
        AppColorTheme(9, "إيجة المهيب", "Aegean Majesty", ThemeCategory.ROYAL_CLASSIC, Color(0xFF1F4E79), Color(0xFF5DADE2), Color(0xFF112D4E)),
        AppColorTheme(10, "نورديك أردوازي", "Nordic Slate", ThemeCategory.ROYAL_CLASSIC, Color(0xFF334E68), Color(0xFF627D98), Color(0xFF102A43)),
        AppColorTheme(11, "الأزرق السيادي", "Sovereign Navy", ThemeCategory.ROYAL_CLASSIC, Color(0xFF0F4C81), Color(0xFF74A9D8), Color(0xFF051C33)),
        AppColorTheme(12, "تاج الإمارة", "Crown Blue", ThemeCategory.ROYAL_CLASSIC, Color(0xFF2A52BE), Color(0xFF85A3F2), Color(0xFF1A2A6C)),

        // Category 2: EMERALD_NATURE (12 themes)
        AppColorTheme(13, "الزمرد النبيل", "Forest Emerald", ThemeCategory.EMERALD_NATURE, Color(0xFF059669), Color(0xFF34D399), Color(0xFF064E3B)),
        AppColorTheme(14, "المريمية الخضراء", "Mint Sage", ThemeCategory.EMERALD_NATURE, Color(0xFF10B981), Color(0xFF6EE7B7), Color(0xFF065F46)),
        AppColorTheme(15, "صنوبر الغابات", "Deep Pine", ThemeCategory.EMERALD_NATURE, Color(0xFF047857), Color(0xFFA7F3D0), Color(0xFF022C22)),
        AppColorTheme(16, "بساتين الزيتون", "Olive Grove", ThemeCategory.EMERALD_NATURE, Color(0xFF65A30D), Color(0xFFA3E635), Color(0xFF365314)),
        AppColorTheme(17, "كنز اليشم", "Jade Treasure", ThemeCategory.EMERALD_NATURE, Color(0xFF0D9488), Color(0xFF5EEAD4), Color(0xFF134E4A)),
        AppColorTheme(18, "طحلب الأدغال", "Jungle Moss", ThemeCategory.EMERALD_NATURE, Color(0xFF15803D), Color(0xFF86EFAC), Color(0xFF14532D)),
        AppColorTheme(19, "فستقي منعش", "Pistachio Fresh", ThemeCategory.EMERALD_NATURE, Color(0xFF84CC16), Color(0xFFBEF264), Color(0xFF4D7C0F)),
        AppColorTheme(20, "الأمازون الحيوي", "Amazon Green", ThemeCategory.EMERALD_NATURE, Color(0xFF16A34A), Color(0xFF4ADE80), Color(0xFF052E16)),
        AppColorTheme(21, "نسيم المروج", "Meadow Breeze", ThemeCategory.EMERALD_NATURE, Color(0xFF22C55E), Color(0xFFBBF7D0), Color(0xFF15803D)),
        AppColorTheme(22, "العرعر الداكن", "Juniper Dark", ThemeCategory.EMERALD_NATURE, Color(0xFF2D6A4F), Color(0xFF74C69D), Color(0xFF1B4332)),
        AppColorTheme(23, "زبد البحر النقي", "Seafoam Pure", ThemeCategory.EMERALD_NATURE, Color(0xFF14B8A6), Color(0xFF99F6E4), Color(0xFF115E59)),
        AppColorTheme(24, "سرو الأطلس", "Cypress Woodland", ThemeCategory.EMERALD_NATURE, Color(0xFF3F6212), Color(0xFFA3E635), Color(0xFF1A2E05)),

        // Category 3: LUXURY_VIOLET (11 themes)
        AppColorTheme(25, "الجمشت الملكي", "Royal Amethyst", ThemeCategory.LUXURY_VIOLET, Color(0xFF7C3AED), Color(0xFFA78BFA), Color(0xFF4C1D95)),
        AppColorTheme(26, "المخمل الأرجواني", "Velvet Violet", ThemeCategory.LUXURY_VIOLET, Color(0xFF8B5CF6), Color(0xFFC4B5FD), Color(0xFF5B21B6)),
        AppColorTheme(27, "ضباب اللافندر", "Lavender Mist", ThemeCategory.LUXURY_VIOLET, Color(0xFF9333EA), Color(0xFFD8B4FE), Color(0xFF6B21A8)),
        AppColorTheme(28, "أوركيد الأباطرة", "Imperial Orchid", ThemeCategory.LUXURY_VIOLET, Color(0xFFA855F7), Color(0xFFE9D5FF), Color(0xFF7E22CE)),
        AppColorTheme(29, "بيزنطي أرجواني", "Byzantium Purple", ThemeCategory.LUXURY_VIOLET, Color(0xFF702963), Color(0xFFC084FC), Color(0xFF4A0E4E)),
        AppColorTheme(30, "برقوق نخبوي", "Plum Dynasty", ThemeCategory.LUXURY_VIOLET, Color(0xFF6B21A8), Color(0xFFC084FC), Color(0xFF3B0764)),
        AppColorTheme(31, "شفق السوسن", "Iris Twilight", ThemeCategory.LUXURY_VIOLET, Color(0xFF5B21B6), Color(0xFFDDD6FE), Color(0xFF2E1065)),
        AppColorTheme(32, "كروم العنب", "Grape Vineyard", ThemeCategory.LUXURY_VIOLET, Color(0xFF86198F), Color(0xFFF0ABFC), Color(0xFF4A044E)),
        AppColorTheme(33, "حلم الليلك", "Lilac Dream", ThemeCategory.LUXURY_VIOLET, Color(0xFFC026D3), Color(0xFFF5D0FE), Color(0xFF701A75)),
        AppColorTheme(34, "توت العليق الملكي", "Deep Mulberry", ThemeCategory.LUXURY_VIOLET, Color(0xFF831843), Color(0xFFF472B6), Color(0xFF500724)),
        AppColorTheme(35, "البنفسج الكوني", "Cosmic Violet", ThemeCategory.LUXURY_VIOLET, Color(0xFF4338CA), Color(0xFF818CF8), Color(0xFF1E1B4B)),

        // Category 4: WARMTH_SUNSET (12 themes)
        AppColorTheme(36, "زعفران فارسي", "Persian Saffron", ThemeCategory.WARMTH_SUNSET, Color(0xFFD97706), Color(0xFFFBBF24), Color(0xFF78350F)),
        AppColorTheme(37, "رمال الصحراء", "Desert Sand", ThemeCategory.WARMTH_SUNSET, Color(0xFFB45309), Color(0xFFFCD34D), Color(0xFF451A03)),
        AppColorTheme(38, "مرجان الغروب", "Sunset Coral", ThemeCategory.WARMTH_SUNSET, Color(0xFFEA580C), Color(0xFFFB923C), Color(0xFF7C2D12)),
        AppColorTheme(39, "كهرمان توسكاني", "Tuscan Amber", ThemeCategory.WARMTH_SUNSET, Color(0xFFF59E0B), Color(0xFFFDE68A), Color(0xFF92400E)),
        AppColorTheme(40, "طمي التيراكوتا", "Terracotta Clay", ThemeCategory.WARMTH_SUNSET, Color(0xFFC2410C), Color(0xFFFDBA74), Color(0xFF431407)),
        AppColorTheme(41, "الشمس الذهبية", "Golden Sun", ThemeCategory.WARMTH_SUNSET, Color(0xFFEAB308), Color(0xFFFEF08A), Color(0xFF713F12)),
        AppColorTheme(42, "لهب النحاس", "Copper Flame", ThemeCategory.WARMTH_SUNSET, Color(0xFFB91C1C), Color(0xFFF87171), Color(0xFF7F1D1D)),
        AppColorTheme(43, "توابل المندرين", "Tangerine Spice", ThemeCategory.WARMTH_SUNSET, Color(0xFFF97316), Color(0xFFFED7AA), Color(0xFF9A3412)),
        AppColorTheme(44, "برونز نجد", "Bronze Desert", ThemeCategory.WARMTH_SUNSET, Color(0xFFA16207), Color(0xFFFDE047), Color(0xFF422006)),
        AppColorTheme(45, "زهرة الأقحوان البرتقالية", "Marigold Bloom", ThemeCategory.WARMTH_SUNSET, Color(0xFFCA8A04), Color(0xFFFEF9C3), Color(0xFF854D0E)),
        AppColorTheme(46, "أناقة الصدأ النبيل", "Rust Elegance", ThemeCategory.WARMTH_SUNSET, Color(0xFF991B1B), Color(0xFFFCA5A5), Color(0xFF450A0A)),
        AppColorTheme(47, "حصاد الخريف", "Autumn Harvest", ThemeCategory.WARMTH_SUNSET, Color(0xFFD946EF), Color(0xFFF5D0FE), Color(0xFF86198F)),

        // Category 5: CRIMSON_RUBY (11 themes)
        AppColorTheme(48, "المخمل العنابي", "Burgundy Velvet", ThemeCategory.CRIMSON_RUBY, Color(0xFFBE123C), Color(0xFFFB7185), Color(0xFF4C0519)),
        AppColorTheme(49, "وردة القرمز", "Crimson Rose", ThemeCategory.CRIMSON_RUBY, Color(0xFFE11D48), Color(0xFFFDA4AF), Color(0xFF881337)),
        AppColorTheme(50, "كرز قرمز نقي", "Cherry Scarlet", ThemeCategory.CRIMSON_RUBY, Color(0xFFDC2626), Color(0xFFFCA5A5), Color(0xFF7F1D1D)),
        AppColorTheme(51, "مارون الأندلس", "Brick Maroon", ThemeCategory.CRIMSON_RUBY, Color(0xFF800000), Color(0xFFE57373), Color(0xFF4A0000)),
        AppColorTheme(52, "حبات الرمان", "Pomegranate Red", ThemeCategory.CRIMSON_RUBY, Color(0xFFC53030), Color(0xFFFEB2B2), Color(0xFF742A2A)),
        AppColorTheme(53, "العقيق الصافي", "Garnet Deep", ThemeCategory.CRIMSON_RUBY, Color(0xFF9B111E), Color(0xFFF87171), Color(0xFF4A080E)),
        AppColorTheme(54, "جوهرة الروبي", "Ruby Jewel", ThemeCategory.CRIMSON_RUBY, Color(0xFFE0115F), Color(0xFFF48FB1), Color(0xFF880E4F)),
        AppColorTheme(55, "سيدونا روج", "Sedona Rouge", ThemeCategory.CRIMSON_RUBY, Color(0xFFA51D24), Color(0xFFF0959A), Color(0xFF5E0B10)),
        AppColorTheme(56, "الفلفل الحار الفاخر", "Chili Pepper", ThemeCategory.CRIMSON_RUBY, Color(0xFFB32821), Color(0xFFE57373), Color(0xFF59110D)),
        AppColorTheme(57, "القرمزي الإمبراطوري", "Carmine Royalty", ThemeCategory.CRIMSON_RUBY, Color(0xFF960018), Color(0xFFE57373), Color(0xFF4F000D)),
        AppColorTheme(58, "بوردو المعتق", "Bordeaux Vintage", ThemeCategory.CRIMSON_RUBY, Color(0xFF6B1D2F), Color(0xFFBC6C7D), Color(0xFF350A14)),

        // Category 6: CYAN_OCEANIC (11 themes)
        AppColorTheme(59, "فيروز المحيط الهادئ", "Pacific Turquoise", ThemeCategory.CYAN_OCEANIC, Color(0xFF0891B2), Color(0xFF22D3EE), Color(0xFF164E63)),
        AppColorTheme(60, "أكوامارين نقي", "Aquamarine Clear", ThemeCategory.CYAN_OCEANIC, Color(0xFF06B6D4), Color(0xFF67E8F9), Color(0xFF083344)),
        AppColorTheme(61, "نسيم الكاريبي", "Caribbean Breeze", ThemeCategory.CYAN_OCEANIC, Color(0xFF0284C7), Color(0xFF7DD3FC), Color(0xFF0C4A6E)),
        AppColorTheme(62, "جليد القطب", "Arctic Glacier", ThemeCategory.CYAN_OCEANIC, Color(0xFF38BDF8), Color(0xFFBAE6FD), Color(0xFF0369A1)),
        AppColorTheme(63, "الأعماق البحرية", "Deep Ocean", ThemeCategory.CYAN_OCEANIC, Color(0xFF0F766E), Color(0xFF2DD4BF), Color(0xFF042F2E)),
        AppColorTheme(64, "تركواز ساحلي", "Coastal Cyan", ThemeCategory.CYAN_OCEANIC, Color(0xFF00ACC1), Color(0xFF80DEEA), Color(0xFF006064)),
        AppColorTheme(65, "بحيرة استوائية", "Island Lagoon", ThemeCategory.CYAN_OCEANIC, Color(0xFF26A69A), Color(0xFF80CBC4), Color(0xFF004D40)),
        AppColorTheme(66, "أفق التيل", "Teal Horizon", ThemeCategory.CYAN_OCEANIC, Color(0xFF00897B), Color(0xFF4DB6AC), Color(0xFF004D40)),
        AppColorTheme(67, "أمواج المارين", "Marine Wave", ThemeCategory.CYAN_OCEANIC, Color(0xFF0277BD), Color(0xFF4FC3F7), Color(0xFF01579B)),
        AppColorTheme(68, "توباز أزرق", "Blue Topaz", ThemeCategory.CYAN_OCEANIC, Color(0xFF039BE5), Color(0xFF81D4FA), Color(0xFF01579B)),
        AppColorTheme(69, "الشعب المرجانية الزرقاء", "Coral Reef", ThemeCategory.CYAN_OCEANIC, Color(0xFF0097A7), Color(0xFF4DD0E1), Color(0xFF006064)),

        // Category 7: MODERN_MONO (10 themes)
        AppColorTheme(70, "جرافيت التيتانيوم", "Titanium Graphite", ThemeCategory.MODERN_MONO, Color(0xFF475569), Color(0xFF94A3B8), Color(0xFF0F172A)),
        AppColorTheme(71, "حجر السج الأسود", "Obsidian Black", ThemeCategory.MODERN_MONO, Color(0xFF1E293B), Color(0xFF64748B), Color(0xFF020617)),
        AppColorTheme(72, "كروم فضي ناصع", "Silver Chrome", ThemeCategory.MODERN_MONO, Color(0xFF64748B), Color(0xFFCBD5E1), Color(0xFF1E293B)),
        AppColorTheme(73, "ضباب البلاتين", "Platinum Mist", ThemeCategory.MODERN_MONO, Color(0xFF52525B), Color(0xFFA1A1AA), Color(0xFF18181B)),
        AppColorTheme(74, "ألياف الكربون", "Carbon Fiber", ThemeCategory.MODERN_MONO, Color(0xFF3F3F46), Color(0xFF71717A), Color(0xFF09090B)),
        AppColorTheme(75, "صلب أردوازي", "Steel Slate", ThemeCategory.MODERN_MONO, Color(0xFF334155), Color(0xFF94A3B8), Color(0xFF0F172A)),
        AppColorTheme(76, "رماد الفحم العصري", "Charcoal Ash", ThemeCategory.MODERN_MONO, Color(0xFF27272A), Color(0xFF71717A), Color(0xFF18181B)),
        AppColorTheme(77, "خرسانة معمارية", "Concrete Modern", ThemeCategory.MODERN_MONO, Color(0xFF57534E), Color(0xFFA8A29E), Color(0xFF1C1917)),
        AppColorTheme(78, "أونيكس الظل", "Dark Onyx", ThemeCategory.MODERN_MONO, Color(0xFF262626), Color(0xFF737373), Color(0xFF0A0A0A)),
        AppColorTheme(79, "مينيمال صناعي", "Industrial Minimal", ThemeCategory.MODERN_MONO, Color(0xFF44403C), Color(0xFFD6D3D1), Color(0xFF1C1917)),

        // Category 8: ELEGANCE_GOLD (8 themes)
        AppColorTheme(80, "بريق الشامبانيا", "Champagne Sparkle", ThemeCategory.ELEGANCE_GOLD, Color(0xFFD4AF37), Color(0xFFF9E79F), Color(0xFF7D6608)),
        AppColorTheme(81, "الذهب الخالص الملكي", "Royal Gold", ThemeCategory.ELEGANCE_GOLD, Color(0xFFB8860B), Color(0xFFF0E68C), Color(0xFF5C4305)),
        AppColorTheme(82, "نحاس أثري معتق", "Antique Brass", ThemeCategory.ELEGANCE_GOLD, Color(0xFFCD7F32), Color(0xFFEDC9AF), Color(0xFF6E401F)),
        AppColorTheme(83, "مغرة البادية", "Desert Ochre", ThemeCategory.ELEGANCE_GOLD, Color(0xFFC68B59), Color(0xFFECC5A8), Color(0xFF6B4423)),
        AppColorTheme(84, "حرير الكراميل", "Caramel Silk", ThemeCategory.ELEGANCE_GOLD, Color(0xFF935116), Color(0xFFDC7633), Color(0xFF512E0F)),
        AppColorTheme(85, "شهد العسل الدافئ", "Honeycomb Warm", ThemeCategory.ELEGANCE_GOLD, Color(0xFFE59866), Color(0xFFFADBD8), Color(0xFF6E2C00)),
        AppColorTheme(86, "كشمير فاخر ناعم", "Cashmere Soft", ThemeCategory.ELEGANCE_GOLD, Color(0xFFBA8C63), Color(0xFFE6D2B5), Color(0xFF543D2B)),
        AppColorTheme(87, "شمس الأزتيك الذهبية", "Aztec Sun", ThemeCategory.ELEGANCE_GOLD, Color(0xFFDAA520), Color(0xFFFFF8DC), Color(0xFF8B6508))
    )

    fun getThemeById(id: Int): AppColorTheme {
        return allThemes.find { it.id == id } ?: allThemes.first()
    }
}
