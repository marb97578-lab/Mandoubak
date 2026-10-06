package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ClientEntity
import com.example.util.BackupManager
import com.example.ui.screens.AboutAppDialog
import com.example.ui.screens.BackupRestoreDialog
import com.example.ui.screens.BarcodeScannerDialog
import com.example.ui.screens.ClientsScreen
import com.example.ui.screens.CurrenciesScreen
import com.example.ui.screens.CreateClientDialog
import com.example.ui.screens.CreateInvoiceDialog
import com.example.ui.screens.InvoiceSavedSuccessDialog
import com.example.ui.screens.CreateProductDialog
import com.example.ui.screens.CreatePurchaseDialog
import com.example.ui.screens.CreateReceiptDialog
import com.example.ui.screens.CreateSupplierDialog
import com.example.ui.screens.CurrencyDialog
import com.example.ui.screens.DataManagementDialog
import com.example.ui.screens.DrawerContent
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImportExportDialog
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LanguageDialog
import com.example.ui.screens.LanguagesScreen
import com.example.ui.screens.NotificationSettingsDialog
import com.example.ui.screens.NotificationsDialog
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.PurchasesScreen
import com.example.ui.screens.ReceiptsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.SearchDialog
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SubscriptionDialog
import com.example.ui.screens.SuppliersScreen
import com.example.ui.screens.ThemeCustomizationDialog
import com.example.ui.theme.MandoubakTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MandoubakViewModel
import com.example.util.LanguageRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MandoubakViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
            val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
            val isRtl = LanguageRepository.isRtlLanguage(selectedLanguage)

            MandoubakTheme(
                themeConfig = themeConfig,
                isRtl = isRtl
            ) {
                MandoubakApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MandoubakApp(viewModel: MandoubakViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val supplierPayments by viewModel.supplierPayments.collectAsStateWithLifecycle()
    val stockMovements by viewModel.stockMovements.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val receipts by viewModel.receipts.collectAsStateWithLifecycle()
    val purchases by viewModel.purchases.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()

    val todaySalesCount by viewModel.todaySalesCount.collectAsStateWithLifecycle()

    val todayClientsCount by viewModel.todayClientsCount.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val todayReceiptsCount by viewModel.todayReceiptsCount.collectAsStateWithLifecycle()
    val todaySalesTotal by viewModel.todaySalesTotal.collectAsStateWithLifecycle()
    val todayReceiptsTotal by viewModel.todayReceiptsTotal.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val companyName by viewModel.companyName.collectAsStateWithLifecycle()
    val representativeName by viewModel.representativeName.collectAsStateWithLifecycle()
    val taxNumber by viewModel.taxNumber.collectAsStateWithLifecycle()
    val phone by viewModel.phone.collectAsStateWithLifecycle()
    val address by viewModel.address.collectAsStateWithLifecycle()

    val showInvoiceSavedDialog by viewModel.showInvoiceSavedDialog.collectAsStateWithLifecycle()
    val lastCreatedInvoice by viewModel.lastCreatedInvoice.collectAsStateWithLifecycle()
    val lastCreatedInvoicePhone by viewModel.lastCreatedInvoicePhone.collectAsStateWithLifecycle()
    val lastCreatedInvoiceItems by viewModel.lastCreatedInvoiceItems.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val localBackups by viewModel.localBackups.collectAsStateWithLifecycle()
    val autoBackupEnabled by viewModel.autoBackupEnabled.collectAsStateWithLifecycle()
    val autoBackupFrequency by viewModel.autoBackupFrequency.collectAsStateWithLifecycle()
    val lastBackupTime by viewModel.lastBackupTime.collectAsStateWithLifecycle()
    val isBackupLoading by viewModel.isBackupOperationInProgress.collectAsStateWithLifecycle()
    val backupStatusMessage by viewModel.backupStatusMessage.collectAsStateWithLifecycle()

    val pickBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    viewModel.restoreFromInputStream(inputStream) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "فشل قراءة الملف: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Operational Dialog states
    val showSearchDialog by viewModel.showSearchDialog.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val showNotificationsDialog by viewModel.showNotificationsDialog.collectAsStateWithLifecycle()
    val showCreateSaleDialog by viewModel.showCreateSaleDialog.collectAsStateWithLifecycle()
    val showCreateReceiptDialog by viewModel.showCreateReceiptDialog.collectAsStateWithLifecycle()
    val showCreateProductDialog by viewModel.showCreateProductDialog.collectAsStateWithLifecycle()
    val showCreateClientDialog by viewModel.showCreateClientDialog.collectAsStateWithLifecycle()
    val showCreateSupplierDialog by viewModel.showCreateSupplierDialog.collectAsStateWithLifecycle()
    val showCreatePurchaseDialog by viewModel.showCreatePurchaseDialog.collectAsStateWithLifecycle()

    val showBarcodeScanner by viewModel.showBarcodeScanner.collectAsStateWithLifecycle()
    val scannedBarcodeForNewProduct by viewModel.scannedBarcodeForNewProduct.collectAsStateWithLifecycle()
    val selectedProductFromBarcode by viewModel.selectedProductFromBarcode.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    // Administrative Menu Dialog states
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showBackupDialog by viewModel.showBackupDialog.collectAsStateWithLifecycle()
    val showSubscriptionDialog by viewModel.showSubscriptionDialog.collectAsStateWithLifecycle()
    val showLanguageDialog by viewModel.showLanguageDialog.collectAsStateWithLifecycle()
    val showCurrencyDialog by viewModel.showCurrencyDialog.collectAsStateWithLifecycle()
    val showThemeDialog by viewModel.showThemeDialog.collectAsStateWithLifecycle()
    val showDataManagementDialog by viewModel.showDataManagementDialog.collectAsStateWithLifecycle()
    val showImportExportDialog by viewModel.showImportExportDialog.collectAsStateWithLifecycle()
    val showAboutAppDialog by viewModel.showAboutAppDialog.collectAsStateWithLifecycle()
    val showNotificationSettingsDialog by viewModel.showNotificationSettingsDialog.collectAsStateWithLifecycle()

    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    val currencyConfig by viewModel.currencyConfig.collectAsStateWithLifecycle()
    val notificationConfig by viewModel.notificationConfig.collectAsStateWithLifecycle()

    var preselectedClientForReceipt by remember { mutableStateOf<ClientEntity?>(null) }
    var filterProductsOnlyLowStock by remember { mutableStateOf(false) }

    // Intercept back button if not on HOME
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentScreen = currentScreen,
                representativeName = representativeName,
                todaySalesTotal = todaySalesTotal,
                todayReceiptsTotal = todayReceiptsTotal,
                currencySymbol = currencySymbol,
                onNavigate = { screen ->
                    viewModel.navigateTo(screen)
                },
                onOpenSettings = { viewModel.openSettings() },
                onOpenBackup = { viewModel.openBackup() },
                onOpenSubscription = { viewModel.openSubscription() },
                onOpenLanguage = { viewModel.navigateTo(AppScreen.LANGUAGES) },
                onOpenCurrency = { viewModel.navigateTo(AppScreen.CURRENCIES) },
                onOpenTheme = { viewModel.openTheme() },
                onOpenNotificationSettings = { viewModel.openNotificationSettings() },
                onOpenDataManagement = { viewModel.openDataManagement() },
                onOpenImportExport = { viewModel.openImportExport() },
                onOpenAboutApp = { viewModel.openAboutApp() },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        when (currentScreen) {
            AppScreen.HOME -> {
                HomeScreen(
                    todaySalesCount = todaySalesCount,
                    todayClientsCount = todayClientsCount,
                    lowStockCount = lowStockCount,
                    todayReceiptsCount = todayReceiptsCount,
                    userMessage = userMessage,
                    onClearUserMessage = { viewModel.clearUserMessage() },
                    onMenuClick = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onSearchClick = { viewModel.openSearch() },
                    onNotificationClick = { viewModel.openNotifications() },
                    onSalesClick = { viewModel.navigateTo(AppScreen.SALES) },
                    onClientsClick = { viewModel.navigateTo(AppScreen.CLIENTS) },
                    onProductsClick = {
                        filterProductsOnlyLowStock = false
                        viewModel.navigateTo(AppScreen.PRODUCTS)
                    },
                    onReceiptClick = { viewModel.navigateTo(AppScreen.RECEIPTS) },
                    onPurchasesClick = { viewModel.navigateTo(AppScreen.PURCHASES) },
                    onInventoryClick = { viewModel.navigateTo(AppScreen.INVENTORY) },
                    onBannerClick = { viewModel.openCreateSale() },
                    onLowStockClick = {
                        filterProductsOnlyLowStock = true
                        viewModel.navigateTo(AppScreen.PRODUCTS)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.SALES -> {
                SalesScreen(
                    invoices = invoices,
                    products = products,
                    clients = clients,
                    currencySymbol = currencySymbol,
                    companyName = companyName,
                    representativeName = representativeName,
                    taxNumber = taxNumber,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onCreateInvoiceClick = { viewModel.openCreateSale() },
                    onUpdateInvoice = { viewModel.updateInvoice(it) },
                    onDeleteInvoice = { viewModel.deleteInvoice(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.CLIENTS -> {
                ClientsScreen(
                    clients = clients,
                    invoices = invoices,
                    receipts = receipts,
                    currencySymbol = currencySymbol,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onCreateClientClick = { viewModel.openCreateClient() },
                    onAddReceiptForClient = { client ->
                        preselectedClientForReceipt = client
                        viewModel.openCreateReceipt()
                    },
                    onUpdateClient = { viewModel.updateClient(it) },
                    onDeleteClient = { viewModel.deleteClient(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.SUPPLIERS -> {
                SuppliersScreen(
                    suppliers = suppliers,
                    purchases = purchases,
                    payments = supplierPayments,
                    currencySymbol = currencySymbol,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onCreateSupplierClick = { viewModel.openCreateSupplier() },
                    onRecordSupplierPayment = { supplier, amount, method, notes ->
                        viewModel.recordSupplierPayment(supplier, amount, method, notes)
                    },
                    onDeleteSupplierPayment = { payment ->
                        viewModel.deleteSupplierPayment(payment)
                    },
                    onUpdateSupplier = { viewModel.updateSupplier(it) },
                    onDeleteSupplier = { viewModel.deleteSupplier(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.PRODUCTS -> {
                ProductsScreen(
                    products = products,
                    lowStockProducts = lowStockProducts,
                    currencySymbol = currencySymbol,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onCreateProductClick = { viewModel.openCreateProduct() },
                    onUpdateProduct = { viewModel.updateProduct(it) },
                    onDeleteProduct = { viewModel.deleteProduct(it) },
                    filterOnlyLowStock = filterProductsOnlyLowStock,
                    onScanBarcodeClick = { viewModel.openBarcodeScanner() },
                    selectedProductFromBarcode = selectedProductFromBarcode,
                    onClearSelectedProductFromBarcode = { viewModel.clearSelectedProductFromBarcode() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.RECEIPTS -> {
                ReceiptsScreen(
                    receipts = receipts,
                    clients = clients,
                    currencySymbol = currencySymbol,
                    companyName = companyName,
                    representativeName = representativeName,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onCreateReceiptClick = {
                        preselectedClientForReceipt = null
                        viewModel.openCreateReceipt()
                    },
                    onDeleteReceipt = { viewModel.deleteReceipt(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.PURCHASES -> {
                PurchasesScreen(
                    purchases = purchases,
                    products = products,
                    suppliers = suppliers,
                    currencySymbol = currencySymbol,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onNavigateToSuppliers = { viewModel.navigateTo(AppScreen.SUPPLIERS) },
                    onCreatePurchaseClick = { viewModel.openCreatePurchase() },
                    onDeletePurchase = { viewModel.deletePurchase(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.INVENTORY -> {
                InventoryScreen(
                    products = products,
                    stockMovements = stockMovements,
                    currencySymbol = currencySymbol,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onUpdateStockDirect = { product, newStock ->
                        viewModel.updateStockDirect(product, newStock)
                    },
                    onRecordStockAdjustment = { product, type, typeArabic, delta, reason ->
                        viewModel.recordStockAdjustment(product, type, typeArabic, delta, reason)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.REPORTS -> {
                ReportsScreen(
                    invoices = invoices,
                    receipts = receipts,
                    purchases = purchases,
                    products = products,
                    clients = clients,
                    suppliers = suppliers,
                    expenses = expenses,
                    currencySymbol = currencySymbol,
                    companyName = companyName,
                    representativeName = representativeName,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onAddExpense = { title, cat, amount, method, notes ->
                        viewModel.recordExpense(title, cat, amount, method, notes)
                    },
                    onDeleteExpense = { viewModel.deleteExpense(it) },
                    modifier = Modifier.fillMaxSize()
                )
            }


            AppScreen.SETTINGS -> {
                SettingsScreen(
                    companyName = companyName,
                    representativeName = representativeName,
                    taxNumber = taxNumber,
                    phone = phone,
                    address = address,
                    currencyConfig = currencyConfig,
                    themeConfig = themeConfig,
                    notificationConfig = notificationConfig,
                    selectedLanguage = selectedLanguage,
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onSaveProfile = { company, rep, tax, p, a ->
                        viewModel.updateSettings(company, rep, tax, p, a)
                    },
                    onOpenBackup = { viewModel.openBackup() },
                    onOpenImportExport = { viewModel.openImportExport() },
                    onOpenCurrency = { viewModel.navigateTo(AppScreen.CURRENCIES) },
                    onOpenLanguage = { viewModel.navigateTo(AppScreen.LANGUAGES) },
                    onOpenTheme = { viewModel.openTheme() },
                    onOpenNotificationSettings = { viewModel.openNotificationSettings() },
                    onOpenSubscription = { viewModel.openSubscription() },
                    onOpenDataManagement = { viewModel.openDataManagement() },
                    onOpenAboutApp = { viewModel.openAboutApp() },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.CURRENCIES -> {
                CurrenciesScreen(
                    currencyConfig = currencyConfig,
                    onSelectCurrency = { item ->
                        viewModel.selectCurrencyItem(item)
                    },
                    onUpdateConfig = { config ->
                        viewModel.updateCurrencyConfig(config)
                    },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AppScreen.LANGUAGES -> {
                LanguagesScreen(
                    selectedLanguage = selectedLanguage,
                    onSelectLanguage = { lang ->
                        viewModel.setLanguage(lang)
                    },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Global Search Dialog
    if (showSearchDialog) {
        SearchDialog(
            searchQuery = searchQuery,
            searchResults = searchResults,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onDismiss = { viewModel.closeSearch() },
            onNavigateToSales = { viewModel.navigateTo(AppScreen.SALES) },
            onNavigateToProducts = {
                filterProductsOnlyLowStock = false
                viewModel.navigateTo(AppScreen.PRODUCTS)
            },
            onNavigateToClients = { viewModel.navigateTo(AppScreen.CLIENTS) },
            onNavigateToReceipts = { viewModel.navigateTo(AppScreen.RECEIPTS) }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        NotificationsDialog(
            lowStockProducts = lowStockProducts,
            clientsWithDebt = clients,
            todaySalesCount = todaySalesCount,
            allProducts = products,
            lastBackupTime = lastBackupTime,
            notificationConfig = notificationConfig,
            currencySymbol = currencySymbol,
            onDismiss = { viewModel.closeNotifications() },
            onNavigateToLowStock = {
                viewModel.closeNotifications()
                filterProductsOnlyLowStock = true
                viewModel.navigateTo(AppScreen.PRODUCTS)
            },
            onNavigateToClients = {
                viewModel.closeNotifications()
                viewModel.navigateTo(AppScreen.CLIENTS)
            },
            onOpenNotificationSettings = {
                viewModel.closeNotifications()
                viewModel.openNotificationSettings()
            },
            onOpenBackup = {
                viewModel.closeNotifications()
                viewModel.openBackup()
            }
        )
    }

    // Create Invoice Dialog
    if (showCreateSaleDialog) {
        CreateInvoiceDialog(
            products = products,
            clients = clients,
            currencySymbol = currencySymbol,
            onDismiss = { viewModel.closeCreateSale() },
            onSubmit = { customerName, customerPhone, items, subtotal, discount, total, paid, isCredit, paymentMethod, notes ->
                viewModel.recordSale(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    items = items,
                    subtotal = subtotal,
                    discount = discount,
                    totalAmount = total,
                    paidAmount = paid,
                    isCredit = isCredit,
                    paymentMethod = paymentMethod,
                    notes = notes
                )
            }
        )
    }

    // Invoice Creation Post-Save Action Dialog (Preview, Generate PDF, Print, Share)
    if (showInvoiceSavedDialog && lastCreatedInvoice != null) {
        InvoiceSavedSuccessDialog(
            invoice = lastCreatedInvoice!!,
            customerPhone = lastCreatedInvoicePhone,
            items = lastCreatedInvoiceItems,
            currencySymbol = currencySymbol,
            companyName = companyName,
            representativeName = representativeName,
            taxNumber = taxNumber,
            onDismiss = { viewModel.closeInvoiceSavedDialog() }
        )
    }

    // Create Receipt Dialog
    if (showCreateReceiptDialog) {
        CreateReceiptDialog(
            clients = clients,
            preselectedClient = preselectedClientForReceipt,
            currencySymbol = currencySymbol,
            onDismiss = {
                viewModel.closeCreateReceipt()
                preselectedClientForReceipt = null
            },
            onSubmit = { client, amount, method, notes ->
                viewModel.recordReceipt(client, amount, method, notes)
                preselectedClientForReceipt = null
            }
        )
    }

    // Barcode Scanner Dialog
    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = { viewModel.closeBarcodeScanner() },
            onBarcodeScanned = { barcode ->
                viewModel.onBarcodeScanned(barcode)
            }
        )
    }

    // Create Product Dialog
    if (showCreateProductDialog) {
        CreateProductDialog(
            initialBarcode = scannedBarcodeForNewProduct ?: "",
            currencySymbol = currencySymbol,
            onDismiss = {
                viewModel.closeCreateProduct()
                viewModel.clearScannedBarcode()
            },
            onRescanBarcode = {
                viewModel.closeCreateProduct()
                viewModel.openBarcodeScanner()
            },
            onSubmit = { name, barcode, batchNumber, mfgDate, expDate, cost, sale, qty, minStock, unit, category, notes ->
                viewModel.addProduct(name, barcode, batchNumber, mfgDate, expDate, cost, sale, qty, minStock, unit, category, notes)
            }
        )
    }

    // Create Client Dialog
    if (showCreateClientDialog) {
        CreateClientDialog(
            currencySymbol = currencySymbol,
            onDismiss = { viewModel.closeCreateClient() },
            onSubmit = { name, phone, address, location, initialDebt, notes ->
                viewModel.addClient(name, phone, address, location, initialDebt, notes)
            }
        )
    }

    // Create Supplier Dialog
    if (showCreateSupplierDialog) {
        CreateSupplierDialog(
            currencySymbol = currencySymbol,
            onDismiss = { viewModel.closeCreateSupplier() },
            onSubmit = { name, phone, address, company, initialBalance, notes ->
                viewModel.addSupplier(name, phone, address, company, initialBalance, notes)
            }
        )
    }

    // Create Purchase Dialog
    if (showCreatePurchaseDialog) {
        CreatePurchaseDialog(
            products = products,
            suppliers = suppliers,
            currencySymbol = currencySymbol,
            onDismiss = { viewModel.closeCreatePurchase() },
            onQuickAddProduct = { name, barcode, cost, sale, unit, cat ->
                viewModel.addProduct(name, barcode, "", "", "", cost, sale, 0, 5, unit, cat, "")
            },
            onSubmit = { supplierId, supplierName, items, subtotal, discount, total, paid, method, isCredit, notes ->
                viewModel.recordPurchase(supplierId, supplierName, items, subtotal, discount, total, paid, method, isCredit, notes)
            }
        )
    }

    // Administrative Menu Dialogs
    if (showSettingsDialog) {
        SettingsDialog(
            companyName = companyName,
            representativeName = representativeName,
            taxNumber = taxNumber,
            autoBackupEnabled = autoBackupEnabled,
            autoBackupFrequency = autoBackupFrequency,
            onSave = { c, r, t, auto, freq ->
                viewModel.updateSettings(c, r, t)
                viewModel.updateAutoBackupConfig(auto, freq)
            },
            onDismiss = { viewModel.closeSettings() }
        )
    }

    if (showBackupDialog) {
        BackupRestoreDialog(
            localBackups = localBackups,
            autoBackupEnabled = autoBackupEnabled,
            autoBackupFrequency = autoBackupFrequency,
            lastBackupTime = lastBackupTime,
            isLoading = isBackupLoading,
            statusMessage = backupStatusMessage,
            onToggleAutoBackup = { enabled, freq ->
                viewModel.updateAutoBackupConfig(enabled, freq)
            },
            onCreateManualBackup = {
                viewModel.createManualBackup()
            },
            onRestoreBackup = { backup ->
                viewModel.restoreFromLocalBackup(backup) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            },
            onDeleteBackup = { backup ->
                viewModel.deleteLocalBackup(backup)
            },
            onShareBackup = { backup ->
                val intent = BackupManager.shareBackupFile(context, backup.filePath)
                context.startActivity(Intent.createChooser(intent, "مشاركة النسخة الاحتياطية").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            },
            onImportExternalFile = {
                pickBackupLauncher.launch("application/json")
            },
            onDismiss = { viewModel.closeBackup() }
        )
    }

    if (showSubscriptionDialog) {
        SubscriptionDialog(
            onDismiss = { viewModel.closeSubscription() }
        )
    }

    if (showLanguageDialog) {
        LanguageDialog(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.closeLanguage() }
        )
    }

    if (showCurrencyDialog) {
        CurrencyDialog(
            currentConfig = currencyConfig,
            onSaveConfig = { viewModel.updateCurrencyConfig(it) },
            onDismiss = { viewModel.closeCurrency() }
        )
    }

    if (showThemeDialog) {
        ThemeCustomizationDialog(
            currentConfig = themeConfig,
            onSaveConfig = { viewModel.updateThemeConfig(it) },
            onDismiss = { viewModel.closeTheme() }
        )
    }

    if (showNotificationSettingsDialog) {
        NotificationSettingsDialog(
            currentConfig = notificationConfig,
            onSaveConfig = { viewModel.updateNotificationConfig(it) },
            onDismiss = { viewModel.closeNotificationSettings() }
        )
    }

    if (showDataManagementDialog) {
        DataManagementDialog(
            totalProductsCount = products.size,
            totalClientsCount = clients.size,
            totalInvoicesCount = invoices.size,
            totalPurchasesCount = purchases.size,
            totalSuppliersCount = suppliers.size,
            totalExpensesCount = expenses.size,
            onClearTestTransactions = { viewModel.clearTestTransactionsOnly() },
            onResetSettingsToDefault = { viewModel.resetSettingsToDefault() },
            onResetToDemoData = { viewModel.resetToDemoData() },
            onClearAllData = { viewModel.clearAllData() },
            onDismiss = { viewModel.closeDataManagement() }
        )
    }

    if (showImportExportDialog) {
        ImportExportDialog(
            onExportCompleteCsv = {
                coroutineScope.launch {
                    val file = viewModel.exportAllDataCsv()
                    if (file != null) {
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, "تصدير بيانات مندوبك الشامل")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة ملف Excel").apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    } else {
                        Toast.makeText(context, "فشل تصدير ملف CSV", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onExportJsonBackup = {
                viewModel.createManualBackup { success, _ ->
                    if (success) {
                        val latest = viewModel.localBackups.value.firstOrNull()
                        if (latest != null) {
                            val intent = BackupManager.shareBackupFile(context, latest.filePath)
                            context.startActivity(Intent.createChooser(intent, "تصدير وحفظ النسخة الاحتياطية").apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        }
                    }
                }
            },
            onImportJsonBackup = {
                pickBackupLauncher.launch("application/json")
            },
            onDismiss = { viewModel.closeImportExport() }
        )
    }

    if (showAboutAppDialog) {
        AboutAppDialog(
            onDismiss = { viewModel.closeAboutApp() }
        )
    }
}
