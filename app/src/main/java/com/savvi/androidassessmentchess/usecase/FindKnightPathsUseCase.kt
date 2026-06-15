package com.savvi.androidassessmentchess.usecase

import com.savvi.androidassessmentchess.model.ChessPath
import com.savvi.androidassessmentchess.model.ChessPosition
import java.util.ArrayList
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class KnightPathResult(
    val paths: List<ChessPath>,
    val totalCount: Int
)

class FindKnightPathsUseCase {

    private val moveOffsets = arrayOf(
        Pair(-2, -1), Pair(-2, 1),
        Pair(-1, -2), Pair(-1, 2),
        Pair(1, -2),  Pair(1, 2),
        Pair(2, -1),  Pair(2, 1)
    )

    suspend fun execute(
        start: ChessPosition,
        end: ChessPosition,
        maxMoves: Int,
        boardSize: Int,
        exactlyNMoves: Boolean = false
    ): KnightPathResult = withContext(Dispatchers.Default){
        if (start == end) return@withContext KnightPathResult(emptyList(), 0)

        //PARITY CHECK
        val startColor = (start.row + start.col) % 2
        val endColor = (end.row + end.col) % 2
        
        if (exactlyNMoves) {
            val targetRequiresEvenMoves = (startColor == endColor)
            val movesAreEven = (maxMoves % 2 == 0)
            
            if (targetRequiresEvenMoves != movesAreEven) {
                return@withContext KnightPathResult(emptyList(), 0)
            }
        }

        // DISTANCE CHECK
        val manhattanDist = abs(start.row - end.row) + abs(start.col - end.col)
        if (manhattanDist > maxMoves * 3) {
            return@withContext KnightPathResult(emptyList(), 0)
        }

        val results = mutableListOf<ChessPath>()
        var totalCount = 0
        val maxDisplayLimit = 200000
        
        val currentPath = ArrayList<ChessPosition>(maxMoves + 1)
        currentPath.add(start)

        fun dfs(current: ChessPosition, movesRemaining: Int) {
            if (current == end) {
                val pathLength = currentPath.size - 1
                if (!exactlyNMoves || pathLength == maxMoves) {
                    totalCount++
                    if (results.size < maxDisplayLimit) {
                        results.add(ChessPath(ArrayList(currentPath)))
                    }
                }
                return
            }

            if (movesRemaining <= 0) return 

            for (offset in moveOffsets) {
                val nextRow = current.row + offset.first
                val nextCol = current.col + offset.second

                if (nextRow in 0 until boardSize && nextCol in 0 until boardSize) {
                    val nextPos = ChessPosition(nextRow, nextCol)

                    if (!currentPath.contains(nextPos)) {
                        currentPath.add(nextPos)
                        dfs(nextPos, movesRemaining - 1)
                        currentPath.removeAt(currentPath.size - 1)
                    }
                }
            }
        }

        dfs(start, maxMoves)
        KnightPathResult(results, totalCount)
    }
}
