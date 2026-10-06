package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SupplierEntity
import com.example.data.entity.SupplierPaymentEntity
import com.example.ui.theme.CardPurchasesAccent
import com.example.ui.theme.CardPurchasesBg
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SuppliersScreen(
    suppliers: List<SupplierEntity>,
    purchases: List<PurchaseInvoiceEntity> = emptyList(),
    payments: List<SupplierPaymentEntity> = emptyList(),
    currencySymbol: String = "ر.س",
    onBackClick: () -> Unit,
    onCreateSupplierClick: () -> Unit,
    onRecordSupplierPayment: (SupplierEntity, Double, String, String) -> Unit,
    onDeleteSupplierPayment: (SupplierPaymentEntity) -> Unit,
    onUpdateSupplier: (SupplierEntity) -> Unit,
    onDeleteSupplier: (SupplierEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSupplierForDetails by remember { mutableStateOf<SupplierEntity?>(null) }
    var supplierToEdit by remember { mutableStateOf<SupplierEntity?>(null) }
    var supplierForPaymentDialog by remember { mutableStateOf<SupplierEntity?>(null) }
    var supplierToDelete by remember { mutableStateOf<SupplierEntity?>(null) }
    var paymentToDelete by remember { mutableStateOf<SupplierPaymentEntity?>(null) }

    val filteredSuppliers = remember(suppliers, searchQuery) {
        if (searchQuery.isBlank()) suppliers
        else {
            val q = searchQuery.trim().lowercase()
            suppliers.filter {
                it.name.lowercase().contains(q) ||
                it.companyName.lowercase().contains(q) ||
                it.phone.contains(q) ||
                it.address.lowercase().contains(q)
            }
        }
    }

    val context = LocalContext.current

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
                        .testTag("suppliers_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MandoubakNavy
                    )
                }

                Text(
                    text = "سجل الموردين والشركات",
                    color = MandoubakNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardPurchasesBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = CardPurchasesAccent
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateSupplierClick,
                containerColor = CardPurchasesAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_supplier_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة مورد")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Stats Overview Card
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
                        Text("إجمالي الموردين", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        Text(
                            text = "${suppliers.size}",
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
                        Text("مستحقات الموردين (آجل)", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        val totalPayables = suppliers.sumOf { it.outstandingBalance }
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f %s", totalPayables, currencySymbol),
                            color = if (totalPayables > 0) Color(0xFFD97706) else Color(0xFF059669),
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
                        Text("موردين دائنين", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        val creditorCount = suppliers.count { it.outstandingBalance > 0 }
                        Text(
                            text = "$creditorCount",
                            color = MandoubakNavy,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث باسم المورد، الشركة، الهاتف، أو المدينة...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MandoubakTextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح", tint = MandoubakTextSecondary)
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
                    .padding(vertical = 6.dp)
            )

            // Suppliers List
            if (filteredSuppliers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
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
                            text = if (searchQuery.isBlank()) "لا يوجد موردون مسجلون حالياً" else "لا توجد نتائج مطابقة لبحثك",
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
                    items(filteredSuppliers, key = { it.id }) { supplier ->
                        SupplierCard(
                            supplier = supplier,
                            currencySymbol = currencySymbol,
                            onCardClick = { selectedSupplierForDetails = supplier },
                            onCallClick = {
                                if (supplier.phone.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${supplier.phone}"))
                                    context.startActivity(intent)
                                }
                            },
                            onPaymentClick = { supplierForPaymentDialog = supplier },
                            onEditClick = { supplierToEdit = supplier },
                            onDeleteClick = { supplierToDelete = supplier }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }

    // Delete Supplier Confirmation Dialog
    supplierToDelete?.let { supplier ->
        AlertDialog(
            onDismissRequest = { supplierToDelete = null },
            title = {
                Text(
                    text = "تأكيد حذف المورد",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
            },
            text = {
                Text("هل أنت متأكد من حذف المورد (${supplier.name}) من سجلات التطبيق؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSupplier(supplier)
                        if (selectedSupplierForDetails?.id == supplier.id) {
                            selectedSupplierForDetails = null
                        }
                        supplierToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حذف المورد", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { supplierToDelete = null }) {
                    Text("إلغاء", color = MandoubakTextSecondary)
                }
            }
        )
    }

    // Delete Payment Confirmation Dialog
    paymentToDelete?.let { payment ->
        AlertDialog(
            onDismissRequest = { paymentToDelete = null },
            title = {
                Text(
                    text = "تأكيد حذف سند الصرف",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
            },
            text = {
                Text("هل أنت متأكد من حذف سند الصرف ${payment.paymentNumber}؟ سيتم استرجاع الرصيد المستحق للمورد تلقائياً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSupplierPayment(payment)
                        paymentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حذف السند", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { paymentToDelete = null }) {
                    Text("إلغاء", color = MandoubakTextSecondary)
                }
            }
        )
    }

    // Details & History Dialog
    selectedSupplierForDetails?.let { sup ->
        val supplierPurchases = purchases.filter { it.supplierId == sup.id || it.supplierName == sup.name }
        val supplierPaymentsList = payments.filter { it.supplierId == sup.id || it.supplierName == sup.name }

        SupplierDetailsDialog(
            supplier = sup,
            purchases = supplierPurchases,
            payments = supplierPaymentsList,
            currencySymbol = currencySymbol,
            onDismiss = { selectedSupplierForDetails = null },
            onAddPayment = {
                selectedSupplierForDetails = null
                supplierForPaymentDialog = sup
            },
            onDeletePayment = { payment ->
                paymentToDelete = payment
            }
        )
    }

    // Edit Supplier Dialog
    supplierToEdit?.let { sup ->
        EditSupplierDialog(
            supplier = sup,
            currencySymbol = currencySymbol,
            onDismiss = { supplierToEdit = null },
            onConfirm = { updated ->
                onUpdateSupplier(updated)
                supplierToEdit = null
            }
        )
    }

    // Record Supplier Payment Dialog (سند صرف للمورد)
    supplierForPaymentDialog?.let { sup ->
        RecordSupplierPaymentDialog(
            supplier = sup,
            currencySymbol = currencySymbol,
            onDismiss = { supplierForPaymentDialog = null },
            onSubmit = { amount, method, notes ->
                onRecordSupplierPayment(sup, amount, method, notes)
                supplierForPaymentDialog = null
            }
        )
    }
}

@Composable
fun SupplierCard(
    supplier: SupplierEntity,
    currencySymbol: String,
    onCardClick: () -> Unit,
    onCallClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = supplier.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MandoubakNavy
                    )
                    if (supplier.companyName.isNotBlank()) {
                        Text(
                            text = supplier.companyName,
                            fontSize = 12.sp,
                            color = CardPurchasesAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Outstanding Balance badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (supplier.outstandingBalance > 0) Color(0xFFFEF3C7) else Color(0xFFECFDF5))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (supplier.outstandingBalance > 0)
                            "مستحق له: ${String.format(Locale.getDefault(), "%.1f %s", supplier.outstandingBalance, currencySymbol)}"
                        else "حساب خالص",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (supplier.outstandingBalance > 0) Color(0xFFB45309) else Color(0xFF059669)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Phone and Address Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (supplier.phone.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCallClick() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = CardPurchasesAccent, modifier = Modifier.size(13.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(supplier.phone, fontSize = 12.sp, color = MandoubakNavy, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (supplier.address.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MandoubakTextSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = supplier.address.take(25) + if (supplier.address.length > 25) "..." else "",
                            fontSize = 11.5.sp,
                            color = MandoubakTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Pay Button
                    Button(
                        onClick = onPaymentClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("سداد دفعة", fontSize = 11.sp, color = Color.White)
                    }

                    // History & Details Button
                    OutlinedButton(
                        onClick = onCardClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("السجل والتفاصيل", fontSize = 11.sp, color = MandoubakNavy)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MandoubakNavy, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SupplierDetailsDialog(
    supplier: SupplierEntity,
    purchases: List<PurchaseInvoiceEntity>,
    payments: List<SupplierPaymentEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onAddPayment: () -> Unit,
    onDeletePayment: (SupplierPaymentEntity) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("بيانات المورد", "فواتير التوريد (${purchases.size})", "سندات الصرف (${payments.size})")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(supplier.name, fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
                    if (supplier.companyName.isNotBlank()) {
                        Text(supplier.companyName, fontSize = 12.sp, color = MandoubakTextSecondary)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (supplier.outstandingBalance > 0) Color(0xFFFEF3C7) else Color(0xFFECFDF5))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (supplier.outstandingBalance > 0) "المستحق: ${supplier.outstandingBalance} $currencySymbol" else "حساب خالص",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (supplier.outstandingBalance > 0) Color(0xFFB45309) else Color(0xFF059669)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = CardPurchasesAccent
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (selectedTab) {
                    0 -> {
                        // Profile Info Tab
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                DetailRowItem(label = "اسم المورد المسؤول:", value = supplier.name)
                                DetailRowItem(label = "الشركة / المؤسسة:", value = supplier.companyName.ifBlank { "غير محدد" })
                                DetailRowItem(label = "رقم الهاتف:", value = supplier.phone.ifBlank { "غير محدد" })
                                DetailRowItem(label = "العنوان والموقع:", value = supplier.address.ifBlank { "غير محدد" })
                                DetailRowItem(label = "الرصيد المستحق الحالي:", value = "${supplier.outstandingBalance} $currencySymbol")
                                DetailRowItem(label = "ملاحظات التوريد:", value = supplier.notes.ifBlank { "لا توجد ملاحظات" })
                            }
                        }
                    }

                    1 -> {
                        // Purchase History Tab
                        if (purchases.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                                Text("لا توجد فواتير شراء سابقة لهذا المورد", color = MandoubakTextSecondary, fontSize = 12.sp)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.height(240.dp)
                            ) {
                                items(purchases) { pur ->
                                    val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(pur.dateMillis))
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(pur.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = MandoubakNavy)
                                                Text("$dateStr", fontSize = 11.sp, color = MandoubakTextSecondary)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(pur.itemsSummary, fontSize = 11.5.sp, color = MandoubakTextPrimary, maxLines = 2)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (pur.isCredit) "آجل للمورد" else "مسددة بالكامل",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (pur.isCredit) Color(0xFFD97706) else Color(0xFF059669)
                                                )
                                                Text("${pur.totalAmount} $currencySymbol", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CardPurchasesAccent)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Payment History Tab (سندات الصرف)
                        if (payments.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                                Text("لا توجد سندات صرف أو دفعات مسددة مسجلة", color = MandoubakTextSecondary, fontSize = 12.sp)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.height(240.dp)
                            ) {
                                items(payments) { p ->
                                    val dateStr = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault()).format(Date(p.dateMillis))
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(p.paymentNumber, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = MandoubakNavy)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("(${p.paymentMethod})", fontSize = 11.sp, color = MandoubakTextSecondary)
                                                }
                                                Text(dateStr, fontSize = 10.5.sp, color = MandoubakTextSecondary)
                                                if (p.notes.isNotBlank()) {
                                                    Text(p.notes, fontSize = 11.sp, color = MandoubakTextPrimary)
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("${p.amount} $currencySymbol", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF059669))
                                                IconButton(
                                                    onClick = { onDeletePayment(p) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(16.dp))
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
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAddPayment,
                    colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent)
                ) {
                    Text("سداد دفعة للمورد", color = Color.White)
                }
                TextButton(onClick = onDismiss) {
                    Text("إغلاق", color = MandoubakNavy)
                }
            }
        }
    )
}

@Composable
fun DetailRowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MandoubakTextSecondary, fontSize = 12.sp)
        Text(value, color = MandoubakTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
fun CreateSupplierDialog(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, address: String, companyName: String, initialBalance: Double, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var initialBalanceText by remember { mutableStateOf("0.0") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إضافة مورد جديد",
                fontWeight = FontWeight.Bold,
                color = MandoubakNavy,
                fontSize = 17.sp
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم المورد أو المسؤول *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("اسم الشركة / المؤسسة المصنعة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم الهاتف / الجوال") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("العنوان / المدينة / المستودع") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = initialBalanceText,
                        onValueChange = { initialBalanceText = it },
                        label = { Text("رصيد مستحق سابق للمورد ($currencySymbol)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات وشروط التوريد") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val bal = initialBalanceText.toDoubleOrNull() ?: 0.0
                        onSubmit(name.trim(), phone.trim(), address.trim(), companyName.trim(), bal, notes.trim())
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ المورد", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )
}

@Composable
fun EditSupplierDialog(
    supplier: SupplierEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (SupplierEntity) -> Unit
) {
    var name by remember { mutableStateOf(supplier.name) }
    var companyName by remember { mutableStateOf(supplier.companyName) }
    var phone by remember { mutableStateOf(supplier.phone) }
    var address by remember { mutableStateOf(supplier.address) }
    var balanceText by remember { mutableStateOf("${supplier.outstandingBalance}") }
    var notes by remember { mutableStateOf(supplier.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تعديل بيانات المورد",
                fontWeight = FontWeight.Bold,
                color = MandoubakNavy,
                fontSize = 17.sp
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم المورد *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("الشركة / المصنع") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم الهاتف") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("العنوان") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = balanceText,
                        onValueChange = { balanceText = it },
                        label = { Text("الرصيد المستحق ($currencySymbol)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val bal = balanceText.toDoubleOrNull() ?: supplier.outstandingBalance
                        onConfirm(
                            supplier.copy(
                                name = name.trim(),
                                companyName = companyName.trim(),
                                phone = phone.trim(),
                                address = address.trim(),
                                outstandingBalance = bal,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ التعديلات", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )
}

@Composable
fun RecordSupplierPaymentDialog(
    supplier: SupplierEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, paymentMethod: String, notes: String) -> Unit
) {
    var amountText by remember { mutableStateOf(if (supplier.outstandingBalance > 0) "${supplier.outstandingBalance}" else "100.0") }
    var paymentMethod by remember { mutableStateOf("نقداً") }
    val methods = listOf("نقداً", "تحويل بنكي", "شيك")
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("سند صرف / سداد لمورد", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
                Icon(Icons.Default.Receipt, contentDescription = null, tint = CardPurchasesAccent)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "المورد المستفيد: ${supplier.name}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MandoubakNavy
                )
                Text(
                    text = "الرصيد المستحق الحالي: ${supplier.outstandingBalance} $currencySymbol",
                    fontSize = 12.sp,
                    color = if (supplier.outstandingBalance > 0) Color(0xFFD97706) else Color(0xFF059669)
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("المبلغ المدفوع ($currencySymbol) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("طريقة السداد:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MandoubakNavy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    methods.forEach { m ->
                        val isSel = paymentMethod == m
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CardPurchasesAccent else Color(0xFFF1F5F9))
                                .clickable { paymentMethod = m }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = m,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else MandoubakNavy
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات / رقم الحوالة أو الشيك") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onSubmit(amt, paymentMethod, notes.trim())
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = CardPurchasesAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("اعتماد وسداد السند", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )
}
