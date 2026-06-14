package com.savvi.androidassessmentchess.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.savvi.androidassessmentchess.model.ChessPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChessViewModel(application: Application) : AndroidViewModel(application){

    private val _boardSize = MutableStateFlow(8)
    val boardSize: StateFlow<Int> = _boardSize.asStateFlow()

    private val _startPosition = MutableStateFlow<ChessPosition?>(null)
    val  startPosition: StateFlow<ChessPosition?> = _startPosition.asStateFlow()

    private val _endPosition = MutableStateFlow<ChessPosition?>(null)
    val endPosition: StateFlow<ChessPosition?> = _endPosition.asStateFlow()

    fun onCellClicked(row: Int, col: Int) {
        val clickedPos = ChessPosition(row, col)

        when {
            _startPosition.value == null -> {
                _startPosition.value = clickedPos
                _endPosition.value = null
            }
            _endPosition.value == null -> {
                if (clickedPos == _startPosition.value) {
                    return
                }
                _endPosition.value = clickedPos
            }
            else -> {
                _startPosition.value = clickedPos
                _endPosition.value = null
            }
        }
    }

    fun onBoardSizeChanged(size: Int) {
        if (size == _boardSize.value) return
        _boardSize.value = size
        resetBoardState()
    }

    fun resetBoardState() {
        _startPosition.value = null
        _endPosition.value = null

        //saveCurrentState()
    }


}