package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrandLogoIcon
import com.example.ui.theme.CardClientsAccent
import com.example.ui.theme.CardInventoryAccent
import com.example.ui.theme.CardProductsAccent
import com.example.ui.theme.CardPurchasesAccent
import com.example.ui.theme.CardReceiptAccent
import com.example.ui.theme.CardSalesAccent
import com.example.ui.theme.MandoubakBlue
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.ui.viewmodel.AppScreen
import java.util.Locale

@Composable
fun DrawerContent(
    currentScreen: AppScreen,
    representativeName: String,
    todaySalesTotal: Double,
    todayReceiptsTotal: Double,
    currencySymbol: String,
    onNavigate: (AppScreen) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenBackup: () -> Unit,
    onOpenSubscription: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenCurrency: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenNotificationSettings: () -> Unit = {},
    onOpenDataManagement: () -> Unit,
    onOpenImportExport: () -> Unit,
    onOpenAboutApp: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .fillMaxHeight(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header Profile & Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrandLogoIcon(modifier = Modifier.size(46.dp, 38.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "مـنـدوبـك",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MandoubakNavy
                        )
                        Text(
                            text = "المندوب: $representativeName",
                            fontSize = 11.5.sp,
                            color = MandoubakTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(10.dp))

                // Shift Summary Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ملخص وردية اليوم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = MandoubakNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("المبيعات:", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                            Text(String.format(Locale.getDefault(), "%.1f %s", todaySalesTotal, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CardSalesAccent)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("المتحصلات النقدية:", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                            Text(String.format(Locale.getDefault(), "%.1f %s", todayReceiptsTotal, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CardReceiptAccent)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section: Feature Navigation
                Text(
                    text = "الأقسام التشغيلية",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MandoubakTextSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )

                DrawerNavigationItem(
                    title = "الرئيسية (لوحة التحكم)",
                    icon = Icons.Default.Home,
                    iconColor = MandoubakBlue,
                    isSelected = currentScreen == AppScreen.HOME,
                    onClick = {
                        onNavigate(AppScreen.HOME)
                        onCloseDrawer()
                    }
                )

                DrawerNavigationItem(
                    title = "المبيعات والفواتير",
                    icon = Icons.Default.ShoppingCart,
                    iconColor = CardSalesAccent,
                    isSelected = currentScreen == AppScreen.SALES,
                    onClick = {
                        onNavigate(AppScreen.SALES)
                        onCloseDrawer()
                    }
                )

                DrawerNavigationItem(
                    title = "سجل العملاء والذمم",
                    icon = Icons.Default.People,
                    iconColor = CardClientsAccent,
                    isSelected = currentScreen == AppScreen.CLIENTS,
                    onClick = {
                        onNavigate(AppScreen.CLIENTS)
                        onCloseDrawer()
                    }
                )

                DrawerNavigationItem(
                    title = "سجل الموردين والشركات",
                    icon = Icons.Default.LocalShipping,
                    iconColor = CardPurchasesAccent,
                    isSelected = currentScreen == AppScreen.SUPPLIERS,
                    onClick = {
                        onNavigate(AppScreen.SUPPLIERS)
                        onCloseDrawer()
                    }
                )

                DrawerNavigationItem(
                    title = "المنتجات والأصناف",
                    icon = Icons.Default.Inventory2,
                    iconColor = CardProductsAccent,
                    isSelected = currentScreen == AppScreen.PRODUCTS,
                    onClick = {
                        onNavigate(AppScreen.PRODUCTS)
                        onCloseDrawer()
                    }
                )

                DrawerNavigationItem(
                    title = "سندات القبض النقدية",
                    icon = Icons.Default.Description,
                    iconColor = CardReceiptAccent,
                    isSelected = currentScreen == AppScreen.RECEIPTS,
                    onClick = {
                        onNavigate(AppScreen.RECEIPTS)
                        onCloseDrawer()
                    }
                )

                DrawerNavigationItem(
                    title = "فواتير المشتريات",
                    icon = Icons.Default.LocalShipping,
                    iconColor = CardPurchasesAccent,
                    isSelected = currentScreen == AppScreen.PURCHASES,
                    onClick = {
                        onNavigate(AppScreen.PURCHASES)
                        onCloseDrawer()
                    }
                )

                DrawerNavigationItem(
                    title = "المخزون والجرد الشامل",
                    icon = Icons.Default.Assessment,
                    iconColor = CardInventoryAccent,
                    isSelected = currentScreen == AppScreen.INVENTORY,
                    onClick = {
                        onNavigate(AppScreen.INVENTORY)
                        onCloseDrawer()
                    }
                )

                DrawerNavigationItem(
                    title = "التقارير والإحصائيات",
                    icon = Icons.Default.Assessment,
                    iconColor = Color(0xFF7C3AED),
                    isSelected = currentScreen == AppScreen.REPORTS,
                    onClick = {
                        onNavigate(AppScreen.REPORTS)
                        onCloseDrawer()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(10.dp))

                // Section: Administrative Menu (الإدارة والإعدادات)
                Text(
                    text = "الإدارة والإعدادات",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MandoubakTextSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )

                DrawerMenuItem(
                    title = "الإعدادات",
                    icon = Icons.Default.Settings,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(AppScreen.SETTINGS)
                    }
                )

                DrawerMenuItem(
                    title = "النسخ الاحتياطي والاستعادة",
                    icon = Icons.Default.Backup,
                    onClick = {
                        onCloseDrawer()
                        onOpenBackup()
                    }
                )

                DrawerMenuItem(
                    title = "الاشتراك والترقية",
                    icon = Icons.Default.Star,
                    badge = "مفعل",
                    onClick = {
                        onCloseDrawer()
                        onOpenSubscription()
                    }
                )

                DrawerMenuItem(
                    title = "لغة التطبيق",
                    icon = Icons.Default.Language,
                    onClick = {
                        onCloseDrawer()
                        onOpenLanguage()
                    }
                )

                DrawerMenuItem(
                    title = "العملات (Currencies)",
                    icon = Icons.Default.AttachMoney,
                    badge = currencySymbol,
                    onClick = {
                        onCloseDrawer()
                        onNavigate(AppScreen.CURRENCIES)
                    }
                )

                DrawerMenuItem(
                    title = "ألوان المظهر",
                    icon = Icons.Default.ColorLens,
                    onClick = {
                        onCloseDrawer()
                        onOpenTheme()
                    }
                )

                DrawerMenuItem(
                    title = "إعدادات الإشعارات والتنبيهات",
                    icon = Icons.Default.NotificationsActive,
                    onClick = {
                        onCloseDrawer()
                        onOpenNotificationSettings()
                    }
                )

                DrawerMenuItem(
                    title = "إدارة البيانات",
                    icon = Icons.Default.Storage,
                    onClick = {
                        onCloseDrawer()
                        onOpenDataManagement()
                    }
                )

                DrawerMenuItem(
                    title = "استيراد وتصدير البيانات",
                    icon = Icons.Default.ImportExport,
                    onClick = {
                        onCloseDrawer()
                        onOpenImportExport()
                    }
                )

                DrawerMenuItem(
                    title = "حول التطبيق",
                    icon = Icons.Default.Info,
                    onClick = {
                        onCloseDrawer()
                        onOpenAboutApp()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "منظومة مندوبك - Offline-First",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "الإصدار 1.0 التجاري المعتمد",
                    fontSize = 10.sp,
                    color = Color(0xFFCBD5E1)
                )
            }
        }
    }
}

@Composable
fun DrawerNavigationItem(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(17.dp))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MandoubakNavy else MandoubakTextPrimary
        )
    }
}

@Composable
fun DrawerMenuItem(
    title: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(17.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = MandoubakTextPrimary
            )
        }

        badge?.let {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFECFDF5))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = it, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
            }
        }
    }
}
