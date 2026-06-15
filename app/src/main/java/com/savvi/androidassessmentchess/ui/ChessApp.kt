package com.savvi.androidassessmentchess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvi.androidassessmentchess.R
import com.savvi.androidassessmentchess.ui.components.ChessBoard
import com.savvi.androidassessmentchess.ui.components.Settings
import androidx.lifecycle.viewmodel.compose.viewModel
import com.savvi.androidassessmentchess.ui.components.PathList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessApp(
    viewModel: ChessViewModel = viewModel()
) {

    val boardSize by viewModel.boardSize.collectAsState()
    val startPosition by viewModel.startPosition.collectAsState()
    val endPosition by viewModel.endPosition.collectAsState()
    val maxMoves by viewModel.maxMoves.collectAsState()
    val exactlyNMoves by viewModel.exactlyNMoves.collectAsState()
    val paths by viewModel.paths.collectAsState()
    val totalPathsCount by viewModel.totalPathsCount.collectAsState()
    val selectedPath by viewModel.selectedPath.collectAsState()
    val isCalculating by viewModel.isCalculating.collectAsState()

    val hasActiveSelection = startPosition != null || endPosition != null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                        )
                    )
                )
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            ChessBoard(
                boardSize = boardSize,
                startPosition = startPosition,
                endPosition = endPosition,
                onCellClicked = {row, col -> viewModel.onCellClicked(row,col)},
                selectedPath = selectedPath
            )

            PathList(
                paths = paths,
                totalPathsCount = totalPathsCount,
                selectedPath = selectedPath,
                isCalculating = isCalculating,
                boardSize = boardSize,
                maxMoves = maxMoves,
                startPositionSet = startPosition != null,
                endPositionSet = endPosition != null,
                onPathSelected = { path -> viewModel.selectPath(path) }
            )

            Settings(
                boardSize = boardSize,
                onBoardSizeChanged = { viewModel.onBoardSizeChanged(it)},
                maxMoves = maxMoves,
                onMaxMovesChanged = { viewModel.onMaxMovesChanged(it) },
                exactlyNMoves = exactlyNMoves,
                onExactlyNMovesChanged = { viewModel.onExactlyNMovesChanged(it) },
                onResetClicked = { viewModel.resetBoardState() },
                hasActiveSelection = hasActiveSelection
                )
        }
    }
}
