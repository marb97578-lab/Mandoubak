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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.CardProductsAccent
import com.example.ui.theme.CardProductsBg
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import java.util.Locale

@Composable
fun ProductsScreen(
    products: List<ProductEntity>,
    lowStockProducts: List<ProductEntity>,
    currencySymbol: String = "ر.س",
    onBackClick: () -> Unit,
    onCreateProductClick: () -> Unit,
    onUpdateProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    filterOnlyLowStock: Boolean = false,
    onScanBarcodeClick: () -> Unit = {},
    selectedProductFromBarcode: ProductEntity? = null,
    onClearSelectedProductFromBarcode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyLowStock by remember { mutableStateOf(filterOnlyLowStock) }
    var selectedProductForDetails by remember { mutableStateOf<ProductEntity?>(null) }
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    LaunchedEffect(selectedProductFromBarcode) {
        if (selectedProductFromBarcode != null) {
            selectedProductForDetails = selectedProductFromBarcode
            onClearSelectedProductFromBarcode()
        }
    }

    val displayedProducts = remember(products, lowStockProducts, searchQuery, showOnlyLowStock) {
        val baseList = if (showOnlyLowStock) lowStockProducts else products
        if (searchQuery.isBlank()) baseList
        else baseList.filter {
            it.name.contains(searchQuery.trim(), ignoreCase = true) ||
            it.barcode.contains(searchQuery.trim()) ||
            it.category.contains(searchQuery.trim(), ignoreCase = true) ||
            it.batchNumber.contains(searchQuery.trim(), ignoreCase = true)
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
                        .testTag("products_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MandoubakNavy
                    )
                }

                Text(
                    text = "كتالوج المنتجات والمخزون",
                    color = MandoubakNavy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onScanBarcodeClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CardProductsBg)
                            .testTag("scan_barcode_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "مسح باركود بالكاميرا",
                            tint = CardProductsAccent
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CardProductsBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = CardProductsAccent
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateProductClick,
                containerColor = CardProductsAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_product_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة منتج")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input with Barcode Camera Scan Action
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث بالاسم، الباركود، التصنيف أو التشغيلة...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MandoubakTextSecondary) },
                trailingIcon = {
                    IconButton(
                        onClick = onScanBarcodeClick,
                        modifier = Modifier.testTag("search_barcode_scan_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "مسح باركود للبحث السريع",
                            tint = CardProductsAccent
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Filter Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !showOnlyLowStock,
                    onClick = { showOnlyLowStock = false },
                    label = { Text("جميع الأصناف (${products.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CardProductsAccent,
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = showOnlyLowStock,
                    onClick = { showOnlyLowStock = true },
                    label = { Text("المخزون المنخفض (${lowStockProducts.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC2626),
                        selectedLabelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (displayedProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (showOnlyLowStock) "لا توجد منتجات ذات مخزون منخفض" else "لا توجد منتجات مطابقة",
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
                    items(displayedProducts, key = { it.id }) { product ->
                        ProductItemCard(
                            product = product,
                            currencySymbol = currencySymbol,
                            onClick = { selectedProductForDetails = product },
                            onEdit = { productToEdit = product },
                            onIncrementStock = {
                                onUpdateProduct(product.copy(stockQuantity = product.stockQuantity + 1))
                            },
                            onDecrementStock = {
                                if (product.stockQuantity > 0) {
                                    onUpdateProduct(product.copy(stockQuantity = product.stockQuantity - 1))
                                }
                            },
                            onDelete = { productToDelete = product }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Delete Product Confirmation Dialog
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = {
                Text(
                    text = "تأكيد حذف المنتج",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
            },
            text = {
                Text("هل أنت متأكد من حذف المنتج (${product.name}) من قائمة الأصناف؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct(product)
                        if (selectedProductForDetails?.id == product.id) {
                            selectedProductForDetails = null
                        }
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حذف المنتج", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("إلغاء", color = MandoubakTextSecondary)
                }
            }
        )
    }

    // Product Details Dialog
    selectedProductForDetails?.let { product ->
        ProductDetailsDialog(
            product = product,
            currencySymbol = currencySymbol,
            onDismiss = { selectedProductForDetails = null },
            onEdit = {
                selectedProductForDetails = null
                productToEdit = product
            }
        )
    }

    // Edit Product Dialog
    productToEdit?.let { product ->
        EditProductDialog(
            product = product,
            currencySymbol = currencySymbol,
            onDismiss = { productToEdit = null },
            onRescanBarcode = { onScanBarcodeClick() },
            onSubmit = { updated ->
                onUpdateProduct(updated)
                productToEdit = null
            }
        )
    }
}

@Composable
fun ProductItemCard(
    product: ProductEntity,
    currencySymbol: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onIncrementStock: () -> Unit,
    onDecrementStock: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = product.stockQuantity <= product.minStockThreshold

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Category/Product Avatar Box
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardProductsBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = CardProductsAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = product.name,
                            color = MandoubakNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (product.barcode.isNotBlank()) {
                                Text(
                                    text = product.barcode,
                                    color = MandoubakTextSecondary,
                                    fontSize = 11.5.sp
                                )
                                Text(" • ", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                            }
                            Text(
                                text = "${product.category} (${product.unit})",
                                color = CardProductsAccent,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Prices
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f %s", product.salePrice, currencySymbol),
                        color = CardProductsAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (product.costPrice > 0) {
                        Text(
                            text = "شراء: ${product.costPrice} $currencySymbol",
                            color = MandoubakTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details tags row
            if (product.batchNumber.isNotBlank() || product.expirationDate.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (product.batchNumber.isNotBlank()) {
                        Text(
                            text = "تشغيلة: ${product.batchNumber}",
                            fontSize = 11.sp,
                            color = MandoubakTextSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (product.expirationDate.isNotBlank()) {
                        Text(
                            text = "انتهاء: ${product.expirationDate}",
                            fontSize = 11.sp,
                            color = MandoubakTextSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom Stock Control Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Badge
                val dozensEq = String.format(Locale.getDefault(), "%.1f", product.stockQuantity / 12.0).trimEnd('0').trimEnd('.')
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLowStock) Color(0xFFFEE2E2) else Color(0xFFECFDF5))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isLowStock) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (isLowStock) "مخزون منخفض: ${product.stockQuantity} ${product.unit} ($dozensEq درزن)" else "المتوفر: ${product.stockQuantity} ${product.unit} ($dozensEq درزن)",
                            color = if (isLowStock) Color(0xFFDC2626) else Color(0xFF059669),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Quick Stock Adjustment (+ / -) & actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onDecrementStock,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "إنقاص", tint = MandoubakNavy, modifier = Modifier.size(14.dp))
                    }

                    Text(
                        text = "${product.stockQuantity}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MandoubakNavy,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = onIncrementStock,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "زيادة", tint = MandoubakNavy, modifier = Modifier.size(14.dp))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailsDialog(
    product: ProductEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تفاصيل وبيانات المنتج",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy,
                    fontSize = 17.sp
                )
                Text(
                    text = product.category,
                    fontSize = 12.sp,
                    color = CardProductsAccent,
                    fontWeight = FontWeight.SemiBold
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
                    Text("اسم الصنف:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(product.name, fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 13.sp)
                }

                if (product.barcode.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الباركود:", color = MandoubakTextSecondary, fontSize = 13.sp)
                        Text(product.barcode, color = MandoubakTextPrimary, fontSize = 13.sp)
                    }
                }

                if (product.batchNumber.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("رقم التشغيلة (Batch):", color = MandoubakTextSecondary, fontSize = 13.sp)
                        Text(product.batchNumber, color = MandoubakTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (product.manufacturingDate.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("تاريخ الإنتاج:", color = MandoubakTextSecondary, fontSize = 13.sp)
                        Text(product.manufacturingDate, color = MandoubakTextPrimary, fontSize = 13.sp)
                    }
                }

                if (product.expirationDate.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("تاريخ الصلاحية / الانتهاء:", color = MandoubakTextSecondary, fontSize = 13.sp)
                        Text(product.expirationDate, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("سعر البيع:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text("${product.salePrice} $currencySymbol", fontWeight = FontWeight.Bold, color = CardProductsAccent, fontSize = 14.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("سعر التكلفة (الشراء):", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text("${product.costPrice} $currencySymbol", color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                val profitMargin = product.salePrice - product.costPrice
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("هامش الربح المتوقع للوحدة:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f %s", profitMargin, currencySymbol),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669),
                        fontSize = 13.sp
                    )
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("الكمية المتوفرة (بالقطع):", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text("${product.stockQuantity} ${product.unit}", fontWeight = FontWeight.Bold, color = MandoubakNavy, fontSize = 14.sp)
                }

                val dozensCount = product.stockQuantity / 12
                val remainderPieces = product.stockQuantity % 12
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("الرصيد بالدرزن (عدد الدرزن):", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text(
                        if (remainderPieces == 0) "$dozensCount درزن" else "$dozensCount درزن و $remainderPieces ${product.unit}",
                        fontWeight = FontWeight.Bold,
                        color = CardProductsAccent,
                        fontSize = 13.5.sp
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("حد التنبيه الأدنى:", color = MandoubakTextSecondary, fontSize = 13.sp)
                    Text("${product.minStockThreshold} ${product.unit}", color = MandoubakTextPrimary, fontSize = 13.sp)
                }

                if (product.notes.isNotBlank()) {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Text("ملاحظات:", color = MandoubakTextSecondary, fontSize = 12.sp)
                    Text(product.notes, color = MandoubakTextPrimary, fontSize = 12.5.sp)
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = CardProductsAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعديل الصنف", color = Color.White)
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
fun EditProductDialog(
    product: ProductEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onRescanBarcode: () -> Unit = {},
    onSubmit: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var barcode by remember { mutableStateOf(product.barcode) }
    var batchNumber by remember { mutableStateOf(product.batchNumber) }
    var mfgDate by remember { mutableStateOf(product.manufacturingDate) }
    var expDate by remember { mutableStateOf(product.expirationDate) }
    var costPriceText by remember { mutableStateOf(product.costPrice.toString()) }
    var salePriceText by remember { mutableStateOf(product.salePrice.toString()) }
    var dozensText by remember {
        val initialDozens = product.stockQuantity / 12.0
        mutableStateOf(String.format(Locale.getDefault(), "%.1f", initialDozens).trimEnd('0').trimEnd('.'))
    }
    var quantityText by remember { mutableStateOf(product.stockQuantity.toString()) }
    var minStockText by remember { mutableStateOf(product.minStockThreshold.toString()) }
    var unit by remember { mutableStateOf(product.unit) }
    var category by remember { mutableStateOf(product.category) }
    var notes by remember { mutableStateOf(product.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("تعديل بيانات المنتج", fontWeight = FontWeight.Bold, color = MandoubakNavy)
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم المنتج *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("الباركود") },
                        trailingIcon = {
                            IconButton(onClick = onRescanBarcode) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "إعادة مسح الباركود بالكاميرا",
                                    tint = CardProductsAccent
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = salePriceText,
                            onValueChange = { salePriceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("سعر البيع *") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = costPriceText,
                            onValueChange = { costPriceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("سعر التكلفة") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dozensText,
                            onValueChange = { input ->
                                dozensText = input.filter { ch -> ch.isDigit() || ch == '.' }
                                val d = dozensText.toDoubleOrNull() ?: 0.0
                                quantityText = kotlin.math.round(d * 12).toInt().toString()
                            },
                            label = { Text("عدد الدرزن (12 قطعة)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("الوحدة") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    // Display calculated pieces summary
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardProductsBg),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("إجمالي القطع المتوفرة:", fontSize = 12.5.sp, color = MandoubakTextSecondary)
                            Text(
                                text = "$quantityText $unit ($dozensText درزن)",
                                fontWeight = FontWeight.Bold,
                                color = CardProductsAccent,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = batchNumber,
                            onValueChange = { batchNumber = it },
                            label = { Text("رقم التشغيلة") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("التصنيف") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mfgDate,
                            onValueChange = { mfgDate = it },
                            label = { Text("تاريخ الإنتاج") },
                            placeholder = { Text("YYYY/MM/DD") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = expDate,
                            onValueChange = { expDate = it },
                            label = { Text("تاريخ الانتهاء") },
                            placeholder = { Text("YYYY/MM/DD") },
                            modifier = Modifier.weight(1f)
                        )
                    }
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
                        val cost = costPriceText.toDoubleOrNull() ?: product.costPrice
                        val sale = salePriceText.toDoubleOrNull() ?: product.salePrice
                        val qty = quantityText.toIntOrNull() ?: product.stockQuantity
                        val minStock = minStockText.toIntOrNull() ?: product.minStockThreshold
                        onSubmit(
                            product.copy(
                                name = name.trim(),
                                barcode = barcode.trim(),
                                batchNumber = batchNumber.trim(),
                                manufacturingDate = mfgDate.trim(),
                                expirationDate = expDate.trim(),
                                costPrice = cost,
                                salePrice = sale,
                                stockQuantity = qty,
                                minStockThreshold = minStock,
                                unit = unit.trim(),
                                category = category.trim(),
                                notes = notes.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CardProductsAccent)
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
fun CreateProductDialog(
    initialBarcode: String = "",
    currencySymbol: String = "ر.س",
    onDismiss: () -> Unit,
    onRescanBarcode: () -> Unit = {},
    onSubmit: (
        name: String,
        barcode: String,
        batchNumber: String,
        mfgDate: String,
        expDate: String,
        cost: Double,
        sale: Double,
        qty: Int,
        minStock: Int,
        unit: String,
        category: String,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var barcode by remember(initialBarcode) { mutableStateOf(initialBarcode) }
    var batchNumber by remember { mutableStateOf("") }
    var mfgDate by remember { mutableStateOf("") }
    var expDate by remember { mutableStateOf("") }
    var costPriceText by remember { mutableStateOf("") }
    var salePriceText by remember { mutableStateOf("") }
    var dozensText by remember { mutableStateOf("") }
    var minStockText by remember { mutableStateOf("5") }
    var unit by remember { mutableStateOf("حبة") }
    var category by remember { mutableStateOf("عام") }
    var notes by remember { mutableStateOf("") }

    val dozensValue = dozensText.toDoubleOrNull() ?: 0.0
    val calculatedTotalPieces = kotlin.math.round(dozensValue * 12).toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إضافة منتج جديد للكتالوج",
                fontWeight = FontWeight.Bold,
                color = MandoubakNavy,
                fontSize = 17.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم المنتج أو السلعة *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = barcode,
                            onValueChange = { barcode = it },
                            label = { Text("الباركود") },
                            trailingIcon = {
                                IconButton(onClick = onRescanBarcode) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "مسح باركود بالكاميرا",
                                        tint = CardProductsAccent
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = batchNumber,
                            onValueChange = { batchNumber = it },
                            label = { Text("رقم التشغيلة") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = salePriceText,
                            onValueChange = { salePriceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("سعر البيع *") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = costPriceText,
                            onValueChange = { costPriceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("سعر التكلفة (الشراء)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Number of Dozens (عدد الدرزن) - Replaced Initial Quantity
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dozensText,
                            onValueChange = { dozensText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("عدد الدرزن *") },
                            placeholder = { Text("مثال: 5") },
                            modifier = Modifier.weight(1.2f)
                        )

                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("الوحدة") },
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }

                // Display Section: Number of Dozens and Total Pieces
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardProductsBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("عدد الدرزن:", color = MandoubakTextSecondary, fontSize = 12.5.sp)
                                Text(
                                    text = if (dozensText.isBlank()) "0 درزن" else "$dozensText درزن",
                                    fontWeight = FontWeight.Bold,
                                    color = MandoubakNavy,
                                    fontSize = 13.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إجمالي القطع (Total Pieces):", color = MandoubakTextSecondary, fontSize = 12.5.sp)
                                Text(
                                    text = "$calculatedTotalPieces $unit",
                                    fontWeight = FontWeight.Bold,
                                    color = CardProductsAccent,
                                    fontSize = 14.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "حساب تلقائي حسب الوحدة: 1 درزن = 12 قطعة / حبة",
                                color = MandoubakTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("التصنيف") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = minStockText,
                            onValueChange = { minStockText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("حد التنبيه") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mfgDate,
                            onValueChange = { mfgDate = it },
                            label = { Text("تاريخ الإنتاج") },
                            placeholder = { Text("YYYY/MM/DD") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = expDate,
                            onValueChange = { expDate = it },
                            label = { Text("تاريخ الصلاحية") },
                            placeholder = { Text("YYYY/MM/DD") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات (شروط الحفظ، تعليمات)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && salePriceText.isNotBlank() && dozensText.isNotBlank()) {
                        val cost = costPriceText.toDoubleOrNull() ?: 0.0
                        val sale = salePriceText.toDoubleOrNull() ?: 0.0
                        val qty = calculatedTotalPieces
                        val minStock = minStockText.toIntOrNull() ?: 5
                        onSubmit(
                            name.trim(),
                            barcode.trim(),
                            batchNumber.trim(),
                            mfgDate.trim(),
                            expDate.trim(),
                            cost,
                            sale,
                            qty,
                            minStock,
                            unit.trim().ifBlank { "حبة" },
                            category.trim().ifBlank { "عام" },
                            notes.trim()
                        )
                    }
                },
                enabled = name.isNotBlank() && salePriceText.isNotBlank() && dozensText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CardProductsAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ المنتج", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = MandoubakTextSecondary)
            }
        }
    )
}
