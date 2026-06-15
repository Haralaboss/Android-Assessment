package com.savvi.androidassessmentchess.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.savvi.androidassessmentchess.model.ChessPosition
import com.savvi.androidassessmentchess.model.ChessPath
import com.savvi.androidassessmentchess.data.preferences.ChessPreferences
import com.savvi.androidassessmentchess.usecase.FindKnightPathsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChessViewModel(application: Application) : AndroidViewModel(application){

    private val chessPreferences = ChessPreferences(application)
    private val findKnightPathsUseCase = FindKnightPathsUseCase()

    private val _boardSize = MutableStateFlow(8)
    val boardSize: StateFlow<Int> = _boardSize.asStateFlow()

    private val _startPosition = MutableStateFlow<ChessPosition?>(null)
    val  startPosition: StateFlow<ChessPosition?> = _startPosition.asStateFlow()

    private val _endPosition = MutableStateFlow<ChessPosition?>(null)
    val endPosition: StateFlow<ChessPosition?> = _endPosition.asStateFlow()

    private val _maxMoves = MutableStateFlow(3)
    val maxMoves: StateFlow<Int> = _maxMoves.asStateFlow()

    private val _exactlyNMoves = MutableStateFlow(true)
    val exactlyNMoves: StateFlow<Boolean> = _exactlyNMoves.asStateFlow()

    private val _paths = MutableStateFlow<List<ChessPath>>(emptyList())
    val paths: StateFlow<List<ChessPath>> = _paths.asStateFlow()

    private val _totalPathsCount = MutableStateFlow(0)
    val totalPathsCount: StateFlow<Int> = _totalPathsCount.asStateFlow()

    private val _selectedPath = MutableStateFlow<ChessPath?>(null)
    val selectedPath: StateFlow<ChessPath?> = _selectedPath.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    private var calculationJob: Job? = null

    init {
        // Load initial state from preferences
        viewModelScope.launch {
            val saved = chessPreferences.savedStateFlow.first()
            _boardSize.value = saved.boardSize
            _maxMoves.value = saved.maxMoves
            _exactlyNMoves.value = saved.exactlyNMoves
            _startPosition.value = saved.startPosition
            _endPosition.value = saved.endPosition
            _paths.value = saved.lastSolution
            _totalPathsCount.value = saved.totalPathsCount

            // If start and end are already set, but paths are empty, run calculation
            if (saved.startPosition != null && saved.endPosition != null && saved.lastSolution.isEmpty()) {
                calculatePaths()
            }
        }
    }

    fun onCellClicked(row: Int, col: Int) {
        val clickedPos = ChessPosition(row, col)

        when {
            _startPosition.value == null -> {
                _startPosition.value = clickedPos
                _endPosition.value = null
                _paths.value = emptyList()
                _totalPathsCount.value = 0
                _selectedPath.value = null
                saveCurrentState()
            }
            _endPosition.value == null -> {
                if (clickedPos == _startPosition.value) {
                    // Cannot have start and end be the same
                    return
                }
                _endPosition.value = clickedPos
                calculatePaths()
            }
            else -> {
                _startPosition.value = clickedPos
                _endPosition.value = null
                _paths.value = emptyList()
                _totalPathsCount.value = 0
                _selectedPath.value = null
                saveCurrentState()
            }
        }
    }

    fun onBoardSizeChanged(size: Int) {
        if (size == _boardSize.value) return
        _boardSize.value = size
        resetBoardState()
    }

    fun onMaxMovesChanged(moves: Int) {
        if (moves == _maxMoves.value) return
        _maxMoves.value = moves
        if (_startPosition.value != null && _endPosition.value != null) {
            calculatePaths()
        } else {
            saveCurrentState()
        }
    }

    fun onExactlyNMovesChanged(exactly: Boolean) {
        if (exactly == _exactlyNMoves.value) return
        _exactlyNMoves.value = exactly
        if (_startPosition.value != null && _endPosition.value != null) {
            calculatePaths()
        } else {
            saveCurrentState()
        }
    }

    fun resetBoardState() {
        calculationJob?.cancel()
        _startPosition.value = null
        _endPosition.value = null
        _paths.value = emptyList()
        _totalPathsCount.value = 0
        _selectedPath.value = null
        _isCalculating.value = false
        saveCurrentState()
    }

    fun selectPath(path: ChessPath?) {
        _selectedPath.value = path
    }

    private fun calculatePaths() {
        val start = _startPosition.value ?: return
        val end = _endPosition.value ?: return
        val maxMoves = _maxMoves.value
        val size = _boardSize.value
        val exactly = _exactlyNMoves.value

        calculationJob?.cancel()
        _isCalculating.value = true
        _paths.value = emptyList()
        _totalPathsCount.value = 0
        _selectedPath.value = null

        calculationJob = viewModelScope.launch {
            try {
                val results = findKnightPathsUseCase.execute(start, end, maxMoves, size, exactly)
                _paths.value = results.paths
                _totalPathsCount.value = results.totalCount
                saveCurrentState()
            } catch (e: CancellationException) {
                // Cancelled due to new settings or reset
            } catch (e: Exception) {
                _paths.value = emptyList()
                _totalPathsCount.value = 0
            } finally {
                _isCalculating.value = false
            }
        }
    }

    private fun saveCurrentState() {
        viewModelScope.launch {
            chessPreferences.saveState(
                boardSize = _boardSize.value,
                maxMoves = _maxMoves.value,
                exactlyNMoves = _exactlyNMoves.value,
                startPosition = _startPosition.value,
                endPosition = _endPosition.value,
                lastSolution = _paths.value,
                totalPathsCount = _totalPathsCount.value
            )
        }
    }

}
