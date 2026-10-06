package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ClientEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.ui.theme.CardReceiptAccent
import com.example.ui.theme.CardReceiptBg
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.util.PdfInvoiceGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptsScreen(
    receipts: List<PaymentReceiptEntity>,
    clients: List<ClientEntity>,
    currencySymbol: String = "ر.س",
    companyName: String = "مؤسسة التوزيع والتجارة الحديثة",
    representativeName: String = "مندوب المبيعات",
    onBackClick: () -> Unit,
    onCreateReceiptClick: () -> Unit,
    onDeleteReceipt: (PaymentReceiptEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedReceiptForDetails by remember { mutableStateOf<PaymentReceiptEntity?>(null) }
    var receiptToDelete by remember { mutableStateOf<PaymentReceiptEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredReceipts = remember(receipts, searchQuery) {
        if (searchQuery.isBlank()) receipts
        else {
            val q = searchQuery.trim().lowercase()
            receipts.filter {
                it.receiptNumber.lowercase().contains(q) ||
                it.clientName.lowercase().contains(q) ||
                it.notes.lowercase().contains(q)
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
                        .testTag("receipts_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MandoubakNavy
                    )
                }

                Text(
                    text = "سندات القبض والتحصيل",
                    color = MandoubakNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardReceiptBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = CardReceiptAccent
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateReceiptClick,
                containerColor = CardReceiptAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_receipt_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "سند قبض جديد")
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
                        Text("إجمالي السندات", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        Text(
                            text = "${receipts.size}",
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
                        Text("إجمالي التحصيلات", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        val totalAmount = receipts.sumOf { it.amount }
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f %s", totalAmount, currencySymbol),
                            color = CardReceiptAccent,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث برقم السند، اسم العميل، أو البيان...", fontSize = 12.5.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CardReceiptAccent,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (filteredReceipts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "لا توجد سندات مطابقة للبحث" else "لا توجد سندات قبض مسجلة حتى الآن",
                            color = MandoubakTextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (searchQuery.isBlank()) {
                            OutlinedButton(onClick = onCreateReceiptClick) {
                                Text("إنشاء أول سند قبض الآن")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredReceipts, key = { it.id }) { receipt ->
                        ReceiptCardItem(
                            receipt = receipt,
                            currencySymbol = currencySymbol,
                            onClick = { selectedReceiptForDetails = receipt },
                            onPrint = {
                                PdfInvoiceGenerator.generateAndShareReceiptPdf(
                                    context = context,
                                    receipt = receipt,
                                    companyName = companyName,
                                    representativeName = representativeName,
                                    currencySymbol = currencySymbol
                                )
                            },
                            onDelete = { receiptToDelete = receipt }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Delete Receipt Confirmation Dialog
    receiptToDelete?.let { receipt ->
        AlertDialog(
            onDismissRequest = { receiptToDelete = null },
            title = {
                Text(
                    text = "تأكيد حذف سند القبض",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
            },
            text = {
                Text("هل أنت متأكد من حذف سند القبض ${receipt.receiptNumber}؟ سيتم استرجاع رصيد مديونية العميل (${receipt.clientName}) تلقائياً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteReceipt(receipt)
                        if (selectedReceiptForDetails?.id == receipt.id) {
                            selectedReceiptForDetails = null
                        }
                        receiptToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حذف السند", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { receiptToDelete = null }) {
                    Text("إلغاء", color = MandoubakTextSecondary)
                }
            }
        )
    }

    // Receipt Detail Dialog
    selectedReceiptForDetails?.let { receipt ->
        ReceiptDetailDialog(
            receipt = receipt,
            currencySymbol = currencySymbol,
            companyName = companyName,
            representativeName = representativeName,
            onDismiss = { selectedReceiptForDetails = null },
            onPrintPdf = {
                PdfInvoiceGenerator.generateAndShareReceiptPdf(
                    context = context,
                    receipt = receipt,
                    companyName = companyName,
                    representativeName = representativeName,
                    currencySymbol = currencySymbol
                )
            }
        )
    }
}

@Composable
fun ReceiptCardItem(
    receipt: PaymentReceiptEntity,
    currencySymbol: String,
    onClick: () -> Unit,
    onPrint: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(receipt.dateMillis))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .testTag("receipt_card_${receipt.id}")
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardReceiptBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = CardReceiptAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = receipt.receiptNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MandoubakNavy
                        )
                        Text(
                            text = receipt.clientName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MandoubakTextPrimary
                        )
                    }
                }

                Text(
                    text = String.format(Locale.getDefault(), "%.1f %s", receipt.amount, currencySymbol),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CardReceiptAccent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "طريقة القبض: ${receipt.paymentMethod}",
                color = MandoubakTextSecondary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            )

            if (receipt.notes.isNotBlank()) {
                Text(
                    text = "البيان: ${receipt.notes}",
                    color = MandoubakTextSecondary,
                    fontSize = 11.5.sp,
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onPrint,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "طباعة ومشاركة",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "حذف السند",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptDetailDialog(
    receipt: PaymentReceiptEntity,
    currencySymbol: String = "ر.س",
    companyName: String = "مؤسسة التوزيع والتجارة الحديثة",
    representativeName: String = "مندوب المبيعات",
    onDismiss: () -> Unit,
    onPrintPdf: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(receipt.dateMillis))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("سند قبض نقدي رسمي", fontWeight = FontWeight.Bold, color = MandoubakNavy)
                Text(receipt.receiptNumber, fontSize = 14.sp, color = CardReceiptAccent, fontWeight = FontWeight.Bold)
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
                    Text("المندوب المستلم:", color = MandoubakTextSecondary, fontSize = 12.5.sp)
                    Text(representativeName, color = MandoubakTextPrimary, fontSize = 12.5.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("استلمنا من السيد/السادة:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(receipt.clientName, fontWeight = FontWeight.Bold, color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("المبلغ المقبوض:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text("${String.format(Locale.getDefault(), "%.1f", receipt.amount)} $currencySymbol", fontWeight = FontWeight.Bold, color = CardReceiptAccent, fontSize = 15.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("طريقة الدفع:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(receipt.paymentMethod, color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("التاريخ والوقت:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(dateStr, color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                if (receipt.notes.isNotBlank()) {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Text("وذلك عن:", fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = MandoubakNavy)
                    Text(receipt.notes, fontSize = 12.sp, color = MandoubakTextPrimary)
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onPrintPdf,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("طباعة ومشاركة PDF", color = Color.White, fontSize = 12.sp)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CardReceiptAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إغلاق", color = Color.White)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReceiptDialog(
    clients: List<ClientEntity>,
    preselectedClient: ClientEntity? = null,
    currencySymbol: String = "ر.س",
    onDismiss: () -> Unit,
    onSubmit: (client: ClientEntity, amount: Double, method: String, notes: String) -> Unit
) {
    if (clients.isEmpty()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("تنبيه") },
            text = { Text("يجب إضافة عميل أولاً قبل إنشاء سند قبض.") },
            confirmButton = { TextButton(onClick = onDismiss) { Text("حسناً") } }
        )
        return
    }

    var selectedClient by remember { mutableStateOf(preselectedClient ?: clients.first()) }
    var clientDropdownExpanded by remember { mutableStateOf(false) }

    var amountText by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("نقداً") }
    var notes by remember { mutableStateOf("دفعة تحت الحساب وتخفيض الذمم") }

    val methods = listOf("نقداً", "تحويل بنكي", "شيك", "شبكة / مدى")

    val amountEntered = amountText.toDoubleOrNull() ?: 0.0
    val newBalance = (selectedClient.currentBalance - amountEntered).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إنشاء سند قبض وتحصيل",
                fontWeight = FontWeight.Bold,
                color = MandoubakNavy,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Client Selector
                Text("اسم العميل *:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MandoubakNavy)
                ExposedDropdownMenuBox(
                    expanded = clientDropdownExpanded,
                    onExpandedChange = { clientDropdownExpanded = !clientDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${selectedClient.name} (الذمة الحالية: ${String.format(Locale.getDefault(), "%.1f", selectedClient.currentBalance)} $currencySymbol)",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = clientDropdownExpanded,
                        onDismissRequest = { clientDropdownExpanded = false }
                    ) {
                        clients.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.name} - مطلوب: ${String.format(Locale.getDefault(), "%.1f", c.currentBalance)} $currencySymbol") },
                                onClick = {
                                    selectedClient = c
                                    clientDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Balance indicator
                if (selectedClient.currentBalance > 0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("الرصيد المدين للعميل حالياً:", fontSize = 11.5.sp, color = Color(0xFF92400E))
                            Text(
                                text = "${String.format(Locale.getDefault(), "%.1f", selectedClient.currentBalance)} $currencySymbol",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("المبلغ المقبوض ($currencySymbol) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Remaining balance preview after payment
                if (amountEntered > 0 && selectedClient.currentBalance > 0) {
                    Text(
                        text = "الرصيد المتبقي بعد السداد: ${String.format(Locale.getDefault(), "%.1f", newBalance)} $currencySymbol",
                        color = if (newBalance > 0) Color(0xFFD97706) else Color(0xFF059669),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Payment Method
                Text("طريقة القبض *:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MandoubakNavy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CardReceiptAccent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("البيان / ملاحظات السند") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onSubmit(selectedClient, amount, selectedMethod, notes.trim())
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = CardReceiptAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ السند واعتماده", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )
}
