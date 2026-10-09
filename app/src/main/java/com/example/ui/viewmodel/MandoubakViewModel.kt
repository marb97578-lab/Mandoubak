package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.ClientEntity
import com.example.data.entity.ExpenseEntity
import com.example.data.entity.LanguageSettingEntity
import com.example.data.entity.PaymentReceiptEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.PurchaseInvoiceEntity
import com.example.data.entity.SaleInvoiceEntity
import com.example.data.entity.StockMovementEntity
import com.example.data.entity.SupplierEntity
import com.example.data.entity.SupplierPaymentEntity
import com.example.data.cloud.CloudBackupRecord
import com.example.data.cloud.FirebaseBackupService
import com.example.data.repository.MandoubakRepository
import com.example.ui.theme.CardColorStyle
import com.example.ui.theme.TextColorOption
import com.example.ui.theme.ThemeConfig
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.ThemeRepository
import com.example.util.BackupFileInfo
import com.example.util.BackupManager
import com.example.util.BackupSettingsData
import com.example.util.BackupValidationResult
import com.example.util.CurrencyConfig
import com.example.util.CurrencyItem
import com.example.util.CurrencyRepository
import com.example.util.LanguageOption
import com.example.util.LanguageRepository
import com.example.util.NotificationConfig
import com.example.util.SymbolPlacement
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    SALES,
    CLIENTS,
    SUPPLIERS,
    PRODUCTS,
    RECEIPTS,
    PURCHASES,
    INVENTORY,
    REPORTS,
    SETTINGS,
    CURRENCIES,
    LANGUAGES
}

data class UniversalSearchResult(
    val products: List<ProductEntity> = emptyList(),
    val clients: List<ClientEntity> = emptyList(),
    val suppliers: List<SupplierEntity> = emptyList(),
    val invoices: List<SaleInvoiceEntity> = emptyList(),
    val receipts: List<PaymentReceiptEntity> = emptyList(),
    val purchases: List<PurchaseInvoiceEntity> = emptyList()
)

class MandoubakViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MandoubakRepository = MandoubakRepository(AppDatabase.getDatabase(application).appDao())

    // App Screen Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Backup & Restore State
    private val _localBackups = MutableStateFlow<List<BackupFileInfo>>(emptyList())
    val localBackups: StateFlow<List<BackupFileInfo>> = _localBackups.asStateFlow()

    private val _autoBackupEnabled = MutableStateFlow(true)
    val autoBackupEnabled: StateFlow<Boolean> = _autoBackupEnabled.asStateFlow()

    private val _autoBackupFrequency = MutableStateFlow("DAILY")
    val autoBackupFrequency: StateFlow<String> = _autoBackupFrequency.asStateFlow()

    private val _lastBackupTime = MutableStateFlow(0L)
    val lastBackupTime: StateFlow<Long> = _lastBackupTime.asStateFlow()

    private val _isBackupOperationInProgress = MutableStateFlow(false)
    val isBackupOperationInProgress: StateFlow<Boolean> = _isBackupOperationInProgress.asStateFlow()

    private val _backupStatusMessage = MutableStateFlow<String?>(null)
    val backupStatusMessage: StateFlow<String?> = _backupStatusMessage.asStateFlow()

    // Cloud Backup State (Firebase Firestore)
    private val firebaseBackupService by lazy { FirebaseBackupService(getApplication()) }

    private val _isCloudSignedIn = MutableStateFlow(false)
    val isCloudSignedIn: StateFlow<Boolean> = _isCloudSignedIn.asStateFlow()

    private val _cloudUserEmail = MutableStateFlow<String?>(null)
    val cloudUserEmail: StateFlow<String?> = _cloudUserEmail.asStateFlow()

    private val _cloudBackups = MutableStateFlow<List<CloudBackupRecord>>(emptyList())
    val cloudBackups: StateFlow<List<CloudBackupRecord>> = _cloudBackups.asStateFlow()

    private val _isCloudOperationInProgress = MutableStateFlow(false)
    val isCloudOperationInProgress: StateFlow<Boolean> = _isCloudOperationInProgress.asStateFlow()

    private val _cloudStatusMessage = MutableStateFlow<String?>(null)
    val cloudStatusMessage: StateFlow<String?> = _cloudStatusMessage.asStateFlow()

    // Operational Dialogs
    private val _showSearchDialog = MutableStateFlow(false)
    val showSearchDialog: StateFlow<Boolean> = _showSearchDialog.asStateFlow()

    private val _showNotificationsDialog = MutableStateFlow(false)
    val showNotificationsDialog: StateFlow<Boolean> = _showNotificationsDialog.asStateFlow()

    private val _showCreateSaleDialog = MutableStateFlow(false)
    val showCreateSaleDialog: StateFlow<Boolean> = _showCreateSaleDialog.asStateFlow()

    private val _showCreateReceiptDialog = MutableStateFlow(false)
    val showCreateReceiptDialog: StateFlow<Boolean> = _showCreateReceiptDialog.asStateFlow()

    private val _showCreateProductDialog = MutableStateFlow(false)
    val showCreateProductDialog: StateFlow<Boolean> = _showCreateProductDialog.asStateFlow()

    private val _showCreateClientDialog = MutableStateFlow(false)
    val showCreateClientDialog: StateFlow<Boolean> = _showCreateClientDialog.asStateFlow()

    private val _showCreateSupplierDialog = MutableStateFlow(false)
    val showCreateSupplierDialog: StateFlow<Boolean> = _showCreateSupplierDialog.asStateFlow()

    private val _showCreatePurchaseDialog = MutableStateFlow(false)
    val showCreatePurchaseDialog: StateFlow<Boolean> = _showCreatePurchaseDialog.asStateFlow()

    // Barcode Scanning State
    private val _showBarcodeScanner = MutableStateFlow(false)
    val showBarcodeScanner: StateFlow<Boolean> = _showBarcodeScanner.asStateFlow()

    private val _scannedBarcodeForNewProduct = MutableStateFlow<String?>(null)
    val scannedBarcodeForNewProduct: StateFlow<String?> = _scannedBarcodeForNewProduct.asStateFlow()

    private val _selectedProductFromBarcode = MutableStateFlow<ProductEntity?>(null)
    val selectedProductFromBarcode: StateFlow<ProductEntity?> = _selectedProductFromBarcode.asStateFlow()

    // Invoice Creation Post-Save Action State (Preview, Generate PDF, Print, Share)
    private val _lastCreatedInvoice = MutableStateFlow<SaleInvoiceEntity?>(null)
    val lastCreatedInvoice: StateFlow<SaleInvoiceEntity?> = _lastCreatedInvoice.asStateFlow()

    private val _lastCreatedInvoicePhone = MutableStateFlow("")
    val lastCreatedInvoicePhone: StateFlow<String> = _lastCreatedInvoicePhone.asStateFlow()

    private val _lastCreatedInvoiceItems = MutableStateFlow<List<Pair<ProductEntity, Int>>>(emptyList())
    val lastCreatedInvoiceItems: StateFlow<List<Pair<ProductEntity, Int>>> = _lastCreatedInvoiceItems.asStateFlow()

    private val _showInvoiceSavedDialog = MutableStateFlow(false)
    val showInvoiceSavedDialog: StateFlow<Boolean> = _showInvoiceSavedDialog.asStateFlow()

    // Administrative Menu Dialogs
    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showBackupDialog = MutableStateFlow(false)
    val showBackupDialog: StateFlow<Boolean> = _showBackupDialog.asStateFlow()

    private val _showSubscriptionDialog = MutableStateFlow(false)
    val showSubscriptionDialog: StateFlow<Boolean> = _showSubscriptionDialog.asStateFlow()

    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog.asStateFlow()

    private val _showCurrencyDialog = MutableStateFlow(false)
    val showCurrencyDialog: StateFlow<Boolean> = _showCurrencyDialog.asStateFlow()

    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog: StateFlow<Boolean> = _showThemeDialog.asStateFlow()

    private val _showDataManagementDialog = MutableStateFlow(false)
    val showDataManagementDialog: StateFlow<Boolean> = _showDataManagementDialog.asStateFlow()

    private val _showImportExportDialog = MutableStateFlow(false)
    val showImportExportDialog: StateFlow<Boolean> = _showImportExportDialog.asStateFlow()

    private val _showAboutAppDialog = MutableStateFlow(false)
    val showAboutAppDialog: StateFlow<Boolean> = _showAboutAppDialog.asStateFlow()

    private val _showNotificationSettingsDialog = MutableStateFlow(false)
    val showNotificationSettingsDialog: StateFlow<Boolean> = _showNotificationSettingsDialog.asStateFlow()

    // Theme Customization (80+ color themes, accent color, card styles, text contrast, dark mode)
    private val _themeConfig = MutableStateFlow(ThemeConfig())
    val themeConfig: StateFlow<ThemeConfig> = _themeConfig.asStateFlow()

    // Currency System
    private val _currencyConfig = MutableStateFlow(CurrencyConfig())
    val currencyConfig: StateFlow<CurrencyConfig> = _currencyConfig.asStateFlow()

    // Notifications & Alert Configuration
    private val _notificationConfig = MutableStateFlow(NotificationConfig())
    val notificationConfig: StateFlow<NotificationConfig> = _notificationConfig.asStateFlow()

    // User Message Toast/Snackbar
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Preferences / Commercial Info
    private val _currencySymbol = MutableStateFlow("﷼")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("العربية")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedLanguageCode = MutableStateFlow("ar")
    val selectedLanguageCode: StateFlow<String> = _selectedLanguageCode.asStateFlow()

    private val _companyName = MutableStateFlow("مؤسسة التوزيع والتجارة الحديثة")
    val companyName: StateFlow<String> = _companyName.asStateFlow()

    private val _representativeName = MutableStateFlow("أحمد الشمري (مبيعات 102)")
    val representativeName: StateFlow<String> = _representativeName.asStateFlow()

    private val _taxNumber = MutableStateFlow("300123456700003")
    val taxNumber: StateFlow<String> = _taxNumber.asStateFlow()

    private val _phone = MutableStateFlow("0550001234")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _address = MutableStateFlow("الرياض - المملكة العربية السعودية")
    val address: StateFlow<String> = _address.asStateFlow()

    // Data streams
    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<SupplierEntity>> = repository.allSuppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplierPayments: StateFlow<List<SupplierPaymentEntity>> = repository.allSupplierPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockMovements: StateFlow<List<StockMovementEntity>> = repository.allStockMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clients: StateFlow<List<ClientEntity>> = repository.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<SaleInvoiceEntity>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receipts: StateFlow<List<PaymentReceiptEntity>> = repository.allReceipts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchases: StateFlow<List<PurchaseInvoiceEntity>> = repository.allPurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Statistics Counters
    val todaySalesCount: StateFlow<Int> = repository.getTodayInvoicesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayClientsCount: StateFlow<Int> = repository.totalClientsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lowStockCount: StateFlow<Int> = repository.lowStockCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val outOfStockCount: StateFlow<Int> = repository.outOfStockCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayReceiptsCount: StateFlow<Int> = repository.getTodayReceiptsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todaySalesTotal: StateFlow<Double> = repository.getTodaySalesTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayReceiptsTotal: StateFlow<Double> = repository.getTodayReceiptsTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Global Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<UniversalSearchResult> = combine(
        _searchQuery,
        products,
        clients,
        suppliers,
        invoices
    ) { query, prods, clis, sups, invs ->
        if (query.isBlank()) {
            UniversalSearchResult()
        } else {
            val q = query.trim().lowercase()
            UniversalSearchResult(
                products = prods.filter {
                    it.name.lowercase().contains(q) ||
                    it.barcode.contains(q) ||
                    it.batchNumber.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    it.supplierName.lowercase().contains(q)
                },
                clients = clis.filter { it.name.lowercase().contains(q) || it.phone.contains(q) },
                suppliers = sups.filter { it.name.lowercase().contains(q) || it.companyName.lowercase().contains(q) || it.phone.contains(q) },
                invoices = invs.filter { it.invoiceNumber.lowercase().contains(q) || it.clientName.lowercase().contains(q) }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UniversalSearchResult())

    init {
        _autoBackupEnabled.value = BackupManager.getAutoBackupEnabled(application)
        _autoBackupFrequency.value = BackupManager.getAutoBackupFrequency(application)
        _lastBackupTime.value = BackupManager.getLastBackupTime(application)
        loadLocalBackups()

        val prefs = application.getSharedPreferences("mandoubak_prefs", android.content.Context.MODE_PRIVATE)
        if (!prefs.getBoolean("clean_empty_start_applied_v3", false)) {
            viewModelScope.launch {
                repository.clearAllData()
                prefs.edit().putBoolean("clean_empty_start_applied_v3", true).apply()
            }
        }

        viewModelScope.launch {
            val settings = getBackupSettingsData()
            val autoBackup = repository.checkAutoBackup(application, settings)
            if (autoBackup != null) {
                _lastBackupTime.value = autoBackup.timestamp
                loadLocalBackups()
            }
        }

        // Room Database: Observe and restore global currency settings securely
        viewModelScope.launch {
            repository.getCurrencySettingFlow().collect { savedSetting ->
                if (savedSetting != null) {
                    val config = CurrencyConfig.fromEntity(savedSetting)
                    _currencyConfig.value = config
                    _currencySymbol.value = config.customSymbol
                } else {
                    // Seed initial default currency (Yemeni Rial - YER - ﷼) into Room Database
                    val defaultEntity = CurrencyConfig(
                        selectedCode = "YER",
                        customSymbol = "﷼"
                    ).toEntity()
                    repository.saveCurrencySetting(defaultEntity)
                }
            }
        }

        // Room Database: Observe and restore global language settings securely
        viewModelScope.launch {
            repository.getLanguageSettingFlow().collect { savedSetting ->
                if (savedSetting != null) {
                    _selectedLanguage.value = savedSetting.nameNative
                    _selectedLanguageCode.value = savedSetting.selectedCode
                } else {
                    // Seed initial default language (Arabic - العربية) into Room Database
                    val defaultEntity = LanguageSettingEntity(
                        selectedCode = "ar",
                        nameNative = "العربية",
                        nameAr = "العربية",
                        nameEn = "Arabic",
                        isRtl = true,
                        flagEmoji = "🇸🇦"
                    )
                    repository.saveLanguageSetting(defaultEntity)
                }
            }
        }
    }

    // Navigation and Modals
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openSearch() {
        _searchQuery.value = ""
        _showSearchDialog.value = true
    }

    fun closeSearch() {
        _showSearchDialog.value = false
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openNotifications() {
        _showNotificationsDialog.value = true
    }

    fun closeNotifications() {
        _showNotificationsDialog.value = false
    }

    fun openCreateSale() {
        _showCreateSaleDialog.value = true
    }

    fun closeCreateSale() {
        _showCreateSaleDialog.value = false
    }

    fun openCreateReceipt() {
        _showCreateReceiptDialog.value = true
    }

    fun closeCreateReceipt() {
        _showCreateReceiptDialog.value = false
    }

    fun openCreateProduct() {
        _showCreateProductDialog.value = true
    }

    fun closeCreateProduct() {
        _showCreateProductDialog.value = false
    }

    fun openCreateClient() {
        _showCreateClientDialog.value = true
    }

    fun closeCreateClient() {
        _showCreateClientDialog.value = false
    }

    fun openCreateSupplier() {
        _showCreateSupplierDialog.value = true
    }

    fun closeCreateSupplier() {
        _showCreateSupplierDialog.value = false
    }

    fun openCreatePurchase() {
        _showCreatePurchaseDialog.value = true
    }

    fun closeCreatePurchase() {
        _showCreatePurchaseDialog.value = false
    }

    // Administrative Dialog openers
    fun openSettings() { _showSettingsDialog.value = true }
    fun closeSettings() { _showSettingsDialog.value = false }

    fun openBackup() {
        _showBackupDialog.value = true
        loadLocalBackups()
        try {
            _isCloudSignedIn.value = firebaseBackupService.isUserSignedIn()
            _cloudUserEmail.value = firebaseBackupService.getCurrentUserEmail()
            if (_isCloudSignedIn.value) {
                loadCloudBackups()
            }
        } catch (_: Exception) {}
    }
    fun closeBackup() { _showBackupDialog.value = false }

    fun openSubscription() { _showSubscriptionDialog.value = true }
    fun closeSubscription() { _showSubscriptionDialog.value = false }

    fun openLanguage() { _showLanguageDialog.value = true }
    fun closeLanguage() { _showLanguageDialog.value = false }

    fun openCurrency() { _showCurrencyDialog.value = true }
    fun closeCurrency() { _showCurrencyDialog.value = false }

    fun openTheme() { _showThemeDialog.value = true }
    fun closeTheme() { _showThemeDialog.value = false }

    fun openDataManagement() { _showDataManagementDialog.value = true }
    fun closeDataManagement() { _showDataManagementDialog.value = false }

    fun openImportExport() { _showImportExportDialog.value = true }
    fun closeImportExport() { _showImportExportDialog.value = false }

    fun openAboutApp() { _showAboutAppDialog.value = true }
    fun closeAboutApp() { _showAboutAppDialog.value = false }

    fun openNotificationSettings() { _showNotificationSettingsDialog.value = true }
    fun closeNotificationSettings() { _showNotificationSettingsDialog.value = false }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showToast(msg: String) {
        _userMessage.value = msg
    }

    // Settings & Configuration Mutators
    fun updateSettings(company: String, rep: String, tax: String, userPhone: String = "", userAddress: String = "") {
        _companyName.value = company
        _representativeName.value = rep
        _taxNumber.value = tax
        if (userPhone.isNotBlank()) _phone.value = userPhone
        if (userAddress.isNotBlank()) _address.value = userAddress
        _showSettingsDialog.value = false
        _userMessage.value = "تم حفظ الإعدادات بنجاح"
    }

    // Theme Customization mutators (80+ themes, accent, card style, text color, light/dark mode)
    fun updateThemeConfig(config: ThemeConfig) {
        _themeConfig.value = config
        _showThemeDialog.value = false
        val theme = ThemeRepository.getThemeById(config.activeThemeId)
        _userMessage.value = "تم اعتماد ثيم: ${theme.nameAr} (${config.themeMode.titleAr})"
    }

    fun updateThemeId(themeId: Int) {
        _themeConfig.value = _themeConfig.value.copy(activeThemeId = themeId)
        val theme = ThemeRepository.getThemeById(themeId)
        _userMessage.value = "تم تفعيل ثيم: ${theme.nameAr}"
    }

    fun updateThemeMode(mode: ThemeMode) {
        _themeConfig.value = _themeConfig.value.copy(themeMode = mode)
        _userMessage.value = "تم تفعيل ${mode.titleAr}"
    }

    fun updateCustomAccent(hex: String?) {
        _themeConfig.value = _themeConfig.value.copy(customAccentHex = hex)
        _userMessage.value = if (hex != null) "تم تخصيص لون التمييز الإضافي" else "تم إلغاء اللون المخصص"
    }

    fun updateCardColorStyle(style: CardColorStyle) {
        _themeConfig.value = _themeConfig.value.copy(cardColorStyle = style)
        _userMessage.value = "تم تحديث نمط بطاقات لوحة التحكم: ${style.titleAr}"
    }

    fun updateTextColorOption(option: TextColorOption) {
        _themeConfig.value = _themeConfig.value.copy(textColorOption = option)
        _userMessage.value = "تم ضبط تباين النصوص: ${option.titleAr}"
    }

    // Currency System Mutators
    fun updateCurrencyConfig(config: CurrencyConfig) {
        _currencyConfig.value = config
        _currencySymbol.value = config.customSymbol
        _showCurrencyDialog.value = false
        _userMessage.value = "تم اعتماد العملة وتنسيق الأسعار: ${config.customSymbol}"
        viewModelScope.launch {
            repository.saveCurrencySetting(config.toEntity())
        }
    }

    fun setCurrency(symbolOrCode: String) {
        val found = CurrencyRepository.findBySymbol(symbolOrCode) ?: CurrencyRepository.findByCode(symbolOrCode)
        val symbol = found?.defaultSymbol ?: symbolOrCode
        _currencySymbol.value = symbol
        val newConfig = _currencyConfig.value.copy(
            selectedCode = found?.code ?: "CUSTOM",
            customSymbol = symbol
        )
        _currencyConfig.value = newConfig
        _showCurrencyDialog.value = false
        _userMessage.value = "تم تغيير العملة إلى ${found?.nameAr ?: symbol}"
        viewModelScope.launch {
            repository.saveCurrencySetting(newConfig.toEntity())
        }
    }

    fun selectCurrencyItem(item: CurrencyItem) {
        val newConfig = _currencyConfig.value.copy(
            selectedCode = item.code,
            customSymbol = item.defaultSymbol
        )
        _currencyConfig.value = newConfig
        _currencySymbol.value = item.defaultSymbol
        _showCurrencyDialog.value = false
        _userMessage.value = "تم اعتماد عملة: ${item.nameAr} (${item.defaultSymbol})"
        viewModelScope.launch {
            repository.saveCurrencySetting(newConfig.toEntity())
        }
    }

    fun formatPrice(amount: Double): String {
        return CurrencyRepository.format(amount, _currencyConfig.value)
    }

    // Language Mutators (منظومة إدارة لغات العالم مع الحفظ في قاعدة بيانات Room)
    fun setLanguage(langOption: LanguageOption) {
        _selectedLanguage.value = langOption.nameNative
        _selectedLanguageCode.value = langOption.code
        _showLanguageDialog.value = false
        _userMessage.value = "تم تحويل لغة التطبيق بالكامل إلى: ${langOption.nameNative} (${langOption.nameAr})"
        viewModelScope.launch {
            repository.saveLanguageSetting(
                LanguageSettingEntity(
                    selectedCode = langOption.code,
                    nameNative = langOption.nameNative,
                    nameAr = langOption.nameAr,
                    nameEn = langOption.nameEn,
                    isRtl = langOption.isRtl,
                    flagEmoji = langOption.flagEmoji
                )
            )
        }
    }

    fun setLanguage(langNameOrCode: String) {
        val found = LanguageRepository.findByCodeOrName(langNameOrCode)
            ?: LanguageOption(
                code = langNameOrCode.take(2).lowercase(),
                nameNative = langNameOrCode,
                nameAr = langNameOrCode,
                nameEn = langNameOrCode,
                isRtl = LanguageRepository.isRtlLanguage(langNameOrCode),
                flagEmoji = "🌐"
            )
        setLanguage(found)
    }

    // Notification Settings Mutators
    fun updateNotificationConfig(config: NotificationConfig) {
        _notificationConfig.value = config
        _showNotificationSettingsDialog.value = false
        _userMessage.value = "تم حفظ إعدادات التنبيهات والإشعارات بنجاح"
    }

    // Barcode Scanning Controls
    fun openBarcodeScanner() {
        _showBarcodeScanner.value = true
    }

    fun closeBarcodeScanner() {
        _showBarcodeScanner.value = false
    }

    fun clearScannedBarcode() {
        _scannedBarcodeForNewProduct.value = null
    }

    fun clearSelectedProductFromBarcode() {
        _selectedProductFromBarcode.value = null
    }

    fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            closeBarcodeScanner()
            val clean = barcode.trim()
            val existing = repository.getProductByBarcode(clean)
            if (existing != null) {
                _selectedProductFromBarcode.value = existing
                _userMessage.value = "تم العثور على المنتج: ${existing.name}"
            } else {
                _scannedBarcodeForNewProduct.value = clean
                _showCreateProductDialog.value = true
                _userMessage.value = "باركود جديد ($clean) - يرجى إدخال تفاصيل الصنف"
            }
        }
    }

    // CRUD: Products
    fun addProduct(
        name: String,
        barcode: String,
        batchNumber: String,
        mfgDate: String,
        expDate: String,
        costPrice: Double,
        salePrice: Double,
        quantity: Int,
        minStock: Int,
        unit: String,
        category: String,
        supplierName: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val cleanBarcode = barcode.trim()
            if (cleanBarcode.isNotBlank() && repository.isBarcodeExists(cleanBarcode)) {
                _userMessage.value = "عذراً: الباركود ($cleanBarcode) مسجل مسبقاً لمنتج آخر! يرجى إدخال باركود فريد."
                return@launch
            }
            repository.insertProduct(
                ProductEntity(
                    name = name.trim(),
                    barcode = cleanBarcode,
                    batchNumber = batchNumber.trim(),
                    manufacturingDate = mfgDate.trim(),
                    expirationDate = expDate.trim(),
                    costPrice = costPrice,
                    salePrice = salePrice,
                    stockQuantity = quantity,
                    minStockThreshold = minStock,
                    unit = unit.trim().ifBlank { "حبة" },
                    category = category.trim().ifBlank { "عام" },
                    supplierName = supplierName.trim(),
                    notes = notes.trim()
                )
            )
            _scannedBarcodeForNewProduct.value = null
            _userMessage.value = "تمت إضافة المنتج $name بنجاح"
            closeCreateProduct()
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            val cleanBarcode = product.barcode.trim()
            if (cleanBarcode.isNotBlank() && repository.isBarcodeExists(cleanBarcode, excludeId = product.id)) {
                _userMessage.value = "عذراً: الباركود ($cleanBarcode) مسجل مسبقاً لمنتج آخر! لم يتم حفظ التعديل."
                return@launch
            }
            repository.updateProduct(product.copy(barcode = cleanBarcode))
            _userMessage.value = "تم تعديل بيانات المنتج ${product.name}"
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _userMessage.value = "تم حذف المنتج"
        }
    }

    fun updateStockDirect(product: ProductEntity, newStock: Int) {
        viewModelScope.launch {
            repository.updateStockDirect(product, newStock)
            _userMessage.value = "تم تحديث جرد ${product.name} إلى $newStock ${product.unit}"
        }
    }

    // Stock Movement Adjustments (تسويات الجرد، المرتجعات، التالف)
    fun recordStockAdjustment(
        product: ProductEntity,
        type: String,
        typeArabic: String,
        quantityDelta: Int,
        reason: String
    ) {
        viewModelScope.launch {
            repository.recordManualAdjustment(product, type, typeArabic, quantityDelta, reason)
            _userMessage.value = "تم تسجيل حركة المخزون ($typeArabic) بنجاح"
        }
    }

    // CRUD: Suppliers (الموردين)
    fun addSupplier(
        name: String,
        phone: String,
        address: String,
        companyName: String = "",
        initialBalance: Double = 0.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.insertSupplier(
                SupplierEntity(
                    name = name,
                    phone = phone,
                    address = address,
                    companyName = companyName,
                    outstandingBalance = initialBalance,
                    notes = notes
                )
            )
            _userMessage.value = "تمت إضافة المورد $name بنجاح"
            closeCreateSupplier()
        }
    }

    fun updateSupplier(supplier: SupplierEntity) {
        viewModelScope.launch {
            repository.updateSupplier(supplier)
            _userMessage.value = "تم تحديث بيانات المورد ${supplier.name}"
        }
    }

    fun deleteSupplier(supplier: SupplierEntity) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
            _userMessage.value = "تم حذف المورد"
        }
    }

    fun recordSupplierPayment(
        supplier: SupplierEntity,
        amount: Double,
        method: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.createSupplierPayment(
                supplierId = supplier.id,
                supplierName = supplier.name,
                amount = amount,
                paymentMethod = method,
                notes = notes
            )
            _userMessage.value = "تم تسجيل سند صرف بمبلغ $amount ${_currencySymbol.value} للمورد ${supplier.name}"
        }
    }

    fun deleteSupplierPayment(payment: SupplierPaymentEntity) {
        viewModelScope.launch {
            repository.deleteSupplierPayment(payment)
            _userMessage.value = "تم حذف سند الصرف"
        }
    }

    // CRUD: Clients
    fun addClient(name: String, phone: String, address: String, location: String = "", initialDebt: Double, notes: String = "") {
        viewModelScope.launch {
            repository.insertClient(
                ClientEntity(
                    name = name,
                    phone = phone,
                    address = address,
                    location = location,
                    currentBalance = initialDebt,
                    notes = notes
                )
            )
            _userMessage.value = "تمت إضافة العميل $name بنجاح"
            closeCreateClient()
        }
    }

    fun updateClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.updateClient(client)
            _userMessage.value = "تم تحديث بيانات العميل"
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
            _userMessage.value = "تم حذف العميل"
        }
    }

    // CRUD: Sales
    fun recordSale(
        customerName: String,
        customerPhone: String,
        items: List<Pair<ProductEntity, Int>> = emptyList(),
        subtotal: Double,
        discount: Double,
        totalAmount: Double,
        paidAmount: Double,
        isCredit: Boolean,
        paymentMethod: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val trimmedName = customerName.trim().ifBlank { "عميل نقدي" }
            val trimmedPhone = customerPhone.trim()
            val existingClients = repository.allClients.first()
            var client = existingClients.find { it.name.trim().equals(trimmedName, ignoreCase = true) }
            if (client == null) {
                val newClientId = repository.insertClient(
                    ClientEntity(
                        name = trimmedName,
                        phone = trimmedPhone,
                        currentBalance = 0.0
                    )
                )
                client = ClientEntity(
                    id = newClientId,
                    name = trimmedName,
                    phone = trimmedPhone,
                    currentBalance = 0.0
                )
            } else if (trimmedPhone.isNotBlank() && client.phone.isBlank()) {
                val updated = client.copy(phone = trimmedPhone)
                repository.updateClient(updated)
                client = updated
            }

            val savedInvoice = repository.createInvoice(
                client = client,
                items = items,
                subtotalAmount = subtotal,
                discountAmount = discount,
                totalAmount = totalAmount,
                paidAmount = paidAmount,
                isCredit = isCredit,
                paymentMethod = paymentMethod,
                notes = notes
            )
            _lastCreatedInvoice.value = savedInvoice
            _lastCreatedInvoicePhone.value = trimmedPhone.ifBlank { client.phone }
            _lastCreatedInvoiceItems.value = items
            _showInvoiceSavedDialog.value = true
            _userMessage.value = "تم حفظ وتأكيد الفاتورة بنجاح: ${savedInvoice.invoiceNumber}"
            closeCreateSale()
        }
    }

    fun recordSale(
        client: ClientEntity,
        items: List<Pair<ProductEntity, Int>>,
        subtotal: Double,
        discount: Double,
        totalAmount: Double,
        paidAmount: Double,
        isCredit: Boolean,
        paymentMethod: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val savedInvoice = repository.createInvoice(
                client = client,
                items = items,
                subtotalAmount = subtotal,
                discountAmount = discount,
                totalAmount = totalAmount,
                paidAmount = paidAmount,
                isCredit = isCredit,
                paymentMethod = paymentMethod,
                notes = notes
            )
            _lastCreatedInvoice.value = savedInvoice
            _lastCreatedInvoicePhone.value = client.phone
            _lastCreatedInvoiceItems.value = items
            _showInvoiceSavedDialog.value = true
            _userMessage.value = "تم حفظ وتأكيد الفاتورة بنجاح: ${savedInvoice.invoiceNumber}"
            closeCreateSale()
        }
    }

    fun closeInvoiceSavedDialog() {
        _showInvoiceSavedDialog.value = false
        _lastCreatedInvoice.value = null
        _lastCreatedInvoicePhone.value = ""
        _lastCreatedInvoiceItems.value = emptyList()
    }

    fun updateInvoice(invoice: SaleInvoiceEntity) {
        viewModelScope.launch {
            repository.updateInvoice(invoice)
            _userMessage.value = "تم تحديث الفاتورة ${invoice.invoiceNumber}"
        }
    }

    fun deleteInvoice(invoice: SaleInvoiceEntity) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
            _userMessage.value = "تم حذف الفاتورة"
        }
    }

    // CRUD: Purchases
    fun recordPurchase(
        supplierId: Long = 0,
        supplierName: String,
        items: List<Pair<ProductEntity, Int>>,
        subtotalAmount: Double = 0.0,
        discountAmount: Double = 0.0,
        totalAmount: Double,
        paidAmount: Double,
        paymentMethod: String = "نقداً",
        isCredit: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            repository.createPurchase(
                supplierId = supplierId,
                supplierName = supplierName,
                items = items,
                subtotalAmount = subtotalAmount,
                discountAmount = discountAmount,
                totalAmount = totalAmount,
                paidAmount = paidAmount,
                paymentMethod = paymentMethod,
                isCredit = isCredit,
                notes = notes
            )
            _userMessage.value = "تم تسجيل فاتورة الشراء وزيادة المخزون بنجاح"
            closeCreatePurchase()
        }
    }

    fun deletePurchase(purchase: PurchaseInvoiceEntity) {
        viewModelScope.launch {
            repository.deletePurchase(purchase)
            _userMessage.value = "تم حذف فاتورة الشراء"
        }
    }

    // CRUD: Receipts (سندات القبض من العملاء)
    fun recordReceipt(
        client: ClientEntity,
        amount: Double,
        paymentMethod: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.createReceipt(
                client = client,
                amount = amount,
                paymentMethod = paymentMethod,
                notes = notes
            )
            _userMessage.value = "تم تسجيل سند القبض بمبلغ $amount ${_currencySymbol.value} للعميل ${client.name}"
            closeCreateReceipt()
        }
    }

    fun deleteReceipt(receipt: PaymentReceiptEntity) {
        viewModelScope.launch {
            repository.deleteReceipt(receipt)
            _userMessage.value = "تم حذف سند القبض"
        }
    }

    // CRUD: Expenses (المصروفات التشغيلية والنثريات)
    fun recordExpense(
        title: String,
        category: String,
        amount: Double,
        paymentMethod: String = "نقداً",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.createExpense(title, category, amount, paymentMethod, notes)
            _userMessage.value = "تم تسجيل المصروف ($title) بقيمة $amount ${_currencySymbol.value} بنجاح"
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _userMessage.value = "تم حذف المصروف"
        }
    }

    // Backup & Data Management Functions
    fun getBackupSettingsData(): BackupSettingsData {
        val theme = _themeConfig.value
        val curr = _currencyConfig.value
        val notif = _notificationConfig.value
        return BackupSettingsData(
            companyName = _companyName.value,
            representativeName = _representativeName.value,
            taxNumber = _taxNumber.value,
            phone = _phone.value,
            address = _address.value,
            currencySymbol = _currencySymbol.value,
            selectedLanguage = _selectedLanguage.value,
            autoBackupEnabled = _autoBackupEnabled.value,
            autoBackupFrequency = _autoBackupFrequency.value,
            lastBackupTimestamp = _lastBackupTime.value,
            themeId = theme.activeThemeId,
            themeMode = theme.themeMode.name,
            customAccent = theme.customAccentHex ?: "",
            cardStyle = theme.cardColorStyle.name,
            textColorOption = theme.textColorOption.name,
            symbolPlacement = curr.symbolPlacement.name,
            decimalPlaces = curr.decimalPlaces,
            expirationAlertsEnabled = notif.expirationAlertsEnabled,
            expirationDaysThreshold = notif.expirationDaysThreshold,
            lowStockAlertsEnabled = notif.lowStockAlertsEnabled,
            lowStockThreshold = notif.lowStockThreshold,
            backupRemindersEnabled = notif.backupRemindersEnabled,
            backupReminderFrequency = notif.backupReminderFrequency
        )
    }

    fun loadLocalBackups() {
        _localBackups.value = repository.getLocalBackups(getApplication())
    }

    fun updateAutoBackupConfig(enabled: Boolean, frequency: String) {
        _autoBackupEnabled.value = enabled
        _autoBackupFrequency.value = frequency
        BackupManager.setAutoBackupEnabled(getApplication(), enabled)
        BackupManager.setAutoBackupFrequency(getApplication(), frequency)
        _userMessage.value = if (enabled) {
            val freqText = if (frequency == "WEEKLY") "أسبوعياً" else "يومياً"
            "تم تفعيل النسخ الاحتياطي التلقائي ($freqText)"
        } else {
            "تم إيقاف النسخ الاحتياطي التلقائي"
        }
    }

    fun createManualBackup(onFinished: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            _isBackupOperationInProgress.value = true
            _backupStatusMessage.value = "جاري إنشاء وتأمين النسخة الاحتياطية..."
            val settings = getBackupSettingsData()
            val result = repository.createBackup(getApplication(), settings, "MANUAL")
            _isBackupOperationInProgress.value = false

            result.onSuccess { info ->
                _lastBackupTime.value = info.timestamp
                loadLocalBackups()
                val msg = "تم إنشاء نسخة احتياطية آمنة بنجاح (${info.formattedSize} - ${info.recordsCount} سجل)"
                _backupStatusMessage.value = msg
                _userMessage.value = msg
                onFinished?.invoke(true, msg)
            }.onFailure { err ->
                val errorMsg = "فشل إنشاء النسخة الاحتياطية: ${err.localizedMessage ?: "خطأ غير متوقع"}"
                _backupStatusMessage.value = errorMsg
                _userMessage.value = errorMsg
                onFinished?.invoke(false, errorMsg)
            }
        }
    }

    fun restoreFromLocalBackup(backupInfo: BackupFileInfo, onFinished: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isBackupOperationInProgress.value = true
            _backupStatusMessage.value = "جاري التحقق من سلامة البيانات واستعادة النسخة..."
            val file = File(backupInfo.filePath)
            val result = repository.restoreFromFile(file)
            _isBackupOperationInProgress.value = false

            result.onSuccess { res ->
                res.settings?.let { s ->
                    _companyName.value = s.companyName
                    _representativeName.value = s.representativeName
                    _taxNumber.value = s.taxNumber
                    if (s.phone.isNotBlank()) _phone.value = s.phone
                    if (s.address.isNotBlank()) _address.value = s.address
                    _currencySymbol.value = s.currencySymbol
                    _selectedLanguage.value = s.selectedLanguage
                    _themeConfig.value = ThemeConfig(
                        activeThemeId = s.themeId,
                        themeMode = try { ThemeMode.valueOf(s.themeMode) } catch (_: Exception) { ThemeMode.LIGHT },
                        customAccentHex = if (s.customAccent.isNotBlank()) s.customAccent else null,
                        cardColorStyle = try { CardColorStyle.valueOf(s.cardStyle) } catch (_: Exception) { CardColorStyle.CLASSIC_PASTEL },
                        textColorOption = try { TextColorOption.valueOf(s.textColorOption) } catch (_: Exception) { TextColorOption.DEFAULT_NAVY }
                    )
                    _currencyConfig.value = CurrencyConfig(
                        selectedCode = s.currencySymbol,
                        customSymbol = s.currencySymbol,
                        symbolPlacement = try { SymbolPlacement.valueOf(s.symbolPlacement) } catch (_: Exception) { SymbolPlacement.AFTER_AMOUNT },
                        decimalPlaces = s.decimalPlaces
                    )
                    _notificationConfig.value = NotificationConfig(
                        expirationAlertsEnabled = s.expirationAlertsEnabled,
                        expirationDaysThreshold = s.expirationDaysThreshold,
                        lowStockAlertsEnabled = s.lowStockAlertsEnabled,
                        lowStockThreshold = s.lowStockThreshold,
                        backupRemindersEnabled = s.backupRemindersEnabled,
                        backupReminderFrequency = s.backupReminderFrequency
                    )
                }
                loadLocalBackups()
                val msg = "تمت استعادة النسخة الاحتياطية بنجاح (${res.totalRecords} سجل)"
                _backupStatusMessage.value = msg
                _userMessage.value = msg
                onFinished(true, msg)
            }.onFailure { err ->
                val errorMsg = "فشل استعادة النسخة: ${err.localizedMessage ?: "بيانات غير صالحة"}"
                _backupStatusMessage.value = errorMsg
                _userMessage.value = errorMsg
                onFinished(false, errorMsg)
            }
        }
    }

    fun restoreFromInputStream(inputStream: java.io.InputStream, onFinished: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isBackupOperationInProgress.value = true
            _backupStatusMessage.value = "جاري فحص الملف واستعادة البيانات..."
            val result = repository.restoreFromInputStream(inputStream)
            _isBackupOperationInProgress.value = false

            result.onSuccess { res ->
                res.settings?.let { s ->
                    _companyName.value = s.companyName
                    _representativeName.value = s.representativeName
                    _taxNumber.value = s.taxNumber
                    if (s.phone.isNotBlank()) _phone.value = s.phone
                    if (s.address.isNotBlank()) _address.value = s.address
                    _currencySymbol.value = s.currencySymbol
                    _selectedLanguage.value = s.selectedLanguage
                    _themeConfig.value = ThemeConfig(
                        activeThemeId = s.themeId,
                        themeMode = try { ThemeMode.valueOf(s.themeMode) } catch (_: Exception) { ThemeMode.LIGHT },
                        customAccentHex = if (s.customAccent.isNotBlank()) s.customAccent else null,
                        cardColorStyle = try { CardColorStyle.valueOf(s.cardStyle) } catch (_: Exception) { CardColorStyle.CLASSIC_PASTEL },
                        textColorOption = try { TextColorOption.valueOf(s.textColorOption) } catch (_: Exception) { TextColorOption.DEFAULT_NAVY }
                    )
                    _currencyConfig.value = CurrencyConfig(
                        selectedCode = s.currencySymbol,
                        customSymbol = s.currencySymbol,
                        symbolPlacement = try { SymbolPlacement.valueOf(s.symbolPlacement) } catch (_: Exception) { SymbolPlacement.AFTER_AMOUNT },
                        decimalPlaces = s.decimalPlaces
                    )
                    _notificationConfig.value = NotificationConfig(
                        expirationAlertsEnabled = s.expirationAlertsEnabled,
                        expirationDaysThreshold = s.expirationDaysThreshold,
                        lowStockAlertsEnabled = s.lowStockAlertsEnabled,
                        lowStockThreshold = s.lowStockThreshold,
                        backupRemindersEnabled = s.backupRemindersEnabled,
                        backupReminderFrequency = s.backupReminderFrequency
                    )
                }
                loadLocalBackups()
                val msg = "تم التحقق واستعادة البيانات بنجاح (${res.totalRecords} سجل)"
                _backupStatusMessage.value = msg
                _userMessage.value = msg
                onFinished(true, msg)
            }.onFailure { err ->
                val errorMsg = "فشل استيراد الملف: ${err.localizedMessage ?: "بيانات غير صالحة"}"
                _backupStatusMessage.value = errorMsg
                _userMessage.value = errorMsg
                onFinished(false, errorMsg)
            }
        }
    }

    fun deleteLocalBackup(backupInfo: BackupFileInfo) {
        viewModelScope.launch {
            val deleted = repository.deleteBackupFile(backupInfo.filePath)
            if (deleted) {
                loadLocalBackups()
                _userMessage.value = "تم حذف ملف النسخة الاحتياطية"
            }
        }
    }

    // -------------------------------------------------------------
    // Cloud Backup Integration (Firebase Firestore & Google Sign-In)
    // -------------------------------------------------------------

    fun loadCloudBackups() {
        if (!firebaseBackupService.isUserSignedIn()) {
            _cloudBackups.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isCloudOperationInProgress.value = true
            val result = firebaseBackupService.getCloudBackupsList()
            _isCloudOperationInProgress.value = false
            result.onSuccess { list ->
                _cloudBackups.value = list
            }.onFailure { err ->
                _cloudStatusMessage.value = "تعذر مزامنة النسخ السحابية: ${err.localizedMessage ?: "تحقق من اتصال الإنترنت"}"
            }
        }
    }

    fun signInToCloudWithGoogle(activityContext: android.content.Context, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isCloudOperationInProgress.value = true
            _cloudStatusMessage.value = "جاري الاتصال بـ Google..."
            val result = firebaseBackupService.signInWithGoogle(activityContext)
            _isCloudOperationInProgress.value = false
            result.onSuccess { user ->
                _isCloudSignedIn.value = true
                _cloudUserEmail.value = user.email ?: user.displayName
                val msg = "تم تسجيل الدخول بنجاح بحساب Google (${user.email ?: user.displayName})"
                _cloudStatusMessage.value = msg
                _userMessage.value = msg
                loadCloudBackups()
                onResult(true, msg)
            }.onFailure { err ->
                val errorMsg = "فشل تسجيل الدخول: ${err.localizedMessage ?: "تعذر إكمال تسجيل الدخول"}"
                _cloudStatusMessage.value = errorMsg
                _userMessage.value = errorMsg
                onResult(false, errorMsg)
            }
        }
    }

    fun signOutFromCloud() {
        firebaseBackupService.signOut()
        _isCloudSignedIn.value = false
        _cloudUserEmail.value = null
        _cloudBackups.value = emptyList()
        val msg = "تم تسجيل الخروج من الحساب السحابي"
        _cloudStatusMessage.value = msg
        _userMessage.value = msg
    }

    fun uploadCloudBackup(customTitle: String? = null, onResult: ((Boolean, String) -> Unit)? = null) {
        if (!firebaseBackupService.isUserSignedIn()) {
            val msg = "يجب تسجيل الدخول باستخدام حساب Google أولاً للنسخ السحابي"
            _cloudStatusMessage.value = msg
            _userMessage.value = msg
            onResult?.invoke(false, msg)
            return
        }
        viewModelScope.launch {
            _isCloudOperationInProgress.value = true
            _cloudStatusMessage.value = "جاري رفع وحفظ نسخة احتياطية على سحابة Firebase..."
            val settings = getBackupSettingsData()
            val appDao = AppDatabase.getDatabase(getApplication()).appDao()
            val result = firebaseBackupService.uploadCloudBackup(
                appDao = appDao,
                settings = settings,
                backupType = "CLOUD",
                customTitle = customTitle
            )
            _isCloudOperationInProgress.value = false
            result.onSuccess { record ->
                loadCloudBackups()
                val msg = "Backup completed successfully. (تم إنشاء النسخة الاحتياطية بنجاح • ${record.formattedDate})"
                _cloudStatusMessage.value = msg
                _userMessage.value = msg
                onResult?.invoke(true, msg)
            }.onFailure { err ->
                val errorMsg = "فشل رفع النسخة السحابية: ${err.localizedMessage ?: "تحقق من اتصال الإنترنت"}"
                _cloudStatusMessage.value = errorMsg
                _userMessage.value = errorMsg
                onResult?.invoke(false, errorMsg)
            }
        }
    }

    fun restoreFromCloudBackup(backup: CloudBackupRecord, onFinished: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isCloudOperationInProgress.value = true
            _cloudStatusMessage.value = "جاري استعادة النسخة السحابية وتحديث قاعدة البيانات المحلية..."
            val appDao = AppDatabase.getDatabase(getApplication()).appDao()
            val result = firebaseBackupService.restoreFromCloudBackup(backup, appDao)
            _isCloudOperationInProgress.value = false

            result.onSuccess { res ->
                res.settings?.let { s ->
                    _companyName.value = s.companyName
                    _representativeName.value = s.representativeName
                    _taxNumber.value = s.taxNumber
                    if (s.phone.isNotBlank()) _phone.value = s.phone
                    if (s.address.isNotBlank()) _address.value = s.address
                    _currencySymbol.value = s.currencySymbol
                    _selectedLanguage.value = s.selectedLanguage
                    _themeConfig.value = ThemeConfig(
                        activeThemeId = s.themeId,
                        themeMode = try { ThemeMode.valueOf(s.themeMode) } catch (_: Exception) { ThemeMode.LIGHT },
                        customAccentHex = if (s.customAccent.isNotBlank()) s.customAccent else null,
                        cardColorStyle = try { CardColorStyle.valueOf(s.cardStyle) } catch (_: Exception) { CardColorStyle.CLASSIC_PASTEL },
                        textColorOption = try { TextColorOption.valueOf(s.textColorOption) } catch (_: Exception) { TextColorOption.DEFAULT_NAVY }
                    )
                    _currencyConfig.value = CurrencyConfig(
                        selectedCode = s.currencySymbol,
                        customSymbol = s.currencySymbol,
                        symbolPlacement = try { SymbolPlacement.valueOf(s.symbolPlacement) } catch (_: Exception) { SymbolPlacement.AFTER_AMOUNT },
                        decimalPlaces = s.decimalPlaces
                    )
                    _notificationConfig.value = NotificationConfig(
                        expirationAlertsEnabled = s.expirationAlertsEnabled,
                        expirationDaysThreshold = s.expirationDaysThreshold,
                        lowStockAlertsEnabled = s.lowStockAlertsEnabled,
                        lowStockThreshold = s.lowStockThreshold,
                        backupRemindersEnabled = s.backupRemindersEnabled,
                        backupReminderFrequency = s.backupReminderFrequency
                    )
                }
                loadLocalBackups()
                loadCloudBackups()
                val msg = "Backup restored successfully. (تمت استعادة النسخة الاحتياطية بنجاح • ${res.totalRecords} سجل)"
                _cloudStatusMessage.value = msg
                _userMessage.value = msg
                onFinished(true, msg)
            }.onFailure { err ->
                val errorMsg = "فشل استعادة النسخة السحابية: ${err.localizedMessage ?: "بيانات غير صالحة"}"
                _cloudStatusMessage.value = errorMsg
                _userMessage.value = errorMsg
                onFinished(false, errorMsg)
            }
        }
    }

    fun deleteCloudBackup(backup: CloudBackupRecord) {
        viewModelScope.launch {
            _isCloudOperationInProgress.value = true
            val result = firebaseBackupService.deleteCloudBackup(backup.id)
            _isCloudOperationInProgress.value = false
            result.onSuccess {
                loadCloudBackups()
                val msg = "تم حذف النسخة السحابية (${backup.title})"
                _cloudStatusMessage.value = msg
                _userMessage.value = msg
            }.onFailure { err ->
                val errorMsg = "فشل حذف النسخة السحابية: ${err.localizedMessage ?: "خطأ غير متوقع"}"
                _cloudStatusMessage.value = errorMsg
                _userMessage.value = errorMsg
            }
        }
    }

    // Data Management: Clear Test Transactions Only
    fun clearTestTransactionsOnly() {
        viewModelScope.launch {
            repository.clearTestTransactionsOnly()
            _userMessage.value = "تم مسح حركات الفواتير والعمليات التجريبية بنجاح، مع الإبقاء على قائمة المنتجات والعملاء"
        }
    }

    // Data Management: Reset Default Settings
    fun resetSettingsToDefault() {
        viewModelScope.launch {
            _companyName.value = "مؤسسة التوزيع والتجارة الحديثة"
            _representativeName.value = "أحمد الشمري (مبيعات 102)"
            _taxNumber.value = "300123456700003"
            _phone.value = "0550001234"
            _address.value = "الرياض - المملكة العربية السعودية"
            _currencySymbol.value = "﷼"
            _selectedLanguage.value = "العربية"
            _selectedLanguageCode.value = "ar"
            _themeConfig.value = ThemeConfig()
            _currencyConfig.value = CurrencyConfig()
            repository.saveCurrencySetting(CurrencyConfig().toEntity())
            repository.saveLanguageSetting(
                LanguageSettingEntity(
                    selectedCode = "ar",
                    nameNative = "العربية",
                    nameAr = "العربية",
                    nameEn = "Arabic",
                    isRtl = true,
                    flagEmoji = "🇸🇦"
                )
            )
            _notificationConfig.value = NotificationConfig()
            updateAutoBackupConfig(true, "DAILY")
            _userMessage.value = "تمت استعادة الإعدادات الافتراضية للتطبيق بنجاح"
        }
    }

    // Data Management: Reset To Demo Data
    fun resetToDemoData() {
        viewModelScope.launch {
            repository.clearAllData()
            repository.populateSampleDataIfEmpty(0)
            _userMessage.value = "تمت استعادة البيانات التجريبية النموذجية بنجاح"
        }
    }

    // Data Management: Clear All Data
    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _userMessage.value = "تم تفريغ قاعدة البيانات بالكامل بنجاح"
        }
    }

    suspend fun exportAllDataCsv(): File? {
        return repository.exportAllDataCsv(getApplication(), _currencySymbol.value)
    }
}
