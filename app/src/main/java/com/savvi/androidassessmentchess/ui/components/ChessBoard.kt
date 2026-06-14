package com.savvi.androidassessmentchess.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvi.androidassessmentchess.model.ChessPosition

@Composable
fun ChessBoard(
    boardSize: Int,
    startPosition: ChessPosition?,
    endPosition: ChessPosition?,
    onCellClicked: (row: Int, col: Int) -> Unit
){

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
    ) {

        val gridWidth = maxWidth - (20.dp * 2)
        val cellSizeDp = gridWidth / boardSize

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                RankLabels(boardSize, cellSizeDp)

                Box(
                    modifier = Modifier
                        .size(gridWidth)
                        .clip(MaterialTheme.shapes.small)
                ){
                    ChessGrid(boardSize, cellSizeDp, startPosition, endPosition, onCellClicked)

                }
                Spacer(modifier = Modifier.width(20.dp))

            }
            FileLabels(boardSize, cellSizeDp)

        }

    }

}

@Composable
private fun FileLabels(boardSize: Int, cellSize: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth().height(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        for (col in 0 until boardSize) {
            Box(modifier = Modifier.width(cellSize), contentAlignment = Alignment.Center) {
                Text(
                    text = ('A'.code + col).toChar().toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
private fun RankLabels(boardSize: Int, cellSize: Dp) {
    Column(
        modifier = Modifier.width(20.dp).fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until boardSize) {
            Box(modifier = Modifier.height(cellSize), contentAlignment = Alignment.Center) {
                Text(
                    text = (boardSize - row).toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
