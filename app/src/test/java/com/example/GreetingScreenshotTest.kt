package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MandoubakTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MandoubakTheme {
        HomeScreen(
          todaySalesCount = 3,
          todayClientsCount = 4,
          lowStockCount = 2,
          todayReceiptsCount = 1,
          userMessage = null,
          onClearUserMessage = {},
          onMenuClick = {},
          onSearchClick = {},
          onNotificationClick = {},
          onSalesClick = {},
          onClientsClick = {},
          onProductsClick = {},
          onReceiptClick = {},
          onPurchasesClick = {},
          onInventoryClick = {},
          onBannerClick = {},
          onLowStockClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
