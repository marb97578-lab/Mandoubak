package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.entity.ClientEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.ui.theme.CardProductsAccent
import com.example.ui.theme.CardProductsBg
import com.example.ui.theme.CardSalesAccent
import com.example.ui.theme.CardSalesBg
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.util.PdfInvoiceGenerator
import com.example.util.PdfInvoiceItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    invoices: List<SaleInvoiceEntity>,
    products: List<ProductEntity>,
    clients: List<ClientEntity>,
    currencySymbol: String = "ر.س",
    companyName: String = "مؤسسة التوزيع والتجارة الحديثة",
    representativeName: String = "مندوب المبيعات",
    taxNumber: String = "",
    onBackClick: () -> Unit,
    onCreateInvoiceClick: () -> Unit,
    onUpdateInvoice: (SaleInvoiceEntity) -> Unit = {},
    onDeleteInvoice: (SaleInvoiceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedInvoiceForDetails by remember { mutableStateOf<SaleInvoiceEntity?>(null) }
    var selectedInvoiceForEdit by remember { mutableStateOf<SaleInvoiceEntity?>(null) }
    var invoiceToDelete by remember { mutableStateOf<SaleInvoiceEntity?>(null) }
    var invoiceSearchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredInvoices = remember(invoices, invoiceSearchQuery) {
        if (invoiceSearchQuery.isBlank()) {
            invoices
        } else {
            val q = invoiceSearchQuery.trim().lowercase()
            invoices.filter {
                it.invoiceNumber.lowercase().contains(q) ||
                it.clientName.lowercase().contains(q) ||
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
                        .testTag("sales_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MandoubakNavy
                    )
                }

                Text(
                    text = "فواتير المبيعات",
                    color = MandoubakNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardSalesBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = CardSalesAccent
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateInvoiceClick,
                containerColor = CardSalesAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_invoice_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "فاتورة جديدة")
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
                    .padding(vertical = 6.dp)
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
                        Text("إجمالي الفواتير", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        Text(
                            text = "${invoices.size}",
                            color = MandoubakNavy,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0xFFE2E8F0))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("إجمالي المبيعات", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        val totalSales = invoices.sumOf { it.totalAmount }
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f %s", totalSales, currencySymbol),
                            color = CardSalesAccent,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(Color(0xFFE2E8F0))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الذمم المعلقة", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        val totalRemaining = invoices.sumOf { it.remainingAmount }
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f %s", totalRemaining, currencySymbol),
                            color = if (totalRemaining > 0) Color(0xFFD97706) else Color(0xFF059669),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Invoices Bar
            OutlinedTextField(
                value = invoiceSearchQuery,
                onValueChange = { invoiceSearchQuery = it },
                placeholder = { Text("بحث برقم الفاتورة أو اسم العميل...", fontSize = 12.5.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (invoiceSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { invoiceSearchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CardSalesAccent,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (filteredInvoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (invoiceSearchQuery.isNotBlank()) "لا توجد فواتير مطابقة للبحث" else "لا توجد فواتير مبيعات مسجلة حتى الآن",
                            color = MandoubakTextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (invoiceSearchQuery.isBlank()) {
                            OutlinedButton(onClick = onCreateInvoiceClick) {
                                Text("إنشاء أول فاتورة الآن")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredInvoices, key = { it.id }) { invoice ->
                        InvoiceCardItem(
                            invoice = invoice,
                            currencySymbol = currencySymbol,
                            onClick = { selectedInvoiceForDetails = invoice },
                            onEdit = { selectedInvoiceForEdit = invoice },
                            onDelete = { invoiceToDelete = invoice }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Delete Invoice Confirmation Dialog
    invoiceToDelete?.let { invoice ->
        AlertDialog(
            onDismissRequest = { invoiceToDelete = null },
            title = {
                Text(
                    text = "تأكيد حذف الفاتورة",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
            },
            text = {
                Text("هل أنت متأكد من حذف الفاتورة ${invoice.invoiceNumber}؟ سيتم استرجاع الرصيد المتبقي بذمة العميل وتعديل القيود المالية تلقائياً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteInvoice(invoice)
                        if (selectedInvoiceForDetails?.id == invoice.id) {
                            selectedInvoiceForDetails = null
                        }
                        invoiceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حذف الفاتورة", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { invoiceToDelete = null }) {
                    Text("إلغاء", color = MandoubakTextSecondary)
                }
            }
        )
    }

    // Invoice Details Dialog
    selectedInvoiceForDetails?.let { invoice ->
        val customerPhone = clients.find { it.id == invoice.clientId || it.name.trim() == invoice.clientName.trim() }?.phone ?: ""
        InvoiceDetailDialog(
            invoice = invoice,
            customerPhone = customerPhone,
            currencySymbol = currencySymbol,
            companyName = companyName,
            representativeName = representativeName,
            taxNumber = taxNumber,
            onDismiss = { selectedInvoiceForDetails = null },
            onEdit = {
                selectedInvoiceForDetails = null
                selectedInvoiceForEdit = invoice
            },
            onGeneratePdf = {
                PdfInvoiceGenerator.generateAndShareInvoicePdf(
                    context = context,
                    invoice = invoice,
                    customerPhone = customerPhone,
                    companyName = companyName,
                    representativeName = representativeName,
                    taxNumber = taxNumber,
                    currencySymbol = currencySymbol
                )
            }
        )
    }

    // Edit Invoice Dialog
    selectedInvoiceForEdit?.let { invoice ->
        EditInvoiceDialog(
            invoice = invoice,
            currencySymbol = currencySymbol,
            onDismiss = { selectedInvoiceForEdit = null },
            onSave = { updated ->
                onUpdateInvoice(updated)
                selectedInvoiceForEdit = null
            }
        )
    }
}

@Composable
fun InvoiceCardItem(
    invoice: SaleInvoiceEntity,
    currencySymbol: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(invoice.dateMillis))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .testTag("invoice_card_${invoice.id}")
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CardSalesBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = CardSalesAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = invoice.invoiceNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MandoubakNavy
                        )
                        Text(
                            text = dateString,
                            fontSize = 10.5.sp,
                            color = MandoubakTextSecondary
                        )
                    }
                }

                // Payment Status Badge
                val isFullyPaid = invoice.remainingAmount <= 0
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isFullyPaid) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isFullyPaid) "مدفوعة بالكامل" else "آجل (متبقي: ${String.format(Locale.getDefault(), "%.1f", invoice.remainingAmount)})",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFullyPaid) Color(0xFF166534) else Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "العميل: ${invoice.clientName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MandoubakTextPrimary
                    )
                    Text(
                        text = invoice.itemsSummary,
                        fontSize = 11.5.sp,
                        color = MandoubakTextSecondary,
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f %s", invoice.totalAmount, currencySymbol),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CardSalesAccent
                    )
                    Text(
                        text = "طريقة الدفع: ${invoice.paymentMethod}",
                        fontSize = 10.5.sp,
                        color = MandoubakTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (invoice.profitAmount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFECFDF5))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "ربح: ${String.format(Locale.getDefault(), "%.1f %s", invoice.profitAmount, currencySymbol)}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل الفاتورة", tint = CardSalesAccent, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف الفاتورة", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceDetailDialog(
    invoice: SaleInvoiceEntity,
    customerPhone: String = "",
    currencySymbol: String,
    companyName: String,
    representativeName: String,
    taxNumber: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onGeneratePdf: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(invoice.dateMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فاتورة مبيعات",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
                Text(
                    text = invoice.invoiceNumber,
                    fontSize = 14.sp,
                    color = CardSalesAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(color = Color(0xFFE2E8F0))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("المؤسسة:", color = MandoubakTextSecondary, fontSize = 12.5.sp)
                    Text(companyName, fontWeight = FontWeight.SemiBold, color = MandoubakTextPrimary, fontSize = 12.5.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("العميل:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(invoice.clientName, fontWeight = FontWeight.Bold, color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                if (customerPhone.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("رقم الهاتف:", color = MandoubakTextSecondary, fontSize = 12.5.sp)
                        Text(customerPhone, fontWeight = FontWeight.SemiBold, color = MandoubakTextPrimary, fontSize = 12.5.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("التاريخ:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(dateStr, color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("طريقة الدفع:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(
                        text = invoice.paymentMethod,
                        fontWeight = FontWeight.Bold,
                        color = if (invoice.remainingAmount > 0) Color(0xFFD97706) else Color(0xFF059669),
                        fontSize = 13.sp
                    )
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Text("الأصناف والمواد:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MandoubakNavy)
                Text(
                    text = invoice.itemsSummary,
                    fontSize = 12.5.sp,
                    color = MandoubakTextPrimary,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = Color(0xFFE2E8F0))

                if (invoice.subtotalAmount > 0 && invoice.discountAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("المجموع قبل الخصم:", color = MandoubakTextSecondary, fontSize = 12.sp)
                        Text(String.format(Locale.getDefault(), "%.1f %s", invoice.subtotalAmount, currencySymbol), fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الخصم الممنوح:", color = Color(0xFFDC2626), fontSize = 12.sp)
                        Text(String.format(Locale.getDefault(), "- %.1f %s", invoice.discountAmount, currencySymbol), color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("المبلغ الإجمالي الصافي:", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = MandoubakNavy)
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f %s", invoice.totalAmount, currencySymbol),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CardSalesAccent
                    )
                }

                if (invoice.isCredit || invoice.remainingAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("المدفوع:", color = MandoubakTextSecondary, fontSize = 13.sp)
                        Text(String.format(Locale.getDefault(), "%.1f %s", invoice.paidAmount, currencySymbol), color = MandoubakTextPrimary, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("المتبقي بذمة العميل:", fontWeight = FontWeight.SemiBold, color = Color(0xFFD97706), fontSize = 13.sp)
                        Text(String.format(Locale.getDefault(), "%.1f %s", invoice.remainingAmount, currencySymbol), fontWeight = FontWeight.Bold, color = Color(0xFFD97706), fontSize = 13.sp)
                    }
                }

                if (invoice.profitAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ربح الفاتورة التقديري:", color = Color(0xFF059669), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(String.format(Locale.getDefault(), "+ %.1f %s", invoice.profitAmount, currencySymbol), color = Color(0xFF059669), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (invoice.notes.isNotBlank()) {
                    Text("ملاحظات: ${invoice.notes}", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Print Button
                Button(
                    onClick = {
                        val file = PdfInvoiceGenerator.buildInvoicePdfFile(
                            context = context,
                            invoice = invoice,
                            customerPhone = customerPhone,
                            companyName = companyName,
                            representativeName = representativeName,
                            taxNumber = taxNumber,
                            currencySymbol = currencySymbol
                        )
                        if (file != null) {
                            PdfInvoiceGenerator.printInvoicePdf(context, file, invoice)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("طباعة", color = Color.White, fontSize = 11.5.sp)
                }

                // Share PDF Button
                Button(
                    onClick = onGeneratePdf,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاركة", color = Color.White, fontSize = 11.5.sp)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CardSalesAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إغلاق", color = Color.White, fontSize = 11.5.sp)
                }
            }
        }
    )
}

@Composable
fun EditInvoiceDialog(
    invoice: SaleInvoiceEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (SaleInvoiceEntity) -> Unit
) {
    var paidText by remember { mutableStateOf(if (invoice.paidAmount > 0) invoice.paidAmount.toString() else "") }
    var notesText by remember { mutableStateOf(invoice.notes) }
    var selectedMethod by remember { mutableStateOf(invoice.paymentMethod) }

    val methods = listOf("نقدي", "آجل (ذمم)", "دفع جزئي", "شبكة / مدى", "تحويل بنكي")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تعديل الفاتورة ${invoice.invoiceNumber}",
                fontWeight = FontWeight.Bold,
                color = MandoubakNavy,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("العميل: ${invoice.clientName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("المبلغ الصافي: ${invoice.totalAmount} $currencySymbol", fontWeight = FontWeight.Bold, color = CardSalesAccent)

                Text("طريقة الدفع:", fontSize = 12.sp, color = MandoubakNavy)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    methods.take(3).forEach { m ->
                        FilterChip(
                            selected = selectedMethod == m,
                            onClick = { selectedMethod = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = paidText,
                    onValueChange = { paidText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("المبلغ المدفوع ($currencySymbol)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("ملاحظات") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val paid = (paidText.toDoubleOrNull() ?: 0.0).coerceAtMost(invoice.totalAmount)
                    val remaining = (invoice.totalAmount - paid).coerceAtLeast(0.0)
                    val isCredit = remaining > 0 || selectedMethod.contains("آجل") || selectedMethod.contains("جزئي")
                    onSave(
                        invoice.copy(
                            paidAmount = paid,
                            remainingAmount = remaining,
                            paymentMethod = selectedMethod,
                            isCredit = isCredit,
                            notes = notesText.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CardSalesAccent)
            ) {
                Text("حفظ التعديل", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )
}

enum class InvoicePaymentOption(val titleAr: String, val titleEn: String) {
    CASH("نقدي", "Cash"),
    CREDIT("آجل", "Credit (Ajel)"),
    PARTIAL("دفع جزئي", "Partial Payment")
}

data class InvoiceProductItem(
    val product: ProductEntity,
    var quantity: Int = 1
)

fun calculateUnitBreakdown(unit: String, quantity: Int): Pair<String, String>? {
    val clean = unit.trim().lowercase()
    val multiplier = when {
        clean.contains("درزن") || clean.contains("dozen") -> 12
        clean.contains("كرتون") || clean.contains("carton") || clean.contains("صندوق") || clean.contains("box") -> 24
        clean.contains("باكيت") || clean.contains("بكت") || clean.contains("pack") -> 10
        clean.contains("علبة") -> 12
        else -> null
    }
    return if (multiplier != null && multiplier > 1) {
        val totalPieces = quantity * multiplier
        val unitName = if (clean.contains("dozen")) "Dozen" else unit
        val qtyDisplay = "الكمية: $quantity $unitName (Quantity: $quantity $unitName)"
        val piecesDisplay = "إجمالي القطع: $totalPieces قطعة (Total pieces: $totalPieces pieces)"
        Pair(qtyDisplay, piecesDisplay)
    } else {
        null
    }
}

@Composable
fun SelectProductsDialog(
    allProducts: List<ProductEntity>,
    alreadySelectedIds: Set<Long>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onAddProducts: (List<Pair<ProductEntity, Int>>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedQuantities = remember { mutableStateMapOf<Long, Int>() }

    val filteredProducts = remember(allProducts, searchQuery) {
        if (searchQuery.isBlank()) {
            allProducts
        } else {
            val q = searchQuery.trim().lowercase()
            allProducts.filter {
                it.name.lowercase().contains(q) ||
                it.barcode.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CardProductsBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = CardProductsAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "اختيار المنتجات للفاتورة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MandoubakNavy
                            )
                            Text(
                                text = "حدد عدة منتجات وأضفها لنفس الفاتورة",
                                fontSize = 11.sp,
                                color = MandoubakTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث بالاسم، الباركود، أو التصنيف...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = MandoubakTextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CardSalesAccent,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Products List
                if (filteredProducts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (allProducts.isEmpty()) "لا توجد منتجات مسجلة في المخزون" else "لا توجد نتائج تطابق بحثك",
                            color = MandoubakTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            val isAlreadyInInvoice = alreadySelectedIds.contains(product.id)
                            val isSelectedNow = selectedQuantities.containsKey(product.id)
                            val currentQty = selectedQuantities[product.id] ?: 1

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isSelectedNow -> Color(0xFFEFF6FF)
                                        isAlreadyInInvoice -> Color(0xFFF8FAFC)
                                        else -> Color.White
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = if (isSelectedNow) 1.5.dp else 1.dp,
                                    color = when {
                                        isSelectedNow -> CardSalesAccent
                                        isAlreadyInInvoice -> Color(0xFFCBD5E1)
                                        else -> Color(0xFFE2E8F0)
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelectedNow) {
                                            selectedQuantities.remove(product.id)
                                        } else {
                                            selectedQuantities[product.id] = 1
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelectedNow || isAlreadyInInvoice,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                selectedQuantities[product.id] = 1
                                            } else {
                                                selectedQuantities.remove(product.id)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = CardSalesAccent
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = product.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = MandoubakNavy
                                            )
                                            Text(
                                                text = String.format(Locale.getDefault(), "%.1f %s", product.salePrice, currencySymbol),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = CardSalesAccent
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (product.barcode.isNotBlank()) {
                                                Surface(
                                                    color = Color(0xFFF1F5F9),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "باركود: ${product.barcode}",
                                                        fontSize = 10.5.sp,
                                                        color = MandoubakNavy,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "الوحدة: ${product.unit.ifBlank { "حبة" }}",
                                                fontSize = 11.sp,
                                                color = MandoubakTextSecondary
                                            )

                                            Text(
                                                text = "المخزون: ${product.stockQuantity}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (product.stockQuantity <= 0) Color(0xFFDC2626) else Color(0xFF059669)
                                            )
                                        }
                                    }

                                    if (isSelectedNow) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    if (currentQty > 1) {
                                                        selectedQuantities[product.id] = currentQty - 1
                                                    } else {
                                                        selectedQuantities.remove(product.id)
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFE2E8F0))
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = "تقليل", modifier = Modifier.size(14.dp))
                                            }

                                            Text(
                                                text = "$currentQty",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MandoubakNavy,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )

                                            IconButton(
                                                onClick = {
                                                    selectedQuantities[product.id] = currentQty + 1
                                                },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(CardSalesAccent)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "زيادة", tint = Color.White, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(12.dp))

                // Bottom actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء", color = MandoubakTextSecondary)
                    }

                    val selectedCount = selectedQuantities.size
                    Button(
                        onClick = {
                            val itemsToAdd = selectedQuantities.mapNotNull { (prodId, qty) ->
                                allProducts.find { it.id == prodId }?.let { Pair(it, qty) }
                            }
                            onAddProducts(itemsToAdd)
                            onDismiss()
                        },
                        enabled = selectedCount > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = CardSalesAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text(
                            text = if (selectedCount > 0) "إضافة المختارة ($selectedCount)" else "اختر منتجات",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceDialog(
    products: List<ProductEntity> = emptyList(),
    clients: List<ClientEntity> = emptyList(),
    currencySymbol: String = "ر.س",
    onDismiss: () -> Unit,
    onSubmit: (
        customerName: String,
        customerPhone: String,
        items: List<Pair<ProductEntity, Int>>,
        subtotal: Double,
        discount: Double,
        total: Double,
        paid: Double,
        isCredit: Boolean,
        paymentMethod: String,
        notes: String
    ) -> Unit
) {
    // 1. Customer Information
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var showClientsDropdown by remember { mutableStateOf(false) }

    // 2. Invoice Products list (Multiple products support)
    val invoiceProducts = remember { mutableStateListOf<InvoiceProductItem>() }
    var showProductSelectionDialog by remember { mutableStateOf(false) }

    // Fallback manual amount if user creates an invoice without selecting products
    var manualInvoiceAmountText by remember { mutableStateOf("") }
    var discountAmountText by remember { mutableStateOf("") }

    // 3. Payment Method
    var selectedPaymentOption by remember { mutableStateOf(InvoicePaymentOption.CASH) }

    // 4. Payment Logic fields
    var partialPaidText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    // Calculations:
    val productsSubtotal = invoiceProducts.sumOf { it.product.salePrice * it.quantity }
    val subtotalAmount = if (invoiceProducts.isNotEmpty()) {
        productsSubtotal
    } else {
        manualInvoiceAmountText.toDoubleOrNull() ?: 0.0
    }

    val discountAmount = (discountAmountText.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0).coerceAtMost(subtotalAmount)
    val finalInvoiceAmount = (subtotalAmount - discountAmount).coerceAtLeast(0.0)

    val productsCost = invoiceProducts.sumOf { it.product.costPrice * it.quantity }
    val totalCost = if (invoiceProducts.isNotEmpty()) {
        productsCost
    } else {
        subtotalAmount * 0.80
    }
    val invoiceProfit = (finalInvoiceAmount - totalCost).coerceAtLeast(0.0)

    var hasAttemptedSubmit by remember { mutableStateOf(false) }
    val isCustomerNameValid = customerName.isNotBlank()
    val isItemsOrAmountValid = invoiceProducts.isNotEmpty() || subtotalAmount > 0.0
    val isPartialPaymentValid = selectedPaymentOption != InvoicePaymentOption.PARTIAL || (
        (partialPaidText.toDoubleOrNull() ?: 0.0) > 0.0 && (partialPaidText.toDoubleOrNull() ?: 0.0) <= finalInvoiceAmount
    )
    val isFormValid = isCustomerNameValid && isItemsOrAmountValid && isPartialPaymentValid

    val (calculatedPaid, calculatedRemaining, isCreditMode, paymentMethodLabel) = when (selectedPaymentOption) {
        InvoicePaymentOption.CASH -> {
            val paid = finalInvoiceAmount
            val remaining = 0.0
            Quadruple(paid, remaining, false, "نقدي (Cash)")
        }
        InvoicePaymentOption.CREDIT -> {
            val paid = 0.0
            val remaining = finalInvoiceAmount
            Quadruple(paid, remaining, true, "آجل (Credit)")
        }
        InvoicePaymentOption.PARTIAL -> {
            val paid = (partialPaidText.toDoubleOrNull() ?: 0.0).coerceAtMost(finalInvoiceAmount)
            val remaining = (finalInvoiceAmount - paid).coerceAtLeast(0.0)
            Quadruple(paid, remaining, remaining > 0, "دفع جزئي (Partial)")
        }
    }

    // Product Selection Dialog
    if (showProductSelectionDialog) {
        SelectProductsDialog(
            allProducts = products,
            alreadySelectedIds = invoiceProducts.map { it.product.id }.toSet(),
            currencySymbol = currencySymbol,
            onDismiss = { showProductSelectionDialog = false },
            onAddProducts = { addedItems ->
                addedItems.forEach { (prod, qty) ->
                    val existingIdx = invoiceProducts.indexOfFirst { it.product.id == prod.id }
                    if (existingIdx >= 0) {
                        val existing = invoiceProducts[existingIdx]
                        invoiceProducts[existingIdx] = existing.copy(quantity = existing.quantity + qty)
                    } else {
                        invoiceProducts.add(InvoiceProductItem(product = prod, quantity = qty))
                    }
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header with prominent "+" button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CardSalesBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = CardSalesAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إنشاء فاتورة مبيعات جديدة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.5.sp,
                                color = MandoubakNavy
                            )
                            Text(
                                text = "إضافة عدة منتجات، مراجعة الطلب وحفظ الفاتورة",
                                fontSize = 11.sp,
                                color = MandoubakTextSecondary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Top "+" Button to quickly add products
                        Button(
                            onClick = { showProductSelectionDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSalesAccent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "إضافة منتجات", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ صنف", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = MandoubakNavy,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form Body
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ==========================================
                    // 1. CUSTOMER INFORMATION (Top of screen)
                    // ==========================================
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = CardSalesAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "بيانات العميل (Customer Information)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MandoubakNavy
                                        )
                                    }

                                    if (clients.isNotEmpty()) {
                                        Box {
                                            TextButton(
                                                onClick = { showClientsDropdown = true },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("اختيار عميل مسجل", fontSize = 11.5.sp, color = CardSalesAccent)
                                            }
                                            DropdownMenu(
                                                expanded = showClientsDropdown,
                                                onDismissRequest = { showClientsDropdown = false }
                                            ) {
                                                clients.forEach { c ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Column {
                                                                Text(c.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                                if (c.phone.isNotBlank()) {
                                                                    Text(c.phone, fontSize = 11.sp, color = MandoubakTextSecondary)
                                                                }
                                                            }
                                                        },
                                                        onClick = {
                                                            customerName = c.name
                                                            customerPhone = c.phone
                                                            showClientsDropdown = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // 1. Customer Name Field
                                OutlinedTextField(
                                    value = customerName,
                                    onValueChange = {
                                        customerName = it
                                        if (it.isNotBlank()) hasAttemptedSubmit = false
                                    },
                                    isError = hasAttemptedSubmit && !isCustomerNameValid,
                                    label = { Text("اسم العميل *") },
                                    placeholder = { Text("أدخل اسم العميل بالكامل") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (hasAttemptedSubmit && !isCustomerNameValid) Color(0xFFDC2626) else CardSalesAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CardSalesAccent,
                                        unfocusedBorderColor = if (hasAttemptedSubmit && !isCustomerNameValid) Color(0xFFDC2626) else Color(0xFFCBD5E1),
                                        errorBorderColor = Color(0xFFDC2626),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (hasAttemptedSubmit && !isCustomerNameValid) {
                                    Text(
                                        text = "⚠️ يرجى إدخال اسم العميل للمتابعة",
                                        fontSize = 11.sp,
                                        color = Color(0xFFDC2626),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // 2. Customer Phone Number Field
                                OutlinedTextField(
                                    value = customerPhone,
                                    onValueChange = { customerPhone = it },
                                    label = { Text("رقم هاتف العميل") },
                                    placeholder = { Text("رقم الجوال (مثال: 05xxxxxxxx)") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = MandoubakNavy,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CardSalesAccent,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // ==========================================
                    // 2. MULTIPLE PRODUCTS IN ONE INVOICE
                    // ==========================================
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Inventory2,
                                            contentDescription = null,
                                            tint = CardSalesAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "أصناف الفاتورة (${invoiceProducts.size})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MandoubakNavy
                                        )
                                    }

                                    // The "+" Button
                                    Button(
                                        onClick = { showProductSelectionDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = CardSalesAccent),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "إضافة منتج",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "إضافة صنف (+)",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                if (invoiceProducts.isEmpty()) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "لم تتم إضافة أي صنف للفاتورة بعد",
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MandoubakTextSecondary
                                            )
                                            OutlinedButton(
                                                onClick = { showProductSelectionDialog = true },
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(1.dp, CardSalesAccent)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null, tint = CardSalesAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("اختر أصناف من المخزون (+)", fontSize = 12.sp, color = CardSalesAccent, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                } else {
                                    // List of added products with full details and quantity system
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        invoiceProducts.forEachIndexed { index, item ->
                                            val product = item.product
                                            val itemTotal = product.salePrice * item.quantity
                                            val unitBreakdown = calculateUnitBreakdown(product.unit, item.quantity)

                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Row 1: Product Name & Delete button
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "${index + 1}. ${product.name}",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.5.sp,
                                                            color = MandoubakNavy,
                                                            modifier = Modifier.weight(1f)
                                                        )

                                                        IconButton(
                                                            onClick = { invoiceProducts.removeAt(index) },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.DeleteOutline,
                                                                contentDescription = "حذف الصنف",
                                                                tint = Color(0xFFDC2626),
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                        }
                                                    }

                                                    // Row 2: Barcode, Unit, Selling price
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (product.barcode.isNotBlank()) {
                                                            Surface(
                                                                color = Color(0xFFF1F5F9),
                                                                shape = RoundedCornerShape(4.dp)
                                                            ) {
                                                                Text(
                                                                    text = "الباركود: ${product.barcode}",
                                                                    fontSize = 10.5.sp,
                                                                    color = MandoubakNavy,
                                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }

                                                        Surface(
                                                            color = Color(0xFFF1F5F9),
                                                            shape = RoundedCornerShape(4.dp)
                                                        ) {
                                                            Text(
                                                                text = "الوحدة: ${product.unit.ifBlank { "حبة" }}",
                                                                fontSize = 10.5.sp,
                                                                color = MandoubakTextSecondary,
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                            )
                                                        }

                                                        Text(
                                                            text = "سعر البيع: " + String.format(Locale.getDefault(), "%.1f %s", product.salePrice, currencySymbol),
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = CardSalesAccent
                                                        )
                                                    }

                                                    // Row 3: Quantity Controls & Item Total Price
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Stepper & Direct Typing
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                        ) {
                                                            Text(
                                                                text = "الكمية:",
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = MandoubakNavy
                                                            )

                                                            // Minus Button
                                                            IconButton(
                                                                onClick = {
                                                                    if (item.quantity > 1) {
                                                                        invoiceProducts[index] = item.copy(quantity = item.quantity - 1)
                                                                    } else {
                                                                        invoiceProducts.removeAt(index)
                                                                    }
                                                                },
                                                                modifier = Modifier
                                                                    .size(30.dp)
                                                                    .clip(CircleShape)
                                                                    .background(Color(0xFFE2E8F0))
                                                            ) {
                                                                Icon(Icons.Default.Remove, contentDescription = "تقليل الكمية", modifier = Modifier.size(16.dp))
                                                            }

                                                            // Direct Number Input field
                                                            OutlinedTextField(
                                                                value = item.quantity.toString(),
                                                                onValueChange = { input ->
                                                                    val cleaned = input.filter { ch -> ch.isDigit() }
                                                                    val newQty = cleaned.toIntOrNull() ?: 1
                                                                    invoiceProducts[index] = item.copy(quantity = newQty.coerceAtLeast(1))
                                                                },
                                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                                singleLine = true,
                                                                shape = RoundedCornerShape(8.dp),
                                                                colors = OutlinedTextFieldDefaults.colors(
                                                                    focusedBorderColor = CardSalesAccent,
                                                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                                                    focusedContainerColor = Color.White,
                                                                    unfocusedContainerColor = Color.White
                                                                ),
                                                                modifier = Modifier.width(64.dp)
                                                            )

                                                            // Plus Button
                                                            IconButton(
                                                                onClick = {
                                                                    invoiceProducts[index] = item.copy(quantity = item.quantity + 1)
                                                                },
                                                                modifier = Modifier
                                                                    .size(30.dp)
                                                                    .clip(CircleShape)
                                                                    .background(CardSalesAccent)
                                                            ) {
                                                                Icon(Icons.Default.Add, contentDescription = "زيادة الكمية", tint = Color.White, modifier = Modifier.size(16.dp))
                                                            }
                                                        }

                                                        // Item Total Price
                                                        Column(horizontalAlignment = Alignment.End) {
                                                            Text(
                                                                text = "الإجمالي:",
                                                                fontSize = 10.5.sp,
                                                                color = MandoubakTextSecondary
                                                            )
                                                            Text(
                                                                text = String.format(Locale.getDefault(), "%.1f %s", itemTotal, currencySymbol),
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MandoubakNavy
                                                            )
                                                        }
                                                    }

                                                    // Row 4: Dynamic Unit pieces calculation (Dozen, Carton, etc.)
                                                    if (unitBreakdown != null) {
                                                        Card(
                                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                                            shape = RoundedCornerShape(8.dp),
                                                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    text = unitBreakdown.first,
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    color = Color(0xFF047857)
                                                                )
                                                                Text(
                                                                    text = unitBreakdown.second,
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFF047857)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // 3. AMOUNT AND DISCOUNT
                    // ==========================================
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = CardSalesAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "المبلغ والخصم (Amount & Discount)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MandoubakNavy
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // 1. Invoice Amount (Auto from products or manual)
                                    OutlinedTextField(
                                        value = if (invoiceProducts.isNotEmpty()) {
                                            String.format(Locale.getDefault(), "%.1f", subtotalAmount)
                                        } else {
                                            manualInvoiceAmountText
                                        },
                                        onValueChange = {
                                            if (invoiceProducts.isEmpty()) {
                                                manualInvoiceAmountText = it.filter { ch -> ch.isDigit() || ch == '.' }
                                            }
                                        },
                                        readOnly = invoiceProducts.isNotEmpty(),
                                        label = { Text("مبلغ الفاتورة *") },
                                        placeholder = { Text("0.0") },
                                        trailingIcon = {
                                            Text(
                                                text = currencySymbol,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CardSalesAccent,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = CardSalesAccent,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    // 2. Discount Amount (Optional)
                                    OutlinedTextField(
                                        value = discountAmountText,
                                        onValueChange = { discountAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                        label = { Text("مبلغ الخصم") },
                                        placeholder = { Text("0.0 (اختياري)") },
                                        trailingIcon = {
                                            Text(
                                                text = currencySymbol,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626),
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = CardSalesAccent,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Automatically recalculated final invoice amount banner
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "المبلغ الصافي للفاتورة:",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MandoubakNavy
                                            )
                                            if (discountAmount > 0) {
                                                Text(
                                                    text = "تم خصم: " + String.format(Locale.getDefault(), "%.1f %s", discountAmount, currencySymbol),
                                                    fontSize = 10.5.sp,
                                                    color = Color(0xFFDC2626)
                                                )
                                            }
                                        }

                                        Text(
                                            text = String.format(Locale.getDefault(), "%.1f %s", finalInvoiceAmount, currencySymbol),
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CardSalesAccent
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // 4. PAYMENT METHOD (Cash, Credit, Partial)
                    // ==========================================
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = CardSalesAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "طريقة الدفع (Payment Method) *",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MandoubakNavy
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    InvoicePaymentOption.values().forEach { option ->
                                        val isSelected = selectedPaymentOption == option
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { selectedPaymentOption = option },
                                            label = {
                                                Text(
                                                    text = "${option.titleAr} (${option.titleEn})",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CardSalesAccent,
                                                selectedLabelColor = Color.White,
                                                containerColor = Color.White,
                                                labelColor = MandoubakNavy
                                            ),
                                            border = FilterChipDefaults.filterChipBorder(
                                                enabled = true,
                                                selected = isSelected,
                                                borderColor = if (isSelected) CardSalesAccent else Color(0xFFCBD5E1)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                when (selectedPaymentOption) {
                                    InvoicePaymentOption.CASH -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = String.format(Locale.getDefault(), "%.1f", finalInvoiceAmount),
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    label = { Text("المبلغ المدفوع (Paid Amount) *") },
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Default.CheckCircle,
                                                            contentDescription = null,
                                                            tint = Color(0xFF059669),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        Text(
                                                            text = currencySymbol,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF059669),
                                                            modifier = Modifier.padding(end = 8.dp)
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFF059669),
                                                        unfocusedBorderColor = Color(0xFF86EFAC),
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White
                                                    ),
                                                    modifier = Modifier.fillMaxWidth()
                                                )

                                                Text(
                                                    text = "✓ الفاتورة مسددة بالكامل نقداً — المتبقي: 0.0 $currencySymbol",
                                                    fontSize = 11.5.sp,
                                                    color = Color(0xFF047857),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    InvoicePaymentOption.CREDIT -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = String.format(Locale.getDefault(), "%.1f", finalInvoiceAmount),
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    label = { Text("الرصيد المتبقي بذمة العميل (Remaining Balance) *") },
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Default.HourglassTop,
                                                            contentDescription = null,
                                                            tint = Color(0xFFD97706),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        Text(
                                                            text = currencySymbol,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFFD97706),
                                                            modifier = Modifier.padding(end = 8.dp)
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFFD97706),
                                                        unfocusedBorderColor = Color(0xFFFCD34D),
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White
                                                    ),
                                                    modifier = Modifier.fillMaxWidth()
                                                )

                                                val clientDisplayName = customerName.ifBlank { "العميل" }
                                                Text(
                                                    text = "⏳ فاتورة آجلة: سيتم تسجيل كامل المبلغ ديناً بذمة $clientDisplayName",
                                                    fontSize = 11.5.sp,
                                                    color = Color(0xFFB45309),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    InvoicePaymentOption.PARTIAL -> {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = partialPaidText,
                                                    onValueChange = { partialPaidText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                                    label = { Text("المبلغ المدفوع مقدماً (Paid Amount) *") },
                                                    placeholder = { Text("أدخل الدفعة المستلمة") },
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Default.Payments,
                                                            contentDescription = null,
                                                            tint = CardSalesAccent,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        Text(
                                                            text = currencySymbol,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = CardSalesAccent,
                                                            modifier = Modifier.padding(end = 8.dp)
                                                        )
                                                    },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                    singleLine = true,
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = CardSalesAccent,
                                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White
                                                    ),
                                                    modifier = Modifier.fillMaxWidth()
                                                )

                                                OutlinedTextField(
                                                    value = String.format(Locale.getDefault(), "%.1f", calculatedRemaining),
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    label = { Text("المبلغ المتبقي المحسوب تلقائياً (Remaining Amount)") },
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Default.HourglassTop,
                                                            contentDescription = null,
                                                            tint = Color(0xFFD97706),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        Text(
                                                            text = currencySymbol,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFFD97706),
                                                            modifier = Modifier.padding(end = 8.dp)
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color(0xFFD97706),
                                                        unfocusedBorderColor = Color(0xFFFCD34D),
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White
                                                    ),
                                                    modifier = Modifier.fillMaxWidth()
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "سداد: " + String.format(Locale.getDefault(), "%.1f %s", calculatedPaid, currencySymbol),
                                                        fontSize = 11.5.sp,
                                                        color = Color(0xFF059669),
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = "المتبقي ديناً: " + String.format(Locale.getDefault(), "%.1f %s", calculatedRemaining, currencySymbol),
                                                        fontSize = 11.5.sp,
                                                        color = Color(0xFFD97706),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Optional Notes
                    item {
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            label = { Text("ملاحظات أو بيان الفاتورة (اختياري)") },
                            placeholder = { Text("أدخل أي شروط أو تفاصيل إضافية...") },
                            leadingIcon = {
                                Icon(Icons.Default.Notes, contentDescription = null, tint = MandoubakTextSecondary, modifier = Modifier.size(18.dp))
                            },
                            maxLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CardSalesAccent,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Summary Card / Review Order (All 6 Financial Calculations)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Receipt,
                                            contentDescription = null,
                                            tint = CardSalesAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ملخص الفاتورة والحسابات (Invoice Summary)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MandoubakNavy
                                        )
                                    }
                                    Text(
                                        text = "${invoiceProducts.size} أصناف (${invoiceProducts.sumOf { it.quantity }} وحدة)",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CardSalesAccent
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFE2E8F0))

                                // 1. Total Before Discount
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("المجموع قبل الخصم (Total Before Discount):", fontSize = 12.sp, color = MandoubakTextSecondary)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f %s", subtotalAmount, currencySymbol),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MandoubakNavy
                                    )
                                }

                                // 2. Discount Amount
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("مبلغ الخصم (Discount Amount):", fontSize = 12.sp, color = if (discountAmount > 0) Color(0xFFDC2626) else MandoubakTextSecondary)
                                    Text(
                                        text = if (discountAmount > 0) {
                                            String.format(Locale.getDefault(), "- %.1f %s", discountAmount, currencySymbol)
                                        } else {
                                            String.format(Locale.getDefault(), "0.0 %s", currencySymbol)
                                        },
                                        fontSize = 12.5.sp,
                                        color = if (discountAmount > 0) Color(0xFFDC2626) else MandoubakTextSecondary,
                                        fontWeight = if (discountAmount > 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFE2E8F0))

                                // 3. Final Net Total
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("الصافي النهائي للفاتورة (Final Net Total):", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MandoubakNavy)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f %s", finalInvoiceAmount, currencySymbol),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = CardSalesAccent
                                    )
                                }

                                // 4. Amount Paid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("المبلغ المدفوع (Amount Paid):", fontSize = 12.sp, color = Color(0xFF059669))
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f %s", calculatedPaid, currencySymbol),
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF059669),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // 5. Remaining Balance
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الرصيد المتبقي (Remaining Balance):",
                                        fontSize = 12.sp,
                                        color = if (calculatedRemaining > 0) Color(0xFFD97706) else Color(0xFF059669),
                                        fontWeight = if (calculatedRemaining > 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f %s", calculatedRemaining, currencySymbol),
                                        fontSize = 12.5.sp,
                                        color = if (calculatedRemaining > 0) Color(0xFFD97706) else Color(0xFF059669),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFE2E8F0))

                                // 6. Invoice Profit
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ربح الفاتورة (Invoice Profit):", fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                    }
                                    Surface(
                                        color = Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = String.format(Locale.getDefault(), "+ %.1f %s", invoiceProfit, currencySymbol),
                                            fontSize = 12.5.sp,
                                            color = Color(0xFF059669),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Cancel and Save & Confirm Invoice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء", color = MandoubakTextSecondary)
                    }

                    Button(
                        onClick = {
                            hasAttemptedSubmit = true
                            if (isFormValid) {
                                val itemsList = invoiceProducts.map { Pair(it.product, it.quantity) }
                                onSubmit(
                                    customerName.trim(),
                                    customerPhone.trim(),
                                    itemsList,
                                    subtotalAmount,
                                    discountAmount,
                                    finalInvoiceAmount,
                                    calculatedPaid,
                                    isCreditMode,
                                    paymentMethodLabel,
                                    notesText.trim()
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CardSalesAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.8f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "حفظ وتأكيد الفاتورة (Save & Confirm Invoice)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceSavedSuccessDialog(
    invoice: SaleInvoiceEntity,
    customerPhone: String = "",
    items: List<Pair<ProductEntity, Int>> = emptyList(),
    currencySymbol: String = "ر.س",
    companyName: String = "مؤسسة التوزيع والتجارة الحديثة",
    representativeName: String = "مندوب المبيعات",
    taxNumber: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isFullPreviewExpanded by remember { mutableStateOf(true) }
    var pdfGeneratedFile by remember { mutableStateOf<File?>(null) }
    var actionStatusMessage by remember { mutableStateOf<String?>(null) }

    val dateFormat = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(invoice.dateMillis))

    val pdfItems = remember(items, invoice) {
        if (items.isNotEmpty()) {
            items.map {
                PdfInvoiceItem(
                    name = it.first.name,
                    barcode = it.first.barcode,
                    unit = it.first.unit.ifBlank { "حبة" },
                    quantity = it.second,
                    unitPrice = it.first.salePrice,
                    totalPrice = it.first.salePrice * it.second
                )
            }
        } else {
            emptyList<PdfInvoiceItem>()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Success Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFECFDF5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تم حفظ وتأكيد الفاتورة بنجاح!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF047857)
                            )
                            Text(
                                text = "${invoice.invoiceNumber}  •  $dateStr",
                                fontSize = 11.5.sp,
                                color = MandoubakTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    }
                }

                if (actionStatusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = actionStatusMessage!!,
                            color = CardSalesAccent,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Invoice Content Preview
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Customer & Invoice Details Bar
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("اسم العميل:", color = MandoubakTextSecondary, fontSize = 12.sp)
                                    Text(invoice.clientName, fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 13.sp)
                                }

                                if (customerPhone.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("رقم الهاتف:", color = MandoubakTextSecondary, fontSize = 12.sp)
                                        Text(customerPhone, fontWeight = FontWeight.SemiBold, color = MandoubakNavy, fontSize = 12.5.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("طريقة الدفع:", color = MandoubakTextSecondary, fontSize = 12.sp)
                                    Text(
                                        text = invoice.paymentMethod,
                                        fontWeight = FontWeight.Bold,
                                        color = if (invoice.remainingAmount > 0) Color(0xFFD97706) else Color(0xFF059669),
                                        fontSize = 12.5.sp
                                    )
                                }
                            }
                        }
                    }

                    // Products Table Header & Rows
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Table Header
                                Surface(
                                    color = MandoubakNavy,
                                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("#", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                                        Text("بيان الصنف", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                        Text("الوحدة", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(45.dp))
                                        Text("الكمية", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                                        Text("السعر", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(55.dp))
                                        Text("الإجمالي", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                                    }
                                }

                                // Table Rows
                                if (items.isNotEmpty()) {
                                    items.forEachIndexed { index, (product, qty) ->
                                        val total = product.salePrice * qty
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (index % 2 == 1) Color(0xFFF8FAFC) else Color.White)
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("${index + 1}", fontSize = 11.sp, color = MandoubakTextSecondary, modifier = Modifier.width(24.dp))
                                            Text(product.name, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = MandoubakNavy, modifier = Modifier.weight(1.5f))
                                            Text(product.unit.ifBlank { "حبة" }, fontSize = 11.sp, color = MandoubakTextSecondary, modifier = Modifier.width(45.dp))
                                            Text("$qty", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MandoubakNavy, modifier = Modifier.width(40.dp))
                                            Text(String.format(Locale.getDefault(), "%.1f", product.salePrice), fontSize = 11.sp, color = MandoubakNavy, modifier = Modifier.width(55.dp))
                                            Text(String.format(Locale.getDefault(), "%.1f", total), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = CardSalesAccent, modifier = Modifier.width(60.dp))
                                        }
                                        HorizontalDivider(color = Color(0xFFF1F5F9))
                                    }
                                } else {
                                    Text(
                                        text = invoice.itemsSummary,
                                        fontSize = 12.sp,
                                        color = MandoubakNavy,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Financial Summary Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val subtotal = if (invoice.subtotalAmount > 0) invoice.subtotalAmount else invoice.totalAmount

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("المجموع قبل الخصم (Total Before Discount):", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                    Text(String.format(Locale.getDefault(), "%.1f %s", subtotal, currencySymbol), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("مبلغ الخصم (Discount Amount):", fontSize = 11.5.sp, color = if (invoice.discountAmount > 0) Color(0xFFDC2626) else MandoubakTextSecondary)
                                    Text(
                                        text = if (invoice.discountAmount > 0) String.format(Locale.getDefault(), "- %.1f %s", invoice.discountAmount, currencySymbol) else "0.0 $currencySymbol",
                                        fontSize = 12.sp,
                                        color = if (invoice.discountAmount > 0) Color(0xFFDC2626) else MandoubakTextSecondary,
                                        fontWeight = if (invoice.discountAmount > 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFCBD5E1))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("الصافي النهائي للفاتورة (Final Net Total):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f %s", invoice.totalAmount, currencySymbol),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = CardSalesAccent
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("المبلغ المدفوع (Amount Paid):", fontSize = 11.5.sp, color = Color(0xFF059669))
                                    Text(String.format(Locale.getDefault(), "%.1f %s", invoice.paidAmount, currencySymbol), fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("الرصيد المتبقي (Remaining Balance):", fontSize = 11.5.sp, color = if (invoice.remainingAmount > 0) Color(0xFFD97706) else Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                                    Text(String.format(Locale.getDefault(), "%.1f %s", invoice.remainingAmount, currencySymbol), fontSize = 12.sp, color = if (invoice.remainingAmount > 0) Color(0xFFD97706) else Color(0xFF059669), fontWeight = FontWeight.Bold)
                                }

                                if (invoice.profitAmount > 0) {
                                    HorizontalDivider(color = Color(0xFFCBD5E1))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("ربح الفاتورة (Invoice Profit):", color = Color(0xFF059669), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        Text(String.format(Locale.getDefault(), "+ %.1f %s", invoice.profitAmount, currencySymbol), color = Color(0xFF059669), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (invoice.notes.isNotBlank()) {
                                    HorizontalDivider(color = Color(0xFFCBD5E1))
                                    Text("ملاحظات: ${invoice.notes}", fontSize = 11.sp, color = MandoubakTextSecondary)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // 4 Action Buttons: Preview, Generate PDF, Print, Share
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Preview Button
                        OutlinedButton(
                            onClick = {
                                isFullPreviewExpanded = !isFullPreviewExpanded
                                actionStatusMessage = if (isFullPreviewExpanded) "عرض المعاينة الكاملة للفاتورة" else "تم إخفاء التفاصيل"
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("معاينة الفاتورة", fontSize = 11.sp)
                        }

                        // 2. Generate PDF Button
                        Button(
                            onClick = {
                                val file = PdfInvoiceGenerator.buildInvoicePdfFile(
                                    context = context,
                                    invoice = invoice,
                                    customerPhone = customerPhone,
                                    items = if (pdfItems.isNotEmpty()) pdfItems else null,
                                    companyName = companyName,
                                    representativeName = representativeName,
                                    taxNumber = taxNumber,
                                    currencySymbol = currencySymbol
                                )
                                pdfGeneratedFile = file
                                actionStatusMessage = if (file != null) "✓ تم توليد ملف PDF بنجاح (${file.name})" else "فشل توليد ملف PDF"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("توليد PDF", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 3. Print Invoice Button
                        Button(
                            onClick = {
                                val file = pdfGeneratedFile ?: PdfInvoiceGenerator.buildInvoicePdfFile(
                                    context = context,
                                    invoice = invoice,
                                    customerPhone = customerPhone,
                                    items = if (pdfItems.isNotEmpty()) pdfItems else null,
                                    companyName = companyName,
                                    representativeName = representativeName,
                                    taxNumber = taxNumber,
                                    currencySymbol = currencySymbol
                                )
                                if (file != null) {
                                    pdfGeneratedFile = file
                                    PdfInvoiceGenerator.printInvoicePdf(context, file, invoice)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("طباعة الفاتورة", fontSize = 11.sp, color = Color.White)
                        }

                        // 4. Share PDF Button
                        Button(
                            onClick = {
                                val file = pdfGeneratedFile ?: PdfInvoiceGenerator.buildInvoicePdfFile(
                                    context = context,
                                    invoice = invoice,
                                    customerPhone = customerPhone,
                                    items = if (pdfItems.isNotEmpty()) pdfItems else null,
                                    companyName = companyName,
                                    representativeName = representativeName,
                                    taxNumber = taxNumber,
                                    currencySymbol = currencySymbol
                                )
                                if (file != null) {
                                    pdfGeneratedFile = file
                                    PdfInvoiceGenerator.shareInvoicePdf(context, file, invoice)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSalesAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مشاركة PDF", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    // Done / Dismiss
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("تم وإغلاق (Done)", color = MandoubakNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
