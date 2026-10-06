package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CardClientsAccent
import com.example.ui.theme.CardProductsAccent
import com.example.ui.theme.CardReceiptAccent
import com.example.ui.theme.CardSalesAccent
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.ui.viewmodel.UniversalSearchResult

@Composable
fun SearchDialog(
    searchQuery: String,
    searchResults: UniversalSearchResult,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToClients: () -> Unit,
    onNavigateToReceipts: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "البحث الشامل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MandoubakNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MandoubakNavy)
                    }
                }

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text("ابحث عن منتج، عميل، فاتورة، أو سند...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MandoubakNavy) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )

                if (searchQuery.isBlank()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "اكتب كلمة البحث للوصول السريع إلى أي سجل في المنظومة",
                            color = MandoubakTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    val hasResults = searchResults.products.isNotEmpty() ||
                            searchResults.clients.isNotEmpty() ||
                            searchResults.invoices.isNotEmpty() ||
                            searchResults.receipts.isNotEmpty()

                    if (!hasResults) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لم يتم العثور على أي نتائج مطابقة لـ \"$searchQuery\"",
                                color = MandoubakTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Products results
                            if (searchResults.products.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "المنتجات (${searchResults.products.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CardProductsAccent
                                    )
                                }
                                items(searchResults.products) { p ->
                                    SearchResultCard(
                                        title = p.name,
                                        subtitle = "السعر: ${p.salePrice} ر.س | المتوفر بالمخزن: ${p.stockQuantity}",
                                        tagColor = CardProductsAccent,
                                        icon = { Icon(Icons.Default.Inventory2, contentDescription = null, tint = CardProductsAccent, modifier = Modifier.size(16.dp)) },
                                        onClick = {
                                            onDismiss()
                                            onNavigateToProducts()
                                        }
                                    )
                                }
                            }

                            // Clients results
                            if (searchResults.clients.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "العملاء (${searchResults.clients.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CardClientsAccent
                                    )
                                }
                                items(searchResults.clients) { c ->
                                    SearchResultCard(
                                        title = c.name,
                                        subtitle = "الهاتف: ${c.phone} | المديونية: ${c.currentBalance} ر.س",
                                        tagColor = CardClientsAccent,
                                        icon = { Icon(Icons.Default.Person, contentDescription = null, tint = CardClientsAccent, modifier = Modifier.size(16.dp)) },
                                        onClick = {
                                            onDismiss()
                                            onNavigateToClients()
                                        }
                                    )
                                }
                            }

                            // Invoices results
                            if (searchResults.invoices.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "فواتير المبيعات (${searchResults.invoices.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CardSalesAccent
                                    )
                                }
                                items(searchResults.invoices) { inv ->
                                    SearchResultCard(
                                        title = "${inv.invoiceNumber} - ${inv.clientName}",
                                        subtitle = "القيمة: ${inv.totalAmount} ر.س | ${if (inv.isCredit) "آجل" else "نقداً"}",
                                        tagColor = CardSalesAccent,
                                        icon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = CardSalesAccent, modifier = Modifier.size(16.dp)) },
                                        onClick = {
                                            onDismiss()
                                            onNavigateToSales()
                                        }
                                    )
                                }
                            }

                            // Receipts results
                            if (searchResults.receipts.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "سندات القبض (${searchResults.receipts.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CardReceiptAccent
                                    )
                                }
                                items(searchResults.receipts) { rec ->
                                    SearchResultCard(
                                        title = "${rec.receiptNumber} - ${rec.clientName}",
                                        subtitle = "المبلغ: ${rec.amount} ر.س (${rec.paymentMethod})",
                                        tagColor = CardReceiptAccent,
                                        icon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = CardReceiptAccent, modifier = Modifier.size(16.dp)) },
                                        onClick = {
                                            onDismiss()
                                            onNavigateToReceipts()
                                        }
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

@Composable
fun SearchResultCard(
    title: String,
    subtitle: String,
    tagColor: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(tagColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MandoubakNavy)
                Text(text = subtitle, fontSize = 11.5.sp, color = MandoubakTextSecondary)
            }
        }
    }
}
