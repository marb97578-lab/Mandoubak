package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakBlue
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.ui.theme.ThemeConfig
import com.example.ui.theme.ThemeRepository
import com.example.util.CurrencyConfig
import com.example.util.CurrencyRepository
import com.example.util.LanguageRepository
import com.example.util.NotificationConfig

enum class SettingsSection(val title: String) {
    ALL("الكل"),
    GENERAL("البيانات العامة"),
    APPEARANCE("المظهر والثيمات"),
    LANGUAGE("اللغة والاتجاه"),
    CURRENCY("إدارة العملات"),
    NOTIFICATIONS("التنبيهات"),
    DATA("إدارة البيانات"),
    BACKUP("النسخ الاحتياطي"),
    ABOUT("حول التطبيق")
}

@Composable
fun SettingsScreen(
    companyName: String,
    representativeName: String,
    taxNumber: String,
    phone: String,
    address: String,
    currencyConfig: CurrencyConfig = CurrencyConfig(),
    themeConfig: ThemeConfig = ThemeConfig(),
    notificationConfig: NotificationConfig = NotificationConfig(),
    selectedLanguage: String = "العربية",
    onBackClick: () -> Unit,
    onSaveProfile: (company: String, rep: String, tax: String, phone: String, address: String) -> Unit,
    onOpenBackup: () -> Unit,
    onOpenImportExport: () -> Unit,
    onOpenCurrency: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenSubscription: () -> Unit,
    onOpenDataManagement: () -> Unit,
    onOpenAboutApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cName by remember { mutableStateOf(companyName) }
    var rName by remember { mutableStateOf(representativeName) }
    var tNum by remember { mutableStateOf(taxNumber) }
    var phoneInput by remember { mutableStateOf(phone) }
    var addrInput by remember { mutableStateOf(address) }
    var activeSection by remember { mutableStateOf(SettingsSection.ALL) }

    val activeTheme = remember(themeConfig.activeThemeId) {
        ThemeRepository.getThemeById(themeConfig.activeThemeId)
    }

    val sampleFormattedPrice = remember(currencyConfig) {
        CurrencyRepository.format(1250.50, currencyConfig)
    }

    Scaffold(
        containerColor = MandoubakBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MandoubakNavy
                    )
                }

                Text(
                    text = "الإعدادات العامة والمنظومة",
                    color = MandoubakNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MandoubakNavy
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Quick Filter Chips for Settings Sections
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SettingsSection.entries) { section ->
                        val isSelected = activeSection == section
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeSection = section },
                            label = { Text(section.title, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MandoubakNavy,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // SECTION 1: General Settings (بيانات المنشأة والمندوب)
            if (activeSection == SettingsSection.ALL || activeSection == SettingsSection.GENERAL) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Business, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("الإعدادات العامة وبيانات المنشأة", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                                    Text("تظهر في ترويسة فواتير المبيعات وسندات القبض", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }
                            }

                            OutlinedTextField(
                                value = cName,
                                onValueChange = { cName = it },
                                label = { Text("اسم المؤسسة / المتجر") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = rName,
                                onValueChange = { rName = it },
                                label = { Text("اسم المندوب التجاري") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = tNum,
                                onValueChange = { tNum = it },
                                label = { Text("الرقم الضريبي (ZATCA / VAT)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("رقم هاتف المندوب / التواصل") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = addrInput,
                                onValueChange = { addrInput = it },
                                label = { Text("العنوان / المدينة والمقر") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    onSaveProfile(cName.trim(), rName.trim(), tNum.trim(), phoneInput.trim(), addrInput.trim())
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("حفظ البيانات العامة", color = Color.White)
                            }
                        }
                    }
                }
            }

            // SECTION 2: Appearance & Theme Customization (المظهر والثيمات)
            if (activeSection == SettingsSection.ALL || activeSection == SettingsSection.APPEARANCE) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF3E8FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("تخصيص المظهر والثيمات", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                                        Text("أكثر من 80 ثيم ملون مع النمط النهاري والليلي", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(activeTheme.primary))
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(activeTheme.secondary))
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(activeTheme.accent))
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("الثيم الحالي: ${activeTheme.nameAr} (${activeTheme.category.titleAr})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MandoubakNavy)
                                    Text("نمط العرض: ${themeConfig.themeMode.titleAr} • نمط البطاقات: ${themeConfig.cardColorStyle.titleAr}", fontSize = 11.sp, color = MandoubakTextSecondary)
                                    Text("تباين النصوص: ${themeConfig.textColorOption.titleAr}", fontSize = 11.sp, color = MandoubakTextSecondary)
                                }
                            }

                            Button(
                                onClick = onOpenTheme,
                                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تخصيص الثيم والألوان ونمط العرض", color = Color.White)
                            }
                        }
                    }
                }
            }

            // SECTION 3: Language & Text Direction (اللغة والاتجاه)
            if (activeSection == SettingsSection.ALL || activeSection == SettingsSection.LANGUAGE) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("إعدادات لغة المنظومة والاتجاه", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                                    Text("دعم كامل للغة العربية (RTL) والإنجليزية (LTR)", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val activeFlag = LanguageRepository.getFlagEmoji(selectedLanguage)
                                    val isRtlLang = LanguageRepository.isRtlLanguage(selectedLanguage)
                                    Column {
                                        Text("اللغة المعتمدة حالياً: $selectedLanguage", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MandoubakNavy)
                                        Text("التوجيه: ${if (isRtlLang) "من اليمين لليسار (RTL)" else "من اليسار لليمين (LTR)"}", fontSize = 11.sp, color = MandoubakTextSecondary)
                                    }
                                    Text(activeFlag, fontSize = 22.sp)
                                }
                            }

                            Button(
                                onClick = onOpenLanguage,
                                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تغيير اللغة والاتجاه", color = Color.White)
                            }
                        }
                    }
                }
            }

            // SECTION 4: Currency System (منظومة العملات)
            if (activeSection == SettingsSection.ALL || activeSection == SettingsSection.CURRENCY) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFECFDF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("نظام العملات والتنسيق الدولي", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                                    Text("دعم العملات المتعددة وتخصيص الرمز والخانات العشرية", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }
                            }

                            val activeCurrency = CurrencyRepository.findByCode(currencyConfig.selectedCode)
                                ?: CurrencyRepository.supportedCurrencies.first()

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(activeCurrency.flagEmoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${activeCurrency.nameAr} (${activeCurrency.code})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = MandoubakNavy
                                            )
                                            Text(
                                                text = "${activeCurrency.countryAr} • رمز العرض: ${currencyConfig.customSymbol}",
                                                fontSize = 11.5.sp,
                                                color = MandoubakTextSecondary
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEFF6FF))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = currencyConfig.customSymbol,
                                            color = MandoubakBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("معاينة السعر بالفواتير والتقارير:", fontSize = 11.sp, color = MandoubakTextSecondary)
                                        Text(sampleFormattedPrice, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MandoubakNavy)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MandoubakNavy)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(currencyConfig.customSymbol, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }

                            Button(
                                onClick = onOpenCurrency,
                                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("إدارة العملات والرموز والتنسيق الدولي", color = Color.White)
                            }
                        }
                    }
                }
            }

            // SECTION 5: Notification Settings (إعدادات التنبيهات والإشعارات)
            if (activeSection == SettingsSection.ALL || activeSection == SettingsSection.NOTIFICATIONS) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF2F2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("إعدادات الإشعارات والتنبيهات الميدانية", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                                    Text("التحكم بتنبيهات انتهاء الصلاحية والمخزون والنسخ", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "• تنبيهات الصلاحية: ${if (notificationConfig.expirationAlertsEnabled) "مفعلة (قبل ${notificationConfig.expirationDaysThreshold} يوم)" else "معطلة"}",
                                        fontSize = 11.5.sp,
                                        color = MandoubakNavy
                                    )
                                    Text(
                                        text = "• تنبيهات نقص المخزون: ${if (notificationConfig.lowStockAlertsEnabled) "مفعلة (عند ${notificationConfig.lowStockThreshold} قطع)" else "معطلة"}",
                                        fontSize = 11.5.sp,
                                        color = MandoubakNavy
                                    )
                                    Text(
                                        text = "• تذكير النسخ الاحتياطي: ${if (notificationConfig.backupRemindersEnabled) "مفعل (${if (notificationConfig.backupReminderFrequency == "DAILY") "يومي" else "أسبوعي"})" else "معطل"}",
                                        fontSize = 11.5.sp,
                                        color = MandoubakNavy
                                    )
                                }
                            }

                            Button(
                                onClick = onOpenNotificationSettings,
                                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تعديل إعدادات التنبيهات والإشعارات", color = Color.White)
                            }
                        }
                    }
                }
            }

            // SECTION 6: Data Management (إدارة البيانات وقاعدة البيانات)
            if (activeSection == SettingsSection.ALL || activeSection == SettingsSection.DATA) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Storage, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("إدارة البيانات وقاعدة البيانات", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                                    Text("تصفير المعاملات التجريبية أو استعادة البيانات النموذجية", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }
                            }

                            Button(
                                onClick = onOpenDataManagement,
                                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("فتح لوحة إدارة وتصفير البيانات", color = Color.White)
                            }
                        }
                    }
                }
            }

            // SECTION 7: Backup Settings (النسخ الاحتياطي والاستعادة)
            if (activeSection == SettingsSection.ALL || activeSection == SettingsSection.BACKUP) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("النسخ الاحتياطي والاستعادة والحماية", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                                    Text("حفظ محلي آمن وموقع ببصمة SHA-256 الرقمية", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onOpenBackup,
                                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("النسخ والاستعادة", color = Color.White, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = onOpenImportExport,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = MandoubakNavy)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("تصدير واستيراد", color = MandoubakNavy, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 8: About Application (حول التطبيق)
            if (activeSection == SettingsSection.ALL || activeSection == SettingsSection.ABOUT) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("حول منظومة مندوبك (Mandoubak)", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                                    Text("الإصدار 1.0.0 التجاري المعتمد • 100% Offline-First", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onOpenAboutApp,
                                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("حول التطبيق", color = Color.White, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = onOpenSubscription,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFEAB308))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("حالة الترخيص", color = MandoubakNavy, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
