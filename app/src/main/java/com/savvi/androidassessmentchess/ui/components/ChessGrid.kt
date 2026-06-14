package com.savvi.androidassessmentchess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.savvi.androidassessmentchess.model.ChessPosition
import com.savvi.androidassessmentchess.ui.theme.BlackSquare
import com.savvi.androidassessmentchess.ui.theme.OffWhite

@Composable
fun ChessGrid(
    boardSize: Int,
    cellSize: Dp
) {
    val startPosition = ChessPosition(2,2)
    val endPosition = ChessPosition(5,5)

    Column {
        for (row in 0 until boardSize) {
            Row {
                for (col in 0 until boardSize) {
                    val isBlack = (row + col) % 2 == 1
                    val isStart = startPosition?.row == row && startPosition.col == col
                    val isEnd = endPosition?.row == row && endPosition.col == col

                    ChessSquare(
                        size = cellSize,
                        isBlack = isBlack,
                        indicator = when {
                            isStart -> SquareIndicator.Start
                            isEnd -> SquareIndicator.End
                            else -> null
                        }
                    )
                }
            }
        }
    }
}

enum class SquareIndicator { Start, End }

@Composable
fun ChessSquare(
    size: Dp,
    isBlack: Boolean,
    indicator: SquareIndicator?
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(if (isBlack) BlackSquare else OffWhite),
        contentAlignment = Alignment.Center
    ) {
        indicator?.let {
            val color = if (it == SquareIndicator.Start) Color(0xFF29D94C) else Color(0xFFFC1414)
            val text = if (it == SquareIndicator.Start) "S" else "E"

            Box(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.45).sp,
                    color = color
                )
            }
        }
    }
}
