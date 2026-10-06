package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MandoubakBackground
import com.example.ui.theme.MandoubakBlue
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextSecondary
import com.example.util.CurrencyConfig
import com.example.util.CurrencyItem
import com.example.util.CurrencyRepository
import com.example.util.SymbolPlacement

/**
 * Currency Management Screen:
 * - One search box at top (searches Country, Currency name, ISO Code, Symbol).
 * - Only ONE section called "All Currencies".
 * - Every official currency in the world displayed inside one single continuous vertical list.
 * - No categories, no tabs, no horizontal scrolling.
 */
@Composable
fun CurrenciesScreen(
    currencyConfig: CurrencyConfig,
    onSelectCurrency: (CurrencyItem) -> Unit,
    onUpdateConfig: (CurrencyConfig) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var searchQuery by remember { mutableStateOf("") }
    var showFormatOptions by remember { mutableStateOf(false) }

    val allCurrencies = CurrencyRepository.supportedCurrencies
    val filteredCurrencies = remember(searchQuery) {
        CurrencyRepository.searchCurrencies(searchQuery)
    }

    val activeCurrency = remember(currencyConfig.selectedCode) {
        CurrencyRepository.findByCode(currencyConfig.selectedCode)
            ?: CurrencyRepository.supportedCurrencies.first()
    }

    val samplePrice = 1250.50
    val sampleFormatted = CurrencyRepository.format(samplePrice, currencyConfig)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("currencies_back_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = MandoubakNavy
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = MandoubakBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "دليل العملات (Currencies)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MandoubakNavy
                            )
                            Text(
                                text = "اختيار وتخصيص العملة المعتمدة للمنظومة",
                                fontSize = 11.5.sp,
                                color = MandoubakTextSecondary
                            )
                        }

                        IconButton(
                            onClick = { showFormatOptions = !showFormatOptions }
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "خيارات التنسيق",
                                tint = if (showFormatOptions) MandoubakBlue else MandoubakTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Single Search input at top (searching by country, currency name, code, symbol)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "بحث بالدولة، اسم العملة، الكود (USD, SAR, YER)، أو الرمز...",
                                fontSize = 12.sp,
                                color = MandoubakTextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "بحث",
                                modifier = Modifier.size(18.dp),
                                tint = MandoubakTextSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "مسح البحث",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MandoubakBlue,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("currency_search_box")
                    )
                }
            }
        },
        containerColor = MandoubakBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Currently Active Default Currency Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.5.dp, RoundedCornerShape(16.dp))
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
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDCFCE7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "العملة المعتمدة حالياً في المنظومة",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MandoubakNavy)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = activeCurrency.code,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(activeCurrency.flagEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeCurrency.displayCountry,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = MandoubakNavy
                                )
                                Text(
                                    text = "${activeCurrency.nameAr} (${activeCurrency.nameEn})",
                                    fontSize = 12.sp,
                                    color = MandoubakTextSecondary
                                )
                            }
                            Text(
                                text = currencyConfig.customSymbol,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MandoubakBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live formatted preview box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "معاينة السعر في الفواتير والتقارير:",
                                    fontSize = 11.5.sp,
                                    color = MandoubakTextSecondary
                                )
                                Text(
                                    text = sampleFormatted,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MandoubakNavy
                                )
                            }
                        }
                    }
                }
            }

            // Optional Formatting Settings panel (collapsed/expanded)
            if (showFormatOptions) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, MandoubakBlue.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "خيارات تنسيق العملة المعتمدة:",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MandoubakNavy
                            )

                            // Placement
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = currencyConfig.symbolPlacement == SymbolPlacement.AFTER_AMOUNT,
                                    onClick = {
                                        onUpdateConfig(currencyConfig.copy(symbolPlacement = SymbolPlacement.AFTER_AMOUNT))
                                    },
                                    label = { Text("بعد المبلغ (100 ${currencyConfig.customSymbol})", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = currencyConfig.symbolPlacement == SymbolPlacement.BEFORE_AMOUNT,
                                    onClick = {
                                        onUpdateConfig(currencyConfig.copy(symbolPlacement = SymbolPlacement.BEFORE_AMOUNT))
                                    },
                                    label = { Text("قبل المبلغ (${currencyConfig.customSymbol} 100)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Decimals
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(0, 2, 3).forEach { d ->
                                    FilterChip(
                                        selected = currencyConfig.decimalPlaces == d,
                                        onClick = {
                                            onUpdateConfig(currencyConfig.copy(decimalPlaces = d))
                                        },
                                        label = { Text(if (d == 0) "بدون كسور" else "$d خانات", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ONLY ONE SECTION: "All Currencies"
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All Currencies",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MandoubakNavy
                    )
                    Text(
                        text = "${filteredCurrencies.size} / ${allCurrencies.size}",
                        fontSize = 12.sp,
                        color = MandoubakTextSecondary
                    )
                }
            }

            // One single continuous list of every official currency in the world
            items(
                items = filteredCurrencies,
                key = { it.code }
            ) { item ->
                val isSelected = currencyConfig.selectedCode == item.code

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) MandoubakBlue else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectCurrency(item) }
                        .testTag("currency_item_${item.code}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Radio indicator + Flag + Country & Currency details
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isSelected) "العملة المحددة" else "اختيار",
                                tint = if (isSelected) MandoubakBlue else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = item.flagEmoji,
                                fontSize = 24.sp
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                // Country Name
                                Text(
                                    text = item.displayCountry,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = MandoubakNavy
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                // Currency Name
                                Text(
                                    text = "${item.nameAr} (${item.nameEn})",
                                    fontSize = 11.5.sp,
                                    color = MandoubakTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // ISO Code & Symbol
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            // Symbol
                            Text(
                                text = item.defaultSymbol,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isSelected) MandoubakBlue else MandoubakNavy
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // ISO Currency Code badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) MandoubakBlue else Color(0xFFF1F5F9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.code,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MandoubakNavy
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
