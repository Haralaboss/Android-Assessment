package com.savvi.androidassessmentchess.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChessViewModel(application: Application) : AndroidViewModel(application){

    private val _boardSize = MutableStateFlow(8)
    val boardSize: StateFlow<Int> = _boardSize.asStateFlow()

    fun onBoardSizeChanged(size: Int) {
        if (size == _boardSize.value) return
        _boardSize.value = size
        resetBoardState()
    }

    fun resetBoardState() {
        //saveCurrentState()
    }


}