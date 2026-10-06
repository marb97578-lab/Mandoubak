package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.BannerCard
import com.example.ui.components.CategoryCardsRow
import com.example.ui.components.MandoubakBackgroundDecorator
import com.example.ui.components.MandoubakTopBar
import com.example.ui.components.MotivationalFooter
import com.example.ui.components.TipCard
import com.example.ui.components.TodayStatsCard
import com.example.ui.theme.MandoubakBackground

@Composable
fun HomeScreen(
    todaySalesCount: Int,
    todayClientsCount: Int,
    lowStockCount: Int,
    todayReceiptsCount: Int,
    userMessage: String?,
    onClearUserMessage: () -> Unit,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSalesClick: () -> Unit,
    onClientsClick: () -> Unit,
    onProductsClick: () -> Unit,
    onReceiptClick: () -> Unit,
    onPurchasesClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onBannerClick: () -> Unit,
    onLowStockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearUserMessage()
        }
    }

    Scaffold(
        containerColor = MandoubakBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MandoubakTopBar(
                onMenuClick = onMenuClick,
                onSearchClick = onSearchClick,
                onNotificationClick = onNotificationClick,
                hasNotifications = lowStockCount > 0
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Subtle pastel curved waves in background matching reference
            MandoubakBackgroundDecorator()

            // Dashboard Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. Six Organized Feature Cards: Sales | Customers | Products | Receipts | Purchases | Inventory
                CategoryCardsRow(
                    onSalesClick = onSalesClick,
                    onClientsClick = onClientsClick,
                    onProductsClick = onProductsClick,
                    onReceiptClick = onReceiptClick,
                    onPurchasesClick = onPurchasesClick,
                    onInventoryClick = onInventoryClick
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Middle Banner Card: "أسرع طريق لعملك" with 3D Illustration
                BannerCard(
                    onBannerClick = onBannerClick
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Tip of the day Card: "نصيحة اليوم"
                TipCard()

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Today's Statistics Card (4 columns): مبيعات اليوم | عملاء اليوم | منتجات منخفضة | سندات اليوم
                TodayStatsCard(
                    todaySalesCount = todaySalesCount,
                    todayClientsCount = todayClientsCount,
                    lowStockCount = lowStockCount,
                    todayReceiptsCount = todayReceiptsCount,
                    onSalesClick = onSalesClick,
                    onClientsClick = onClientsClick,
                    onLowStockClick = onLowStockClick,
                    onReceiptsClick = onReceiptClick
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Motivational Slogan & Underline: "بخطوة واحدة .. نصل إلى أهدافك"
                MotivationalFooter()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
