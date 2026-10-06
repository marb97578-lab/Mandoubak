package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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

@Dao
interface AppDao {

    // Products
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE stockQuantity <= minStockThreshold ORDER BY stockQuantity ASC")
    fun getLowStockProducts(): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity <= minStockThreshold")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity = 0")
    fun getOutOfStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products")
    fun getTotalProductsCount(): Flow<Int>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    @Query("SELECT COUNT(*) FROM products WHERE barcode = :barcode AND (:excludeId <= 0 OR id != :excludeId)")
    suspend fun countProductsWithBarcode(barcode: String, excludeId: Long = 0): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :productId AND stockQuantity >= :quantity")
    suspend fun deductStock(productId: Long, quantity: Int): Int

    @Query("UPDATE products SET stockQuantity = stockQuantity + :quantity WHERE id = :productId")
    suspend fun increaseStock(productId: Long, quantity: Int): Int

    @Query("UPDATE products SET stockQuantity = :newStock WHERE id = :productId")
    suspend fun updateStockDirect(productId: Long, newStock: Int): Int

    // Suppliers (الموردين)
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getSupplierById(id: Long): SupplierEntity?

    @Query("SELECT COUNT(*) FROM suppliers")
    fun getTotalSuppliersCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuppliers(suppliers: List<SupplierEntity>)

    @Update
    suspend fun updateSupplier(supplier: SupplierEntity)

    @Query("UPDATE suppliers SET outstandingBalance = outstandingBalance + :amountDelta WHERE id = :supplierId")
    suspend fun adjustSupplierBalance(supplierId: Long, amountDelta: Double)

    @Delete
    suspend fun deleteSupplier(supplier: SupplierEntity)

    // Supplier Payments (سندات صرف لمستحقات الموردين)
    @Query("SELECT * FROM supplier_payments ORDER BY dateMillis DESC")
    fun getAllSupplierPayments(): Flow<List<SupplierPaymentEntity>>

    @Query("SELECT * FROM supplier_payments WHERE supplierId = :supplierId ORDER BY dateMillis DESC")
    fun getSupplierPaymentsForSupplier(supplierId: Long): Flow<List<SupplierPaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplierPayment(payment: SupplierPaymentEntity): Long

    @Delete
    suspend fun deleteSupplierPayment(payment: SupplierPaymentEntity)

    // Clients
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: Long): ClientEntity?

    @Query("SELECT COUNT(*) FROM clients")
    fun getTotalClientsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Query("UPDATE clients SET currentBalance = currentBalance + :amountDelta WHERE id = :clientId")
    suspend fun adjustClientBalance(clientId: Long, amountDelta: Double)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    // Sale Invoices
    @Query("SELECT * FROM sale_invoices ORDER BY dateMillis DESC")
    fun getAllInvoices(): Flow<List<SaleInvoiceEntity>>

    @Query("SELECT * FROM sale_invoices WHERE dateMillis >= :startOfDay ORDER BY dateMillis DESC")
    fun getTodayInvoices(startOfDay: Long): Flow<List<SaleInvoiceEntity>>

    @Query("SELECT COUNT(*) FROM sale_invoices WHERE dateMillis >= :startOfDay")
    fun getTodayInvoicesCount(startOfDay: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM sale_invoices WHERE dateMillis >= :startOfDay")
    fun getTodaySalesTotal(startOfDay: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM sale_invoices")
    fun getAllSalesTotal(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: SaleInvoiceEntity): Long

    @Update
    suspend fun updateInvoice(invoice: SaleInvoiceEntity)

    @Delete
    suspend fun deleteInvoice(invoice: SaleInvoiceEntity)

    // Purchases (المشتريات)
    @Query("SELECT * FROM purchases ORDER BY dateMillis DESC")
    fun getAllPurchases(): Flow<List<PurchaseInvoiceEntity>>

    @Query("SELECT * FROM purchases WHERE supplierId = :supplierId ORDER BY dateMillis DESC")
    fun getPurchasesForSupplier(supplierId: Long): Flow<List<PurchaseInvoiceEntity>>

    @Query("SELECT * FROM purchases WHERE supplierName = :supplierName ORDER BY dateMillis DESC")
    fun getPurchasesForSupplierName(supplierName: String): Flow<List<PurchaseInvoiceEntity>>

    @Query("SELECT COUNT(*) FROM purchases WHERE dateMillis >= :startOfDay")
    fun getTodayPurchasesCount(startOfDay: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM purchases WHERE dateMillis >= :startOfDay")
    fun getTodayPurchasesTotal(startOfDay: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM purchases")
    fun getAllPurchasesTotal(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseInvoiceEntity): Long

    @Delete
    suspend fun deletePurchase(purchase: PurchaseInvoiceEntity)

    // Stock Movements (حركات المخزون)
    @Query("SELECT * FROM stock_movements ORDER BY dateMillis DESC")
    fun getAllStockMovements(): Flow<List<StockMovementEntity>>

    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY dateMillis DESC")
    fun getStockMovementsForProduct(productId: Long): Flow<List<StockMovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovement(movement: StockMovementEntity): Long

    // Expenses (المصروفات)
    @Query("SELECT * FROM expenses ORDER BY dateMillis DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE dateMillis >= :startOfDay")
    fun getTodayExpensesTotal(startOfDay: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses")
    fun getAllExpensesTotal(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    // Payment Receipts (سند القبض)
    @Query("SELECT * FROM payment_receipts ORDER BY dateMillis DESC")
    fun getAllReceipts(): Flow<List<PaymentReceiptEntity>>

    @Query("SELECT * FROM payment_receipts WHERE dateMillis >= :startOfDay ORDER BY dateMillis DESC")
    fun getTodayReceipts(startOfDay: Long): Flow<List<PaymentReceiptEntity>>

    @Query("SELECT COUNT(*) FROM payment_receipts WHERE dateMillis >= :startOfDay")
    fun getTodayReceiptsCount(startOfDay: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM payment_receipts WHERE dateMillis >= :startOfDay")
    fun getTodayReceiptsTotal(startOfDay: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: PaymentReceiptEntity): Long

    @Delete
    suspend fun deleteReceipt(receipt: PaymentReceiptEntity)

    // Direct Lists for Backup & Integrity Validation
    @Query("SELECT * FROM products ORDER BY id ASC")
    suspend fun getAllProductsList(): List<ProductEntity>

    @Query("SELECT * FROM clients ORDER BY id ASC")
    suspend fun getAllClientsList(): List<ClientEntity>

    @Query("SELECT * FROM suppliers ORDER BY id ASC")
    suspend fun getAllSuppliersList(): List<SupplierEntity>

    @Query("SELECT * FROM supplier_payments ORDER BY id ASC")
    suspend fun getAllSupplierPaymentsList(): List<SupplierPaymentEntity>

    @Query("SELECT * FROM sale_invoices ORDER BY id ASC")
    suspend fun getAllInvoicesList(): List<SaleInvoiceEntity>

    @Query("SELECT * FROM purchases ORDER BY id ASC")
    suspend fun getAllPurchasesList(): List<PurchaseInvoiceEntity>

    @Query("SELECT * FROM stock_movements ORDER BY id ASC")
    suspend fun getAllStockMovementsList(): List<StockMovementEntity>

    @Query("SELECT * FROM expenses ORDER BY id ASC")
    suspend fun getAllExpensesList(): List<ExpenseEntity>

    @Query("SELECT * FROM payment_receipts ORDER BY id ASC")
    suspend fun getAllReceiptsList(): List<PaymentReceiptEntity>

    // Bulk Inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplierPayments(payments: List<SupplierPaymentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<SaleInvoiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchases(purchases: List<PurchaseInvoiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovements(movements: List<StockMovementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipts(receipts: List<PaymentReceiptEntity>)

    // Clear all data (for Data Management reset)
    @Query("DELETE FROM products")
    suspend fun clearProducts()

    @Query("DELETE FROM clients")
    suspend fun clearClients()

    @Query("DELETE FROM suppliers")
    suspend fun clearSuppliers()

    @Query("DELETE FROM supplier_payments")
    suspend fun clearSupplierPayments()

    @Query("DELETE FROM sale_invoices")
    suspend fun clearInvoices()

    @Query("DELETE FROM purchases")
    suspend fun clearPurchases()

    @Query("DELETE FROM stock_movements")
    suspend fun clearStockMovements()

    @Query("DELETE FROM expenses")
    suspend fun clearExpenses()

    @Query("DELETE FROM payment_receipts")
    suspend fun clearReceipts()

    // Clear Test Data (Transaction history only, keeps products/clients/suppliers with 0 balance)
    @Transaction
    suspend fun clearTestTransactionsOnly() {
        clearInvoices()
        clearPurchases()
        clearStockMovements()
        clearExpenses()
        clearReceipts()
        clearSupplierPayments()
        // Reset balances
        resetAllClientBalances()
        resetAllSupplierBalances()
    }

    @Query("UPDATE clients SET currentBalance = 0.0")
    suspend fun resetAllClientBalances()

    @Query("UPDATE suppliers SET outstandingBalance = 0.0")
    suspend fun resetAllSupplierBalances()

    // Atomic Transaction: Create Sale Invoice with stock deductions, movements, and balance adjustments
    @Transaction
    suspend fun createInvoiceTransaction(
        invoice: SaleInvoiceEntity,
        items: List<Pair<Long, Int>>, // productId, quantity
        clientDebtAdjustment: Double
    ): Long {
        for ((productId, qty) in items) {
            val product = getProductById(productId)
            val prevStock = product?.stockQuantity ?: 0
            val newStock = (prevStock - qty).coerceAtLeast(0)
            deductStock(productId, qty)
            insertStockMovement(
                StockMovementEntity(
                    productId = productId,
                    productName = product?.name ?: "",
                    movementType = "STOCK_OUT",
                    movementTypeArabic = "صرف مبيعات",
                    quantity = -qty,
                    previousStock = prevStock,
                    newStock = newStock,
                    referenceNumber = invoice.invoiceNumber,
                    reason = "فاتورة بيع للعميل ${invoice.clientName}"
                )
            )
        }
        if (clientDebtAdjustment > 0) {
            adjustClientBalance(invoice.clientId, clientDebtAdjustment)
        }
        return insertInvoice(invoice)
    }

    // Atomic Transaction: Delete Sale Invoice and reverse client balance
    @Transaction
    suspend fun deleteInvoiceTransaction(invoice: SaleInvoiceEntity) {
        if (invoice.remainingAmount > 0) {
            adjustClientBalance(invoice.clientId, -invoice.remainingAmount)
        }
        deleteInvoice(invoice)
    }

    // Atomic Transaction: Create Purchase Invoice with stock additions, movements, and supplier balance
    @Transaction
    suspend fun createPurchaseTransaction(
        purchase: PurchaseInvoiceEntity,
        items: List<Pair<Long, Int>>, // productId, quantity
        supplierDebtAdjustment: Double
    ): Long {
        for ((productId, qty) in items) {
            val product = getProductById(productId)
            val prevStock = product?.stockQuantity ?: 0
            val newStock = prevStock + qty
            increaseStock(productId, qty)
            insertStockMovement(
                StockMovementEntity(
                    productId = productId,
                    productName = product?.name ?: "",
                    movementType = "STOCK_IN",
                    movementTypeArabic = "توريد مشتريات",
                    quantity = qty,
                    previousStock = prevStock,
                    newStock = newStock,
                    referenceNumber = purchase.invoiceNumber,
                    reason = "فاتورة شراء من ${purchase.supplierName}"
                )
            )
        }
        if (supplierDebtAdjustment > 0 && purchase.supplierId > 0) {
            adjustSupplierBalance(purchase.supplierId, supplierDebtAdjustment)
        }
        return insertPurchase(purchase)
    }

    // Atomic Transaction: Delete Purchase Invoice and reverse supplier balance
    @Transaction
    suspend fun deletePurchaseTransaction(purchase: PurchaseInvoiceEntity) {
        if (purchase.remainingAmount > 0 && purchase.supplierId > 0) {
            adjustSupplierBalance(purchase.supplierId, -purchase.remainingAmount)
        }
        deletePurchase(purchase)
    }

    // Atomic Transaction: Create Receipt and adjust client debt
    @Transaction
    suspend fun createReceiptTransaction(receipt: PaymentReceiptEntity): Long {
        adjustClientBalance(receipt.clientId, -receipt.amount)
        return insertReceipt(receipt)
    }

    // Atomic Transaction: Delete Receipt and restore client debt
    @Transaction
    suspend fun deleteReceiptTransaction(receipt: PaymentReceiptEntity) {
        adjustClientBalance(receipt.clientId, receipt.amount)
        deleteReceipt(receipt)
    }

    // Atomic Transaction: Create Supplier Payment and adjust supplier balance
    @Transaction
    suspend fun createSupplierPaymentTransaction(payment: SupplierPaymentEntity): Long {
        if (payment.supplierId > 0) {
            adjustSupplierBalance(payment.supplierId, -payment.amount)
        }
        return insertSupplierPayment(payment)
    }

    // Atomic Transaction: Delete Supplier Payment and restore supplier balance
    @Transaction
    suspend fun deleteSupplierPaymentTransaction(payment: SupplierPaymentEntity) {
        if (payment.supplierId > 0) {
            adjustSupplierBalance(payment.supplierId, payment.amount)
        }
        deleteSupplierPayment(payment)
    }

    // Atomic Transaction: Direct stock inventory adjustment
    @Transaction
    suspend fun updateStockDirectTransaction(
        productId: Long,
        productName: String,
        prevStock: Int,
        newStock: Int
    ) {
        val diff = newStock - prevStock
        updateStockDirect(productId, newStock)
        insertStockMovement(
            StockMovementEntity(
                productId = productId,
                productName = productName,
                movementType = "ADJUSTMENT",
                movementTypeArabic = "تسوية جرد فعلي",
                quantity = diff,
                previousStock = prevStock,
                newStock = newStock,
                referenceNumber = "ADJ-${System.currentTimeMillis() % 100000}",
                reason = "تعديل رصيد الجرد الفعلي"
            )
        )
    }

    // Atomic Transaction: Manual stock movement adjustment
    @Transaction
    suspend fun recordManualAdjustmentTransaction(
        productId: Long,
        productName: String,
        prevStock: Int,
        newStock: Int,
        type: String,
        typeArabic: String,
        quantityDelta: Int,
        reason: String
    ) {
        updateStockDirect(productId, newStock)
        insertStockMovement(
            StockMovementEntity(
                productId = productId,
                productName = productName,
                movementType = type,
                movementTypeArabic = typeArabic,
                quantity = quantityDelta,
                previousStock = prevStock,
                newStock = newStock,
                referenceNumber = "MOV-${System.currentTimeMillis() % 100000}",
                reason = reason
            )
        )
    }

    // Full atomic restore transaction to prevent data corruption
    @Transaction
    suspend fun restoreAllData(
        products: List<ProductEntity>,
        clients: List<ClientEntity>,
        suppliers: List<SupplierEntity>,
        supplierPayments: List<SupplierPaymentEntity>,
        invoices: List<SaleInvoiceEntity>,
        purchases: List<PurchaseInvoiceEntity>,
        stockMovements: List<StockMovementEntity>,
        expenses: List<ExpenseEntity>,
        receipts: List<PaymentReceiptEntity>
    ) {
        clearProducts()
        clearClients()
        clearSuppliers()
        clearSupplierPayments()
        clearInvoices()
        clearPurchases()
        clearStockMovements()
        clearExpenses()
        clearReceipts()

        if (products.isNotEmpty()) insertProducts(products)
        if (clients.isNotEmpty()) insertClients(clients)
        if (suppliers.isNotEmpty()) insertSuppliers(suppliers)
        if (supplierPayments.isNotEmpty()) insertSupplierPayments(supplierPayments)
        if (invoices.isNotEmpty()) insertInvoices(invoices)
        if (purchases.isNotEmpty()) insertPurchases(purchases)
        if (stockMovements.isNotEmpty()) insertStockMovements(stockMovements)
        if (expenses.isNotEmpty()) insertExpenses(expenses)
        if (receipts.isNotEmpty()) insertReceipts(receipts)
    }

    // Currency Settings (تخزين إعدادات العملة في قاعدة البيانات)
    @Query("SELECT * FROM currency_settings WHERE id = 1 LIMIT 1")
    fun getCurrencySettingFlow(): Flow<CurrencySettingEntity?>

    @Query("SELECT * FROM currency_settings WHERE id = 1 LIMIT 1")
    suspend fun getCurrencySetting(): CurrencySettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrencySetting(setting: CurrencySettingEntity)

    // Language Settings (تخزين إعدادات اللغة في قاعدة البيانات)
    @Query("SELECT * FROM language_settings WHERE id = 1 LIMIT 1")
    fun getLanguageSettingFlow(): Flow<LanguageSettingEntity?>

    @Query("SELECT * FROM language_settings WHERE id = 1 LIMIT 1")
    suspend fun getLanguageSetting(): LanguageSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLanguageSetting(setting: LanguageSettingEntity)
}
