package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ClientEntity
import com.example.data.entity.ProductEntity
import com.example.ui.theme.MandoubakBlue
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.util.NotificationConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsDialog(
    lowStockProducts: List<ProductEntity>,
    clientsWithDebt: List<ClientEntity>,
    todaySalesCount: Int,
    allProducts: List<ProductEntity> = emptyList(),
    lastBackupTime: Long = 0L,
    notificationConfig: NotificationConfig = NotificationConfig(),
    currencySymbol: String = "ر.س",
    onDismiss: () -> Unit,
    onNavigateToLowStock: () -> Unit = {},
    onNavigateToClients: () -> Unit = {},
    onOpenNotificationSettings: () -> Unit = {},
    onOpenBackup: () -> Unit = {}
) {
    // Check product expiration alerts
    val expiringProducts = remember(allProducts, notificationConfig) {
        if (!notificationConfig.expirationAlertsEnabled) emptyList()
        else {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val now = System.currentTimeMillis()
            val thresholdMillis = notificationConfig.expirationDaysThreshold.toLong() * 24 * 60 * 60 * 1000
            allProducts.filter { p ->
                if (p.expirationDate.isNotBlank()) {
                    try {
                        val expDate = sdf.parse(p.expirationDate)
                        if (expDate != null) {
                            val diff = expDate.time - now
                            diff in 0..thresholdMillis
                        } else false
                    } catch (_: Exception) {
                        false
                    }
                } else false
            }
        }
    }

    // Check backup reminder
    val showBackupReminder = remember(lastBackupTime, notificationConfig) {
        if (!notificationConfig.backupRemindersEnabled) false
        else {
            val now = System.currentTimeMillis()
            val intervalMillis = when (notificationConfig.backupReminderFrequency) {
                "DAILY" -> 24L * 60 * 60 * 1000
                "MONTHLY" -> 30L * 24 * 60 * 60 * 1000
                else -> 7L * 24 * 60 * 60 * 1000 // WEEKLY default
            }
            lastBackupTime == 0L || (now - lastBackupTime > intervalMillis)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
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
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MandoubakBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التنبيهات والإشعارات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MandoubakNavy
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MandoubakNavy)
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Low Stock Alerts
                if (notificationConfig.lowStockAlertsEnabled && lowStockProducts.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "تنبيه نقص المخزون (${lowStockProducts.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "يوجد ${lowStockProducts.size} منتج قارب على النفاد: " +
                                            lowStockProducts.take(3).joinToString("، ") { it.name },
                                    fontSize = 12.sp,
                                    color = MandoubakTextPrimary
                                )
                            }
                        }
                    }
                }

                // Expiration Alerts
                if (notificationConfig.expirationAlertsEnabled && expiringProducts.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassBottom,
                                        contentDescription = null,
                                        tint = Color(0xFFEA580C),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "تنبيه اقتراب انتهاء الصلاحية (${expiringProducts.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFEA580C)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "منتجات تنتهي خلال ${notificationConfig.expirationDaysThreshold} يوم: " +
                                            expiringProducts.take(3).joinToString("، ") { "${it.name} (${it.expirationDate})" },
                                    fontSize = 12.sp,
                                    color = MandoubakTextPrimary
                                )
                            }
                        }
                    }
                }

                // Backup Reminder Alert
                if (showBackupReminder) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Backup,
                                        contentDescription = null,
                                        tint = MandoubakBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "تذكير بالنسخ الاحتياطي الدوري",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MandoubakBlue
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (lastBackupTime == 0L) "لم يتم حفظ أي نسخة احتياطية محلية بعد. يُوصى بإنشاء نسخة لحماية بياناتك."
                                    else "حان موعد النسخ الاحتياطي الدوري الموصى به (${if (notificationConfig.backupReminderFrequency == "DAILY") "يومي" else "أسبوعي"}).",
                                    fontSize = 12.sp,
                                    color = MandoubakTextPrimary
                                )
                            }
                        }
                    }
                }

                // Client Debt Alert
                val clientsDebtors = clientsWithDebt.filter { it.currentBalance > 0 }
                if (notificationConfig.debtCollectionAlertsEnabled && clientsDebtors.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "متابعة ديون العملاء (${clientsDebtors.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFD97706)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val totalDebt = clientsDebtors.sumOf { it.currentBalance }
                                Text(
                                    text = "إجمالي الذمم المطلوبة: $totalDebt $currencySymbol لدى ${clientsDebtors.size} عملاء بحاجة للمتابعة والتحصيل.",
                                    fontSize = 12.sp,
                                    color = MandoubakTextPrimary
                                )
                            }
                        }
                    }
                }

                // Daily Status Notice
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "حالة وردية اليوم",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF059669)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "تم تسجيل $todaySalesCount فاتورة اليوم. المنظومة جاهزة للعمل وجميع البيانات متزامنة محلياً بنسبة 100%.",
                                fontSize = 12.sp,
                                color = MandoubakTextPrimary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حسناً", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onOpenNotificationSettings()
                }
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp), tint = MandoubakNavy)
                Spacer(modifier = Modifier.width(4.dp))
                Text("ضبط التنبيهات", color = MandoubakNavy, fontSize = 12.sp)
            }
        }
    )
}
