package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ProductEntity
import com.example.data.entity.StockMovementEntity
import com.example.ui.theme.CardInventoryAccent
import com.example.ui.theme.CardInventoryBg
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class InventoryFilter {
    ALL,
    LOW_STOCK,
    OUT_OF_STOCK,
    EXPIRING_SOON,
    EXPIRED
}

@Composable
fun InventoryScreen(
    products: List<ProductEntity>,
    stockMovements: List<StockMovementEntity> = emptyList(),
    currencySymbol: String,
    onBackClick: () -> Unit,
    onUpdateStockDirect: (ProductEntity, Int) -> Unit,
    onRecordStockAdjustment: (ProductEntity, String, String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(InventoryFilter.ALL) }

    var selectedProductForStocktake by remember { mutableStateOf<ProductEntity?>(null) }
    var selectedProductForAdjustment by remember { mutableStateOf<ProductEntity?>(null) }
    var showMovementsDialog by remember { mutableStateOf(false) }

    val now = System.currentTimeMillis()
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }
    val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000

    fun parseExpiry(dateStr: String): Long? {
        if (dateStr.isBlank()) return null
        return try {
            val normalized = dateStr.replace("-", "/")
            dateFormat.parse(normalized)?.time
        } catch (_: Exception) {
            null
        }
    }

    val displayedProducts = remember(products, searchQuery, selectedFilter) {
        val filteredByStatus = when (selectedFilter) {
            InventoryFilter.ALL -> products
            InventoryFilter.LOW_STOCK -> products.filter { it.stockQuantity <= it.minStockThreshold && it.stockQuantity > 0 }
            InventoryFilter.OUT_OF_STOCK -> products.filter { it.stockQuantity == 0 }
            InventoryFilter.EXPIRING_SOON -> products.filter {
                val exp = parseExpiry(it.expirationDate)
                exp != null && exp > now && (exp - now) <= thirtyDaysMillis
            }
            InventoryFilter.EXPIRED -> products.filter {
                val exp = parseExpiry(it.expirationDate)
                exp != null && exp <= now
            }
        }

        if (searchQuery.isBlank()) {
            filteredByStatus
        } else {
            val q = searchQuery.trim().lowercase()
            filteredByStatus.filter {
                it.name.lowercase().contains(q) ||
                it.barcode.contains(q) ||
                it.batchNumber.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.supplierName.lowercase().contains(q)
            }
        }
    }

    // Calculations
    val totalCostValuation = products.sumOf { it.stockQuantity * it.costPrice }
    val totalRetailValuation = products.sumOf { it.stockQuantity * it.salePrice }
    val expectedProfit = (totalRetailValuation - totalCostValuation).coerceAtLeast(0.0)
    val totalUnits = products.sumOf { it.stockQuantity }
    val totalReservedUnits = products.sumOf { it.reservedQuantity }
    val totalAvailableUnits = (totalUnits - totalReservedUnits).coerceAtLeast(0)

    val outOfStockCount = products.count { it.stockQuantity == 0 }
    val lowStockCount = products.count { it.stockQuantity <= it.minStockThreshold && it.stockQuantity > 0 }
    val expiringSoonCount = products.count {
        val exp = parseExpiry(it.expirationDate)
        exp != null && exp > now && (exp - now) <= thirtyDaysMillis
    }
    val expiredCount = products.count {
        val exp = parseExpiry(it.expirationDate)
        exp != null && exp <= now
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
                        .testTag("inventory_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MandoubakNavy
                    )
                }

                Text(
                    text = "المخزون والجرد الشامل",
                    color = MandoubakNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { showMovementsDialog = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardInventoryBg)
                        .testTag("stock_movements_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "سجل الحركات",
                        tint = CardInventoryAccent
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Valuation Dashboard Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .shadow(1.dp, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تقييم أصول المخزون والسيولة",
                            fontWeight = FontWeight.Bold,
                            color = MandoubakNavy,
                            fontSize = 14.sp
                        )

                        OutlinedButton(
                            onClick = { showMovementsDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = CardInventoryAccent, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("سجل حركات المخزون", fontSize = 11.sp, color = CardInventoryAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("قيمة المخزون (بالتكلفة)", fontSize = 11.sp, color = MandoubakTextSecondary)
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f %s", totalCostValuation, currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MandoubakNavy
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("القيمة البيعية", fontSize = 11.sp, color = MandoubakTextSecondary)
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f %s", totalRetailValuation, currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = CardInventoryAccent
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("الربح المتوقع", fontSize = 11.sp, color = MandoubakTextSecondary)
                            Text(
                                text = String.format(Locale.getDefault(), "+%.1f %s", expectedProfit, currencySymbol),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF059669)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "إجمالي: $totalUnits قطعة  |  المتاح: $totalAvailableUnits  |  المحجوز: $totalReservedUnits",
                            fontSize = 11.5.sp,
                            color = MandoubakTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${products.size} صنف مسجل",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MandoubakNavy
                        )
                    }
                }
            }

            // Zero Stock / Low Stock Warnings
            if (outOfStockCount > 0 || lowStockCount > 0) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (outOfStockCount > 0) Color(0xFFFEF2F2) else Color(0xFFFFFBEB)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (outOfStockCount > 0) Icons.Default.Dangerous else Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = if (outOfStockCount > 0) Color(0xFFDC2626) else Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = buildString {
                                if (outOfStockCount > 0) append("يوجد $outOfStockCount صنف نفد رصيده بالكامل (0)! ")
                                if (lowStockCount > 0) append("يوجد $lowStockCount صنف قارب على النفاد.")
                            },
                            fontSize = 11.5.sp,
                            color = if (outOfStockCount > 0) Color(0xFFDC2626) else Color(0xFFD97706),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث باسم الصنف، الباركود، رقم التشغيلة، التصنيف، المورد...") },
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
                    focusedBorderColor = CardInventoryAccent,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Horizontal Filter Chips Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == InventoryFilter.ALL,
                    onClick = { selectedFilter = InventoryFilter.ALL },
                    label = { Text("الكل (${products.size})", fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CardInventoryAccent,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == InventoryFilter.LOW_STOCK,
                    onClick = { selectedFilter = InventoryFilter.LOW_STOCK },
                    label = { Text("نواقص ($lowStockCount)", fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFD97706),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == InventoryFilter.OUT_OF_STOCK,
                    onClick = { selectedFilter = InventoryFilter.OUT_OF_STOCK },
                    label = { Text("نفد المخزون ($outOfStockCount)", fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC2626),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == InventoryFilter.EXPIRING_SOON,
                    onClick = { selectedFilter = InventoryFilter.EXPIRING_SOON },
                    label = { Text("قريب الانتهاء ($expiringSoonCount)", fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF7C3AED),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = selectedFilter == InventoryFilter.EXPIRED,
                    onClick = { selectedFilter = InventoryFilter.EXPIRED },
                    label = { Text("منتهي الصلاحية ($expiredCount)", fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF991B1B),
                        selectedLabelColor = Color.White
                    )
                )
            }

            // List of Inventory Items
            if (displayedProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "لا توجد أصناف مطابقة لهذا التصنيف" else "لا توجد نتائج بحث مطابقة",
                            color = MandoubakTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayedProducts, key = { it.id }) { product ->
                        val isOutOfStock = product.stockQuantity == 0
                        val isLowStock = product.stockQuantity <= product.minStockThreshold && !isOutOfStock
                        val availableStock = (product.stockQuantity - product.reservedQuantity).coerceAtLeast(0)

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MandoubakNavy
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (product.barcode.isNotBlank()) {
                                                Text("باركود: ${product.barcode}", fontSize = 11.sp, color = MandoubakTextSecondary)
                                            }
                                            if (product.batchNumber.isNotBlank()) {
                                                Text("تشغيلة: ${product.batchNumber}", fontSize = 11.sp, color = CardInventoryAccent)
                                            }
                                        }

                                        if (product.supplierName.isNotBlank()) {
                                            Text("المورد: ${product.supplierName}", fontSize = 11.sp, color = MandoubakTextSecondary)
                                        }
                                    }

                                    // Status Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when {
                                                    isOutOfStock -> Color(0xFFFEE2E2)
                                                    isLowStock -> Color(0xFFFEF3C7)
                                                    else -> Color(0xFFECFDF5)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                isOutOfStock -> "نفد المخزون (0)"
                                                isLowStock -> "حرج: ${product.stockQuantity}"
                                                else -> "متوفر: ${product.stockQuantity} ${product.unit}"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isOutOfStock -> Color(0xFFDC2626)
                                                isLowStock -> Color(0xFFB45309)
                                                else -> Color(0xFF059669)
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Quantities Breakdown & Pricing
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "المتاح: $availableStock  |  المحجوز: ${product.reservedQuantity}  |  الأمان: ${product.minStockThreshold}",
                                        fontSize = 11.sp,
                                        color = MandoubakTextSecondary
                                    )
                                    Text(
                                        text = "تكلفة: ${product.costPrice} - بيع: ${product.salePrice} $currencySymbol",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MandoubakNavy
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(6.dp))

                                // Quick Actions Row: جرد فعلي / تسوية / تالف / مرتجع
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        // Stocktake Button
                                        Button(
                                            onClick = { selectedProductForStocktake = product },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = MandoubakNavy, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("تعديل الجرد", color = MandoubakNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Adjustment / Returned / Damaged Button
                                        OutlinedButton(
                                            onClick = { selectedProductForAdjustment = product },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.KeyboardReturn, contentDescription = null, tint = CardInventoryAccent, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("تسوية / تالف / مرتجع", color = CardInventoryAccent, fontSize = 11.sp)
                                        }
                                    }

                                    if (product.expirationDate.isNotBlank()) {
                                        Text(
                                            text = "صلاحية: ${product.expirationDate}",
                                            fontSize = 10.5.sp,
                                            color = MandoubakTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }
    }

    // Direct Stocktake Reconciliation Dialog
    selectedProductForStocktake?.let { prod ->
        StocktakeReconciliationDialog(
            product = prod,
            onDismiss = { selectedProductForStocktake = null },
            onConfirmNewStock = { newStock ->
                onUpdateStockDirect(prod, newStock)
                selectedProductForStocktake = null
            }
        )
    }

    // Advanced Inventory Adjustment (Damaged / Returned / Adjustment)
    selectedProductForAdjustment?.let { prod ->
        InventoryAdjustmentDialog(
            product = prod,
            onDismiss = { selectedProductForAdjustment = null },
            onSubmit = { type, typeArabic, qtyDelta, reason ->
                onRecordStockAdjustment(prod, type, typeArabic, qtyDelta, reason)
                selectedProductForAdjustment = null
            }
        )
    }

    // Stock Movements History Dialog
    if (showMovementsDialog) {
        StockMovementsHistoryDialog(
            movements = stockMovements,
            onDismiss = { showMovementsDialog = false }
        )
    }
}

@Composable
fun StocktakeReconciliationDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onConfirmNewStock: (Int) -> Unit
) {
    var newStockText by remember { mutableStateOf("${product.stockQuantity}") }
    val newStockVal = newStockText.toIntOrNull() ?: product.stockQuantity
    val diff = newStockVal - product.stockQuantity

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "جرد فعلي وتعديل المخزون",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MandoubakNavy
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "الصنف: ${product.name}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MandoubakTextPrimary
                )
                Text(
                    text = "الرصيد الدفتري الحالي: ${product.stockQuantity} ${product.unit}",
                    fontSize = 12.sp,
                    color = MandoubakTextSecondary
                )

                OutlinedTextField(
                    value = newStockText,
                    onValueChange = { newStockText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("الكمية الفعلية المحصورة بعد الجرد (لا يمكن أن تكون سالبة)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (diff != 0) {
                    Text(
                        text = if (diff > 0) "فائض جرد بمقدار: +$diff ${product.unit}" else "عجز جرد بمقدار: $diff ${product.unit}",
                        color = if (diff > 0) Color(0xFF059669) else Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newStockVal >= 0) {
                        onConfirmNewStock(newStockVal)
                    }
                },
                enabled = newStockVal >= 0,
                colors = ButtonDefaults.buttonColors(containerColor = CardInventoryAccent)
            ) {
                Text("اعتماد رصيد الجرد", color = Color.White)
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
fun InventoryAdjustmentDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSubmit: (type: String, typeArabic: String, qtyDelta: Int, reason: String) -> Unit
) {
    var selectedOption by remember { mutableStateOf("DAMAGED") } // DAMAGED, RETURNED, ADJUSTMENT
    var quantityText by remember { mutableStateOf("1") }
    var reasonText by remember { mutableStateOf("") }

    val qty = quantityText.toIntOrNull() ?: 1

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تسوية حركة مخزون",
                fontWeight = FontWeight.Bold,
                color = MandoubakNavy,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("الصنف: ${product.name} (الرصيد الحالي: ${product.stockQuantity})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MandoubakNavy)

                Text("نوع الحركة:", fontSize = 12.sp, color = MandoubakTextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val opts = listOf(
                        Triple("DAMAGED", "تالف وهالك", Color(0xFFDC2626)),
                        Triple("RETURNED", "مرتجع من عميل", Color(0xFF059669)),
                        Triple("ADJUSTMENT", "تسوية عجز/زيادة", Color(0xFF2563EB))
                    )

                    opts.forEach { (type, label, color) ->
                        val isSel = selectedOption == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) color else Color(0xFFF1F5F9))
                                .clickable { selectedOption = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else MandoubakNavy
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("الكمية (${product.unit})") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedOption == "DAMAGED" && qty > product.stockQuantity) {
                    Text(
                        text = "تحذير: لا يمكن إتلاف كمية أكبر من الرصيد المتوفر لمنع الرصيد السالب!",
                        color = Color(0xFFDC2626),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("السبب أو الملاحظات (مثال: كسر أثناء النقل، انتهاء صلاحية...)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (qty > 0) {
                        val delta = when (selectedOption) {
                            "DAMAGED" -> -qty
                            "RETURNED" -> qty
                            else -> -qty
                        }
                        val typeArabic = when (selectedOption) {
                            "DAMAGED" -> "تالف وهالك"
                            "RETURNED" -> "مرتجع بضاعة"
                            else -> "تسوية مخزنية"
                        }
                        onSubmit(selectedOption, typeArabic, delta, reasonText.trim())
                    }
                },
                enabled = qty > 0 && !(selectedOption == "DAMAGED" && qty > product.stockQuantity),
                colors = ButtonDefaults.buttonColors(containerColor = CardInventoryAccent)
            ) {
                Text("تسجيل الحركة", color = Color.White)
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
fun StockMovementsHistoryDialog(
    movements: List<StockMovementEntity>,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("سجل حركات المخزون الشامل", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 16.sp)
                Icon(Icons.Default.History, contentDescription = null, tint = CardInventoryAccent)
            }
        },
        text = {
            if (movements.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد حركات مخزون مسجلة بعد", color = MandoubakTextSecondary, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(movements, key = { it.id }) { mov ->
                        val dateStr = dateFormat.format(Date(mov.dateMillis))
                        val isPositive = mov.quantity > 0

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
                                    Text(mov.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text(
                                        text = "${mov.movementTypeArabic.ifBlank { mov.movementType }} (${mov.referenceNumber})",
                                        fontSize = 11.sp,
                                        color = if (isPositive) Color(0xFF059669) else Color(0xFFDC2626),
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (mov.reason.isNotBlank()) {
                                        Text(mov.reason, fontSize = 11.sp, color = MandoubakTextSecondary)
                                    }
                                    Text(dateStr, fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (isPositive) "+${mov.quantity}" else "${mov.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isPositive) Color(0xFF059669) else Color(0xFFDC2626)
                                    )
                                    Text(
                                        text = "الرصيد: ${mov.newStock}",
                                        fontSize = 11.sp,
                                        color = MandoubakTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CardInventoryAccent)
            ) {
                Text("إغلاق", color = Color.White)
            }
        }
    )
}
