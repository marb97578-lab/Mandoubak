package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.data.entity.SupplierEntity
import com.example.ui.theme.CardClientsAccent
import com.example.ui.theme.CardInventoryAccent
import com.example.ui.theme.CardProductsAccent
import com.example.ui.theme.CardPurchasesAccent
import com.example.ui.theme.CardReceiptAccent
import com.example.ui.theme.CardSalesAccent
import com.example.ui.theme.CardSalesBg
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakBlue
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary
import com.example.util.PdfReportGenerator
import com.example.util.ReportExportUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ReportSection(val title: String) {
    FINANCIAL("الأرباح والمالية"),
    SALES("المبيعات"),
    PURCHASES("المشتريات"),
    EXPENSES("المصروفات"),
    INVENTORY("المخزون"),
    CLIENTS("العملاء"),
    SUPPLIERS("الموردين")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    invoices: List<SaleInvoiceEntity>,
    receipts: List<PaymentReceiptEntity>,
    purchases: List<PurchaseInvoiceEntity>,
    products: List<ProductEntity>,
    clients: List<ClientEntity>,
    suppliers: List<SupplierEntity> = emptyList(),
    expenses: List<ExpenseEntity> = emptyList(),
    currencySymbol: String = "ر.س",
    companyName: String = "مؤسسة التوزيع والتجارة الحديثة",
    representativeName: String = "مندوب المبيعات والتوزيع",
    onBackClick: () -> Unit,
    onAddExpense: (title: String, category: String, amount: Double, paymentMethod: String, notes: String) -> Unit = { _, _, _, _, _ -> },
    onDeleteExpense: (ExpenseEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf(ReportSection.FINANCIAL) }
    var selectedPeriod by remember { mutableStateOf("الكل") }
    val periods = listOf("الكل", "اليوم", "هذا الأسبوع", "هذا الشهر", "هذه السنة")

    // Additional Filters
    var selectedCategoryFilter by remember { mutableStateOf("الكل") }
    var selectedCustomerFilter by remember { mutableStateOf<String?>("الكل") }
    var selectedProductFilter by remember { mutableStateOf<String?>("الكل") }

    // Dialog state for adding a new expense
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    val context = LocalContext.current
    val now = System.currentTimeMillis()
    val oneDayMillis = 24L * 60 * 60 * 1000
    val oneWeekMillis = 7L * oneDayMillis
    val oneMonthMillis = 30L * oneDayMillis
    val oneYearMillis = 365L * oneDayMillis

    // Filtering base datasets by period
    val periodFilteredInvoices = remember(invoices, selectedPeriod) {
        when (selectedPeriod) {
            "اليوم" -> invoices.filter { now - it.dateMillis <= oneDayMillis }
            "هذا الأسبوع" -> invoices.filter { now - it.dateMillis <= oneWeekMillis }
            "هذا الشهر" -> invoices.filter { now - it.dateMillis <= oneMonthMillis }
            "هذه السنة" -> invoices.filter { now - it.dateMillis <= oneYearMillis }
            else -> invoices
        }
    }

    val periodFilteredPurchases = remember(purchases, selectedPeriod) {
        when (selectedPeriod) {
            "اليوم" -> purchases.filter { now - it.dateMillis <= oneDayMillis }
            "هذا الأسبوع" -> purchases.filter { now - it.dateMillis <= oneWeekMillis }
            "هذا الشهر" -> purchases.filter { now - it.dateMillis <= oneMonthMillis }
            "هذه السنة" -> purchases.filter { now - it.dateMillis <= oneYearMillis }
            else -> purchases
        }
    }

    val periodFilteredReceipts = remember(receipts, selectedPeriod) {
        when (selectedPeriod) {
            "اليوم" -> receipts.filter { now - it.dateMillis <= oneDayMillis }
            "هذا الأسبوع" -> receipts.filter { now - it.dateMillis <= oneWeekMillis }
            "هذا الشهر" -> receipts.filter { now - it.dateMillis <= oneMonthMillis }
            "هذه السنة" -> receipts.filter { now - it.dateMillis <= oneYearMillis }
            else -> receipts
        }
    }

    val periodFilteredExpenses = remember(expenses, selectedPeriod) {
        when (selectedPeriod) {
            "اليوم" -> expenses.filter { now - it.dateMillis <= oneDayMillis }
            "هذا الأسبوع" -> expenses.filter { now - it.dateMillis <= oneWeekMillis }
            "هذا الشهر" -> expenses.filter { now - it.dateMillis <= oneMonthMillis }
            "هذه السنة" -> expenses.filter { now - it.dateMillis <= oneYearMillis }
            else -> expenses
        }
    }

    // Applying Customer & Product search/filters to Sales
    val finalFilteredInvoices = remember(periodFilteredInvoices, selectedCustomerFilter, selectedProductFilter) {
        periodFilteredInvoices.filter { inv ->
            val matchCustomer = selectedCustomerFilter == null || selectedCustomerFilter == "الكل" ||
                    inv.clientName.equals(selectedCustomerFilter, ignoreCase = true)
            val matchProduct = selectedProductFilter == null || selectedProductFilter == "الكل" ||
                    inv.itemsSummary.contains(selectedProductFilter!!, ignoreCase = true)
            matchCustomer && matchProduct
        }
    }

    // Financial Metrics Calculation (Selling price - Purchase price - Expenses)
    val totalSales = finalFilteredInvoices.sumOf { it.totalAmount }
    val totalPurchases = periodFilteredPurchases.sumOf { it.totalAmount }
    val totalExpenses = periodFilteredExpenses.sumOf { it.amount }
    val totalCollectedReceipts = periodFilteredReceipts.sumOf { it.amount } + finalFilteredInvoices.filter { !it.isCredit }.sumOf { it.paidAmount }

    val grossProfit = totalSales - totalPurchases
    val netProfit = totalSales - totalPurchases - totalExpenses

    // Inventory Metrics
    val inventoryCostValue = products.sumOf { it.costPrice * it.stockQuantity }
    val inventorySaleValue = products.sumOf { it.salePrice * it.stockQuantity }
    val inventoryExpectedProfit = (inventorySaleValue - inventoryCostValue).coerceAtLeast(0.0)

    val lowStockProducts = products.filter { it.stockQuantity <= it.minStockThreshold && it.stockQuantity > 0 }
    val outOfStockProducts = products.filter { it.stockQuantity <= 0 }

    Scaffold(
        containerColor = MandoubakBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = 8.dp)
            ) {
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
                            .background(Color(0xFFF1F5F9))
                            .testTag("reports_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = MandoubakNavy
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "التقارير والتحليلات الشاملة",
                            color = MandoubakNavy,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$companyName | 100% محاسبي",
                            color = MandoubakTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // Action: Export / Print Button
                    IconButton(
                        onClick = {
                            // Quick export current report
                            when (selectedSection) {
                                ReportSection.FINANCIAL -> {
                                    PdfReportGenerator.generateAndShareComprehensiveReportPdf(
                                        context = context,
                                        periodName = selectedPeriod,
                                        totalSales = totalSales,
                                        totalPurchases = totalPurchases,
                                        totalExpenses = totalExpenses,
                                        grossProfit = grossProfit,
                                        netProfit = netProfit,
                                        invoicesCount = finalFilteredInvoices.size,
                                        companyName = companyName,
                                        representativeName = representativeName,
                                        currencySymbol = currencySymbol
                                    )
                                }
                                ReportSection.SALES -> {
                                    ReportExportUtil.exportSalesReportCsv(context, finalFilteredInvoices, currencySymbol)
                                }
                                ReportSection.PURCHASES -> {
                                    ReportExportUtil.exportPurchasesReportCsv(context, periodFilteredPurchases, currencySymbol)
                                }
                                ReportSection.EXPENSES -> {
                                    ReportExportUtil.exportExpensesReportCsv(context, periodFilteredExpenses, currencySymbol)
                                }
                                ReportSection.INVENTORY -> {
                                    PdfReportGenerator.generateAndShareInventoryReportPdf(
                                        context = context,
                                        products = products,
                                        companyName = companyName,
                                        representativeName = representativeName,
                                        currencySymbol = currencySymbol
                                    )
                                }
                                ReportSection.CLIENTS -> {
                                    ReportExportUtil.exportClientsReportCsv(context, clients, currencySymbol)
                                }
                                ReportSection.SUPPLIERS -> {
                                    ReportExportUtil.exportSuppliersReportCsv(context, suppliers, currencySymbol)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE9FE))
                            .testTag("reports_export_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "تصدير أو طباعة",
                            tint = Color(0xFF7C3AED)
                        )
                    }
                }

                // Section Tabs (Horizontal Scrollable)
                val sections = ReportSection.values()
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sections.forEach { sec ->
                        val isSelected = selectedSection == sec
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) MandoubakNavy else Color(0xFFF1F5F9))
                                .clickable { selectedSection = sec }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("report_tab_${sec.name}")
                        ) {
                            Text(
                                text = sec.title,
                                color = if (isSelected) Color.White else MandoubakTextPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Period Filter Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "الفترة الزمنية:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MandoubakNavy
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        periods.forEach { period ->
                            FilterChip(
                                selected = selectedPeriod == period,
                                onClick = { selectedPeriod = period },
                                label = { Text(period, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF7C3AED),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Export Actions Row (PDF, Excel CSV)
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // PDF Export Button
                        OutlinedButton(
                            onClick = {
                                when (selectedSection) {
                                    ReportSection.INVENTORY -> {
                                        PdfReportGenerator.generateAndShareInventoryReportPdf(
                                            context = context,
                                            products = products,
                                            companyName = companyName,
                                            representativeName = representativeName,
                                            currencySymbol = currencySymbol
                                        )
                                    }
                                    else -> {
                                        PdfReportGenerator.generateAndShareComprehensiveReportPdf(
                                            context = context,
                                            periodName = selectedPeriod,
                                            totalSales = totalSales,
                                            totalPurchases = totalPurchases,
                                            totalExpenses = totalExpenses,
                                            grossProfit = grossProfit,
                                            netProfit = netProfit,
                                            invoicesCount = finalFilteredInvoices.size,
                                            companyName = companyName,
                                            representativeName = representativeName,
                                            currencySymbol = currencySymbol
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("btn_export_pdf")
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("طباعة PDF", fontSize = 11.5.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Excel / CSV Export Button
                        Button(
                            onClick = {
                                when (selectedSection) {
                                    ReportSection.SALES -> ReportExportUtil.exportSalesReportCsv(context, finalFilteredInvoices, currencySymbol)
                                    ReportSection.PURCHASES -> ReportExportUtil.exportPurchasesReportCsv(context, periodFilteredPurchases, currencySymbol)
                                    ReportSection.EXPENSES -> ReportExportUtil.exportExpensesReportCsv(context, periodFilteredExpenses, currencySymbol)
                                    ReportSection.INVENTORY -> ReportExportUtil.exportInventoryReportCsv(context, products, currencySymbol)
                                    ReportSection.CLIENTS -> ReportExportUtil.exportClientsReportCsv(context, clients, currencySymbol)
                                    ReportSection.SUPPLIERS -> ReportExportUtil.exportSuppliersReportCsv(context, suppliers, currencySymbol)
                                    ReportSection.FINANCIAL -> ReportExportUtil.exportProfitLossCsv(
                                        context = context,
                                        totalSales = totalSales,
                                        totalPurchases = totalPurchases,
                                        totalExpenses = totalExpenses,
                                        grossProfit = grossProfit,
                                        netProfit = netProfit,
                                        currencySymbol = currencySymbol
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("btn_export_excel")
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تصدير Excel", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ==========================================
            // CONTENT ACCORDING TO SELECTED SECTION
            // ==========================================
            when (selectedSection) {
                ReportSection.FINANCIAL -> {
                    // Financial KPI Cards & Strict Formula
                    item {
                        Text(
                            text = "قائمة الدخل والأرباح المحاسبية ($selectedPeriod)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = MandoubakNavy
                        )
                    }

                    // 4 Financial Pillars
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            KpiCard(
                                title = "إجمالي المبيعات",
                                value = String.format(Locale.getDefault(), "%.1f %s", totalSales, currencySymbol),
                                subtitle = "${finalFilteredInvoices.size} فاتورة",
                                icon = Icons.Default.Receipt,
                                accentColor = CardSalesAccent,
                                bgColor = Color(0xFFEFF6FF),
                                modifier = Modifier.weight(1f)
                            )

                            KpiCard(
                                title = "إجمالي المشتريات",
                                value = String.format(Locale.getDefault(), "%.1f %s", totalPurchases, currencySymbol),
                                subtitle = "${periodFilteredPurchases.size} توريدات",
                                icon = Icons.Default.ShoppingCart,
                                accentColor = CardPurchasesAccent,
                                bgColor = Color(0xFFF0FDFA),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            KpiCard(
                                title = "المصروفات التشغيلية",
                                value = String.format(Locale.getDefault(), "%.1f %s", totalExpenses, currencySymbol),
                                subtitle = "${periodFilteredExpenses.size} سندات صرف",
                                icon = Icons.Default.MoneyOff,
                                accentColor = Color(0xFFDC2626),
                                bgColor = Color(0xFFFEF2F2),
                                modifier = Modifier.weight(1f)
                            )

                            KpiCard(
                                title = "صافي الأرباح (Net Profit)",
                                value = String.format(Locale.getDefault(), "%.1f %s", netProfit, currencySymbol),
                                subtitle = if (netProfit >= 0) "أرباح محققة" else "عجز مالي",
                                icon = Icons.Default.TrendingUp,
                                accentColor = if (netProfit >= 0) Color(0xFF059669) else Color(0xFFDC2626),
                                bgColor = if (netProfit >= 0) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Strict Accounting Formula Breakdown Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "جدول الاحتساب المحاسبي (Selling - Cost - Expenses)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = MandoubakNavy
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("1. إجمالي المبيعات المحققة (+):", fontSize = 12.5.sp, color = MandoubakTextSecondary)
                                    Text(String.format(Locale.getDefault(), "%.1f %s", totalSales, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CardSalesAccent)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("2. ناقص تكلفة المشتريات (-):", fontSize = 12.5.sp, color = MandoubakTextSecondary)
                                    Text(String.format(Locale.getDefault(), "- %.1f %s", totalPurchases, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CardPurchasesAccent)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("• مجمل الربح التجاري (Gross Profit):", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = MandoubakNavy)
                                    Text(String.format(Locale.getDefault(), "%.1f %s", grossProfit, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (grossProfit >= 0) Color(0xFF059669) else Color(0xFFDC2626))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("3. ناقص المصروفات والنثريات (-):", fontSize = 12.5.sp, color = MandoubakTextSecondary)
                                    Text(String.format(Locale.getDefault(), "- %.1f %s", totalExpenses, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFDC2626))
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("صافي الربح الفعلي النهائي (Net Profit):", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MandoubakNavy)
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f %s", netProfit, currencySymbol),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (netProfit >= 0) Color(0xFF059669) else Color(0xFFDC2626)
                                    )
                                }
                            }
                        }
                    }
                }

                ReportSection.SALES -> {
                    // Filters row for Sales: Customer & Category
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("تصفية المبيعات حسب العميل:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MandoubakNavy)
                                Spacer(modifier = Modifier.height(6.dp))
                                val clientNames = listOf("الكل") + clients.map { it.name }.distinct()
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    clientNames.forEach { cName ->
                                        FilterChip(
                                            selected = selectedCustomerFilter == cName,
                                            onClick = { selectedCustomerFilter = cName },
                                            label = { Text(cName, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Best Selling Products
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("المنتجات الأكثر مبيعاً ونشاطاً", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MandoubakNavy)
                                Spacer(modifier = Modifier.height(10.dp))

                                val topProducts = products.sortedByDescending { it.costPrice }.take(5)
                                topProducts.forEachIndexed { i, p ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFEFF6FF)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("${i + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CardSalesAccent)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(p.name, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = MandoubakTextPrimary)
                                                Text(p.category, fontSize = 10.5.sp, color = MandoubakTextSecondary)
                                            }
                                        }
                                        Text(String.format(Locale.getDefault(), "%.1f %s", p.salePrice, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = CardSalesAccent)
                                    }
                                }
                            }
                        }
                    }

                    // Invoices List preview
                    item {
                        Text("قائمة الفواتير المعتمدة (${finalFilteredInvoices.size}):", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MandoubakNavy)
                    }

                    items(finalFilteredInvoices) { inv ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text(inv.clientName, fontSize = 12.sp, color = MandoubakTextPrimary)
                                    Text(inv.itemsSummary, fontSize = 10.5.sp, color = MandoubakTextSecondary, maxLines = 1)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(String.format(Locale.getDefault(), "%.1f %s", inv.totalAmount, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CardSalesAccent)
                                    Text(if (inv.isCredit) "آجل" else "مسدد", fontSize = 11.sp, color = if (inv.isCredit) Color(0xFFD97706) else Color(0xFF059669), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                ReportSection.PURCHASES -> {
                    // Purchases Overview
                    item {
                        KpiCard(
                            title = "إجمالي التوريدات والمشتريات",
                            value = String.format(Locale.getDefault(), "%.1f %s", totalPurchases, currencySymbol),
                            subtitle = "${periodFilteredPurchases.size} فاتورة شراء",
                            icon = Icons.Default.ShoppingCart,
                            accentColor = CardPurchasesAccent,
                            bgColor = Color(0xFFF0FDFA),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("فواتير المشتريات والتوريد:", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MandoubakNavy)
                    }

                    items(periodFilteredPurchases) { p ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(p.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text("المورد: ${p.supplierName}", fontSize = 12.sp, color = MandoubakTextPrimary)
                                    Text(p.itemsSummary, fontSize = 10.5.sp, color = MandoubakTextSecondary, maxLines = 1)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(String.format(Locale.getDefault(), "%.1f %s", p.totalAmount, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CardPurchasesAccent)
                                    Text(p.paymentMethod, fontSize = 11.sp, color = MandoubakTextSecondary)
                                }
                            }
                        }
                    }
                }

                ReportSection.EXPENSES -> {
                    // Operational Expenses Section with quick Add
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("المصروفات التشغيلية والنثريات", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MandoubakNavy)
                            Button(
                                onClick = { showAddExpenseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_add_expense_report")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تسجيل مصروف", fontSize = 11.5.sp)
                            }
                        }
                    }

                    item {
                        KpiCard(
                            title = "إجمالي المصروفات في الفترة",
                            value = String.format(Locale.getDefault(), "%.1f %s", totalExpenses, currencySymbol),
                            subtitle = "${periodFilteredExpenses.size} سندات ومصاريف",
                            icon = Icons.Default.MoneyOff,
                            accentColor = Color(0xFFDC2626),
                            bgColor = Color(0xFFFEF2F2),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    items(periodFilteredExpenses) { exp ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(exp.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text("التصنيف: ${exp.category} | ${exp.paymentMethod}", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                    if (exp.notes.isNotBlank()) {
                                        Text(exp.notes, fontSize = 10.5.sp, color = Color(0xFF64748B))
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(String.format(Locale.getDefault(), "%.1f %s", exp.amount, currencySymbol), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFDC2626))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(onClick = { expenseToDelete = exp }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                ReportSection.INVENTORY -> {
                    // Inventory Valuation & Health
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("تقييم المخزون الإجمالي", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MandoubakNavy)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("القيمة بالتكلفة", fontSize = 11.sp, color = MandoubakTextSecondary)
                                        Text(String.format(Locale.getDefault(), "%.1f %s", inventoryCostValue, currencySymbol), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MandoubakNavy)
                                    }
                                    Column {
                                        Text("القيمة بالبيع", fontSize = 11.sp, color = MandoubakTextSecondary)
                                        Text(String.format(Locale.getDefault(), "%.1f %s", inventorySaleValue, currencySymbol), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                                    }
                                    Column {
                                        Text("الربح المتوقع", fontSize = 11.sp, color = MandoubakTextSecondary)
                                        Text(String.format(Locale.getDefault(), "%.1f %s", inventoryExpectedProfit, currencySymbol), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                    }
                                }
                            }
                        }
                    }

                    // Warnings for low stock / out of stock
                    if (lowStockProducts.isNotEmpty() || outOfStockProducts.isNotEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("تنبيهات حالة المخزون", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFDC2626))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (outOfStockProducts.isNotEmpty()) {
                                        Text("• يوجد ${outOfStockProducts.size} صنف نفد رصيدها بالكامل.", fontSize = 11.5.sp, color = Color(0xFFB91C1C))
                                    }
                                    if (lowStockProducts.isNotEmpty()) {
                                        Text("• يوجد ${lowStockProducts.size} صنف وصلت لحد الطلب الأدنى.", fontSize = 11.5.sp, color = Color(0xFFD97706))
                                    }
                                }
                            }
                        }
                    }

                    // Stock Item rows
                    items(products) { p ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text("التصنيف: ${p.category} | المورد: ${p.supplierName}", fontSize = 11.sp, color = MandoubakTextSecondary)
                                    Text("سعر التكلفة: ${p.costPrice} | سعر البيع: ${p.salePrice} $currencySymbol", fontSize = 10.5.sp, color = Color(0xFF475569))
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${p.stockQuantity} ${p.unit}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (p.stockQuantity <= 0) Color(0xFFDC2626) else if (p.stockQuantity <= p.minStockThreshold) Color(0xFFD97706) else Color(0xFF059669)
                                    )
                                    Text(
                                        text = if (p.stockQuantity <= 0) "نفد المخزون" else if (p.stockQuantity <= p.minStockThreshold) "منخفض" else "متوفر",
                                        fontSize = 10.5.sp,
                                        color = if (p.stockQuantity <= 0) Color(0xFFDC2626) else if (p.stockQuantity <= p.minStockThreshold) Color(0xFFD97706) else Color(0xFF059669)
                                    )
                                }
                            }
                        }
                    }
                }

                ReportSection.CLIENTS -> {
                    // Customer Balances & Breakdown
                    val totalDebts = clients.sumOf { it.currentBalance }
                    item {
                        KpiCard(
                            title = "إجمالي ذمم وديون العملاء",
                            value = String.format(Locale.getDefault(), "%.1f %s", totalDebts, currencySymbol),
                            subtitle = "${clients.count { it.currentBalance > 0 }} عملاء عليهم ذمم",
                            icon = Icons.Default.People,
                            accentColor = if (totalDebts > 0) Color(0xFFDC2626) else Color(0xFF059669),
                            bgColor = if (totalDebts > 0) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    items(clients) { c ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(c.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text("الجوال: ${c.phone} | ${c.address}", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }

                                Text(
                                    text = if (c.currentBalance > 0) "مطلوب: ${c.currentBalance} $currencySymbol" else "حساب مسدد",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (c.currentBalance > 0) Color(0xFFDC2626) else Color(0xFF059669)
                                )
                            }
                        }
                    }
                }

                ReportSection.SUPPLIERS -> {
                    // Supplier balances & obligations
                    val totalSupplierDebts = suppliers.sumOf { it.outstandingBalance }
                    item {
                        KpiCard(
                            title = "مستحقات الموردين والشركات",
                            value = String.format(Locale.getDefault(), "%.1f %s", totalSupplierDebts, currencySymbol),
                            subtitle = "${suppliers.size} موردين مسجلين",
                            icon = Icons.Default.ShoppingCart,
                            accentColor = CardPurchasesAccent,
                            bgColor = Color(0xFFF0FDFA),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    items(suppliers) { s ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().shadow(1.dp, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(s.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MandoubakNavy)
                                    Text("الشركة: ${s.companyName} | هاتف: ${s.phone}", fontSize = 11.5.sp, color = MandoubakTextSecondary)
                                }

                                Text(
                                    text = "المستحق: ${s.outstandingBalance} $currencySymbol",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (s.outstandingBalance > 0) Color(0xFFD97706) else Color(0xFF059669)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Delete Expense Confirmation Dialog
    expenseToDelete?.let { exp ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = {
                Text(
                    text = "تأكيد حذف المصروف",
                    fontWeight = FontWeight.Bold,
                    color = MandoubakNavy
                )
            },
            text = {
                Text("هل أنت متأكد من حذف هذا المصروف (${exp.title}) بقيمة ${exp.amount} $currencySymbol؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteExpense(exp)
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حذف المصروف", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("إلغاء", color = MandoubakTextSecondary)
                }
            }
        )
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            currencySymbol = currencySymbol,
            onDismiss = { showAddExpenseDialog = false },
            onConfirm = { title, cat, amount, method, notes ->
                onAddExpense(title, cat, amount, method, notes)
                showAddExpenseDialog = false
            }
        )
    }
}

@Composable
fun AddExpenseDialog(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, amount: Double, paymentMethod: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("وقود ومحروقات") }
    var amountText by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("نقداً") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("وقود ومحروقات", "صيانة دورية", "إعاشة وضيافة", "نثريات ومواقف", "رسوم ومستندات")
    val paymentMethods = listOf("نقداً", "بطاقة مدى", "شبكة", "تحويل بنكي")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("تسجيل مصروف تشغيلي جديد", fontWeight = FontWeight.Bold, color = MandoubakNavy)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("بيان المصروف (مثال: بنزين السيارة)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("المبلغ ($currencySymbol)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category chips
                Text("تصنيف المصروف:", fontSize = 12.sp, color = MandoubakTextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Payment Method chips
                Text("طريقة الدفع:", fontSize = 12.sp, color = MandoubakTextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    paymentMethods.forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0) {
                        onConfirm(title.trim(), category, amount, paymentMethod, notes.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Text("حفظ المصروف", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = MandoubakTextSecondary) }
        }
    )
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.shadow(1.dp, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = title, fontSize = 11.5.sp, color = MandoubakTextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MandoubakNavy)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.5.sp, color = accentColor, fontWeight = FontWeight.Medium)
        }
    }
}
