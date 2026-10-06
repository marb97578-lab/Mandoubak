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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.entity.ClientEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.ui.theme.CardClientsAccent
import com.example.ui.theme.CardClientsBg
import com.example.ui.theme.CardReceiptAccent
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClientsScreen(
    clients: List<ClientEntity>,
    invoices: List<SaleInvoiceEntity> = emptyList(),
    receipts: List<PaymentReceiptEntity> = emptyList(),
    currencySymbol: String = "ر.س",
    onBackClick: () -> Unit,
    onCreateClientClick: () -> Unit,
    onAddReceiptForClient: (ClientEntity) -> Unit,
    onUpdateClient: (ClientEntity) -> Unit,
    onDeleteClient: (ClientEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedClientForDetails by remember { mutableStateOf<ClientEntity?>(null) }
    var clientToEdit by remember { mutableStateOf<ClientEntity?>(null) }
    var clientToDelete by remember { mutableStateOf<ClientEntity?>(null) }

    val filteredClients = remember(clients, searchQuery) {
        if (searchQuery.isBlank()) clients
        else {
            val q = searchQuery.trim().lowercase()
            clients.filter {
                it.name.lowercase().contains(q) ||
                it.phone.contains(q) ||
                it.address.lowercase().contains(q) ||
                it.location.lowercase().contains(q)
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
                        .testTag("clients_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MandoubakNavy
                    )
                }

                Text(
                    text = "سجل العملاء والزبائن",
                    color = MandoubakNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardClientsBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = CardClientsAccent
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClientClick,
                containerColor = CardClientsAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_client_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة عميل")
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
                        Text("إجمالي العملاء", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        Text(
                            text = "${clients.size}",
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
                        Text("إجمالي الذمم والديون", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        val totalDebts = clients.sumOf { it.currentBalance }
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f %s", totalDebts, currencySymbol),
                            color = if (totalDebts > 0) Color(0xFFDC2626) else Color(0xFF059669),
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
                        Text("العملاء المدينون", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                        val debtorsCount = clients.count { it.currentBalance > 0 }
                        Text(
                            text = "$debtorsCount",
                            color = if (debtorsCount > 0) Color(0xFFD97706) else Color(0xFF059669),
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
                placeholder = { Text("بحث باسم العميل، الهاتف، العنوان، أو الموقع...", fontSize = 12.5.sp) },
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
                    focusedBorderColor = CardClientsAccent,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (filteredClients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "لا توجد نتائج مطابقة للبحث" else "لا يوجد عملاء مسجلين حتى الآن",
                            color = MandoubakTextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (searchQuery.isBlank()) {
                            OutlinedButton(onClick = onCreateClientClick) {
                                Text("إضافة أول عميل الآن")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredClients, key = { it.id }) { client ->
                        ClientCardItem(
                            client = client,
                            currencySymbol = currencySymbol,
                            onClick = { selectedClientForDetails = client },
                            onCall = {
                                if (client.phone.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${client.phone}"))
                                    context.startActivity(intent)
                                }
                            },
                            onAddReceipt = { onAddReceiptForClient(client) },
                            onEdit = { clientToEdit = client },
                            onDelete = { clientToDelete = client }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Delete Client Confirmation Dialog
    clientToDelete?.let { client ->
        AlertDialog(
            onDismissRequest = { clientToDelete = null },
            title = {
                Text(
                    text = "تأكيد حذف العميل",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
            },
            text = {
                Text("هل أنت متأكد من حذف العميل ${client.name} من سجلات التطبيق؟ لن يتم مسح فواتيره المسجلة سابقاً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClient(client)
                        if (selectedClientForDetails?.id == client.id) {
                            selectedClientForDetails = null
                        }
                        clientToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حذف العميل", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { clientToDelete = null }) {
                    Text("إلغاء", color = MandoubakTextSecondary)
                }
            }
        )
    }

    // Detail / Profile Dialog
    selectedClientForDetails?.let { client ->
        val clientInvoices = invoices.filter { it.clientId == client.id }
        val clientReceipts = receipts.filter { it.clientId == client.id }
        CustomerProfileDialog(
            client = client,
            purchaseHistory = clientInvoices,
            paymentHistory = clientReceipts,
            currencySymbol = currencySymbol,
            onDismiss = { selectedClientForDetails = null },
            onCall = {
                if (client.phone.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${client.phone}"))
                    context.startActivity(intent)
                }
            },
            onAddReceipt = {
                selectedClientForDetails = null
                onAddReceiptForClient(client)
            }
        )
    }

    // Edit Client Dialog
    clientToEdit?.let { client ->
        EditClientDialog(
            client = client,
            currencySymbol = currencySymbol,
            onDismiss = { clientToEdit = null },
            onSubmit = { updatedClient ->
                onUpdateClient(updatedClient)
                clientToEdit = null
            }
        )
    }
}

@Composable
fun ClientCardItem(
    client: ClientEntity,
    currencySymbol: String,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onAddReceipt: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .testTag("client_card_${client.id}")
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CardClientsBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = CardClientsAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = client.name,
                            color = MandoubakNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                        if (client.phone.isNotBlank()) {
                            Text(
                                text = client.phone,
                                color = MandoubakTextSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }

                // Balance Badge
                val isDebtor = client.currentBalance > 0
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDebtor) Color(0xFFFEE2E2) else Color(0xFFECFDF5))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isDebtor) "مطلوب: ${String.format(Locale.getDefault(), "%.1f", client.currentBalance)} $currencySymbol" else "الحساب خالص",
                        color = if (isDebtor) Color(0xFFDC2626) else Color(0xFF059669),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (client.address.isNotBlank() || client.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val locText = if (client.location.isNotBlank()) "${client.address} (${client.location})" else client.address
                    Text(
                        text = locText,
                        color = MandoubakTextSecondary,
                        fontSize = 11.5.sp,
                        maxLines = 1
                    )
                }
            }

            if (client.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ملاحظة: ${client.notes}",
                    color = MandoubakTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (client.phone.isNotBlank()) {
                        IconButton(
                            onClick = onCall,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF))
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "اتصال", tint = Color(0xFF2563EB), modifier = Modifier.size(15.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Add Receipt Button
                    Button(
                        onClick = onAddReceipt,
                        colors = ButtonDefaults.buttonColors(containerColor = CardReceiptAccent),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("سند قبض", fontSize = 10.5.sp, color = Color.White)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = Color(0xFF64748B), modifier = Modifier.size(15.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerProfileDialog(
    client: ClientEntity,
    purchaseHistory: List<SaleInvoiceEntity>,
    paymentHistory: List<PaymentReceiptEntity> = emptyList(),
    currencySymbol: String,
    onDismiss: () -> Unit,
    onCall: () -> Unit,
    onAddReceipt: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ملف العميل وسجل العمليات",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy,
                    fontSize = 15.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (client.currentBalance > 0) Color(0xFFFEE2E2) else Color(0xFFECFDF5))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (client.currentBalance > 0) "${String.format(Locale.getDefault(), "%.1f", client.currentBalance)} $currencySymbol" else "0.0 $currencySymbol",
                        color = if (client.currentBalance > 0) Color(0xFFDC2626) else Color(0xFF059669),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // Profile Info Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text("الاسم: ${client.name}", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 13.5.sp)

                            if (client.phone.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("الهاتف: ${client.phone}", color = MandoubakTextPrimary, fontSize = 12.5.sp)
                                    IconButton(onClick = onCall, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(15.dp))
                                    }
                                }
                            }

                            if (client.address.isNotBlank() || client.location.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val locDesc = if (client.location.isNotBlank()) "${client.address} [${client.location}]" else client.address
                                    Text("العنوان: $locDesc", color = MandoubakTextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))

                                    IconButton(
                                        onClick = {
                                            val query = client.location.ifBlank { client.address }
                                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(query)}"))
                                            context.startActivity(mapIntent)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Map, contentDescription = "خريطة", tint = CardClientsAccent, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            if (client.notes.isNotBlank()) {
                                Text("ملاحظات: ${client.notes}", color = MandoubakTextSecondary, fontSize = 11.5.sp)
                            }

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("الرصيد المدين (ذمة معلقة):", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(
                                    text = "${String.format(Locale.getDefault(), "%.1f", client.currentBalance)} $currencySymbol",
                                    color = if (client.currentBalance > 0) Color(0xFFDC2626) else Color(0xFF059669),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Tabs: Purchase History vs Debt & Payments Ledger
                item {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = MandoubakNavy,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("سجل المشتريات (${purchaseHistory.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("الديون والسدادات (${paymentHistory.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                if (selectedTabIndex == 0) {
                    // Purchase History List
                    if (purchaseHistory.isEmpty()) {
                        item {
                            Text(
                                text = "لا توجد فواتير مبيعات سابقة لهذا العميل.",
                                color = MandoubakTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        items(purchaseHistory) { inv ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(0.5.dp, RoundedCornerShape(10.dp))
                            ) {
                                Column(modifier = Modifier.padding(9.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MandoubakNavy)
                                        Text(dateFormat.format(Date(inv.dateMillis)), fontSize = 10.5.sp, color = Color(0xFF94A3B8))
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(inv.itemsSummary, fontSize = 11.sp, color = MandoubakTextPrimary, maxLines = 2)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (inv.isCredit) "آجل (${inv.paymentMethod})" else "نقدي",
                                            fontSize = 10.5.sp,
                                            color = if (inv.isCredit) Color(0xFFD97706) else Color(0xFF059669)
                                        )
                                        Text(
                                            text = "${String.format(Locale.getDefault(), "%.1f", inv.totalAmount)} $currencySymbol",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = CardClientsAccent
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Debts & Payments Ledger
                    if (paymentHistory.isEmpty() && purchaseHistory.none { it.remainingAmount > 0 }) {
                        item {
                            Text(
                                text = "لا توجد ديون أو سندات سداد مسجلة لهذا العميل.",
                                color = MandoubakTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    } else {
                        // Show payments receipts
                        items(paymentHistory) { receipt ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(0.5.dp, RoundedCornerShape(10.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("سند قبض: ${receipt.receiptNumber}", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF166534))
                                        Text("التاريخ: ${dateFormat.format(Date(receipt.dateMillis))} • ${receipt.paymentMethod}", fontSize = 10.5.sp, color = Color(0xFF15803D))
                                        if (receipt.notes.isNotBlank()) {
                                            Text("بيان: ${receipt.notes}", fontSize = 10.5.sp, color = MandoubakTextSecondary)
                                        }
                                    }
                                    Text(
                                        text = "+ ${String.format(Locale.getDefault(), "%.1f", receipt.amount)} $currencySymbol",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color(0xFF166534)
                                    )
                                }
                            }
                        }

                        // Show outstanding credit invoices
                        val creditInvoices = purchaseHistory.filter { it.remainingAmount > 0 }
                        items(creditInvoices) { inv ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(0.5.dp, RoundedCornerShape(10.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("فاتورة آجلة: ${inv.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF991B1B))
                                        Text("التاريخ: ${dateFormat.format(Date(inv.dateMillis))}", fontSize = 10.5.sp, color = Color(0xFFB91C1C))
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "- ${String.format(Locale.getDefault(), "%.1f", inv.remainingAmount)} $currencySymbol",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = Color(0xFFDC2626)
                                        )
                                        Text("متبقي بذمته", fontSize = 10.sp, color = Color(0xFF991B1B))
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
                    onClick = onAddReceipt,
                    colors = ButtonDefaults.buttonColors(containerColor = CardReceiptAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("سند قبض", color = Color.White)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MandoubakNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إغلاق", color = Color.White)
                }
            }
        }
    )
}

@Composable
fun EditClientDialog(
    client: ClientEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSubmit: (ClientEntity) -> Unit
) {
    var name by remember { mutableStateOf(client.name) }
    var phone by remember { mutableStateOf(client.phone) }
    var address by remember { mutableStateOf(client.address) }
    var location by remember { mutableStateOf(client.location) }
    var balanceText by remember { mutableStateOf(if (client.currentBalance > 0) client.currentBalance.toString() else "") }
    var notes by remember { mutableStateOf(client.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("تعديل بيانات العميل", fontWeight = FontWeight.Bold, color = MandoubakNavy)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم العميل *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("العنوان") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("الموقع الجغرافي / الإحداثيات (اختياري)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("الرصيد المدين ($currencySymbol)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val bal = balanceText.toDoubleOrNull() ?: client.currentBalance
                        onSubmit(
                            client.copy(
                                name = name.trim(),
                                phone = phone.trim(),
                                address = address.trim(),
                                location = location.trim(),
                                currentBalance = bal,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CardClientsAccent)
            ) {
                Text("حفظ التعديلات", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun CreateClientDialog(
    currencySymbol: String = "ر.س",
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, address: String, location: String, initialDebt: Double, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var initialDebtText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إضافة عميل جديد",
                fontWeight = FontWeight.Bold,
                color = MandoubakNavy,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم العميل / المؤسسة *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف / الجوال *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("العنوان / المدينة / الحي") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("الموقع الجغرافي / خريطة (اختياري)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = initialDebtText,
                    onValueChange = { initialDebtText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("رصيد افتتاحي سابق ($currencySymbol) (إن وجد)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val debt = initialDebtText.toDoubleOrNull() ?: 0.0
                        onSubmit(name.trim(), phone.trim(), address.trim(), location.trim(), debt, notes.trim())
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CardClientsAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ العميل", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )
}
