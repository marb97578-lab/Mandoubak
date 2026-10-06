package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MandoubakTextSecondary
import com.example.ui.theme.StatBlueBg
import com.example.ui.theme.StatBlueText
import com.example.ui.theme.StatGreenBg
import com.example.ui.theme.StatGreenText
import com.example.ui.theme.StatPurpleBg
import com.example.ui.theme.StatPurpleText
import com.example.ui.theme.StatReceiptBg
import com.example.ui.theme.StatReceiptText

@Composable
fun TodayStatsCard(
    todaySalesCount: Int,
    todayClientsCount: Int,
    lowStockCount: Int,
    todayReceiptsCount: Int,
    onSalesClick: () -> Unit,
    onClientsClick: () -> Unit,
    onLowStockClick: () -> Unit,
    onReceiptsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), clip = false)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(width = 1.dp, color = Color(0xFFE2E8F0), shape = RoundedCornerShape(22.dp))
            .padding(vertical = 16.dp, horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: مبيعات اليوم (Today's Sales)
            StatColumn(
                pillBg = StatPurpleBg,
                count = todaySalesCount.toString(),
                countColor = StatPurpleText,
                label = "مبيعات اليوم",
                testTag = "stat_today_sales",
                onClick = onSalesClick,
                modifier = Modifier.weight(1f)
            ) {
                StatWalletIcon(color = StatPurpleText)
            }

            StatDivider()

            // Column 2: عملاء اليوم (Today's Clients)
            StatColumn(
                pillBg = StatBlueBg,
                count = todayClientsCount.toString(),
                countColor = StatBlueText,
                label = "عملاء اليوم",
                testTag = "stat_today_clients",
                onClick = onClientsClick,
                modifier = Modifier.weight(1f)
            ) {
                StatUsersIcon(color = StatBlueText)
            }

            StatDivider()

            // Column 3: منتجات منخفضة (Low Stock Products)
            StatColumn(
                pillBg = StatGreenBg,
                count = lowStockCount.toString(),
                countColor = StatGreenText,
                label = "منتجات منخفضة",
                testTag = "stat_low_stock",
                onClick = onLowStockClick,
                modifier = Modifier.weight(1f)
            ) {
                StatBoxIcon(color = StatGreenText)
            }

            StatDivider()

            // Column 4: سندات اليوم (Today's Receipts)
            StatColumn(
                pillBg = StatReceiptBg,
                count = todayReceiptsCount.toString(),
                countColor = StatReceiptText,
                label = "سندات اليوم",
                testTag = "stat_today_receipts",
                onClick = onReceiptsClick,
                modifier = Modifier.weight(1f)
            ) {
                StatReceiptDocumentIcon(color = StatReceiptText)
            }
        }
    }
}

@Composable
fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(48.dp)
            .background(Color(0xFFF1F5F9))
    )
}

@Composable
fun StatColumn(
    pillBg: Color,
    count: String,
    countColor: Color,
    label: String,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pill Icon Container
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(pillBg),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Big Bold Count
        Text(
            text = count,
            color = countColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Label
        Text(
            text = label,
            color = MandoubakTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// Crisp Vector Icons for Stats matching the screenshot:

@Composable
fun StatWalletIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()

        val bodyW = w * 0.78f
        val bodyH = h * 0.60f
        val left = (w - bodyW) / 2f
        val top = (h - bodyH) / 2f + 1.dp.toPx()

        drawRoundRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
            style = Stroke(width = stroke)
        )

        // Wallet flap
        val flapW = w * 0.35f
        val flapH = bodyH * 0.50f
        drawRoundRect(
            color = color,
            topLeft = Offset(left + bodyW - flapW * 0.8f, top + (bodyH - flapH) / 2f),
            size = Size(flapW, flapH),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = stroke)
        )

        // Lock dot
        drawCircle(
            color = color,
            radius = 1.6.dp.toPx(),
            center = Offset(left + bodyW - flapW * 0.35f, top + bodyH / 2f)
        )
    }
}

@Composable
fun StatUsersIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()

        // Two people icons
        val headCenterX = w * 0.52f
        val headCenterY = h * 0.36f
        val headRadius = 4.dp.toPx()
        drawCircle(
            color = color,
            radius = headRadius,
            center = Offset(headCenterX, headCenterY),
            style = Stroke(width = stroke)
        )

        val bodyPath = Path().apply {
            moveTo(w * 0.28f, h * 0.78f)
            quadraticTo(headCenterX, h * 0.55f, w * 0.76f, h * 0.78f)
        }
        drawPath(path = bodyPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
    }
}

@Composable
fun StatBoxIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()
        val centerX = w * 0.5f

        val topY = h * 0.22f
        val rightX = w * 0.82f
        val leftX = w * 0.18f
        val midY = h * 0.40f
        val bottomY = h * 0.80f
        val centerY = h * 0.58f

        val boxPath = Path().apply {
            moveTo(centerX, topY)
            lineTo(rightX, midY)
            lineTo(rightX, bottomY - (midY - topY))
            lineTo(centerX, bottomY)
            lineTo(leftX, bottomY - (midY - topY))
            lineTo(leftX, midY)
            close()
        }
        drawPath(path = boxPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(color = color, start = Offset(centerX, centerY), end = Offset(centerX, bottomY), strokeWidth = stroke)
        drawLine(color = color, start = Offset(centerX, centerY), end = Offset(rightX, midY), strokeWidth = stroke)
        drawLine(color = color, start = Offset(centerX, centerY), end = Offset(leftX, midY), strokeWidth = stroke)
    }
}

@Composable
fun StatReceiptDocumentIcon(color: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()

        val docPath = Path().apply {
            moveTo(w * 0.28f, h * 0.18f)
            lineTo(w * 0.72f, h * 0.18f)
            lineTo(w * 0.72f, h * 0.82f)
            lineTo(w * 0.28f, h * 0.82f)
            close()
        }
        drawPath(path = docPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Lines on receipt
        drawLine(color = color, start = Offset(w * 0.40f, h * 0.36f), end = Offset(w * 0.60f, h * 0.36f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.40f, h * 0.50f), end = Offset(w * 0.60f, h * 0.50f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.40f, h * 0.64f), end = Offset(w * 0.54f, h * 0.64f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}
