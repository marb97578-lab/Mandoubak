package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppColorTheme
import com.example.ui.theme.CardColorStyle
import com.example.ui.theme.MandoubakBlue
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.ui.theme.TextColorOption
import com.example.ui.theme.ThemeCategory
import com.example.ui.theme.ThemeConfig
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.ThemeRepository
import com.example.data.cloud.CloudBackupRecord
import com.example.util.BackupFileInfo
import com.example.util.CurrencyConfig
import com.example.util.CurrencyItem
import com.example.util.CurrencyRepository
import com.example.util.LanguageOption
import com.example.util.LanguageRepository
import com.example.util.NotificationConfig
import com.example.util.SymbolPlacement

@Composable
fun SettingsDialog(
    companyName: String,
    representativeName: String,
    taxNumber: String,
    autoBackupEnabled: Boolean = true,
    autoBackupFrequency: String = "DAILY",
    onSave: (company: String, rep: String, tax: String, autoBackup: Boolean, frequency: String) -> Unit,
    onDismiss: () -> Unit
) {
    var cName by remember { mutableStateOf(companyName) }
    var rName by remember { mutableStateOf(representativeName) }
    var tNum by remember { mutableStateOf(taxNumber) }
    var autoBackup by remember { mutableStateOf(autoBackupEnabled) }
    var frequency by remember { mutableStateOf(autoBackupFrequency) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إعدادات المنظومة والمندوب", fontWeight = FontWeight.Bold, color = MandoubakNavy)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    label = { Text("الرقم الضريبي (اختياري)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("النسخ الاحتياطي التلقائي الدوري:", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                            Switch(checked = autoBackup, onCheckedChange = { autoBackup = it })
                        }

                        if (autoBackup) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (frequency == "DAILY") Color(0xFFEFF6FF) else Color.White
                                    ),
                                    border = BorderStroke(1.dp, if (frequency == "DAILY") MandoubakBlue else Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { frequency = "DAILY" }
                                ) {
                                    Text(
                                        text = "يومياً (Daily)",
                                        fontSize = 11.5.sp,
                                        fontWeight = if (frequency == "DAILY") FontWeight.Bold else FontWeight.Normal,
                                        color = if (frequency == "DAILY") MandoubakBlue else MandoubakTextSecondary,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                                    )
                                }

                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (frequency == "WEEKLY") Color(0xFFEFF6FF) else Color.White
                                    ),
                                    border = BorderStroke(1.dp, if (frequency == "WEEKLY") MandoubakBlue else Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { frequency = "WEEKLY" }
                                ) {
                                    Text(
                                        text = "أسبوعياً (Weekly)",
                                        fontSize = 11.5.sp,
                                        fontWeight = if (frequency == "WEEKLY") FontWeight.Bold else FontWeight.Normal,
                                        color = if (frequency == "WEEKLY") MandoubakBlue else MandoubakTextSecondary,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(cName, rName, tNum, autoBackup, frequency) },
                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
            ) {
                Text("حفظ الإعدادات", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = MandoubakTextSecondary) }
        }
    )
}

@Composable
fun BackupRestoreDialog(
    localBackups: List<BackupFileInfo>,
    autoBackupEnabled: Boolean,
    autoBackupFrequency: String,
    lastBackupTime: Long,
    isLoading: Boolean,
    statusMessage: String?,
    onToggleAutoBackup: (Boolean, String) -> Unit,
    onCreateManualBackup: () -> Unit,
    onRestoreBackup: (BackupFileInfo) -> Unit,
    onDeleteBackup: (BackupFileInfo) -> Unit,
    onShareBackup: (BackupFileInfo) -> Unit,
    onImportExternalFile: () -> Unit,
    isCloudSignedIn: Boolean = false,
    cloudUserEmail: String? = null,
    cloudBackups: List<CloudBackupRecord> = emptyList(),
    isCloudLoading: Boolean = false,
    cloudStatusMessage: String? = null,
    onSignInWithGoogle: () -> Unit = {},
    onSignOutFromCloud: () -> Unit = {},
    onUploadCloudBackup: () -> Unit = {},
    onRefreshCloudBackups: () -> Unit = {},
    onRestoreCloudBackup: (CloudBackupRecord) -> Unit = {},
    onDeleteCloudBackup: (CloudBackupRecord) -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedFreq by remember { mutableStateOf(autoBackupFrequency) }
    var autoEnabled by remember { mutableStateOf(autoBackupEnabled) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Cloud (Firebase), 1: Local (Room)
    var backupToRestore by remember { mutableStateOf<BackupFileInfo?>(null) }
    var backupToDelete by remember { mutableStateOf<BackupFileInfo?>(null) }
    var cloudBackupToRestore by remember { mutableStateOf<CloudBackupRecord?>(null) }
    var cloudBackupToDelete by remember { mutableStateOf<CloudBackupRecord?>(null) }

    // Confirmation Alert for Cloud Restore
    cloudBackupToRestore?.let { backup ->
        AlertDialog(
            onDismissRequest = { cloudBackupToRestore = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تأكيد استعادة النسخة الاحتياطية", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "هل أنت متأكد من استعادة النسخة الاحتياطية السحابية من Firebase:\n(${backup.title})؟",
                        fontSize = 13.sp,
                        color = MandoubakNavy,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "• تاريخ النسخة: ${backup.formattedDate}\n• إجمالي السجلات: ${backup.totalRecords} سجل\n• الحجم: ${backup.formattedSize}",
                        fontSize = 12.sp,
                        color = MandoubakTextSecondary
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "⚠️ تنبيه هام حول البيانات المحلية:",
                                color = Color(0xFFB45309),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "استعادة النسخة الاحتياطية ستقوم باستبدال البيانات المحلية الحالية في قاعدة بيانات Room بالبيانات المستعادة بدقة، بما يشمل كافة المنتجات، العملاء، الموردين، الفواتير، المخزون، المصروفات، والتقارير.",
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val b = cloudBackupToRestore
                        cloudBackupToRestore = null
                        if (b != null) onRestoreCloudBackup(b)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
                ) {
                    Text("نعم، استعادة النسخة", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { cloudBackupToRestore = null }) { Text("إلغاء", color = MandoubakTextSecondary) }
            }
        )
    }

    // Confirmation Alert for Cloud Delete
    cloudBackupToDelete?.let { backup ->
        AlertDialog(
            onDismissRequest = { cloudBackupToDelete = null },
            title = { Text("حذف النسخة السحابية", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)) },
            text = { Text("هل أنت متأكد من حذف النسخة السحابية (${backup.title}) نهائياً من سحابة Firebase؟") },
            confirmButton = {
                Button(
                    onClick = {
                        val b = cloudBackupToDelete
                        cloudBackupToDelete = null
                        if (b != null) onDeleteCloudBackup(b)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("حذف من السحابة", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { cloudBackupToDelete = null }) { Text("إلغاء") }
            }
        )
    }

    // Confirmation Alert for Restore
    backupToRestore?.let { backup ->
        AlertDialog(
            onDismissRequest = { backupToRestore = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تأكيد استعادة النسخة الاحتياطية", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "هل أنت متأكد من استعادة النسخة الاحتياطية:\n(${backup.fileName})؟",
                        fontSize = 13.sp,
                        color = MandoubakNavy,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "• تاريخ النسخة: ${backup.formattedDate}\n• عدد السجلات: ${backup.recordsCount} سجل\n• الحجم: ${backup.formattedSize}",
                        fontSize = 12.sp,
                        color = MandoubakTextSecondary
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "تنبيه: سيتم استبدال البيانات الحالية بالكامل بالبيانات المستعادة ضمن عملية ذرية آمنة (Atomic Transaction) تضمن سلامة قاعدة البيانات.",
                            color = Color(0xFFB45309),
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val b = backupToRestore
                        backupToRestore = null
                        if (b != null) onRestoreBackup(b)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
                ) {
                    Text("نعم، استعادة فورية", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { backupToRestore = null }) { Text("إلغاء", color = MandoubakTextSecondary) }
            }
        )
    }

    // Confirmation Alert for Delete
    backupToDelete?.let { backup ->
        AlertDialog(
            onDismissRequest = { backupToDelete = null },
            title = { Text("حذف النسخة الاحتياطية", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)) },
            text = { Text("هل أنت متأكد من حذف ملف النسخة الاحتياطية (${backup.fileName}) من ذاكرة الجهاز؟") },
            confirmButton = {
                Button(
                    onClick = {
                        val b = backupToDelete
                        backupToDelete = null
                        if (b != null) onDeleteBackup(b)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("حذف نهائي", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { backupToDelete = null }) { Text("إلغاء") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Backup & Restore (النسخ الاحتياطي)", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 15.5.sp)
                    Text("قاعدة بيانات Room محلياً • سحابة Firebase الآمنة", fontSize = 11.sp, color = MandoubakTextSecondary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Section Tabs: Cloud vs Local
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedTab == 0) Color.White else Color.Transparent
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (selectedTab == 0) 2.dp else 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 0 }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = if (selectedTab == 0) MandoubakBlue else MandoubakTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "النسخ السحابي (Firebase)",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 0) MandoubakNavy else MandoubakTextSecondary
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedTab == 1) Color.White else Color.Transparent
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (selectedTab == 1) 2.dp else 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 1 }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = if (selectedTab == 1) MandoubakBlue else MandoubakTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "النسخ المحلي (Room)",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 1) MandoubakNavy else MandoubakTextSecondary
                                )
                            }
                        }
                    }
                }

                // ========================================================
                // TAB 0: CLOUD BACKUP (FIREBASE FIRESTORE)
                // ========================================================
                if (selectedTab == 0) {
                    // Offline / Room Database Architecture Clarification
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "المعمارية: العمل بدون إنترنت أولاً",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = MandoubakNavy
                                    )
                                    Text(
                                        text = "جميع العمليات اليومية تُخزن محلياً في قاعدة بيانات Room وتعمل دائماً دون الحاجة لشبكة الإنترنت. خدمة Firebase مخصصة حصرياً للنسخ السحابي الاحتياطي والاستعادة.",
                                        fontSize = 10.5.sp,
                                        color = MandoubakTextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    // Google Account Status & Auth Gate
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCloudSignedIn) Color(0xFFEFF6FF) else Color(0xFFFEFCE8)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (isCloudSignedIn) Color(0xFFBFDBFE) else Color(0xFFFEF08A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (!isCloudSignedIn) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ربط حساب Google للنسخ السحابي",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = Color(0xFFB45309)
                                        )
                                    }
                                    Text(
                                        text = "قم بتسجيل الدخول بحساب Google لتمكين رفع واستعادة النسخ الاحتياطية على سحابة Firebase المشفرة بأمان تام.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF78350F)
                                    )
                                    Button(
                                        onClick = onSignInWithGoogle,
                                        enabled = !isCloudLoading,
                                        colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isCloudLoading) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("جاري الاتصال بـ Google...", color = Color.White, fontSize = 12.sp)
                                        } else {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Sign in with Google (تسجيل الدخول بحساب Google)", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = "متصل بسحابة Firebase",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = MandoubakNavy
                                                )
                                                Text(
                                                    text = cloudUserEmail ?: "مستخدم Google",
                                                    fontSize = 11.sp,
                                                    color = MandoubakTextSecondary
                                                )
                                            }
                                        }
                                        TextButton(onClick = onSignOutFromCloud) {
                                            Text("تسجيل الخروج", fontSize = 11.sp, color = Color(0xFFDC2626))
                                        }
                                    }

                                    // Last Cloud Backup Date & Time (if exists)
                                    val lastCloudBackup = cloudBackups.maxByOrNull { it.timestamp }
                                    if (lastCloudBackup != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFF1F5F9))
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "آخر نسخة احتياطية سحابية: ${lastCloudBackup.formattedDate}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MandoubakNavy
                                                )
                                            }
                                            Text(
                                                text = "${lastCloudBackup.totalRecords} سجل",
                                                fontSize = 10.5.sp,
                                                color = MandoubakTextSecondary
                                            )
                                        }
                                    }

                                    // Create Backup Button
                                    Button(
                                        onClick = onUploadCloudBackup,
                                        enabled = !isCloudLoading,
                                        colors = ButtonDefaults.buttonColors(containerColor = MandoubakBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isCloudLoading) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("جاري إنشاء ورفع النسخة الاحتياطية...", color = Color.White, fontSize = 12.sp)
                                        } else {
                                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Create Backup (إنشاء نسخة احتياطية سحابية)", color = Color.White, fontSize = 12.sp)
                                        }
                                    }

                                    // Restore Backup Button (Downloads and restores latest backup from Firebase)
                                    Button(
                                        onClick = {
                                            val latest = cloudBackups.maxByOrNull { it.timestamp }
                                            if (latest != null) {
                                                cloudBackupToRestore = latest
                                            } else {
                                                onRefreshCloudBackups()
                                            }
                                        },
                                        enabled = !isCloudLoading && cloudBackups.isNotEmpty(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isCloudLoading) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("جاري استعادة النسخة السحابية...", color = Color.White, fontSize = 12.sp)
                                        } else {
                                            Icon(Icons.Default.Restore, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Restore Backup (استعادة النسخة الاحتياطية)", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Cloud Status Message
                    cloudStatusMessage?.let { msg ->
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = msg,
                                    color = Color(0xFF059669),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    // Cloud Backups List
                    if (isCloudSignedIn) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "النسخ السحابية المحفوظة (${cloudBackups.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MandoubakNavy
                                )
                                TextButton(
                                    onClick = onRefreshCloudBackups,
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = MandoubakBlue)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تحديث", fontSize = 11.sp, color = MandoubakBlue)
                                }
                            }
                        }

                        if (cloudBackups.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "لا توجد نسخ سحابية مخزنة حتى الآن. اضغط زر 'رفع نسخة احتياطية سحابية' لحفظ نسختك الأولى على Firebase.",
                                        fontSize = 11.5.sp,
                                        color = MandoubakTextSecondary,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        } else {
                            items(cloudBackups) { cloudBackup ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = cloudBackup.title,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = MandoubakNavy,
                                                    maxLines = 1
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFFEFF6FF))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "سحابي Firebase",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MandoubakBlue
                                                )
                                            }
                                        }

                                        Text(
                                            text = "${cloudBackup.formattedDate} • ${cloudBackup.formattedSize} • ${cloudBackup.totalRecords} سجل",
                                            fontSize = 11.sp,
                                            color = MandoubakTextSecondary
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = { cloudBackupToRestore = cloudBackup },
                                                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("استعادة محلياً", fontSize = 11.sp, color = Color.White)
                                            }

                                            Spacer(modifier = Modifier.width(6.dp))

                                            IconButton(
                                                onClick = { cloudBackupToDelete = cloudBackup },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ========================================================
                // TAB 1: LOCAL BACKUP (ROOM DATABASE)
                // ========================================================
                if (selectedTab == 1) {
                    // Auto-backup configuration card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("الحفظ التلقائي الدوري:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                }
                                Switch(
                                    checked = autoEnabled,
                                    onCheckedChange = {
                                        autoEnabled = it
                                        onToggleAutoBackup(it, selectedFreq)
                                    }
                                )
                            }

                            if (autoEnabled) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedFreq == "DAILY") Color(0xFFEFF6FF) else Color(0xFFF1F5F9)
                                        ),
                                        border = BorderStroke(1.dp, if (selectedFreq == "DAILY") MandoubakBlue else Color(0xFFCBD5E1)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedFreq = "DAILY"
                                                onToggleAutoBackup(true, "DAILY")
                                            }
                                    ) {
                                        Text(
                                            text = "يومياً (Daily)",
                                            fontSize = 11.5.sp,
                                            fontWeight = if (selectedFreq == "DAILY") FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedFreq == "DAILY") MandoubakBlue else MandoubakTextSecondary,
                                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                                        )
                                    }

                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedFreq == "WEEKLY") Color(0xFFEFF6FF) else Color(0xFFF1F5F9)
                                        ),
                                        border = BorderStroke(1.dp, if (selectedFreq == "WEEKLY") MandoubakBlue else Color(0xFFCBD5E1)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedFreq = "WEEKLY"
                                                onToggleAutoBackup(true, "WEEKLY")
                                            }
                                    ) {
                                        Text(
                                            text = "أسبوعياً (Weekly)",
                                            fontSize = 11.5.sp,
                                            fontWeight = if (selectedFreq == "WEEKLY") FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedFreq == "WEEKLY") MandoubakBlue else MandoubakTextSecondary,
                                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                                        )
                                    }
                                }
                            }

                            if (lastBackupTime > 0) {
                                val dateStr = java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(lastBackupTime))
                                Text(
                                    text = "آخر نسخة ناجحة: $dateStr",
                                    fontSize = 11.sp,
                                    color = Color(0xFF059669),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Primary Quick Actions
                item {
                    Button(
                        onClick = onCreateManualBackup,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جاري التأمين والنسخ...", color = Color.White, fontSize = 12.5.sp)
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إنشاء نسخة احتياطية فورية (حفظ محلي)", color = Color.White, fontSize = 12.5.sp)
                        }
                    }
                }

                item {
                    Button(
                        onClick = onImportExternalFile,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("استيراد واستعادة من ملف خارجي (JSON)", color = MandoubakNavy, fontSize = 12.5.sp)
                    }
                }

                // Status Message Toast inside Dialog
                statusMessage?.let { msg ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = Color(0xFF059669),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                // Local Backups History Section
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "النسخ المحفوظة على الجهاز (${localBackups.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MandoubakNavy
                        )
                    }
                }

                if (localBackups.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "لا توجد نسخ سابقة مخزنة محلياً. اضغط زر إنشاء نسخة احتياطية لحفظ أول نسخة الآن.",
                                fontSize = 11.5.sp,
                                color = MandoubakTextSecondary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    items(localBackups) { backup ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = backup.fileName,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp,
                                        color = MandoubakNavy,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (backup.backupType.contains("تلقائي")) Color(0xFFECFDF5) else Color(0xFFEFF6FF)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = backup.backupType,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (backup.backupType.contains("تلقائي")) Color(0xFF059669) else MandoubakBlue
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${backup.formattedDate} • ${backup.formattedSize} • ${backup.recordsCount} سجل",
                                        fontSize = 11.sp,
                                        color = MandoubakTextSecondary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { onShareBackup(backup) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = MandoubakNavy)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مشاركة", fontSize = 11.sp, color = MandoubakNavy)
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Button(
                                        onClick = { backupToRestore = backup },
                                        colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("استعادة", fontSize = 11.sp, color = Color.White)
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(
                                        onClick = { backupToDelete = backup },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
            ) {
                Text("إغلاق", color = Color.White)
            }
        }
    )
}

@Composable
fun SubscriptionDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("الاشتراك والترقية", fontWeight = FontWeight.Bold, color = MandoubakNavy)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFCE8)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("النسخة التجارية المعتمدة: مفعلة بالكامل", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• فواتير مبيعات ومشتريات غير محدودة", fontSize = 12.sp, color = MandoubakTextPrimary)
                        Text("• جرد مستمر بدون قيود", fontSize = 12.sp, color = MandoubakTextPrimary)
                        Text("• قاعدة بيانات SQLite محلية غير محدودة السجلات", fontSize = 12.sp, color = MandoubakTextPrimary)
                        Text("• عمل كامل بدون إنترنت Offline-First 100%", fontSize = 12.sp, color = MandoubakTextPrimary)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)) {
                Text("حسناً", color = Color.White)
            }
        }
    )
}

@Composable
fun LanguageDialog(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredLanguages = remember(searchQuery) {
        LanguageRepository.searchLanguages(searchQuery)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = MandoubakBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "لغات التطبيق",
                        fontWeight = FontWeight.Bold,
                        color = MandoubakNavy,
                        fontSize = 16.sp
                    )
                }

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${filteredLanguages.size} لغة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MandoubakBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Box at the top
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "ابحث باسم اللغة... / Search language...",
                            fontSize = 12.5.sp,
                            color = MandoubakTextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = MandoubakNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "مسح",
                                    tint = MandoubakTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MandoubakBlue,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // One complete list of all available languages
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        items = filteredLanguages,
                        key = { it.code }
                    ) { lang ->
                        val isSelected = selectedLanguage.equals(lang.nameNative, ignoreCase = true) ||
                                selectedLanguage.equals(lang.code, ignoreCase = true) ||
                                (lang.code == "ar" && (selectedLanguage == "العربية" || selectedLanguage.startsWith("ar", ignoreCase = true))) ||
                                (lang.code == "en" && (selectedLanguage == "English" || selectedLanguage.startsWith("en", ignoreCase = true)))

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
                            ),
                            border = if (isSelected) {
                                BorderStroke(1.5.dp, MandoubakBlue)
                            } else {
                                BorderStroke(1.dp, Color(0xFFE2E8F0))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageSelected(lang.nameNative)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(lang.flagEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                lang.nameNative,
                                                fontWeight = FontWeight.Bold,
                                                color = MandoubakNavy,
                                                fontSize = 13.5.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "(${lang.nameAr})",
                                                fontSize = 11.5.sp,
                                                color = MandoubakTextSecondary
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Text(
                                                text = if (lang.isRtl) "RTL (يمين)" else "LTR (يسار)",
                                                fontSize = 10.5.sp,
                                                color = if (lang.isRtl) Color(0xFF92400E) else MandoubakTextSecondary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "• ${lang.nameEn}",
                                                fontSize = 10.5.sp,
                                                color = MandoubakTextSecondary
                                            )
                                        }
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MandoubakBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق", color = MandoubakTextSecondary) }
        }
    )
}

@Composable
fun CurrencyDialog(
    currentConfig: CurrencyConfig = CurrencyConfig(),
    onSaveConfig: (CurrencyConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCode by remember { mutableStateOf(currentConfig.selectedCode) }
    var customSymbol by remember { mutableStateOf(currentConfig.customSymbol) }
    var placement by remember { mutableStateOf(currentConfig.symbolPlacement) }
    var decimals by remember { mutableStateOf(currentConfig.decimalPlaces) }

    val allCurrencies = CurrencyRepository.supportedCurrencies
    val filteredCurrencies = remember(searchQuery) {
        CurrencyRepository.searchCurrencies(searchQuery)
    }

    val samplePrice = 1250.50
    val sampleFormatted = CurrencyRepository.format(
        samplePrice,
        CurrencyConfig(
            selectedCode = selectedCode,
            customSymbol = customSymbol,
            symbolPlacement = placement,
            decimalPlaces = decimals
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("إدارة العملات والتنسيق الدولي", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
                    Text("جميع العملات الرسمية معتمدة ومتزامنة تلقائياً", fontSize = 11.sp, color = MandoubakTextSecondary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Live Preview Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(1.dp, MandoubakBlue.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("معاينة السعر في الفواتير والتقارير:", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                val currentItem = CurrencyRepository.findByCode(selectedCode)
                                if (currentItem != null) {
                                    Text("${currentItem.flagEmoji} ${currentItem.code}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MandoubakBlue)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sampleFormatted,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = MandoubakNavy
                            )
                        }
                    }
                }

                // Custom Symbol & Options
                item {
                    OutlinedTextField(
                        value = customSymbol,
                        onValueChange = { customSymbol = it },
                        label = { Text("رمز العملة المخصص (يظهر بالفواتير والتقارير)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Placement selection
                item {
                    Text("موضع رمز العملة:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = placement == SymbolPlacement.AFTER_AMOUNT,
                            onClick = { placement = SymbolPlacement.AFTER_AMOUNT },
                            label = { Text("بعد المبلغ (100 $customSymbol)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = placement == SymbolPlacement.BEFORE_AMOUNT,
                            onClick = { placement = SymbolPlacement.BEFORE_AMOUNT },
                            label = { Text("قبل المبلغ ($customSymbol 100)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Decimals selection
                item {
                    Text("عدد الخانات العشرية:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0, 2, 3).forEach { d ->
                            FilterChip(
                                selected = decimals == d,
                                onClick = { decimals = d },
                                label = { Text(if (d == 0) "بدون كسور" else "$d خانات", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Search currency & ONE section: All Currencies
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "All Currencies",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MandoubakNavy
                        )
                        Text(
                            text = "${filteredCurrencies.size} / ${allCurrencies.size}",
                            fontSize = 11.sp,
                            color = MandoubakTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("بحث بالدولة، اسم العملة، الكود (USD, SAR, YER)، أو الرمز...", fontSize = 11.5.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Clear, contentDescription = "مسح", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(filteredCurrencies) { curr ->
                    val isSelected = selectedCode == curr.code
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)),
                        border = if (isSelected) BorderStroke(1.5.dp, MandoubakBlue) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCode = curr.code
                                customSymbol = curr.defaultSymbol
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(curr.flagEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = curr.nameAr,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = MandoubakNavy
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFE2E8F0))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(curr.code, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                                        }
                                    }
                                    Text(
                                        text = "${curr.countryAr} • ${curr.nameEn}",
                                        fontSize = 11.sp,
                                        color = MandoubakTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(curr.defaultSymbol, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MandoubakBlue)
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.CheckCircle, contentDescription = "محدد", tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val defaultSym = CurrencyRepository.findByCode(selectedCode)?.defaultSymbol ?: "﷼"
                    onSaveConfig(
                        CurrencyConfig(
                            selectedCode = selectedCode,
                            customSymbol = if (customSymbol.isNotBlank()) customSymbol else defaultSym,
                            symbolPlacement = placement,
                            decimalPlaces = decimals
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
            ) {
                Text("حفظ واعتماد العملة", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = MandoubakTextSecondary) }
        }
    )
}

@Composable
fun ThemeCustomizationDialog(
    currentConfig: ThemeConfig = ThemeConfig(),
    onSaveConfig: (ThemeConfig) -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedThemeId by remember { mutableStateOf(currentConfig.activeThemeId) }
    var selectedMode by remember { mutableStateOf(currentConfig.themeMode) }
    var customAccentHex by remember { mutableStateOf(currentConfig.customAccentHex ?: "") }
    var selectedCategory by remember { mutableStateOf<ThemeCategory?>(null) }
    var cardColorStyle by remember { mutableStateOf(currentConfig.cardColorStyle) }
    var textColorOption by remember { mutableStateOf(currentConfig.textColorOption) }

    val allThemes = ThemeRepository.allThemes
    val filteredThemes = remember(selectedCategory) {
        if (selectedCategory == null) allThemes
        else allThemes.filter { it.category == selectedCategory }
    }

    val activeTheme = remember(selectedThemeId) {
        ThemeRepository.getThemeById(selectedThemeId)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("تخصيص المظهر والثيمات (80+ ثيم)", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Active Theme Preview Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, activeTheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "الثيم المختار: ${activeTheme.nameAr}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = MandoubakNavy
                                    )
                                    Text(
                                        text = "${activeTheme.nameEn} • ${activeTheme.category.titleAr}",
                                        fontSize = 11.sp,
                                        color = MandoubakTextSecondary
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(activeTheme.primary))
                                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(activeTheme.secondary))
                                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(activeTheme.accent))
                                }
                            }
                        }
                    }
                }

                // Mode Selection: Light vs Dark vs System
                item {
                    Text("نمط العرض (نهاري / ليلي):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            val isSel = selectedMode == mode
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedMode = mode },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (mode) {
                                                ThemeMode.LIGHT -> Icons.Default.Brightness7
                                                ThemeMode.DARK -> Icons.Default.Brightness4
                                                ThemeMode.SYSTEM -> Icons.Default.Settings
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(mode.titleAr, fontSize = 11.sp)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Category Filter Tabs
                item {
                    Text("تصنيفات الثيمات (إجمالي 87 ثيم متاح):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null },
                                label = { Text("الكل (87)", fontSize = 11.sp) }
                            )
                        }
                        items(ThemeCategory.entries) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.titleAr, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Themes Grid/List
                items(filteredThemes) { theme ->
                    val isSelected = selectedThemeId == theme.id
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)),
                        border = if (isSelected) BorderStroke(1.5.dp, theme.primary) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedThemeId = theme.id }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(theme.primary))
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(theme.secondary))
                                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(theme.accent))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(theme.nameAr, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MandoubakNavy)
                                    Text(theme.nameEn, fontSize = 10.5.sp, color = MandoubakTextSecondary)
                                }
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = theme.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Custom Accent Color
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("لون تمييز إضافي مخصص (Accent):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    OutlinedTextField(
                        value = customAccentHex,
                        onValueChange = { customAccentHex = it },
                        placeholder = { Text("مثال: #1D64F2 أو اتركه فارغاً للثيم") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Card Color Customization
                item {
                    Text("تخصيص ألوان بطاقات لوحة التحكم:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CardColorStyle.entries.forEach { style ->
                            val isSel = cardColorStyle == style
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isSel) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)),
                                border = if (isSel) BorderStroke(1.5.dp, MandoubakBlue) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { cardColorStyle = style }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(style.titleAr, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = MandoubakNavy)
                                    if (isSel) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Text Contrast Options
                item {
                    Text("خيارات تباين ولون النصوص:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextColorOption.entries.forEach { opt ->
                            val isSel = textColorOption == opt
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isSel) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)),
                                border = if (isSel) BorderStroke(1.5.dp, MandoubakBlue) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { textColorOption = opt }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(opt.titleAr, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = opt.primaryColor)
                                    if (isSel) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveConfig(
                        ThemeConfig(
                            activeThemeId = selectedThemeId,
                            themeMode = selectedMode,
                            customAccentHex = if (customAccentHex.isNotBlank()) customAccentHex.trim() else null,
                            cardColorStyle = cardColorStyle,
                            textColorOption = textColorOption
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
            ) {
                Text("حفظ وتطبيق المظهر", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = MandoubakTextSecondary) }
        }
    )
}

@Composable
fun NotificationSettingsDialog(
    currentConfig: NotificationConfig = NotificationConfig(),
    onSaveConfig: (NotificationConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var expirationAlerts by remember { mutableStateOf(currentConfig.expirationAlertsEnabled) }
    var expirationDays by remember { mutableStateOf(currentConfig.expirationDaysThreshold) }
    var lowStockAlerts by remember { mutableStateOf(currentConfig.lowStockAlertsEnabled) }
    var lowStockThreshold by remember { mutableStateOf(currentConfig.lowStockThreshold) }
    var backupReminders by remember { mutableStateOf(currentConfig.backupRemindersEnabled) }
    var backupFrequency by remember { mutableStateOf(currentConfig.backupReminderFrequency) }
    var debtAlerts by remember { mutableStateOf(currentConfig.debtCollectionAlertsEnabled) }
    var targetAlerts by remember { mutableStateOf(currentConfig.dailySalesTargetAlertsEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("إعدادات التنبيهات والإشعارات", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Expiration Alerts Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("تنبيهات تاريخ انتهاء الصلاحية", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text("تنبيه المنتجات التي تقترب صلاحيتها من الانتهاء", fontSize = 11.sp, color = MandoubakTextSecondary)
                                }
                                Switch(checked = expirationAlerts, onCheckedChange = { expirationAlerts = it })
                            }
                            if (expirationAlerts) {
                                Text("التنبيه قبل الانتهاء بـ:", fontSize = 11.5.sp, color = MandoubakNavy)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(7, 15, 30, 60).forEach { days ->
                                        FilterChip(
                                            selected = expirationDays == days,
                                            onClick = { expirationDays = days },
                                            label = { Text("$days يوم", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Low Stock Alerts Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("تنبيهات انخفاض ونفاد المخزون", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text("إشعار فوري عند وصول كمية المنتج للحد الأدنى", fontSize = 11.sp, color = MandoubakTextSecondary)
                                }
                                Switch(checked = lowStockAlerts, onCheckedChange = { lowStockAlerts = it })
                            }
                            if (lowStockAlerts) {
                                Text("الحد الأدنى الافتراضي للكمية:", fontSize = 11.5.sp, color = MandoubakNavy)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(3, 5, 10, 20).forEach { qty ->
                                        FilterChip(
                                            selected = lowStockThreshold == qty,
                                            onClick = { lowStockThreshold = qty },
                                            label = { Text("$qty قطع", fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Backup Reminders Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("تذكير النسخ الاحتياطي الدوري", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text("تذكير بحفظ ومزامنة نسخة احتياطية من البيانات", fontSize = 11.sp, color = MandoubakTextSecondary)
                                }
                                Switch(checked = backupReminders, onCheckedChange = { backupReminders = it })
                            }
                            if (backupReminders) {
                                Text("تكرار التذكير:", fontSize = 11.5.sp, color = MandoubakNavy)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("DAILY" to "يومياً", "WEEKLY" to "أسبوعياً", "MONTHLY" to "شهرياً").forEach { (f, title) ->
                                        FilterChip(
                                            selected = backupFrequency == f,
                                            onClick = { backupFrequency = f },
                                            label = { Text(title, fontSize = 11.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Important Notifications (Debt & Target) Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("الإشعارات والتعاميم الهامة", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("تنبيهات مديونيات العملاء المستحقة", fontSize = 12.sp, color = MandoubakNavy)
                                Switch(checked = debtAlerts, onCheckedChange = { debtAlerts = it })
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("تنبيه تحقيق هدف المبيعات اليومي", fontSize = 12.sp, color = MandoubakNavy)
                                Switch(checked = targetAlerts, onCheckedChange = { targetAlerts = it })
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveConfig(
                        NotificationConfig(
                            expirationAlertsEnabled = expirationAlerts,
                            expirationDaysThreshold = expirationDays,
                            lowStockAlertsEnabled = lowStockAlerts,
                            lowStockThreshold = lowStockThreshold,
                            backupRemindersEnabled = backupReminders,
                            backupReminderFrequency = backupFrequency,
                            debtCollectionAlertsEnabled = debtAlerts,
                            dailySalesTargetAlertsEnabled = targetAlerts
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
            ) {
                Text("حفظ الإعدادات", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = MandoubakTextSecondary) }
        }
    )
}

@Composable
fun DataManagementDialog(
    totalProductsCount: Int,
    totalClientsCount: Int,
    totalInvoicesCount: Int,
    totalPurchasesCount: Int = 0,
    totalSuppliersCount: Int = 0,
    totalExpensesCount: Int = 0,
    onClearTestTransactions: () -> Unit,
    onResetSettingsToDefault: () -> Unit,
    onResetToDemoData: () -> Unit,
    onClearAllData: () -> Unit,
    onDismiss: () -> Unit
) {
    var showConfirmClearTest by remember { mutableStateOf(false) }
    var showConfirmResetSettings by remember { mutableStateOf(false) }
    var showConfirmResetDemo by remember { mutableStateOf(false) }
    var showConfirmClearAll by remember { mutableStateOf(false) }

    // Alert 1: Clear Test Transactions
    if (showConfirmClearTest) {
        AlertDialog(
            onDismissRequest = { showConfirmClearTest = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مسح العمليات والحركات التجريبية", color = MandoubakNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                Text(
                    "هل أنت متأكد من مسح جميع فواتير المبيعات، فواتير المشتريات، سندات القبض، والمصروفات التجريبية؟\n\n✓ سيتم الاحتفاظ بقائمة الأصناف، العملاء، والموردين كاملة.\n✓ سيتم تصفير الأرصدة المالية وحركات المخزون للبدء بالعمل الفعلي النظيف.",
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearTestTransactions()
                        showConfirmClearTest = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
                ) {
                    Text("نعم، تفريغ الحركات فقط", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearTest = false }) { Text("إلغاء") }
            }
        )
    }

    // Alert 2: Reset Settings to Default
    if (showConfirmResetSettings) {
        AlertDialog(
            onDismissRequest = { showConfirmResetSettings = false },
            title = { Text("استعادة الإعدادات الافتراضية", color = MandoubakNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Text("هل ترغب في إعادة ضبط إعدادات المنظومة (اسم المتجر، اسم المندوب، العملة، واللغة) إلى قيم التثبيت الافتراضية؟ لن يؤثر ذلك على سجلاتك.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetSettingsToDefault()
                        showConfirmResetSettings = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
                ) {
                    Text("استعادة الافتراضي", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmResetSettings = false }) { Text("إلغاء") }
            }
        )
    }

    // Alert 3: Reset To Demo Data
    if (showConfirmResetDemo) {
        AlertDialog(
            onDismissRequest = { showConfirmResetDemo = false },
            title = { Text("إعادة ملء البيانات التجريبية", color = MandoubakNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Text("سيتم استبدال البيانات الحالية بالبيانات النموذجية الافتراضية لغايات التدريب والتجربة. هل ترغب بالاستمرار؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetToDemoData()
                        showConfirmResetDemo = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
                ) {
                    Text("نعم، تعبئة نموذجية", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmResetDemo = false }) { Text("إلغاء") }
            }
        )
    }

    // Alert 4: Clear All Data (Full Wipe)
    if (showConfirmClearAll) {
        AlertDialog(
            onDismissRequest = { showConfirmClearAll = false },
            title = { Text("تأكيد تصفير قاعدة البيانات بالكامل", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Text(
                    "تحذير فائق الأهمية: هذا الإجراء سيقوم بحذف جميع المنتجات، العملاء، الموردين، الفواتير، وحركات المخزون نهائياً من قاعدة بيانات الجهاز.\n\nلا يمكن التراجع عن هذا الإجراء إلا إذا كانت لديك نسخة احتياطية محفوظة.",
                    fontSize = 12.5.sp,
                    color = Color(0xFF991B1B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showConfirmClearAll = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("نعم، مسح شامل ونهائي", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearAll = false }) { Text("إلغاء") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("إدارة وتهيئة البيانات", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("إحصائيات السجلات المخزنة محلياً:", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = MandoubakNavy)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• الأصناف والمنتجات: $totalProductsCount صنف", fontSize = 12.sp, color = MandoubakTextPrimary)
                        Text("• العملاء المسجلون: $totalClientsCount عميل", fontSize = 12.sp, color = MandoubakTextPrimary)
                        Text("• الموردون المسجلون: $totalSuppliersCount مورد", fontSize = 12.sp, color = MandoubakTextPrimary)
                        Text("• الفواتير المسجلة: $totalInvoicesCount بيع / $totalPurchasesCount شراء", fontSize = 12.sp, color = MandoubakTextPrimary)
                        Text("• المصروفات المسجلة: $totalExpensesCount مصروف", fontSize = 12.sp, color = MandoubakTextPrimary)
                    }
                }

                // Action 1: Clear test transactions only
                Button(
                    onClick = { showConfirmClearTest = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مسح الحركات والبيانات التجريبية فقط (بدء العمل النظيف)", color = MandoubakNavy, fontSize = 12.sp)
                }

                // Action 2: Reset default settings
                Button(
                    onClick = { showConfirmResetSettings = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("استعادة الإعدادات الافتراضية للتطبيق", color = MandoubakNavy, fontSize = 12.sp)
                }

                // Action 3: Reset demo data
                Button(
                    onClick = { showConfirmResetDemo = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إعادة ملء البيانات التجريبية النموذجية", color = MandoubakNavy, fontSize = 12.sp)
                }

                // Action 4: Clear all data
                Button(
                    onClick = { showConfirmClearAll = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تفريغ قاعدة البيانات (تصفير شامل)", color = Color(0xFFDC2626), fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق", color = MandoubakTextSecondary) }
        }
    )
}

@Composable
fun ImportExportDialog(
    onExportCompleteCsv: () -> Unit,
    onExportJsonBackup: () -> Unit,
    onImportJsonBackup: () -> Unit,
    onDismiss: () -> Unit
) {
    var statusText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MandoubakBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("استيراد وتصدير البيانات", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "يمكنك تصدير جداول التطبيق إلى جداول Excel أو إنشاء نسخة للترحيل والاستيراد:",
                    fontSize = 12.5.sp,
                    color = MandoubakTextSecondary
                )

                Button(
                    onClick = {
                        statusText = "تم تجهيز ملف Excel الشامل (Mandoubak_Complete_Export.csv) مع دعم اللغة العربية UTF-8 BOM."
                        onExportCompleteCsv()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تصدير جميع السجلات إلى Excel (CSV شامل)", color = MandoubakNavy, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        statusText = "تم إنشاء نسخة احتياطية كاملة وموقعة برمز SHA-256."
                        onExportJsonBackup()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تصدير نسخة احتياطية كاملة (JSON)", color = MandoubakNavy, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        statusText = "جاهز لاختيار ملف النسخة الاحتياطية وفحص البصمة الرقمية..."
                        onImportJsonBackup()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("استيراد واستعادة من ملف خارجي (JSON)", color = MandoubakNavy, fontSize = 12.sp)
                }

                statusText?.let {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it,
                            color = Color(0xFF059669),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("إغلاق", color = MandoubakTextSecondary) }
        }
    )
}

@Composable
fun AboutAppDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("حول منظومة مندوبك (Mandoubak)", fontWeight = FontWeight.Bold, color = MandoubakNavy)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "منظومة مندوبك هي أداة ميدانية متكاملة واحترافية للمندوبين التجاريين ومسؤولي التوزيع.",
                    fontSize = 13.sp,
                    color = MandoubakTextPrimary,
                    lineHeight = 19.sp
                )

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Text("• الإصدار: 1.0.0 (النسخة التجارية المعتمدة)", fontSize = 12.sp, color = MandoubakNavy)
                Text("• بنية العمل: 100% Offline-First بدون إنترنت", fontSize = 12.sp, color = MandoubakNavy)
                Text("• التشفير والأمان: تشفير قاعدة البيانات المحلية SQLite / Room", fontSize = 12.sp, color = MandoubakNavy)
                Text("• الدعم الفني: متاح على مدار الساعة للمشتركين", fontSize = 12.sp, color = MandoubakNavy)

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Text(
                    text = "جميع الحقوق محفوظة © 2026 Mandoubak",
                    fontSize = 11.sp,
                    color = MandoubakTextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy)
            ) {
                Text("تم", color = Color.White)
            }
        }
    )
}

