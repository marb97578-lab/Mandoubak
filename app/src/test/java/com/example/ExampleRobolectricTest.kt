package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.entity.ClientEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.data.entity.SupplierEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var context: Context
  private lateinit var db: AppDatabase

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val appName = context.getString(R.string.app_name)
    assertEquals("Mandoubak", appName)
  }

  @Test
  fun testCreateInvoiceTransactionDeductsStockAndAdjustsClientBalance() = runBlocking {
    val dao = db.appDao()

    // 1. Create client & product
    val clientId = dao.insertClient(
      ClientEntity(name = "عميل تجريبي", phone = "0500000000", currentBalance = 0.0)
    )
    val productId = dao.insertProduct(
      ProductEntity(name = "منتج تجريبي", costPrice = 10.0, salePrice = 15.0, stockQuantity = 50)
    )

    // 2. Create invoice (sold 10 units on credit, remaining 150.0 debt)
    val invoice = SaleInvoiceEntity(
      invoiceNumber = "INV-TEST-001",
      clientId = clientId,
      clientName = "عميل تجريبي",
      subtotalAmount = 150.0,
      discountAmount = 0.0,
      totalAmount = 150.0,
      paidAmount = 0.0,
      remainingAmount = 150.0,
      profitAmount = 50.0,
      isCredit = true,
      itemsSummary = "منتج تجريبي x 10"
    )

    val invoiceId = dao.createInvoiceTransaction(
      invoice = invoice,
      items = listOf(Pair(productId, 10)),
      clientDebtAdjustment = 150.0
    )

    assertTrue(invoiceId > 0)

    // Verify stock deducted
    val updatedProduct = dao.getProductById(productId)
    assertEquals(40, updatedProduct?.stockQuantity)

    // Verify client debt adjusted
    val updatedClient = dao.getClientById(clientId)
    assertEquals(150.0, updatedClient?.currentBalance ?: 0.0, 0.001)

    // Verify stock movement logged
    val movements = dao.getAllStockMovements().first()
    assertEquals(1, movements.size)
    assertEquals(-10, movements.first().quantity)
    assertEquals("STOCK_OUT", movements.first().movementType)
  }

  @Test
  fun testCreateReceiptTransactionAdjustsClientDebt() = runBlocking {
    val dao = db.appDao()

    val clientId = dao.insertClient(
      ClientEntity(name = "عميل مدين", phone = "0500000001", currentBalance = 200.0)
    )

    val receipt = PaymentReceiptEntity(
      receiptNumber = "REC-TEST-001",
      clientId = clientId,
      clientName = "عميل مدين",
      amount = 80.0
    )

    dao.createReceiptTransaction(receipt)

    val updatedClient = dao.getClientById(clientId)
    assertEquals(120.0, updatedClient?.currentBalance ?: 0.0, 0.001)

    // Delete receipt should restore balance
    dao.deleteReceiptTransaction(receipt)
    val restoredClient = dao.getClientById(clientId)
    assertEquals(200.0, restoredClient?.currentBalance ?: 0.0, 0.001)
  }

  @Test
  fun testCreatePurchaseTransactionIncreasesStockAndAdjustsSupplierBalance() = runBlocking {
    val dao = db.appDao()

    val supplierId = dao.insertSupplier(
      SupplierEntity(name = "مورد أجهزة", phone = "0555555555", outstandingBalance = 0.0)
    )
    val productId = dao.insertProduct(
      ProductEntity(name = "لوح إلكتروني", costPrice = 100.0, salePrice = 140.0, stockQuantity = 5)
    )

    val purchase = PurchaseInvoiceEntity(
      invoiceNumber = "PUR-TEST-001",
      supplierId = supplierId,
      supplierName = "مورد أجهزة",
      totalAmount = 500.0,
      paidAmount = 200.0,
      remainingAmount = 300.0,
      isCredit = true,
      itemsSummary = "لوح إلكتروني x 5"
    )

    dao.createPurchaseTransaction(
      purchase = purchase,
      items = listOf(Pair(productId, 5)),
      supplierDebtAdjustment = 300.0
    )

    val updatedProduct = dao.getProductById(productId)
    assertEquals(10, updatedProduct?.stockQuantity)

    val updatedSupplier = dao.getSupplierById(supplierId)
    assertEquals(300.0, updatedSupplier?.outstandingBalance ?: 0.0, 0.001)
  }

  @Test
  fun testDirectSalesInvoiceCreationWithCustomerAndDiscount() = runBlocking {
    val dao = db.appDao()

    val clientId = dao.insertClient(
      ClientEntity(name = "مؤسسة الوفاء", phone = "0512345678", currentBalance = 0.0)
    )

    val subtotal = 1000.0
    val discount = 100.0
    val finalAmount = subtotal - discount // 900.0
    val paid = 400.0 // partial payment
    val remaining = finalAmount - paid // 500.0 debt

    val invoice = SaleInvoiceEntity(
      invoiceNumber = "INV-DIRECT-001",
      clientId = clientId,
      clientName = "مؤسسة الوفاء",
      subtotalAmount = subtotal,
      discountAmount = discount,
      totalAmount = finalAmount,
      paidAmount = paid,
      remainingAmount = remaining,
      profitAmount = finalAmount * 0.20,
      isCredit = true,
      paymentMethod = "دفع جزئي (Partial)",
      itemsSummary = "فاتورة مبيعات مباشرة"
    )

    val id = dao.createInvoiceTransaction(
      invoice = invoice,
      items = emptyList(),
      clientDebtAdjustment = remaining
    )

    assertTrue(id > 0)

    val client = dao.getClientById(clientId)
    assertEquals(500.0, client?.currentBalance ?: 0.0, 0.001)

    val savedInvoice = dao.getAllInvoicesList().find { it.id == id }
    assertEquals(900.0, savedInvoice?.totalAmount ?: 0.0, 0.001)
    assertEquals(400.0, savedInvoice?.paidAmount ?: 0.0, 0.001)
    assertEquals(500.0, savedInvoice?.remainingAmount ?: 0.0, 0.001)
  }
}

