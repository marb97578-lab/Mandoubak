package com.example.data.repository

import android.content.Context
import com.example.data.dao.AppDao
import com.example.util.BackupFileInfo
import com.example.util.BackupManager
import com.example.util.BackupSettingsData
import com.example.util.BackupValidationResult
import java.io.File
import java.io.InputStream
import com.example.data.entity.ClientEntity
import com.example.data.entity.CurrencySettingEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.LanguageSettingEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.data.entity.StockMovementEntity
import com.example.data.entity.SupplierEntity
import com.example.data.entity.SupplierPaymentEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class MandoubakRepository(private val appDao: AppDao) {

    private fun getStartOfDayMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    // Products
    val allProducts: Flow<List<ProductEntity>> = appDao.getAllProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = appDao.getLowStockProducts()
    val lowStockCount: Flow<Int> = appDao.getLowStockCount()
    val outOfStockCount: Flow<Int> = appDao.getOutOfStockCount()
    val totalProductsCount: Flow<Int> = appDao.getTotalProductsCount()

    suspend fun insertProduct(product: ProductEntity): Long {
        val id = appDao.insertProduct(product)
        if (product.stockQuantity > 0) {
            appDao.insertStockMovement(
                StockMovementEntity(
                    productId = id,
                    productName = product.name,
                    movementType = "STOCK_IN",
                    movementTypeArabic = "رصيد افتتاحي",
                    quantity = product.stockQuantity,
                    previousStock = 0,
                    newStock = product.stockQuantity,
                    referenceNumber = "INIT-${id}",
                    reason = "رصيد أولي عند إضافة المنتج"
                )
            )
        }
        return id
    }

    suspend fun updateProduct(product: ProductEntity) = appDao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = appDao.deleteProduct(product)

    suspend fun getProductByBarcode(barcode: String): ProductEntity? = appDao.getProductByBarcode(barcode.trim())
    suspend fun isBarcodeExists(barcode: String, excludeId: Long = 0): Boolean {
        if (barcode.isBlank()) return false
        return appDao.countProductsWithBarcode(barcode.trim(), excludeId) > 0
    }

    suspend fun updateStockDirect(product: ProductEntity, newStock: Int) {
        appDao.updateStockDirectTransaction(product.id, product.name, product.stockQuantity, newStock)
    }

    // Suppliers (الموردين)
    val allSuppliers: Flow<List<SupplierEntity>> = appDao.getAllSuppliers()
    val totalSuppliersCount: Flow<Int> = appDao.getTotalSuppliersCount()

    suspend fun insertSupplier(supplier: SupplierEntity): Long = appDao.insertSupplier(supplier)
    suspend fun updateSupplier(supplier: SupplierEntity) = appDao.updateSupplier(supplier)
    suspend fun deleteSupplier(supplier: SupplierEntity) = appDao.deleteSupplier(supplier)
    suspend fun adjustSupplierBalance(supplierId: Long, delta: Double) = appDao.adjustSupplierBalance(supplierId, delta)

    // Supplier Payments (سندات صرف لمستحقات الموردين)
    val allSupplierPayments: Flow<List<SupplierPaymentEntity>> = appDao.getAllSupplierPayments()
    fun getSupplierPaymentsForSupplier(supplierId: Long): Flow<List<SupplierPaymentEntity>> = appDao.getSupplierPaymentsForSupplier(supplierId)

    suspend fun createSupplierPayment(
        supplierId: Long,
        supplierName: String,
        amount: Double,
        paymentMethod: String = "نقداً",
        notes: String = ""
    ): Long {
        val paymentNum = "SPAY-${System.currentTimeMillis() % 100000}"
        val payment = SupplierPaymentEntity(
            paymentNumber = paymentNum,
            supplierId = supplierId,
            supplierName = supplierName,
            amount = amount,
            paymentMethod = paymentMethod,
            notes = notes,
            dateMillis = System.currentTimeMillis()
        )
        return appDao.createSupplierPaymentTransaction(payment)
    }

    suspend fun deleteSupplierPayment(payment: SupplierPaymentEntity) {
        appDao.deleteSupplierPaymentTransaction(payment)
    }

    // Clients
    val allClients: Flow<List<ClientEntity>> = appDao.getAllClients()
    val totalClientsCount: Flow<Int> = appDao.getTotalClientsCount()

    suspend fun insertClient(client: ClientEntity): Long = appDao.insertClient(client)
    suspend fun updateClient(client: ClientEntity) = appDao.updateClient(client)
    suspend fun deleteClient(client: ClientEntity) = appDao.deleteClient(client)

    // Sales
    val allInvoices: Flow<List<SaleInvoiceEntity>> = appDao.getAllInvoices()
    fun getTodayInvoices(): Flow<List<SaleInvoiceEntity>> = appDao.getTodayInvoices(getStartOfDayMillis())
    fun getTodayInvoicesCount(): Flow<Int> = appDao.getTodayInvoicesCount(getStartOfDayMillis())
    fun getTodaySalesTotal(): Flow<Double> = appDao.getTodaySalesTotal(getStartOfDayMillis())
    val allSalesTotal: Flow<Double> = appDao.getAllSalesTotal()

    suspend fun createInvoice(
        client: ClientEntity,
        items: List<Pair<ProductEntity, Int>>, // product and quantity
        subtotalAmount: Double,
        discountAmount: Double,
        totalAmount: Double,
        paidAmount: Double,
        isCredit: Boolean,
        paymentMethod: String = "نقدي",
        notes: String = ""
    ): SaleInvoiceEntity {
        val invoiceNum = "INV-${System.currentTimeMillis() % 100000}"
        val summary = if (items.isNotEmpty()) {
            items.joinToString("، ") { "${it.first.name} (×${it.second} ${it.first.unit.ifBlank { "حبة" }})" }
        } else {
            if (notes.isNotBlank()) notes else "فاتورة مبيعات مباشرة"
        }
        val remaining = if (isCredit) (totalAmount - paidAmount).coerceAtLeast(0.0) else 0.0
        val totalCost = items.sumOf { it.first.costPrice * it.second }
        val profit = if (items.isNotEmpty()) {
            (totalAmount - totalCost).coerceAtLeast(0.0)
        } else {
            (totalAmount * 0.20)
        }

        val invoice = SaleInvoiceEntity(
            invoiceNumber = invoiceNum,
            clientId = client.id,
            clientName = client.name,
            subtotalAmount = subtotalAmount,
            discountAmount = discountAmount,
            totalAmount = totalAmount,
            paidAmount = paidAmount,
            remainingAmount = remaining,
            profitAmount = profit,
            isCredit = isCredit,
            paymentMethod = paymentMethod,
            itemsSummary = summary,
            notes = notes,
            dateMillis = System.currentTimeMillis()
        )

        val id = appDao.createInvoiceTransaction(
            invoice = invoice,
            items = items.map { Pair(it.first.id, it.second) },
            clientDebtAdjustment = if (remaining > 0) remaining else 0.0
        )
        return invoice.copy(id = id)
    }

    suspend fun updateInvoice(invoice: SaleInvoiceEntity) = appDao.updateInvoice(invoice)

    suspend fun deleteInvoice(invoice: SaleInvoiceEntity) {
        appDao.deleteInvoiceTransaction(invoice)
    }

    // Purchases (المشتريات)
    val allPurchases: Flow<List<PurchaseInvoiceEntity>> = appDao.getAllPurchases()
    fun getPurchasesForSupplier(supplierId: Long): Flow<List<PurchaseInvoiceEntity>> = appDao.getPurchasesForSupplier(supplierId)
    fun getPurchasesForSupplierName(supplierName: String): Flow<List<PurchaseInvoiceEntity>> = appDao.getPurchasesForSupplierName(supplierName)
    fun getTodayPurchasesCount(): Flow<Int> = appDao.getTodayPurchasesCount(getStartOfDayMillis())
    fun getTodayPurchasesTotal(): Flow<Double> = appDao.getTodayPurchasesTotal(getStartOfDayMillis())
    val allPurchasesTotal: Flow<Double> = appDao.getAllPurchasesTotal()

    suspend fun createPurchase(
        supplierId: Long = 0,
        supplierName: String,
        items: List<Pair<ProductEntity, Int>>,
        subtotalAmount: Double = 0.0,
        discountAmount: Double = 0.0,
        totalAmount: Double,
        paidAmount: Double,
        paymentMethod: String = "نقداً",
        isCredit: Boolean,
        notes: String
    ): Long {
        val purchaseNum = "PUR-${System.currentTimeMillis() % 100000}"
        val summary = items.joinToString("، ") { "${it.first.name} (×${it.second})" }
        val remaining = if (isCredit) (totalAmount - paidAmount).coerceAtLeast(0.0) else 0.0

        val purchase = PurchaseInvoiceEntity(
            invoiceNumber = purchaseNum,
            supplierId = supplierId,
            supplierName = supplierName,
            subtotalAmount = if (subtotalAmount > 0) subtotalAmount else totalAmount + discountAmount,
            discountAmount = discountAmount,
            totalAmount = totalAmount,
            paidAmount = paidAmount,
            remainingAmount = remaining,
            paymentMethod = paymentMethod,
            isCredit = isCredit,
            itemsSummary = summary,
            notes = notes,
            dateMillis = System.currentTimeMillis()
        )

        return appDao.createPurchaseTransaction(
            purchase = purchase,
            items = items.map { Pair(it.first.id, it.second) },
            supplierDebtAdjustment = if (remaining > 0 && supplierId > 0) remaining else 0.0
        )
    }

    suspend fun deletePurchase(purchase: PurchaseInvoiceEntity) {
        appDao.deletePurchaseTransaction(purchase)
    }

    // Stock Movements (سجل حركات المخزون والتسويات)
    val allStockMovements: Flow<List<StockMovementEntity>> = appDao.getAllStockMovements()
    fun getStockMovementsForProduct(productId: Long): Flow<List<StockMovementEntity>> = appDao.getStockMovementsForProduct(productId)

    suspend fun recordManualAdjustment(
        product: ProductEntity,
        type: String, // "RETURNED", "DAMAGED", "ADJUSTMENT"
        typeArabic: String,
        quantityDelta: Int,
        reason: String
    ) {
        val prev = product.stockQuantity
        val newStock = (prev + quantityDelta).coerceAtLeast(0)
        appDao.recordManualAdjustmentTransaction(
            productId = product.id,
            productName = product.name,
            prevStock = prev,
            newStock = newStock,
            type = type,
            typeArabic = typeArabic,
            quantityDelta = quantityDelta,
            reason = reason
        )
    }

    // Expenses (المصروفات)
    val allExpenses: Flow<List<ExpenseEntity>> = appDao.getAllExpenses()
    fun getTodayExpensesTotal(): Flow<Double> = appDao.getTodayExpensesTotal(getStartOfDayMillis())
    val allExpensesTotal: Flow<Double> = appDao.getAllExpensesTotal()

    suspend fun createExpense(
        title: String,
        category: String,
        amount: Double,
        paymentMethod: String,
        notes: String
    ): Long {
        val exp = ExpenseEntity(
            title = title,
            category = category,
            amount = amount,
            paymentMethod = paymentMethod,
            notes = notes,
            dateMillis = System.currentTimeMillis()
        )
        return appDao.insertExpense(exp)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = appDao.deleteExpense(expense)

    // Payment Receipts (سند القبض)
    val allReceipts: Flow<List<PaymentReceiptEntity>> = appDao.getAllReceipts()
    fun getTodayReceipts(): Flow<List<PaymentReceiptEntity>> = appDao.getTodayReceipts(getStartOfDayMillis())
    fun getTodayReceiptsCount(): Flow<Int> = appDao.getTodayReceiptsCount(getStartOfDayMillis())
    fun getTodayReceiptsTotal(): Flow<Double> = appDao.getTodayReceiptsTotal(getStartOfDayMillis())

    suspend fun createReceipt(
        client: ClientEntity,
        amount: Double,
        paymentMethod: String,
        notes: String
    ): Long {
        val receiptNum = "REC-${System.currentTimeMillis() % 100000}"
        val receipt = PaymentReceiptEntity(
            receiptNumber = receiptNum,
            clientId = client.id,
            clientName = client.name,
            amount = amount,
            paymentMethod = paymentMethod,
            notes = notes,
            dateMillis = System.currentTimeMillis()
        )
        return appDao.createReceiptTransaction(receipt)
    }

    suspend fun deleteReceipt(receipt: PaymentReceiptEntity) {
        appDao.deleteReceiptTransaction(receipt)
    }

    // Clear all data
    suspend fun clearAllData() {
        appDao.clearProducts()
        appDao.clearClients()
        appDao.clearSuppliers()
        appDao.clearSupplierPayments()
        appDao.clearInvoices()
        appDao.clearPurchases()
        appDao.clearStockMovements()
        appDao.clearExpenses()
        appDao.clearReceipts()
    }

    // Clear test transactions only (keep products, clients, suppliers)
    suspend fun clearTestTransactionsOnly() {
        appDao.clearTestTransactionsOnly()
    }

    // Backup & Restore
    suspend fun createBackup(
        context: Context,
        settings: BackupSettingsData,
        backupType: String = "MANUAL"
    ): Result<BackupFileInfo> {
        return BackupManager.createBackup(context, appDao, settings, backupType)
    }

    suspend fun checkAutoBackup(
        context: Context,
        settings: BackupSettingsData
    ): BackupFileInfo? {
        return BackupManager.checkAndPerformAutoBackup(context, appDao, settings)
    }

    fun getLocalBackups(context: Context): List<BackupFileInfo> {
        return BackupManager.getLocalBackupsList(context)
    }

    suspend fun restoreFromFile(file: File): Result<BackupValidationResult> {
        return BackupManager.restoreFromFile(file, appDao)
    }

    suspend fun restoreFromInputStream(inputStream: InputStream): Result<BackupValidationResult> {
        return BackupManager.restoreFromInputStream(inputStream, appDao)
    }

    suspend fun exportAllDataCsv(context: Context, currencySymbol: String): File? {
        return BackupManager.exportAllDataCsv(context, appDao, currencySymbol)
    }

    fun deleteBackupFile(filePath: String): Boolean {
        return BackupManager.deleteBackupFile(filePath)
    }

    // Clean start: No automatic demo or sample data is populated.
    // The user will add all real data manually.
    suspend fun populateSampleDataIfEmpty(currentProductsCount: Int) {
        // Application starts clean and empty as requested
    }

    // Currency Settings Database Persistence
    fun getCurrencySettingFlow(): Flow<CurrencySettingEntity?> = appDao.getCurrencySettingFlow()

    suspend fun getCurrencySetting(): CurrencySettingEntity? = appDao.getCurrencySetting()

    suspend fun saveCurrencySetting(setting: CurrencySettingEntity) {
        appDao.saveCurrencySetting(setting)
    }

    // Language Settings Database Persistence
    fun getLanguageSettingFlow(): Flow<LanguageSettingEntity?> = appDao.getLanguageSettingFlow()

    suspend fun getLanguageSetting(): LanguageSettingEntity? = appDao.getLanguageSetting()

    suspend fun saveLanguageSetting(setting: LanguageSettingEntity) {
        appDao.saveLanguageSetting(setting)
    }
}

