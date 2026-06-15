package com.savvi.androidassessmentchess.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.savvi.androidassessmentchess.model.ChessPath
import com.savvi.androidassessmentchess.model.ChessPosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chess_preferences")


class ChessPreferences(private val context: Context) {

    companion object{
        private val KEY_BOARD_SIZE = intPreferencesKey("board_size")
        private val KEY_MAX_MOVES = intPreferencesKey("max_moves")
        private val KEY_EXACTLY_N_MOVES = booleanPreferencesKey("exactly_n_moves")

        private val KEY_START_ROW = intPreferencesKey("start_row")
        private val KEY_START_COL = intPreferencesKey("start_col")

        private val KEY_END_ROW = intPreferencesKey("end_row")
        private val KEY_END_COL = intPreferencesKey("end_col")

        private val KEY_LAST_SOLUTION = stringPreferencesKey("last_solution")
        private val KEY_TOTAL_PATHS_COUNT = intPreferencesKey("total_paths_count")

    }

    data class SavedState(
        val boardSize: Int,
        val maxMoves: Int,
        val exactlyNMoves: Boolean,
        val startPosition: ChessPosition?,
        val endPosition: ChessPosition?,
        val lastSolution: List<ChessPath>,
        val totalPathsCount: Int
    )

    val savedStateFlow: Flow<SavedState> = context.dataStore.data.map { preferences ->
        val boardSize = preferences[KEY_BOARD_SIZE] ?: 8
        val maxMoves = preferences[KEY_MAX_MOVES] ?: 3
        val exactlyNMoves = preferences[KEY_EXACTLY_N_MOVES] ?: true

        val startRow = preferences[KEY_START_ROW]
        val startCol = preferences[KEY_START_COL]
        val startPosition = if (startRow != null && startCol != null) ChessPosition(startRow, startCol) else null

        val endRow = preferences[KEY_END_ROW]
        val endCol = preferences[KEY_END_COL]
        val endPosition = if (endRow != null && endCol != null) ChessPosition(endRow, endCol) else null

        val solutionStr = preferences[KEY_LAST_SOLUTION] ?: ""
        val lastSolution = deserializePaths(solutionStr)
        val totalPathsCount = preferences[KEY_TOTAL_PATHS_COUNT] ?: 0

        SavedState(
            boardSize = boardSize,
            maxMoves = maxMoves,
            exactlyNMoves = exactlyNMoves,
            startPosition = startPosition,
            endPosition = endPosition,
            lastSolution = lastSolution,
            totalPathsCount = totalPathsCount
        )
    }

    suspend fun saveState(
        boardSize: Int,
        maxMoves: Int,
        exactlyNMoves: Boolean,
        startPosition: ChessPosition?,
        endPosition: ChessPosition?,
        lastSolution: List<ChessPath>,
        totalPathsCount: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BOARD_SIZE] = boardSize
            preferences[KEY_MAX_MOVES] = maxMoves
            preferences[KEY_EXACTLY_N_MOVES] = exactlyNMoves

            if (startPosition != null) {
                preferences[KEY_START_ROW] = startPosition.row
                preferences[KEY_START_COL] = startPosition.col
            } else {
                preferences.remove(KEY_START_ROW)
                preferences.remove(KEY_START_COL)
            }

            if (endPosition != null) {
                preferences[KEY_END_ROW] = endPosition.row
                preferences[KEY_END_COL] = endPosition.col
            } else {
                preferences.remove(KEY_END_ROW)
                preferences.remove(KEY_END_COL)
            }

            preferences[KEY_LAST_SOLUTION] = serializePaths(lastSolution)
            preferences[KEY_TOTAL_PATHS_COUNT] = totalPathsCount
        }
    }

    private fun serializePaths(paths: List<ChessPath>): String {
        return paths.joinToString("|") { path ->
            path.positions.joinToString(";") { "${it.row},${it.col}" }
        }
    }

    private fun deserializePaths(serialized: String): List<ChessPath> {
        if (serialized.isEmpty()) return emptyList()
        return try {
            serialized.split("|").map { pathStr ->
                val positions = pathStr.split(";").map { stepStr ->
                    val (r, c) = stepStr.split(",").map { it.toInt() }
                    ChessPosition(r, c)
                }
                ChessPath(positions)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
