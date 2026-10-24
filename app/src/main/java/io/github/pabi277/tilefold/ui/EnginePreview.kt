@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.domain.Board
import io.github.pabi277.tilefold.domain.Direction
import io.github.pabi277.tilefold.domain.Game

/** Fixed seed, so the debug APK opens the same board every time it is launched. */
internal const val ENGINE_PREVIEW_SEED = 20261024L

internal const val ENGINE_STATUS_TAG = "engine-status"
internal const val ENGINE_BOARD_TAG = "engine-board"

private val CELL_SIZE = 56.dp
private val CELL_GAP = 2.dp

internal fun engineCellTag(
    row: Int,
    col: Int,
): String = "engine-cell-$row-$col"

/**
 * Phase-1 **debug-only** engine preview (roadmap 1.4): four arrow buttons, plain
 * numerals, nothing else.
 *
 * This is deliberately *not* a designed screen — the approved reference in
 * `docs/UI-REFERENCE.md` is the phase-2 target, and no colour, spacing or type
 * value here is taken from it. Everything is a Material theme default or a
 * layout necessity, so this file cannot pre-empt a frozen token.
 *
 * [MainActivity] shows it only when `BuildConfig.DEBUG` is set; the release
 * build still starts on the phase-0 hello screen.
 */
@Composable
fun EnginePreview(
    modifier: Modifier = Modifier,
    seed: Long = ENGINE_PREVIEW_SEED,
) {
    var game by remember { mutableStateOf(Game.newGame(seed)) }
    var round by remember { mutableIntStateOf(0) }

    EnginePreviewContent(
        game = game,
        onMove = { direction -> game = game.move(direction) },
        onUndo = { game.undo()?.let { restored -> game = restored } },
        onNewGame = {
            round += 1
            game = Game.newGame(seed + round)
        },
        modifier = modifier,
    )
}

/** Stateless body of [EnginePreview] — the composable a test can drive directly. */
@Composable
internal fun EnginePreviewContent(
    game: Game,
    onMove: (Direction) -> Unit,
    onUndo: () -> Unit,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Engine preview (debug build)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Score ${game.score} · Moves ${game.moves} · ${game.status.name}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .testTag(ENGINE_STATUS_TAG),
        )
        EngineBoard(
            board = game.board,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MoveButton(label = "←", direction = Direction.LEFT, onMove = onMove)
            MoveButton(label = "↑", direction = Direction.UP, onMove = onMove)
            MoveButton(label = "↓", direction = Direction.DOWN, onMove = onMove)
            MoveButton(label = "→", direction = Direction.RIGHT, onMove = onMove)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            OutlinedButton(onClick = onUndo, enabled = game.canUndo) {
                Text("Undo")
            }
            OutlinedButton(onClick = onNewGame) {
                Text("New game")
            }
        }
    }
}

@Composable
private fun MoveButton(
    label: String,
    direction: Direction,
    onMove: (Direction) -> Unit,
) {
    Button(onClick = { onMove(direction) }) {
        Text(label)
    }
}

@Composable
private fun EngineBoard(
    board: Board,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.testTag(ENGINE_BOARD_TAG)) {
        repeat(Board.WIDTH) { row ->
            Row {
                repeat(Board.WIDTH) { col ->
                    EngineCell(
                        value = board.valueAt(row, col),
                        tag = engineCellTag(row, col),
                    )
                }
            }
        }
    }
}

@Composable
private fun EngineCell(
    value: Int,
    tag: String,
) {
    Box(
        modifier =
            Modifier
                .size(CELL_SIZE)
                .padding(CELL_GAP)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (value != 0) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag(tag),
            )
        }
    }
}
