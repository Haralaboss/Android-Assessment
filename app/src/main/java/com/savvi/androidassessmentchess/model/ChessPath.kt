package com.savvi.androidassessmentchess.model

//Creates the path shown (e1 → f3 → ...)

data class ChessPath(val positions: List<ChessPosition>){

    fun toAlgebraicString(boardSize: Int): String {
        return positions.joinToString(" → ") { it.toAlgebraic(boardSize) }
    }
}
