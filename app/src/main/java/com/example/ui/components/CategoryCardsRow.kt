package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.CardClientsAccent
import com.example.ui.theme.CardClientsBg
import com.example.ui.theme.CardClientsPill
import com.example.ui.theme.CardInventoryAccent
import com.example.ui.theme.CardInventoryBg
import com.example.ui.theme.CardInventoryPill
import com.example.ui.theme.CardProductsAccent
import com.example.ui.theme.CardProductsBg
import com.example.ui.theme.CardProductsPill
import com.example.ui.theme.CardPurchasesAccent
import com.example.ui.theme.CardPurchasesBg
import com.example.ui.theme.CardPurchasesPill
import com.example.ui.theme.CardReceiptAccent
import com.example.ui.theme.CardReceiptBg
import com.example.ui.theme.CardReceiptPill
import com.example.ui.theme.CardSalesAccent
import com.example.ui.theme.CardSalesBg
import com.example.ui.theme.CardSalesPill
import com.example.ui.theme.MandoubakTextPrimary
import com.example.ui.theme.MandoubakTextSecondary

@Composable
fun CategoryCardsRow(
    onSalesClick: () -> Unit,
    onClientsClick: () -> Unit,
    onProductsClick: () -> Unit,
    onReceiptClick: () -> Unit,
    onPurchasesClick: () -> Unit,
    onInventoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Sales | Customers | Products
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card 1: المبيعات
            CategoryCard(
                title = "المبيعات",
                bgColor = CardSalesBg,
                pillColor = CardSalesPill,
                accentColor = CardSalesAccent,
                testTag = "card_sales",
                onClick = onSalesClick,
                modifier = Modifier.weight(1f)
            ) {
                SalesCartIcon(color = CardSalesAccent)
            }

            // Card 2: العملاء
            CategoryCard(
                title = "العملاء",
                bgColor = CardClientsBg,
                pillColor = CardClientsPill,
                accentColor = CardClientsAccent,
                testTag = "card_clients",
                onClick = onClientsClick,
                modifier = Modifier.weight(1f)
            ) {
                ClientsTeamIcon(color = CardClientsAccent)
            }

            // Card 3: المنتجات
            CategoryCard(
                title = "المنتجات",
                bgColor = CardProductsBg,
                pillColor = CardProductsPill,
                accentColor = CardProductsAccent,
                testTag = "card_products",
                onClick = onProductsClick,
                modifier = Modifier.weight(1f)
            ) {
                ProductBoxIcon(color = CardProductsAccent)
            }
        }

        // Row 2: Receipts | Purchases | Inventory
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card 4: سند القبض
            CategoryCard(
                title = "سند القبض",
                bgColor = CardReceiptBg,
                pillColor = CardReceiptPill,
                accentColor = CardReceiptAccent,
                testTag = "card_receipt",
                onClick = onReceiptClick,
                modifier = Modifier.weight(1f)
            ) {
                ReceiptVoucherIcon(color = CardReceiptAccent)
            }

            // Card 5: المشتريات
            CategoryCard(
                title = "المشتريات",
                bgColor = CardPurchasesBg,
                pillColor = CardPurchasesPill,
                accentColor = CardPurchasesAccent,
                testTag = "card_purchases",
                onClick = onPurchasesClick,
                modifier = Modifier.weight(1f)
            ) {
                PurchasesBagIcon(color = CardPurchasesAccent)
            }

            // Card 6: المخزون والجرد
            CategoryCard(
                title = "المخزون",
                bgColor = CardInventoryBg,
                pillColor = CardInventoryPill,
                accentColor = CardInventoryAccent,
                testTag = "card_inventory",
                onClick = onInventoryClick,
                modifier = Modifier.weight(1f)
            ) {
                InventoryAuditIcon(color = CardInventoryAccent)
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    bgColor: Color,
    pillColor: Color,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .height(126.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
            .clickable { onClick() }
            .testTag(testTag)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon Pill Container
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(pillColor),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }

        // Card Title
        Text(
            text = title,
            color = MandoubakTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        // Chevron Forward Arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "الانتقال إلى $title",
            tint = MandoubakTextSecondary,
            modifier = Modifier.size(17.dp)
        )
    }
}

// Crisp Vector Icons:

@Composable
fun ReceiptVoucherIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.2.dp.toPx()

        // Document outline
        val docPath = Path().apply {
            moveTo(w * 0.25f, h * 0.15f)
            lineTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.78f, h * 0.32f)
            lineTo(w * 0.78f, h * 0.85f)
            lineTo(w * 0.25f, h * 0.85f)
            close()
        }
        drawPath(
            path = docPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Fold corner
        val foldPath = Path().apply {
            moveTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.65f, h * 0.32f)
            lineTo(w * 0.78f, h * 0.32f)
        }
        drawPath(path = foldPath, color = color, style = Stroke(width = stroke))

        // Horizontal lines inside
        drawLine(
            color = color,
            start = Offset(w * 0.38f, h * 0.45f),
            end = Offset(w * 0.65f, h * 0.45f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.38f, h * 0.60f),
            end = Offset(w * 0.58f, h * 0.60f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        // Small circle with '+' at bottom right
        val circleCenter = Offset(w * 0.75f, h * 0.75f)
        val circleRadius = 5.dp.toPx()
        drawCircle(
            color = Color.White,
            radius = circleRadius + 1.dp.toPx(),
            center = circleCenter
        )
        drawCircle(
            color = color,
            radius = circleRadius,
            center = circleCenter
        )
        // White '+'
        drawLine(
            color = Color.White,
            start = Offset(circleCenter.x - 2.5.dp.toPx(), circleCenter.y),
            end = Offset(circleCenter.x + 2.5.dp.toPx(), circleCenter.y),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(circleCenter.x, circleCenter.y - 2.5.dp.toPx()),
            end = Offset(circleCenter.x, circleCenter.y + 2.5.dp.toPx()),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ProductBoxIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.2.dp.toPx()
        val centerX = w * 0.5f
        val centerY = h * 0.5f

        val topVertex = Offset(centerX, h * 0.20f)
        val rightVertex = Offset(w * 0.85f, h * 0.38f)
        val bottomVertex = Offset(centerX, h * 0.82f)
        val leftVertex = Offset(w * 0.15f, h * 0.38f)
        val centerVertex = Offset(centerX, centerY * 1.05f)

        val boxPath = Path().apply {
            moveTo(topVertex.x, topVertex.y)
            lineTo(rightVertex.x, rightVertex.y)
            lineTo(rightVertex.x, rightVertex.y + h * 0.26f)
            lineTo(bottomVertex.x, bottomVertex.y)
            lineTo(leftVertex.x, leftVertex.y + h * 0.26f)
            lineTo(leftVertex.x, leftVertex.y)
            close()
        }
        drawPath(
            path = boxPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        drawLine(color = color, start = centerVertex, end = Offset(centerX, bottomVertex.y), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = color, start = centerVertex, end = topVertex, strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = color, start = centerVertex, end = rightVertex, strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = color, start = centerVertex, end = leftVertex, strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

@Composable
fun ClientsTeamIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.2.dp.toPx()

        val headCenterX = w * 0.55f
        val headCenterY = h * 0.36f
        val headRadius = 4.2.dp.toPx()
        drawCircle(
            color = color,
            radius = headRadius,
            center = Offset(headCenterX, headCenterY),
            style = Stroke(width = stroke)
        )

        val bodyPath = Path().apply {
            moveTo(w * 0.32f, h * 0.78f)
            quadraticTo(headCenterX, h * 0.54f, w * 0.78f, h * 0.78f)
        }
        drawPath(
            path = bodyPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        val head2X = w * 0.34f
        val head2Y = h * 0.32f
        drawCircle(
            color = color,
            radius = 3.4.dp.toPx(),
            center = Offset(head2X, head2Y),
            style = Stroke(width = stroke * 0.9f)
        )

        val body2Path = Path().apply {
            moveTo(w * 0.18f, h * 0.70f)
            quadraticTo(w * 0.28f, h * 0.52f, w * 0.40f, h * 0.58f)
        }
        drawPath(
            path = body2Path,
            color = color,
            style = Stroke(width = stroke * 0.9f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SalesCartIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.2.dp.toPx()

        val cartPath = Path().apply {
            moveTo(w * 0.16f, h * 0.26f)
            lineTo(w * 0.28f, h * 0.26f)
            lineTo(w * 0.38f, h * 0.62f)
            lineTo(w * 0.80f, h * 0.62f)
            lineTo(w * 0.86f, h * 0.35f)
            lineTo(w * 0.30f, h * 0.35f)
        }
        drawPath(
            path = cartPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val wheelRadius = 2.2.dp.toPx()
        drawCircle(color = color, radius = wheelRadius, center = Offset(w * 0.42f, h * 0.76f))
        drawCircle(color = color, radius = wheelRadius, center = Offset(w * 0.74f, h * 0.76f))
    }
}

@Composable
fun PurchasesBagIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.2.dp.toPx()

        // Shopping / delivery bag
        val bagPath = Path().apply {
            moveTo(w * 0.24f, h * 0.34f)
            lineTo(w * 0.76f, h * 0.34f)
            lineTo(w * 0.82f, h * 0.82f)
            lineTo(w * 0.18f, h * 0.82f)
            close()
        }
        drawPath(
            path = bagPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Handle arc
        val handlePath = Path().apply {
            moveTo(w * 0.38f, h * 0.34f)
            cubicTo(
                w * 0.38f, h * 0.16f,
                w * 0.62f, h * 0.16f,
                w * 0.62f, h * 0.34f
            )
        }
        drawPath(
            path = handlePath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        // Tag or check icon on bag
        drawLine(
            color = color,
            start = Offset(w * 0.42f, h * 0.58f),
            end = Offset(w * 0.50f, h * 0.66f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.50f, h * 0.66f),
            end = Offset(w * 0.62f, h * 0.50f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun InventoryAuditIcon(color: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.2.dp.toPx()

        // Clipboard outline
        val clipPath = Path().apply {
            moveTo(w * 0.22f, h * 0.22f)
            lineTo(w * 0.78f, h * 0.22f)
            lineTo(w * 0.78f, h * 0.84f)
            lineTo(w * 0.22f, h * 0.84f)
            close()
        }
        drawPath(
            path = clipPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Clip on top
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.36f, h * 0.14f),
            size = Size(w * 0.28f, h * 0.12f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = stroke)
        )

        // Checkmark and line 1
        drawLine(
            color = color,
            start = Offset(w * 0.32f, h * 0.42f),
            end = Offset(w * 0.38f, h * 0.48f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.38f, h * 0.48f),
            end = Offset(w * 0.46f, h * 0.38f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.52f, h * 0.43f),
            end = Offset(w * 0.68f, h * 0.43f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        // Checkmark and line 2
        drawLine(
            color = color,
            start = Offset(w * 0.32f, h * 0.62f),
            end = Offset(w * 0.38f, h * 0.68f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.38f, h * 0.68f),
            end = Offset(w * 0.46f, h * 0.58f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.52f, h * 0.63f),
            end = Offset(w * 0.68f, h * 0.63f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}
