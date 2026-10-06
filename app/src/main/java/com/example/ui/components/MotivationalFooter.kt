package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MandoubakBlue
import com.example.ui.theme.MandoubakNavy

@Composable
fun MotivationalFooter(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // "بخطوة واحدة .."
        Text(
            text = "بخطوة واحدة ..",
            color = MandoubakNavy,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        // "نصل إلى أهدافك"
        Text(
            text = "نصل إلى أهدافك",
            color = MandoubakNavy,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(3.dp))

        // Curved Artistic Blue Underline beneath "أهدافك"
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                val w = size.width
                val h = size.height

                val underlinePath = Path().apply {
                    moveTo(w * 0.15f, h * 0.2f)
                    quadraticTo(
                        w * 0.52f,
                        h * 0.95f,
                        w * 0.90f,
                        h * 0.15f
                    )
                }

                drawPath(
                    path = underlinePath,
                    color = MandoubakBlue,
                    style = Stroke(
                        width = 2.4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}
