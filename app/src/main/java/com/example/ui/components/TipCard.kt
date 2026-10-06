package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TipBg
import com.example.ui.theme.TipBorder
import com.example.ui.theme.TipIcon
import com.example.ui.theme.TipTitle

@Composable
fun TipCard(
    modifier: Modifier = Modifier
) {
    val tips = listOf(
        "حافظ على تحديث أسعار المنتجات للحصول على أفضل ربحية.",
        "سجل سندات القبض أولاً بأول لضبط حسابات العملاء بدقة.",
        "راجع المنتجات منخفضة المخزون يومياً لتفادي نفاد البضائع.",
        "تأكد من إرسال إيصال الفاتورة للعميل فور إتمام عملية البيع."
    )
    var currentTipIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(TipBg)
            .border(width = 1.dp, color = TipBorder, shape = RoundedCornerShape(18.dp))
            .clickable {
                currentTipIndex = (currentTipIndex + 1) % tips.size
            }
            .testTag("tip_of_the_day_card")
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Glowing Lightbulb Icon with Sparkles
            LightbulbSparkleIcon(
                color = TipIcon,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "نصيحة اليوم",
                    color = TipTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = tips[currentTipIndex],
                    color = Color(0xFF475569),
                    fontSize = 11.8.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun LightbulbSparkleIcon(
    color: Color,
    modifier: Modifier = Modifier.size(32.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.dp.toPx()

        val bulbCenterX = w * 0.5f
        val bulbCenterY = h * 0.44f
        val bulbRadius = 7.dp.toPx()

        // Bulb outline
        val bulbPath = Path().apply {
            moveTo(bulbCenterX - 4.dp.toPx(), bulbCenterY + 7.dp.toPx())
            lineTo(bulbCenterX - 3.dp.toPx(), bulbCenterY + 4.dp.toPx())
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    bulbCenterX - bulbRadius,
                    bulbCenterY - bulbRadius,
                    bulbCenterX + bulbRadius,
                    bulbCenterY + bulbRadius
                ),
                startAngleDegrees = 135f,
                sweepAngleDegrees = 270f,
                forceMoveTo = false
            )
            lineTo(bulbCenterX + 4.dp.toPx(), bulbCenterY + 7.dp.toPx())
            close()
        }
        drawPath(
            path = bulbPath,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        // Bulb base threads
        drawLine(
            color = color,
            start = Offset(bulbCenterX - 3.5.dp.toPx(), bulbCenterY + 8.5.dp.toPx()),
            end = Offset(bulbCenterX + 3.5.dp.toPx(), bulbCenterY + 8.5.dp.toPx()),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(bulbCenterX - 2.dp.toPx(), bulbCenterY + 11.5.dp.toPx()),
            end = Offset(bulbCenterX + 2.dp.toPx(), bulbCenterY + 11.5.dp.toPx()),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        // Sparkle rays around the bulb
        val rayLength = 3.dp.toPx()
        // Top ray
        drawLine(color = color, start = Offset(bulbCenterX, bulbCenterY - bulbRadius - 2.dp.toPx()), end = Offset(bulbCenterX, bulbCenterY - bulbRadius - 2.dp.toPx() - rayLength), strokeWidth = stroke, cap = StrokeCap.Round)
        // Top right ray
        drawLine(color = color, start = Offset(bulbCenterX + 8.dp.toPx(), bulbCenterY - 6.dp.toPx()), end = Offset(bulbCenterX + 11.dp.toPx(), bulbCenterY - 8.dp.toPx()), strokeWidth = stroke, cap = StrokeCap.Round)
        // Top left ray
        drawLine(color = color, start = Offset(bulbCenterX - 8.dp.toPx(), bulbCenterY - 6.dp.toPx()), end = Offset(bulbCenterX - 11.dp.toPx(), bulbCenterY - 8.dp.toPx()), strokeWidth = stroke, cap = StrokeCap.Round)
        // Left ray
        drawLine(color = color, start = Offset(bulbCenterX - bulbRadius - 2.dp.toPx(), bulbCenterY), end = Offset(bulbCenterX - bulbRadius - 2.dp.toPx() - rayLength, bulbCenterY), strokeWidth = stroke, cap = StrokeCap.Round)
        // Right ray
        drawLine(color = color, start = Offset(bulbCenterX + bulbRadius + 2.dp.toPx(), bulbCenterY), end = Offset(bulbCenterX + bulbRadius + 2.dp.toPx() + rayLength, bulbCenterY), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}
