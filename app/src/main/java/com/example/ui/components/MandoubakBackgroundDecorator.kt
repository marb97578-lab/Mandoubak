package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun MandoubakBackgroundDecorator(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Top soft curved wave
        val topWavePath = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, h * 0.14f)
            cubicTo(
                w * 0.70f, h * 0.17f,
                w * 0.30f, h * 0.10f,
                0f, h * 0.15f
            )
            close()
        }

        drawPath(
            path = topWavePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE9EDFB).copy(alpha = 0.85f),
                    Color(0xFFF3F1FD).copy(alpha = 0.50f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = h * 0.16f
            )
        )

        // Bottom soft curved wave
        val bottomWavePath = Path().apply {
            moveTo(0f, h)
            lineTo(w, h)
            lineTo(w, h * 0.88f)
            cubicTo(
                w * 0.65f, h * 0.86f,
                w * 0.35f, h * 0.94f,
                0f, h * 0.90f
            )
            close()
        }

        drawPath(
            path = bottomWavePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFFF0F0FC).copy(alpha = 0.60f),
                    Color(0xFFE4EDFC).copy(alpha = 0.85f)
                ),
                startY = h * 0.84f,
                endY = h
            )
        )
    }
}
