package com.savvi.androidassessmentchess.model

data class ChessPosition(val row: Int, val col: Int) {

    fun toAlgebraic(boardSize: Int): String {
        val colLetter = ('a'.code + col).toChar()
        val rowNum = boardSize - row
        return "$colLetter$rowNum"
    }
}
