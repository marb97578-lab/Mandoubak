package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MandoubakCyan
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.MandoubakTextSecondary

@Composable
fun BrandLogoIcon(
    modifier: Modifier = Modifier.size(54.dp, 44.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Colors
        val navyColor = Color(0xFF0F3066)
        val cyanColor = Color(0xFF0284C7)
        val strokeWidth = 3.dp.toPx()

        // 1. Briefcase Handle
        val handleW = w * 0.32f
        val handleH = h * 0.22f
        val handleLeft = (w - handleW) / 2f
        val handleTop = h * 0.04f
        val handlePath = Path().apply {
            moveTo(handleLeft, handleTop + handleH)
            lineTo(handleLeft, handleTop + 3.dp.toPx())
            quadraticTo(handleLeft, handleTop, handleLeft + 4.dp.toPx(), handleTop)
            lineTo(handleLeft + handleW - 4.dp.toPx(), handleTop)
            quadraticTo(handleLeft + handleW, handleTop, handleLeft + handleW, handleTop + 3.dp.toPx())
            lineTo(handleLeft + handleW, handleTop + handleH)
        }
        drawPath(
            path = handlePath,
            color = navyColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 2. Briefcase Outer Body
        val bodyTop = h * 0.22f
        val bodyW = w * 0.88f
        val bodyH = h * 0.72f
        val bodyLeft = (w - bodyW) / 2f

        drawRoundRect(
            color = navyColor,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        // 3. Inside Growth Chart Bars
        val bar1H = bodyH * 0.35f
        val bar2H = bodyH * 0.52f
        val bar3H = bodyH * 0.70f
        val barW = 4.dp.toPx()
        val baseLineY = bodyTop + bodyH - 4.dp.toPx()

        val bar1X = bodyLeft + bodyW * 0.22f
        val bar2X = bodyLeft + bodyW * 0.40f
        val bar3X = bodyLeft + bodyW * 0.58f

        // Bar 1
        drawRoundRect(
            color = cyanColor,
            topLeft = Offset(bar1X, baseLineY - bar1H),
            size = Size(barW, bar1H),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // Bar 2
        drawRoundRect(
            color = cyanColor,
            topLeft = Offset(bar2X, baseLineY - bar2H),
            size = Size(barW, bar2H),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // Bar 3
        drawRoundRect(
            color = cyanColor,
            topLeft = Offset(bar3X, baseLineY - bar3H),
            size = Size(barW, bar3H),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // 4. Upward Rising Line
        val chartLine = Path().apply {
            moveTo(bar1X + barW / 2, baseLineY - bar1H + 3.dp.toPx())
            lineTo(bar2X + barW / 2, baseLineY - bar2H)
            lineTo(bar3X + barW / 2, baseLineY - bar3H)
            lineTo(bodyLeft + bodyW * 0.78f, bodyTop + bodyH * 0.20f)
        }
        drawPath(
            path = chartLine,
            color = cyanColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 5. Target / Crosshair circle at top right
        val targetCenterX = bodyLeft + bodyW * 0.80f
        val targetCenterY = bodyTop + bodyH * 0.16f
        val radius = 5.5.dp.toPx()
        drawCircle(
            color = cyanColor,
            radius = radius,
            center = Offset(targetCenterX, targetCenterY),
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = cyanColor,
            radius = 2.dp.toPx(),
            center = Offset(targetCenterX, targetCenterY)
        )
    }
}

@Composable
fun MandoubakBrandHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BrandLogoIcon()

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Arabic main brand title "مـنـدوبـك"
                Text(
                    text = "مـنـدوبـك",
                    color = MandoubakNavy,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )

                // English brand subtitle "MANDOUBAK"
                Text(
                    text = "MANDOUBAK",
                    color = MandoubakCyan,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // System description "منظومة المحاسبة وإدارة المبيعات"
        Text(
            text = "منظومة المحاسبة وإدارة المبيعات",
            color = MandoubakTextSecondary,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
