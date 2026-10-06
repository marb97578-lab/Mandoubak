package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SupplierEntity
import com.example.ui.theme.CardPurchasesAccent
import com.example.ui.theme.CardPurchasesBg
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.util.PdfReportGenerator

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PurchaseCartItem(
    val product: ProductEntity,
    val quantity: Int,
    val unitCostPrice: Double
)

@Composable
fun PurchasesScreen(
    purchases: List<PurchaseInvoiceEntity>,
    products: List<ProductEntity>,
    suppliers: List<SupplierEntity> = emptyList(),
    currencySymbol: String,
    onBackClick: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onCreatePurchaseClick: () -> Unit,
    onDeletePurchase: (PurchaseInvoiceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPurchaseForDetails by remember { mutableStateOf<PurchaseInvoiceEntity?>(null) }
    var purchaseToDelete by remember { mutableStateOf<PurchaseInvoiceEntity?>(null) }

    val filteredPurchases = remember(purchases, searchQuery) {
        if (searchQuery.isBlank()) purchases
        else {
            val q = searchQuery.trim().lowercase()
            purchases.filter {
                it.invoiceNumber.lowercase().contains(q) ||
                it.supplierName.lowercase().contains(q) ||
                it.itemsSummary.lowercase().contains(q)
            }
        }
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
                        .testTag("purchases_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MandoubakNavy
                    )
                }

                Text(
                    text = "فواتير المشتريات والتوريد",
                    color = MandoubakNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onNavigateToSuppliers,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardPurchasesBg)
                        .testTag("open_suppliers_shortcut")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "الموردين",
                        tint = CardPurchasesAccent
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePurchaseClick,
                containerColor = CardPurchasesAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_purchase_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "فاتورة شراء جديدة")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .shadow(1.dp, RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("فواتير الشراء", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        Text(
                            text = "${purchases.size}",
                            color = MandoubakNavy,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFE2E8F0)))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("إجمالي المشتريات", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        val total = purchases.sumOf { it.totalAmount }
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f %s", total, currencySymbol),
                            color = CardPurchasesAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFE2E8F0)))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الموردون", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        Text(
                            text = "${suppliers.size}",
                            color = MandoubakNavy,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Suppliers Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سجل فواتير التوريد",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MandoubakNavy
                )

                OutlinedButton(
                    onClick = onNavigateToSuppliers,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = CardPurchasesAccent, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إدارة الموردين والشركات", fontSize = 11.sp, color = CardPurchasesAccent)
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث برقم الفاتورة، اسم المورد، أو الصنف...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MandoubakTextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح", tint = MandoubakTextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CardPurchasesAccent,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            if (filteredPurchases.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "لا توجد فواتير مشتريات مسجلة حتى الآن" else "لا توجد نتائج بحث مطابقة",
                            color = MandoubakTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPurchases, key = { it.id }) { purchase ->
                        PurchaseItemCard(
                            purchase = purchase,
                            currencySymbol = currencySymbol,
                            onClick = { selectedPurchaseForDetails = purchase },
                            onDelete = { purchaseToDelete = purchase }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Delete Purchase Confirmation Dialog
    purchaseToDelete?.let { purchase ->
        AlertDialog(
            onDismissRequest = { purchaseToDelete = null },
            title = {
                Text(
                    text = "تأكيد حذف فاتورة الشراء",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
            },
            text = {
                Text("هل أنت متأكد من حذف فاتورة الشراء ${purchase.invoiceNumber}؟ سيتم تعديل مستحقات المورد تلقائياً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePurchase(purchase)
                        if (selectedPurchaseForDetails?.id == purchase.id) {
                            selectedPurchaseForDetails = null
                        }
                        purchaseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حذف الفاتورة", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { purchaseToDelete = null }) {
                    Text("إلغاء", color = MandoubakTextSecondary)
                }
            }
        )
    }

    selectedPurchaseForDetails?.let { pur ->
        PurchaseDetailDialog(
            purchase = pur,
            currencySymbol = currencySymbol,
            onDismiss = { selectedPurchaseForDetails = null }
        )
    }
}

@Composable
fun PurchaseItemCard(
    purchase: PurchaseInvoiceEntity,
    currencySymbol: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(purchase.dateMillis))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .testTag("purchase_card_${purchase.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (purchase.isCredit) Color(0xFFFEF3C7) else Color(0xFFD1FAE5))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (purchase.isCredit) "آجل" else "نقداً",
                            color = if (purchase.isCredit) Color(0xFFD97706) else Color(0xFF059669),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = purchase.invoiceNumber,
                        color = MandoubakNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = String.format(Locale.getDefault(), "%.1f %s", purchase.totalAmount, currencySymbol),
                    color = CardPurchasesAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "المورد: ${purchase.supplierName}",
                color = MandoubakTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            if (purchase.itemsSummary.isNotBlank()) {
                Text(
                    text = "الأصناف: ${purchase.itemsSummary}",
                    color = MandoubakTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف الفاتورة",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseDetailDialog(
    purchase: PurchaseInvoiceEntity,
    currencySymbol: String,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(purchase.dateMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("فاتورة شراء واردة", fontWeight = FontWeight.Bold, color = MandoubakNavy)
                Text(purchase.invoiceNumber, fontSize = 14.sp, color = CardPurchasesAccent, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(color = Color(0xFFE2E8F0))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("المورد:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(purchase.supplierName, fontWeight = FontWeight.Bold, color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("التاريخ:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(dateStr, color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("طريقة السداد:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(
                        text = if (purchase.isCredit) "آجل (${purchase.paymentMethod})" else "مسددة (${purchase.paymentMethod})",
                        fontWeight = FontWeight.Bold,
                        color = if (purchase.isCredit) Color(0xFFD97706) else Color(0xFF059669),
                        fontSize = 13.sp
                    )
                }

                if (purchase.discountAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الخصم الممنوح:", color = MandoubakTextSecondary, fontSize = 13.sp)
                        Text("${purchase.discountAmount} $currencySymbol", color = Color(0xFF059669), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Text("الأصناف والكميات الموردة:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MandoubakNavy)
                Text(
                    text = purchase.itemsSummary,
                    fontSize = 12.5.sp,
                    color = MandoubakTextPrimary,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("المبلغ الإجمالي:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MandoubakNavy)
                    Text("${purchase.totalAmount} $currencySymbol", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CardPurchasesAccent)
                }

                if (purchase.remainingAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("المتبقي آجل على المنشأة:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFFD97706))
                        Text("${purchase.remainingAmount} $currencySymbol", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFD97706))
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val context = LocalContext.current
                OutlinedButton(
                    onClick = {
                        PdfReportGenerator.generateAndSharePurchaseInvoicePdf(
                            context = context,
                            purchase = purchase,
                            companyName = "مؤسسة التوزيع والتجارة الحديثة",
                            representativeName = "مندوب المشتريات والتوريد",
                            currencySymbol = currencySymbol
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة وطباعة PDF",
                        tint = CardPurchasesAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("طباعة PDF", color = CardPurchasesAccent, fontSize = 12.sp)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent)
                ) {
                    Text("إغلاق", color = Color.White)
                }
            }
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePurchaseDialog(
    products: List<ProductEntity>,
    suppliers: List<SupplierEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onQuickAddProduct: (name: String, barcode: String, costPrice: Double, salePrice: Double, unit: String, category: String) -> Unit,
    onSubmit: (
        supplierId: Long,
        supplierName: String,
        items: List<Pair<ProductEntity, Int>>,
        subtotal: Double,
        discount: Double,
        total: Double,
        paid: Double,
        paymentMethod: String,
        isCredit: Boolean,
        notes: String
    ) -> Unit
) {
    var supplierSearchText by remember { mutableStateOf("") }
    var selectedSupplierId by remember { mutableStateOf(0L) }
    var supplierDropdownExpanded by remember { mutableStateOf(false) }

    val selectedItems = remember { mutableStateListOf<PurchaseCartItem>() }

    var selectedProductToAdd by remember { mutableStateOf(products.firstOrNull()) }
    var productDropdownExpanded by remember { mutableStateOf(false) }
    var quantityToAdd by remember { mutableStateOf("10") }
    var costPriceToAdd by remember { mutableStateOf(selectedProductToAdd?.costPrice?.toString() ?: "5.0") }

    var discountText by remember { mutableStateOf("0.0") }
    var selectedPaymentMethod by remember { mutableStateOf("نقداً") } // نقداً, آجل للمورد, تحويل بنكي, دفع جزئي
    val paymentMethods = listOf("نقداً", "آجل للمورد", "تحويل بنكي", "دفع جزئي")
    var partialPaidText by remember { mutableStateOf("0.0") }
    var notes by remember { mutableStateOf("") }

    var showQuickAddProductDialog by remember { mutableStateOf(false) }

    // Calculations
    val subtotalAmount = selectedItems.sumOf { it.unitCostPrice * it.quantity }
    val discountVal = discountText.toDoubleOrNull() ?: 0.0
    val netTotalAmount = (subtotalAmount - discountVal).coerceAtLeast(0.0)

    val isCredit = selectedPaymentMethod == "آجل للمورد" || selectedPaymentMethod == "دفع جزئي"
    val paidAmount = when (selectedPaymentMethod) {
        "نقداً", "تحويل بنكي" -> netTotalAmount
        "آجل للمورد" -> 0.0
        "دفع جزئي" -> (partialPaidText.toDoubleOrNull() ?: 0.0).coerceIn(0.0, netTotalAmount)
        else -> netTotalAmount
    }
    val remainingAmount = (netTotalAmount - paidAmount).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تسجيل فاتورة شراء وتوريد",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy,
                    fontSize = 17.sp
                )
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = CardPurchasesAccent)
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Supplier Selection
                item {
                    Text("المورد أو الشركة الموردة *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    ExposedDropdownMenuBox(
                        expanded = supplierDropdownExpanded,
                        onExpandedChange = { supplierDropdownExpanded = !supplierDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = supplierSearchText,
                            onValueChange = {
                                supplierSearchText = it
                                selectedSupplierId = 0L
                                supplierDropdownExpanded = true
                            },
                            placeholder = { Text("اختر من القائمة أو اكتب اسم المورد...") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        if (suppliers.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = supplierDropdownExpanded,
                                onDismissRequest = { supplierDropdownExpanded = false }
                            ) {
                                suppliers.forEach { sup ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(sup.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                if (sup.companyName.isNotBlank()) {
                                                    Text(sup.companyName, fontSize = 11.sp, color = MandoubakTextSecondary)
                                                }
                                            }
                                        },
                                        onClick = {
                                            supplierSearchText = sup.name
                                            selectedSupplierId = sup.id
                                            supplierDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Products to Buy Section
                item {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إضافة أصناف للشراء (ستزيد المخزون فورياً):",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MandoubakNavy
                        )

                        TextButton(
                            onClick = { showQuickAddProductDialog = true },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = CardPurchasesAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("صنف جديد", fontSize = 11.5.sp, color = CardPurchasesAccent, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (products.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = productDropdownExpanded,
                            onExpandedChange = { productDropdownExpanded = !productDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedProductToAdd?.let { "${it.name} (المخزون الحالي: ${it.stockQuantity})" } ?: "اختر منتجاً",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = productDropdownExpanded,
                                onDismissRequest = { productDropdownExpanded = false }
                            ) {
                                products.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text("${p.name} - تكلفة: ${p.costPrice} $currencySymbol (متوفر: ${p.stockQuantity})") },
                                        onClick = {
                                            selectedProductToAdd = p
                                            costPriceToAdd = "${p.costPrice}"
                                            productDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = quantityToAdd,
                                onValueChange = { quantityToAdd = it.filter { ch -> ch.isDigit() } },
                                label = { Text("الكمية") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = costPriceToAdd,
                                onValueChange = { costPriceToAdd = it },
                                label = { Text("سعر الشراء") },
                                singleLine = true,
                                modifier = Modifier.weight(1.2f)
                            )

                            Button(
                                onClick = {
                                    val prod = selectedProductToAdd
                                    val qty = quantityToAdd.toIntOrNull() ?: 1
                                    val cost = costPriceToAdd.toDoubleOrNull() ?: (prod?.costPrice ?: 0.0)
                                    if (prod != null && qty > 0 && cost >= 0) {
                                        selectedItems.add(PurchaseCartItem(prod, qty, cost))
                                        quantityToAdd = "10"
                                    }
                                },
                                enabled = selectedProductToAdd != null,
                                colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("إضافة", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = { showQuickAddProductDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إضافة صنف جديد للنظام الآن", color = Color.White)
                        }
                    }
                }

                // Selected Items Cart
                if (selectedItems.isNotEmpty()) {
                    item {
                        Text("أصناف الفاتورة (${selectedItems.size}):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MandoubakNavy)
                    }
                    items(selectedItems) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${item.product.name} × ${item.quantity} ${item.product.unit}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("سعر الوحدة: ${item.unitCostPrice} $currencySymbol", fontSize = 11.sp, color = MandoubakTextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${item.unitCostPrice * item.quantity} $currencySymbol", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CardPurchasesAccent)
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { selectedItems.remove(item) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Financials & Payment Method
                item {
                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    OutlinedTextField(
                        value = discountText,
                        onValueChange = { discountText = it },
                        label = { Text("خصم التوريد أو الشراء الممنوح ($currencySymbol)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("طريقة السداد:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        paymentMethods.forEach { m ->
                            val isSel = selectedPaymentMethod == m
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CardPurchasesAccent else Color(0xFFF1F5F9))
                                    .clickable { selectedPaymentMethod = m }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = m,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MandoubakNavy
                                )
                            }
                        }
                    }

                    if (selectedPaymentMethod == "دفع جزئي") {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = partialPaidText,
                            onValueChange = { partialPaidText = it },
                            label = { Text("المبلغ المدفوع نقداً الآن ($currencySymbol)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات / رقم سند الشحن أو التسليم") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calculations summary card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المجموع الفرعي:", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                Text("$subtotalAmount $currencySymbol", fontSize = 11.5.sp, color = MandoubakNavy)
                            }
                            if (discountVal > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("الخصم:", fontSize = 11.5.sp, color = Color(0xFF059669))
                                    Text("-$discountVal $currencySymbol", fontSize = 11.5.sp, color = Color(0xFF059669))
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("صافي فاتورة الشراء:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MandoubakNavy)
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f %s", netTotalAmount, currencySymbol),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CardPurchasesAccent
                                )
                            }
                            if (isCredit && remainingAmount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("المتبقي آجل على المنشأة للمورد:", fontSize = 11.5.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                                    Text("$remainingAmount $currencySymbol", fontSize = 11.5.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
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
                    if (supplierSearchText.isNotBlank() && selectedItems.isNotEmpty()) {
                        val pairs = selectedItems.map { Pair(it.product, it.quantity) }
                        onSubmit(
                            selectedSupplierId,
                            supplierSearchText.trim(),
                            pairs,
                            subtotalAmount,
                            discountVal,
                            netTotalAmount,
                            paidAmount,
                            selectedPaymentMethod,
                            isCredit,
                            notes.trim()
                        )
                    }
                },
                enabled = supplierSearchText.isNotBlank() && selectedItems.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ وزيادة المخزون", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )

    // Inline Quick Add Product Dialog
    if (showQuickAddProductDialog) {
        QuickAddProductDialog(
            currencySymbol = currencySymbol,
            onDismiss = { showQuickAddProductDialog = false },
            onSubmit = { name, barcode, cost, sale, unit, cat ->
                onQuickAddProduct(name, barcode, cost, sale, unit, cat)
                showQuickAddProductDialog = false
            }
        )
    }
}

@Composable
fun QuickAddProductDialog(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSubmit: (name: String, barcode: String, costPrice: Double, salePrice: Double, unit: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var costPriceText by remember { mutableStateOf("5.0") }
    var salePriceText by remember { mutableStateOf("8.0") }
    var unit by remember { mutableStateOf("حبة") }
    var category by remember { mutableStateOf("عام") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إضافة صنف جديد للمخزون", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الصنف *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("الباركود (اختياري)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = costPriceText,
                        onValueChange = { costPriceText = it },
                        label = { Text("سعر التكلفة ($currencySymbol)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = salePriceText,
                        onValueChange = { salePriceText = it },
                        label = { Text("سعر البيع ($currencySymbol)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("الوحدة") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("التصنيف") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val cost = costPriceText.toDoubleOrNull() ?: 0.0
                        val sale = salePriceText.toDoubleOrNull() ?: 0.0
                        onSubmit(name.trim(), barcode.trim(), cost, sale, unit.trim(), category.trim())
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent)
            ) {
                Text("إضافة الصنف", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )
}
