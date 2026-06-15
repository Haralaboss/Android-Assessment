package com.savvi.androidassessmentchess.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvi.androidassessmentchess.model.ChessPath
import com.savvi.androidassessmentchess.ui.theme.PathEndColor
import com.savvi.androidassessmentchess.ui.theme.PathStartColor
import com.savvi.androidassessmentchess.ui.theme.White
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sin

@Composable
fun PathOverlay(path: ChessPath, cellSizePx: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val positions = path.positions
        if (positions.isEmpty()) return@Canvas

        val strokeWidth = 3.dp.toPx()
        val arrowSize = 8.dp.toPx()
        
        val fullPath = Path()
        for (i in 0 until positions.size - 1) {
            val p1 = positions[i]
            val p2 = positions[i + 1]
            val startX = p1.col * cellSizePx + cellSizePx / 2
            val startY = p1.row * cellSizePx + cellSizePx / 2
            
            if (i == 0) fullPath.moveTo(startX, startY)
            
            val dr = p2.row - p1.row
            if (abs(dr) == 2) {
                fullPath.lineTo(p1.col * cellSizePx + cellSizePx / 2, p2.row * cellSizePx + cellSizePx / 2)
            } else {
                fullPath.lineTo(p2.col * cellSizePx + cellSizePx / 2, p1.row * cellSizePx + cellSizePx / 2)
            }
            fullPath.lineTo(p2.col * cellSizePx + cellSizePx / 2, p2.row * cellSizePx + cellSizePx / 2)
        }

        drawPath(
            path = fullPath,
            brush = Brush.linearGradient(
                colors = listOf(PathStartColor.copy(alpha = 0.7f), PathEndColor.copy(alpha = 0.7f)),
                start = Offset(positions.first().col * cellSizePx, positions.first().row * cellSizePx),
                end = Offset(positions.last().col * cellSizePx, positions.last().row * cellSizePx)
            ),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(fullPath, false)
        val length = pathMeasure.length
        val step = 28.dp.toPx() 
        
        var distance = 0f
        while (distance < length) {
            val pos = pathMeasure.getPosition(distance)
            val tangent = pathMeasure.getTangent(distance)
            val degrees = Math.toDegrees(atan2(tangent.y, tangent.x).toDouble()).toFloat()
            
            val fraction = distance / length
            val color = lerp(PathStartColor, PathEndColor, fraction)

            rotate(degrees, pivot = pos) {
                val arrowPath = Path().apply {
                    moveTo(pos.x - arrowSize / 2, pos.y - arrowSize / 2)
                    lineTo(pos.x + arrowSize / 2, pos.y)
                    lineTo(pos.x - arrowSize / 2, pos.y + arrowSize / 2)
                }
                drawPath(
                    path = arrowPath,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            distance += step
        }

        val dotRadius = 4.dp.toPx()
        for (i in 1 until positions.size - 1) {
            val p = positions[i]
            val center = Offset(p.col * cellSizePx + cellSizePx / 2, p.row * cellSizePx + cellSizePx / 2)
            val color = lerp(PathStartColor, PathEndColor, i.toFloat() / (positions.size - 1))
            drawCircle(Color.White, radius = dotRadius + 1.5f, center = center)
            drawCircle(color, radius = dotRadius, center = center)
        }
    }
}

@Composable
fun AnimatedKnight(path: ChessPath, cellSizeDp: Dp, cellSizePx: Float) {
    if (path.positions.size < 2) return

    val stepsCount = path.positions.size - 1
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(path) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = stepsCount.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1200 * stepsCount, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val progress = animatable.value
    val currentIndex = progress.toInt().coerceAtMost(stepsCount - 1)
    val fraction = progress - progress.toInt()

    val pStart = path.positions[currentIndex]
    val pEnd = path.positions[currentIndex + 1]

    val startX = pStart.col * cellSizePx + cellSizePx / 2
    val startY = pStart.row * cellSizePx + cellSizePx / 2
    val endX = pEnd.col * cellSizePx + cellSizePx / 2
    val endY = pEnd.row * cellSizePx + cellSizePx / 2

    val dr = pEnd.row - pStart.row
    val (posX, posY) = if (abs(dr) == 2) {
        if (fraction < 0.66f) {
            val f = fraction / 0.66f
            startX to startY + (endY - startY) * f
        } else {
            val f = (fraction - 0.66f) / 0.34f
            startX + (endX - startX) * f to endY
        }
    } else {
        if (fraction < 0.66f) {
            val f = fraction / 0.66f
            startX + (endX - startX) * f to startY
        } else {
            val f = (fraction - 0.66f) / 0.34f
            endX to startY + (endY - startY) * f
        }
    }

    val jumpHeight = run {
        val jumpFraction = if (fraction < 0.66f) {
            (fraction / 0.33f) % 1.0f
        } else {
            (fraction - 0.66f) / 0.34f
        }
        sin(jumpFraction * PI.toFloat())
    }

    val hopOffset = -jumpHeight * (cellSizePx * 0.45f)
    val scale = 1f + 0.15f * jumpHeight

    val density = LocalDensity.current
    Box(
        modifier = Modifier
            .absoluteOffset(
                x = with(density) { posX.toDp() } - (cellSizeDp / 2),
                y = with(density) { posY.toDp() } - (cellSizeDp / 2) + with(density) { hopOffset.toDp() }
            )
            .size(cellSizeDp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "♞",
            style = TextStyle(
                shadow= Shadow(
                    color = White,
                    offset = Offset (2f, 2f)
                )
            ),
            fontSize = (cellSizeDp.value * 0.8f * scale).sp,
            color = Color.Black

        )
    }
}
