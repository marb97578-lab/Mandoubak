package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.dao.AppDao
import com.example.data.entity.ClientEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.data.entity.StockMovementEntity
import com.example.data.entity.SupplierEntity
import com.example.data.entity.SupplierPaymentEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupSettingsData(
    val companyName: String = "مؤسسة التوزيع التجاري",
    val representativeName: String = "المندوب الميداني",
    val taxNumber: String = "",
    val phone: String = "",
    val address: String = "",
    val currencySymbol: String = "ر.س",
    val selectedLanguage: String = "العربية",
    val autoBackupEnabled: Boolean = true,
    val autoBackupFrequency: String = "DAILY", // "DAILY", "WEEKLY", "MANUAL"
    val lastBackupTimestamp: Long = 0L,
    val themeId: Int = 1,
    val themeMode: String = "LIGHT",
    val customAccent: String = "",
    val cardStyle: String = "CLASSIC_PASTEL",
    val textColorOption: String = "DEFAULT_NAVY",
    val symbolPlacement: String = "AFTER_AMOUNT",
    val decimalPlaces: Int = 2,
    val expirationAlertsEnabled: Boolean = true,
    val expirationDaysThreshold: Int = 30,
    val lowStockAlertsEnabled: Boolean = true,
    val lowStockThreshold: Int = 5,
    val backupRemindersEnabled: Boolean = true,
    val backupReminderFrequency: String = "WEEKLY"
)

data class BackupFileInfo(
    val fileName: String,
    val filePath: String,
    val fileSizeBytes: Long,
    val formattedSize: String,
    val timestamp: Long,
    val formattedDate: String,
    val backupType: String, // "يدوي", "يومي تلقائي", "أسبوعي تلقائي"
    val recordsCount: Int,
    val isValid: Boolean = true
)

data class BackupValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val formatVersion: Int = 1,
    val timestamp: Long = 0L,
    val totalRecords: Int = 0,
    val productsCount: Int = 0,
    val clientsCount: Int = 0,
    val suppliersCount: Int = 0,
    val invoicesCount: Int = 0,
    val purchasesCount: Int = 0,
    val receiptsCount: Int = 0,
    val supplierPaymentsCount: Int = 0,
    val expensesCount: Int = 0,
    val stockMovementsCount: Int = 0,
    val settings: BackupSettingsData? = null,
    val parsedPayload: BackupPayload? = null
)

data class BackupPayload(
    val settings: BackupSettingsData,
    val products: List<ProductEntity>,
    val clients: List<ClientEntity>,
    val suppliers: List<SupplierEntity>,
    val salesInvoices: List<SaleInvoiceEntity>,
    val purchases: List<PurchaseInvoiceEntity>,
    val receipts: List<PaymentReceiptEntity>,
    val supplierPayments: List<SupplierPaymentEntity>,
    val expenses: List<ExpenseEntity>,
    val stockMovements: List<StockMovementEntity>
)

object BackupManager {

    private const val PREFS_NAME = "mandoubak_backup_prefs"
    private const val KEY_AUTO_BACKUP_ENABLED = "auto_backup_enabled"
    private const val KEY_AUTO_BACKUP_FREQ = "auto_backup_freq"
    private const val KEY_LAST_BACKUP_TIME = "last_backup_time"
    private const val KEY_LAST_BACKUP_NAME = "last_backup_name"

    private const val CURRENT_FORMAT_VERSION = 1
    private const val APP_IDENTIFIER = "Mandoubak"

    private val displayDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    private val fileNameDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    private fun getBackupsDir(context: Context): File {
        val dir = File(context.filesDir, "backups")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun calculateSha256(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(content.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes بايت"
            bytes < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f ك.ب", bytes / 1024f)
            else -> String.format(Locale.getDefault(), "%.1f م.ب", bytes / (1024f * 1024f))
        }
    }

    // -------------------------------------------------------------
    // Settings Preferences for Automatic Backup
    // -------------------------------------------------------------

    fun getAutoBackupEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_AUTO_BACKUP_ENABLED, true)
    }

    fun setAutoBackupEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AUTO_BACKUP_ENABLED, enabled).apply()
    }

    fun getAutoBackupFrequency(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_AUTO_BACKUP_FREQ, "DAILY") ?: "DAILY"
    }

    fun setAutoBackupFrequency(context: Context, frequency: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_AUTO_BACKUP_FREQ, frequency).apply()
    }

    fun getLastBackupTime(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_BACKUP_TIME, 0L)
    }

    private fun recordBackupCompleted(context: Context, fileName: String, time: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putLong(KEY_LAST_BACKUP_TIME, time)
            .putString(KEY_LAST_BACKUP_NAME, fileName)
            .apply()
    }

    // -------------------------------------------------------------
    // Create Backup File
    // -------------------------------------------------------------

    suspend fun createBackup(
        context: Context,
        appDao: AppDao,
        settings: BackupSettingsData,
        backupType: String = "MANUAL" // "MANUAL", "DAILY", "WEEKLY"
    ): Result<BackupFileInfo> {
        return try {
            val products = appDao.getAllProductsList()
            val clients = appDao.getAllClientsList()
            val suppliers = appDao.getAllSuppliersList()
            val invoices = appDao.getAllInvoicesList()
            val purchases = appDao.getAllPurchasesList()
            val receipts = appDao.getAllReceiptsList()
            val supplierPayments = appDao.getAllSupplierPaymentsList()
            val expenses = appDao.getAllExpensesList()
            val stockMovements = appDao.getAllStockMovementsList()

            val totalRecords = products.size + clients.size + suppliers.size +
                    invoices.size + purchases.size + receipts.size +
                    supplierPayments.size + expenses.size + stockMovements.size

            val now = System.currentTimeMillis()
            val prefix = when (backupType) {
                "DAILY" -> "mandoubak_auto_daily"
                "WEEKLY" -> "mandoubak_auto_weekly"
                else -> "mandoubak_backup_manual"
            }
            val fileName = "${prefix}_${fileNameDateFormat.format(Date(now))}.json"
            val backupsDir = getBackupsDir(context)
            val backupFile = File(backupsDir, fileName)

            // Construct JSON data payload
            val dataObj = JSONObject().apply {
                put("formatVersion", CURRENT_FORMAT_VERSION)
                put("appName", APP_IDENTIFIER)
                put("timestamp", now)
                put("backupType", backupType)

                // Settings
                val settingsObj = JSONObject().apply {
                    put("companyName", settings.companyName)
                    put("representativeName", settings.representativeName)
                    put("taxNumber", settings.taxNumber)
                    put("phone", settings.phone)
                    put("address", settings.address)
                    put("currencySymbol", settings.currencySymbol)
                    put("selectedLanguage", settings.selectedLanguage)
                    put("autoBackupEnabled", settings.autoBackupEnabled)
                    put("autoBackupFrequency", settings.autoBackupFrequency)
                    put("lastBackupTimestamp", now)
                    put("themeId", settings.themeId)
                    put("themeMode", settings.themeMode)
                    put("customAccent", settings.customAccent)
                    put("cardStyle", settings.cardStyle)
                    put("textColorOption", settings.textColorOption)
                    put("symbolPlacement", settings.symbolPlacement)
                    put("decimalPlaces", settings.decimalPlaces)
                    put("expirationAlertsEnabled", settings.expirationAlertsEnabled)
                    put("expirationDaysThreshold", settings.expirationDaysThreshold)
                    put("lowStockAlertsEnabled", settings.lowStockAlertsEnabled)
                    put("lowStockThreshold", settings.lowStockThreshold)
                    put("backupRemindersEnabled", settings.backupRemindersEnabled)
                    put("backupReminderFrequency", settings.backupReminderFrequency)
                }
                put("settings", settingsObj)

                // Products
                val productsArr = JSONArray()
                products.forEach { p ->
                    productsArr.put(JSONObject().apply {
                        put("id", p.id)
                        put("name", p.name)
                        put("barcode", p.barcode)
                        put("batchNumber", p.batchNumber)
                        put("manufacturingDate", p.manufacturingDate)
                        put("expirationDate", p.expirationDate)
                        put("costPrice", p.costPrice)
                        put("salePrice", p.salePrice)
                        put("stockQuantity", p.stockQuantity)
                        put("reservedQuantity", p.reservedQuantity)
                        put("minStockThreshold", p.minStockThreshold)
                        put("unit", p.unit)
                        put("category", p.category)
                        put("supplierName", p.supplierName)
                        put("notes", p.notes)
                        put("imageUri", p.imageUri)
                        put("createdAt", p.createdAt)
                    })
                }
                put("products", productsArr)

                // Clients
                val clientsArr = JSONArray()
                clients.forEach { c ->
                    clientsArr.put(JSONObject().apply {
                        put("id", c.id)
                        put("name", c.name)
                        put("phone", c.phone)
                        put("address", c.address)
                        put("location", c.location)
                        put("currentBalance", c.currentBalance)
                        put("notes", c.notes)
                        put("createdAt", c.createdAt)
                    })
                }
                put("clients", clientsArr)

                // Suppliers
                val suppliersArr = JSONArray()
                suppliers.forEach { s ->
                    suppliersArr.put(JSONObject().apply {
                        put("id", s.id)
                        put("name", s.name)
                        put("phone", s.phone)
                        put("address", s.address)
                        put("companyName", s.companyName)
                        put("outstandingBalance", s.outstandingBalance)
                        put("notes", s.notes)
                        put("createdAt", s.createdAt)
                    })
                }
                put("suppliers", suppliersArr)

                // Sales Invoices
                val invoicesArr = JSONArray()
                invoices.forEach { inv ->
                    invoicesArr.put(JSONObject().apply {
                        put("id", inv.id)
                        put("invoiceNumber", inv.invoiceNumber)
                        put("clientId", inv.clientId)
                        put("clientName", inv.clientName)
                        put("subtotalAmount", inv.subtotalAmount)
                        put("discountAmount", inv.discountAmount)
                        put("totalAmount", inv.totalAmount)
                        put("paidAmount", inv.paidAmount)
                        put("remainingAmount", inv.remainingAmount)
                        put("profitAmount", inv.profitAmount)
                        put("isCredit", inv.isCredit)
                        put("paymentMethod", inv.paymentMethod)
                        put("itemsSummary", inv.itemsSummary)
                        put("notes", inv.notes)
                        put("dateMillis", inv.dateMillis)
                    })
                }
                put("salesInvoices", invoicesArr)

                // Purchases
                val purchasesArr = JSONArray()
                purchases.forEach { pur ->
                    purchasesArr.put(JSONObject().apply {
                        put("id", pur.id)
                        put("invoiceNumber", pur.invoiceNumber)
                        put("supplierId", pur.supplierId)
                        put("supplierName", pur.supplierName)
                        put("dateMillis", pur.dateMillis)
                        put("subtotalAmount", pur.subtotalAmount)
                        put("discountAmount", pur.discountAmount)
                        put("totalAmount", pur.totalAmount)
                        put("paidAmount", pur.paidAmount)
                        put("remainingAmount", pur.remainingAmount)
                        put("paymentMethod", pur.paymentMethod)
                        put("isCredit", pur.isCredit)
                        put("itemsSummary", pur.itemsSummary)
                        put("notes", pur.notes)
                    })
                }
                put("purchases", purchasesArr)

                // Receipts (سندات القبض من العملاء)
                val receiptsArr = JSONArray()
                receipts.forEach { r ->
                    receiptsArr.put(JSONObject().apply {
                        put("id", r.id)
                        put("receiptNumber", r.receiptNumber)
                        put("clientId", r.clientId)
                        put("clientName", r.clientName)
                        put("amount", r.amount)
                        put("paymentMethod", r.paymentMethod)
                        put("notes", r.notes)
                        put("dateMillis", r.dateMillis)
                    })
                }
                put("receipts", receiptsArr)

                // Supplier Payments (سندات صرف الموردين)
                val supplierPaymentsArr = JSONArray()
                supplierPayments.forEach { sp ->
                    supplierPaymentsArr.put(JSONObject().apply {
                        put("id", sp.id)
                        put("paymentNumber", sp.paymentNumber)
                        put("supplierId", sp.supplierId)
                        put("supplierName", sp.supplierName)
                        put("amount", sp.amount)
                        put("paymentMethod", sp.paymentMethod)
                        put("notes", sp.notes)
                        put("dateMillis", sp.dateMillis)
                    })
                }
                put("supplierPayments", supplierPaymentsArr)

                // Expenses
                val expensesArr = JSONArray()
                expenses.forEach { exp ->
                    expensesArr.put(JSONObject().apply {
                        put("id", exp.id)
                        put("title", exp.title)
                        put("category", exp.category)
                        put("amount", exp.amount)
                        put("dateMillis", exp.dateMillis)
                        put("paymentMethod", exp.paymentMethod)
                        put("notes", exp.notes)
                    })
                }
                put("expenses", expensesArr)

                // Stock Movements
                val stockArr = JSONArray()
                stockMovements.forEach { sm ->
                    stockArr.put(JSONObject().apply {
                        put("id", sm.id)
                        put("productId", sm.productId)
                        put("productName", sm.productName)
                        put("movementType", sm.movementType)
                        put("movementTypeArabic", sm.movementTypeArabic)
                        put("quantity", sm.quantity)
                        put("previousStock", sm.previousStock)
                        put("newStock", sm.newStock)
                        put("referenceNumber", sm.referenceNumber)
                        put("reason", sm.reason)
                        put("dateMillis", sm.dateMillis)
                    })
                }
                put("stockMovements", stockArr)
            }

            // Calculate SHA-256 Checksum on the data object string to prevent data corruption
            val dataString = dataObj.toString(2)
            val checksum = calculateSha256(dataString)

            // Final Root Container
            val rootObj = JSONObject().apply {
                put("appName", APP_IDENTIFIER)
                put("formatVersion", CURRENT_FORMAT_VERSION)
                put("checksumSha256", checksum)
                put("exportedAt", now)
                put("totalRecords", totalRecords)
                put("payload", dataObj)
            }

            // Write securely to disk
            FileOutputStream(backupFile).use { fos ->
                OutputStreamWriter(fos, Charsets.UTF_8).use { writer ->
                    writer.write(rootObj.toString(2))
                    writer.flush()
                }
            }

            recordBackupCompleted(context, fileName, now)

            val fileSizeBytes = backupFile.length()
            val backupTypeArabic = when (backupType) {
                "DAILY" -> "يومي تلقائي"
                "WEEKLY" -> "أسبوعي تلقائي"
                else -> "يدوي"
            }

            // Auto-rotate if needed
            rotateOldBackups(context, backupType)

            Result.success(
                BackupFileInfo(
                    fileName = fileName,
                    filePath = backupFile.absolutePath,
                    fileSizeBytes = fileSizeBytes,
                    formattedSize = formatFileSize(fileSizeBytes),
                    timestamp = now,
                    formattedDate = displayDateFormat.format(Date(now)),
                    backupType = backupTypeArabic,
                    recordsCount = totalRecords,
                    isValid = true
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // Automatic Backup Check & Rotation
    // -------------------------------------------------------------

    suspend fun checkAndPerformAutoBackup(
        context: Context,
        appDao: AppDao,
        currentSettings: BackupSettingsData
    ): BackupFileInfo? {
        val enabled = getAutoBackupEnabled(context)
        if (!enabled) return null

        val frequency = getAutoBackupFrequency(context)
        if (frequency == "MANUAL") return null

        val lastBackup = getLastBackupTime(context)
        val now = System.currentTimeMillis()

        val intervalMillis = when (frequency) {
            "WEEKLY" -> 7L * 24 * 3600 * 1000L
            else -> 24L * 3600 * 1000L // DAILY
        }

        if (now - lastBackup >= intervalMillis) {
            val type = if (frequency == "WEEKLY") "WEEKLY" else "DAILY"
            val res = createBackup(context, appDao, currentSettings, type)
            return res.getOrNull()
        }

        return null
    }

    private fun rotateOldBackups(context: Context, backupType: String) {
        try {
            val dir = getBackupsDir(context)
            val files = dir.listFiles { _, name ->
                when (backupType) {
                    "DAILY" -> name.startsWith("mandoubak_auto_daily")
                    "WEEKLY" -> name.startsWith("mandoubak_auto_weekly")
                    else -> name.startsWith("mandoubak_backup_manual")
                }
            } ?: return

            val maxAllowed = when (backupType) {
                "DAILY" -> 7
                "WEEKLY" -> 4
                else -> 10
            }

            if (files.size > maxAllowed) {
                files.sortBy { it.lastModified() }
                val toDeleteCount = files.size - maxAllowed
                for (i in 0 until toDeleteCount) {
                    files[i].delete()
                }
            }
        } catch (_: Exception) {
        }
    }

    // -------------------------------------------------------------
    // Validation & Integrity Check
    // -------------------------------------------------------------

    fun validateBackupContent(jsonString: String): BackupValidationResult {
        return try {
            val root = JSONObject(jsonString)

            val appName = root.optString("appName")
            if (appName != APP_IDENTIFIER) {
                return BackupValidationResult(
                    isValid = false,
                    errorMessage = "الملف غير متوافق: ليس نسخة احتياطية صالحة لتطبيق مندوبك (Mandoubak)."
                )
            }

            val formatVersion = root.optInt("formatVersion", 1)
            val expectedChecksum = root.optString("checksumSha256")
            val payloadObj = root.optJSONObject("payload")
                ?: return BackupValidationResult(isValid = false, errorMessage = "الملف فارغ أو تالف، تعذر قراءة بيانات السجلات.")

            // Checksum Verification for data protection & anti-corruption
            if (expectedChecksum.isNotEmpty()) {
                val calculatedChecksum = calculateSha256(payloadObj.toString(2))
                if (calculatedChecksum != expectedChecksum) {
                    return BackupValidationResult(
                        isValid = false,
                        errorMessage = "تنبيه أمان وسلامة البيانات: فشل فحص البصمة الرقمية (Checksum) للملف. قد يكون الملف تالفاً أو تم تعديله خارج التطبيق."
                    )
                }
            }

            val timestamp = payloadObj.optLong("timestamp", root.optLong("exportedAt", System.currentTimeMillis()))

            // Extract Settings
            val settingsObj = payloadObj.optJSONObject("settings")
            val settings = if (settingsObj != null) {
                BackupSettingsData(
                    companyName = settingsObj.optString("companyName", "مؤسسة التوزيع التجاري"),
                    representativeName = settingsObj.optString("representativeName", "المندوب الميداني"),
                    taxNumber = settingsObj.optString("taxNumber", ""),
                    phone = settingsObj.optString("phone", ""),
                    address = settingsObj.optString("address", ""),
                    currencySymbol = settingsObj.optString("currencySymbol", "ر.س"),
                    selectedLanguage = settingsObj.optString("selectedLanguage", "العربية"),
                    autoBackupEnabled = settingsObj.optBoolean("autoBackupEnabled", true),
                    autoBackupFrequency = settingsObj.optString("autoBackupFrequency", "DAILY"),
                    lastBackupTimestamp = settingsObj.optLong("lastBackupTimestamp", timestamp),
                    themeId = settingsObj.optInt("themeId", 1),
                    themeMode = settingsObj.optString("themeMode", "LIGHT"),
                    customAccent = settingsObj.optString("customAccent", ""),
                    cardStyle = settingsObj.optString("cardStyle", "CLASSIC_PASTEL"),
                    textColorOption = settingsObj.optString("textColorOption", "DEFAULT_NAVY"),
                    symbolPlacement = settingsObj.optString("symbolPlacement", "AFTER_AMOUNT"),
                    decimalPlaces = settingsObj.optInt("decimalPlaces", 2),
                    expirationAlertsEnabled = settingsObj.optBoolean("expirationAlertsEnabled", true),
                    expirationDaysThreshold = settingsObj.optInt("expirationDaysThreshold", 30),
                    lowStockAlertsEnabled = settingsObj.optBoolean("lowStockAlertsEnabled", true),
                    lowStockThreshold = settingsObj.optInt("lowStockThreshold", 5),
                    backupRemindersEnabled = settingsObj.optBoolean("backupRemindersEnabled", true),
                    backupReminderFrequency = settingsObj.optString("backupReminderFrequency", "WEEKLY")
                )
            } else {
                BackupSettingsData()
            }

            // Parse Products
            val productsList = mutableListOf<ProductEntity>()
            val productsArr = payloadObj.optJSONArray("products") ?: JSONArray()
            for (i in 0 until productsArr.length()) {
                val p = productsArr.getJSONObject(i)
                val name = p.optString("name").trim()
                if (name.isNotEmpty()) {
                    productsList.add(
                        ProductEntity(
                            id = p.optLong("id", 0L),
                            name = name,
                            barcode = p.optString("barcode", ""),
                            batchNumber = p.optString("batchNumber", ""),
                            manufacturingDate = p.optString("manufacturingDate", ""),
                            expirationDate = p.optString("expirationDate", ""),
                            costPrice = p.optDouble("costPrice", 0.0).coerceAtLeast(0.0),
                            salePrice = p.optDouble("salePrice", 0.0).coerceAtLeast(0.0),
                            stockQuantity = p.optInt("stockQuantity", 0).coerceAtLeast(0),
                            reservedQuantity = p.optInt("reservedQuantity", 0).coerceAtLeast(0),
                            minStockThreshold = p.optInt("minStockThreshold", 5).coerceAtLeast(0),
                            unit = p.optString("unit", "حبة"),
                            category = p.optString("category", "عام"),
                            supplierName = p.optString("supplierName", ""),
                            notes = p.optString("notes", ""),
                            imageUri = p.optString("imageUri", ""),
                            createdAt = p.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            // Parse Clients
            val clientsList = mutableListOf<ClientEntity>()
            val clientsArr = payloadObj.optJSONArray("clients") ?: JSONArray()
            for (i in 0 until clientsArr.length()) {
                val c = clientsArr.getJSONObject(i)
                val name = c.optString("name").trim()
                if (name.isNotEmpty()) {
                    clientsList.add(
                        ClientEntity(
                            id = c.optLong("id", 0L),
                            name = name,
                            phone = c.optString("phone", ""),
                            address = c.optString("address", ""),
                            location = c.optString("location", ""),
                            currentBalance = c.optDouble("currentBalance", 0.0),
                            notes = c.optString("notes", ""),
                            createdAt = c.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            // Parse Suppliers
            val suppliersList = mutableListOf<SupplierEntity>()
            val suppliersArr = payloadObj.optJSONArray("suppliers") ?: JSONArray()
            for (i in 0 until suppliersArr.length()) {
                val s = suppliersArr.getJSONObject(i)
                val name = s.optString("name").trim()
                if (name.isNotEmpty()) {
                    suppliersList.add(
                        SupplierEntity(
                            id = s.optLong("id", 0L),
                            name = name,
                            phone = s.optString("phone", ""),
                            address = s.optString("address", ""),
                            companyName = s.optString("companyName", ""),
                            outstandingBalance = s.optDouble("outstandingBalance", 0.0),
                            notes = s.optString("notes", ""),
                            createdAt = s.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            // Parse Sales Invoices
            val salesList = mutableListOf<SaleInvoiceEntity>()
            val salesArr = payloadObj.optJSONArray("salesInvoices") ?: JSONArray()
            for (i in 0 until salesArr.length()) {
                val inv = salesArr.getJSONObject(i)
                salesList.add(
                    SaleInvoiceEntity(
                        id = inv.optLong("id", 0L),
                        invoiceNumber = inv.optString("invoiceNumber", "INV-${System.currentTimeMillis() % 100000}"),
                        clientId = inv.optLong("clientId", 0L),
                        clientName = inv.optString("clientName", "عميل عام"),
                        subtotalAmount = inv.optDouble("subtotalAmount", 0.0),
                        discountAmount = inv.optDouble("discountAmount", 0.0),
                        totalAmount = inv.optDouble("totalAmount", 0.0),
                        paidAmount = inv.optDouble("paidAmount", 0.0),
                        remainingAmount = inv.optDouble("remainingAmount", 0.0),
                        profitAmount = inv.optDouble("profitAmount", 0.0),
                        isCredit = inv.optBoolean("isCredit", false),
                        paymentMethod = inv.optString("paymentMethod", "نقدي"),
                        itemsSummary = inv.optString("itemsSummary", ""),
                        notes = inv.optString("notes", ""),
                        dateMillis = inv.optLong("dateMillis", System.currentTimeMillis())
                    )
                )
            }

            // Parse Purchases
            val purchasesList = mutableListOf<PurchaseInvoiceEntity>()
            val purchasesArr = payloadObj.optJSONArray("purchases") ?: JSONArray()
            for (i in 0 until purchasesArr.length()) {
                val pur = purchasesArr.getJSONObject(i)
                purchasesList.add(
                    PurchaseInvoiceEntity(
                        id = pur.optLong("id", 0L),
                        invoiceNumber = pur.optString("invoiceNumber", "PUR-${System.currentTimeMillis() % 100000}"),
                        supplierId = pur.optLong("supplierId", 0L),
                        supplierName = pur.optString("supplierName", "مورد عام"),
                        dateMillis = pur.optLong("dateMillis", System.currentTimeMillis()),
                        subtotalAmount = pur.optDouble("subtotalAmount", 0.0),
                        discountAmount = pur.optDouble("discountAmount", 0.0),
                        totalAmount = pur.optDouble("totalAmount", 0.0),
                        paidAmount = pur.optDouble("paidAmount", 0.0),
                        remainingAmount = pur.optDouble("remainingAmount", 0.0),
                        paymentMethod = pur.optString("paymentMethod", "نقداً"),
                        isCredit = pur.optBoolean("isCredit", false),
                        itemsSummary = pur.optString("itemsSummary", ""),
                        notes = pur.optString("notes", "")
                    )
                )
            }

            // Parse Receipts
            val receiptsList = mutableListOf<PaymentReceiptEntity>()
            val receiptsArr = payloadObj.optJSONArray("receipts") ?: JSONArray()
            for (i in 0 until receiptsArr.length()) {
                val r = receiptsArr.getJSONObject(i)
                receiptsList.add(
                    PaymentReceiptEntity(
                        id = r.optLong("id", 0L),
                        receiptNumber = r.optString("receiptNumber", "REC-${System.currentTimeMillis() % 100000}"),
                        clientId = r.optLong("clientId", 0L),
                        clientName = r.optString("clientName", ""),
                        amount = r.optDouble("amount", 0.0),
                        paymentMethod = r.optString("paymentMethod", "نقداً"),
                        notes = r.optString("notes", ""),
                        dateMillis = r.optLong("dateMillis", System.currentTimeMillis())
                    )
                )
            }

            // Parse Supplier Payments
            val supplierPaymentsList = mutableListOf<SupplierPaymentEntity>()
            val spArr = payloadObj.optJSONArray("supplierPayments") ?: JSONArray()
            for (i in 0 until spArr.length()) {
                val sp = spArr.getJSONObject(i)
                supplierPaymentsList.add(
                    SupplierPaymentEntity(
                        id = sp.optLong("id", 0L),
                        paymentNumber = sp.optString("paymentNumber", "SPAY-${System.currentTimeMillis() % 100000}"),
                        supplierId = sp.optLong("supplierId", 0L),
                        supplierName = sp.optString("supplierName", ""),
                        amount = sp.optDouble("amount", 0.0),
                        paymentMethod = sp.optString("paymentMethod", "نقداً"),
                        notes = sp.optString("notes", ""),
                        dateMillis = sp.optLong("dateMillis", System.currentTimeMillis())
                    )
                )
            }

            // Parse Expenses
            val expensesList = mutableListOf<ExpenseEntity>()
            val expensesArr = payloadObj.optJSONArray("expenses") ?: JSONArray()
            for (i in 0 until expensesArr.length()) {
                val exp = expensesArr.getJSONObject(i)
                expensesList.add(
                    ExpenseEntity(
                        id = exp.optLong("id", 0L),
                        title = exp.optString("title", "مصروف"),
                        category = exp.optString("category", "عام"),
                        amount = exp.optDouble("amount", 0.0),
                        dateMillis = exp.optLong("dateMillis", System.currentTimeMillis()),
                        paymentMethod = exp.optString("paymentMethod", "نقداً"),
                        notes = exp.optString("notes", "")
                    )
                )
            }

            // Parse Stock Movements
            val stockList = mutableListOf<StockMovementEntity>()
            val stockArr = payloadObj.optJSONArray("stockMovements") ?: JSONArray()
            for (i in 0 until stockArr.length()) {
                val sm = stockArr.getJSONObject(i)
                stockList.add(
                    StockMovementEntity(
                        id = sm.optLong("id", 0L),
                        productId = sm.optLong("productId", 0L),
                        productName = sm.optString("productName", ""),
                        movementType = sm.optString("movementType", "STOCK_IN"),
                        movementTypeArabic = sm.optString("movementTypeArabic", "توريد"),
                        quantity = sm.optInt("quantity", 0),
                        previousStock = sm.optInt("previousStock", 0),
                        newStock = sm.optInt("newStock", 0),
                        referenceNumber = sm.optString("referenceNumber", ""),
                        reason = sm.optString("reason", ""),
                        dateMillis = sm.optLong("dateMillis", System.currentTimeMillis())
                    )
                )
            }

            val totalRecords = productsList.size + clientsList.size + suppliersList.size +
                    salesList.size + purchasesList.size + receiptsList.size +
                    supplierPaymentsList.size + expensesList.size + stockList.size

            val payload = BackupPayload(
                settings = settings,
                products = productsList,
                clients = clientsList,
                suppliers = suppliersList,
                salesInvoices = salesList,
                purchases = purchasesList,
                receipts = receiptsList,
                supplierPayments = supplierPaymentsList,
                expenses = expensesList,
                stockMovements = stockList
            )

            BackupValidationResult(
                isValid = true,
                formatVersion = formatVersion,
                timestamp = timestamp,
                totalRecords = totalRecords,
                productsCount = productsList.size,
                clientsCount = clientsList.size,
                suppliersCount = suppliersList.size,
                invoicesCount = salesList.size,
                purchasesCount = purchasesList.size,
                receiptsCount = receiptsList.size,
                supplierPaymentsCount = supplierPaymentsList.size,
                expensesCount = expensesList.size,
                stockMovementsCount = stockList.size,
                settings = settings,
                parsedPayload = payload
            )
        } catch (e: Exception) {
            BackupValidationResult(
                isValid = false,
                errorMessage = "خطأ في قراءة ملف النسخة الاحتياطية: ${e.localizedMessage ?: "تنسيق غير سليم"}"
            )
        }
    }

    // -------------------------------------------------------------
    // Atomic Database Restore Transaction
    // -------------------------------------------------------------

    suspend fun restoreFromPayload(
        appDao: AppDao,
        payload: BackupPayload
    ): Result<Int> {
        return try {
            // Uses Room's @Transaction restoreAllData method so everything is atomic
            appDao.restoreAllData(
                products = payload.products,
                clients = payload.clients,
                suppliers = payload.suppliers,
                supplierPayments = payload.supplierPayments,
                invoices = payload.salesInvoices,
                purchases = payload.purchases,
                stockMovements = payload.stockMovements,
                expenses = payload.expenses,
                receipts = payload.receipts
            )

            val totalRestored = payload.products.size + payload.clients.size + payload.suppliers.size +
                    payload.salesInvoices.size + payload.purchases.size + payload.receipts.size +
                    payload.supplierPayments.size + payload.expenses.size + payload.stockMovements.size

            Result.success(totalRestored)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // Restore from File or InputStream
    // -------------------------------------------------------------

    suspend fun restoreFromFile(
        file: File,
        appDao: AppDao
    ): Result<BackupValidationResult> {
        return try {
            val content = file.readText(Charsets.UTF_8)
            val validation = validateBackupContent(content)
            if (!validation.isValid) {
                return Result.failure(Exception(validation.errorMessage ?: "فشل التحقق من الملف"))
            }

            val payload = validation.parsedPayload
                ?: return Result.failure(Exception("تعذر استخراج بيانات النسخة"))

            restoreFromPayload(appDao, payload).getOrThrow()
            Result.success(validation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreFromInputStream(
        inputStream: InputStream,
        appDao: AppDao
    ): Result<BackupValidationResult> {
        return try {
            val content = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val validation = validateBackupContent(content)
            if (!validation.isValid) {
                return Result.failure(Exception(validation.errorMessage ?: "فشل التحقق من الملف"))
            }

            val payload = validation.parsedPayload
                ?: return Result.failure(Exception("تعذر استخراج بيانات النسخة"))

            restoreFromPayload(appDao, payload).getOrThrow()
            Result.success(validation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // History & Local Backups List
    // -------------------------------------------------------------

    fun getLocalBackupsList(context: Context): List<BackupFileInfo> {
        val dir = getBackupsDir(context)
        val files = dir.listFiles { _, name -> name.endsWith(".json") } ?: return emptyList()

        files.sortByDescending { it.lastModified() }

        return files.mapNotNull { file ->
            try {
                val size = file.length()
                val modified = file.lastModified()
                val type = when {
                    file.name.contains("daily", ignoreCase = true) -> "يومي تلقائي"
                    file.name.contains("weekly", ignoreCase = true) -> "أسبوعي تلقائي"
                    else -> "يدوي"
                }

                BackupFileInfo(
                    fileName = file.name,
                    filePath = file.absolutePath,
                    fileSizeBytes = size,
                    formattedSize = formatFileSize(size),
                    timestamp = modified,
                    formattedDate = displayDateFormat.format(Date(modified)),
                    backupType = type,
                    recordsCount = estimateRecordCount(file),
                    isValid = true
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun estimateRecordCount(file: File): Int {
        return try {
            val preview = FileInputStream(file).use { fis ->
                val buffer = ByteArray(minOf(file.length().toInt(), 4096))
                fis.read(buffer)
                String(buffer, Charsets.UTF_8)
            }
            val obj = JSONObject(preview)
            obj.optInt("totalRecords", 0)
        } catch (_: Exception) {
            0
        }
    }

    fun deleteBackupFile(filePath: String): Boolean {
        return try {
            val f = File(filePath)
            if (f.exists()) f.delete() else false
        } catch (_: Exception) {
            false
        }
    }

    // -------------------------------------------------------------
    // Share / Export Backup File
    // -------------------------------------------------------------

    fun shareBackupFile(context: Context, filePath: String): Intent {
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "نسخة احتياطية - تطبيق مندوبك")
            putExtra(Intent.EXTRA_TEXT, "نسخة احتياطية لقاعدة بيانات مندوبك (${file.name})")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    // -------------------------------------------------------------
    // Comprehensive CSV Data Export
    // -------------------------------------------------------------

    suspend fun exportAllDataCsv(
        context: Context,
        appDao: AppDao,
        currencySymbol: String
    ): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mandoubak_Complete_Export_${fileNameDateFormat.format(Date())}.csv"
            val file = File(exportDir, fileName)

            val products = appDao.getAllProductsList()
            val clients = appDao.getAllClientsList()
            val suppliers = appDao.getAllSuppliersList()
            val invoices = appDao.getAllInvoicesList()
            val expenses = appDao.getAllExpensesList()

            FileOutputStream(file).use { fos ->
                // Write UTF-8 BOM
                fos.write(0xEF)
                fos.write(0xBB)
                fos.write(0xBF)

                OutputStreamWriter(fos, Charsets.UTF_8).use { writer ->
                    // 1. Products Section
                    writer.append("=== جدول المنتجات والمخزون ===\n")
                    writer.append("رقم الصنف,اسم المنتج,الباركود,التصنيف,سعر الشراء,سعر البيع,الرصيد المتوفر,حد الأمان,الوحدة,المورد\n")
                    for (p in products) {
                        writer.append("${p.id},\"${p.name.replace("\"", "\"\"")}\",\"${p.barcode}\",\"${p.category}\",${p.costPrice},${p.salePrice},${p.stockQuantity},${p.minStockThreshold},\"${p.unit}\",\"${p.supplierName}\"\n")
                    }

                    writer.append("\n\n")

                    // 2. Clients Section
                    writer.append("=== جدول العملاء ومستحقاتهم ===\n")
                    writer.append("رقم العميل,اسم العميل,الهاتف,العنوان,الرصيد المالي الحالي ($currencySymbol),ملاحظات\n")
                    for (c in clients) {
                        writer.append("${c.id},\"${c.name.replace("\"", "\"\"")}\",\"${c.phone}\",\"${c.address}\",${c.currentBalance},\"${c.notes}\"\n")
                    }

                    writer.append("\n\n")

                    // 3. Suppliers Section
                    writer.append("=== جدول الموردين ===\n")
                    writer.append("رقم المورد,اسم المورد,الشركة,الهاتف,العنوان,الرصيد المستحق للمورد ($currencySymbol)\n")
                    for (s in suppliers) {
                        writer.append("${s.id},\"${s.name.replace("\"", "\"\"")}\",\"${s.companyName}\",\"${s.phone}\",\"${s.address}\",${s.outstandingBalance}\n")
                    }

                    writer.append("\n\n")

                    // 4. Sales Invoices Section
                    writer.append("=== جدول فواتير المبيعات ===\n")
                    writer.append("رقم الفاتورة,التاريخ,العميل,الإجمالي ($currencySymbol),المدفوع,المتبقي,الربح المقدر,طريقة الدفع,الأصناف المباعة\n")
                    for (inv in invoices) {
                        val dateStr = displayDateFormat.format(Date(inv.dateMillis))
                        writer.append("${inv.invoiceNumber},$dateStr,\"${inv.clientName}\",${inv.totalAmount},${inv.paidAmount},${inv.remainingAmount},${inv.profitAmount},\"${inv.paymentMethod}\",\"${inv.itemsSummary.replace("\"", "\"\"")}\"\n")
                    }

                    writer.append("\n\n")

                    // 5. Expenses Section
                    writer.append("=== جدول المصروفات ===\n")
                    writer.append("رقم الإيصال,بيان المصروف,البند,المبلغ ($currencySymbol),التاريخ,طريقة الدفع,ملاحظات\n")
                    for (exp in expenses) {
                        val dateStr = displayDateFormat.format(Date(exp.dateMillis))
                        writer.append("${exp.id},\"${exp.title}\",\"${exp.category}\",${exp.amount},$dateStr,\"${exp.paymentMethod}\",\"${exp.notes}\"\n")
                    }

                    writer.flush()
                }
            }

            file
        } catch (_: Exception) {
            null
        }
    }
}
