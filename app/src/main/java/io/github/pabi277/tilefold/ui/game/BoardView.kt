@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.domain.Board
import io.github.pabi277.tilefold.domain.Direction
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.Dimens
import io.github.pabi277.tilefold.ui.theme.TextCaps
import io.github.pabi277.tilefold.ui.theme.TilefoldThemeDefaults
import io.github.pabi277.tilefold.ui.theme.TypeScale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val BOARD_TAG = "game-board"

/** Test tag of the tile sitting on a given cell (row-major index). */
internal fun tileTag(index: Int): String = "board-tile-$index"

/**
 * The board: a rounded frame holding 16 empty cells and the tiles on top of them.
 *
 * Every tile is positioned by offset and carries a stable key, so when a move
 * changes its [TileView.index] Compose animates it from where it was to where the
 * engine put it — the tiles slide, they do not jump. Merge pops, spawn scales and
 * the fade of an absorbed tile are timed from the frozen
 * `Durations`; with reduced motion every animation is a snap.
 *
 * Swiping is read by [SwipeAxis] from the whole board, so a short brush is
 * ignored rather than costing a move.
 */
@Composable
internal fun BoardFrame(
    scene: BoardScene,
    onMove: (Direction) -> Unit,
    modifier: Modifier = Modifier,
    dim: Boolean = false,
    reducedMotion: Boolean = rememberReducedMotion(),
) {
    val palette = TilefoldThemeDefaults.palette
    val swipeThreshold = with(LocalDensity.current) { SwipeAxis.MIN_DRAG_DP.dp.toPx() }
    // Held as state so an in-flight drag survives the recompositions a running
    // animation causes (a lambda captured by pointerInput would restart it).
    val currentOnMove by rememberUpdatedState(onMove)

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(Corners.Board))
                .background(palette.boardFrame)
                .alpha(if (dim) DIM_ALPHA else 1f)
                .pointerInput(swipeThreshold) { swipeDetector(swipeThreshold) { currentOnMove(it) } }
                .testTag(BOARD_TAG),
    ) {
        val cell = (maxWidth - Dimens.BoardPadding * 2 - Dimens.TileGap * (Board.WIDTH - 1)) / Board.WIDTH

        repeat(Board.CELLS) { index ->
            EmptyCell(index = index, cell = cell)
        }
        scene.leaving.forEach { tile ->
            key(tile.key) {
                AnimatedTile(tile = tile, cell = cell, reducedMotion = reducedMotion)
            }
        }
        scene.tiles.forEach { tile ->
            key(tile.key) {
                AnimatedTile(tile = tile, cell = cell, reducedMotion = reducedMotion)
            }
        }
    }
}

/** Reads one swipe per gesture and ignores anything shorter than [SwipeAxis.MIN_DRAG_DP]. */
private suspend fun PointerInputScope.swipeDetector(
    threshold: Float,
    onMove: (Direction) -> Unit,
) {
    var totalX = 0f
    var totalY = 0f
    detectDragGestures(
        onDragStart = {
            totalX = 0f
            totalY = 0f
        },
        onDragEnd = { SwipeAxis.direction(totalX, totalY, threshold)?.let(onMove) },
        onDrag = { change, drag ->
            change.consume()
            totalX += drag.x
            totalY += drag.y
        },
    )
}

@Composable
private fun EmptyCell(
    index: Int,
    cell: Dp,
) {
    val palette = TilefoldThemeDefaults.palette
    Box(
        modifier =
            Modifier
                .offset(x = xOf(index, cell), y = yOf(index, cell))
                .size(cell)
                .background(palette.emptyCell, RoundedCornerShape(Corners.Tile)),
    )
}

/**
 * One tile, drawn wherever it currently is.
 *
 * The slide is an [Animatable] in dp, so a tile interrupted by the next move
 * continues from where it is instead of snapping; the pop and the spawn are
 * scale animations that start as the tile lands.
 */
@Composable
private fun AnimatedTile(
    tile: TileView,
    cell: Dp,
    reducedMotion: Boolean,
) {
    val slideMs = Motion.slide(reducedMotion)
    val popMs = Motion.pop(reducedMotion)
    val spawnMs = Motion.spawn(reducedMotion)

    val x = remember { Animatable(xOf(tile.originIndex, cell).value) }
    val y = remember { Animatable(yOf(tile.originIndex, cell).value) }
    val scale = remember { Animatable(if (tile.spawned) SPAWN_SCALE else 1f) }
    val fade = remember { Animatable(1f) }

    LaunchedEffect(tile.index, slideMs) {
        val targetX = xOf(tile.index, cell).value
        val targetY = yOf(tile.index, cell).value
        if (slideMs == 0) {
            x.snapTo(targetX)
            y.snapTo(targetY)
        } else {
            launch { x.animateTo(targetX, tween(slideMs)) }
            launch { y.animateTo(targetY, tween(slideMs)) }
        }
    }

    LaunchedEffect(tile.key, tile.index, tile.spawned, tile.merged, tile.leaving, reducedMotion) {
        if (slideMs == 0 && popMs == 0 && spawnMs == 0) {
            scale.snapTo(1f)
            fade.snapTo(if (tile.leaving) 0f else 1f)
            return@LaunchedEffect
        }
        if (tile.spawned) {
            delay(slideMs.toLong())
            scale.animateTo(1f, tween(spawnMs))
        }
        if (tile.merged) {
            delay(slideMs.toLong())
            scale.animateTo(POP_SCALE, tween(popMs / 2))
            scale.animateTo(1f, tween(popMs / 2))
        }
        if (tile.leaving) {
            fade.animateTo(0f, tween(slideMs))
        }
    }

    Box(
        modifier =
            Modifier
                .offset(x = x.value.dp, y = y.value.dp)
                .size(cell)
                .testTag(if (tile.leaving) "board-leaving-${tile.key}" else tileTag(tile.index))
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    alpha = fade.value
                },
    ) {
        TileFace(value = tile.value)
    }
}

/** A tile's colours and numeral: the ramp pair plus the capped numeral size. */
@Composable
private fun TileFace(value: Int) {
    val palette = TilefoldThemeDefaults.palette
    val pair = palette.tile(value)
    val numeral =
        TextCaps.capped(
            base = TypeScale.tileNumeral(value),
            cap = TypeScale.TILE_NUMERAL_MAX_SCALE,
            fontScale = LocalDensity.current.fontScale,
        )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(pair.background, RoundedCornerShape(Corners.Tile)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            color = pair.numeral,
            style =
                MaterialTheme.typography.titleLarge.copy(
                    fontSize = numeral,
                    lineHeight = numeral,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum",
                ),
            maxLines = 1,
        )
    }
}

/** Left edge of a cell, in dp, inside the board's own padding. */
internal fun xOf(
    index: Int,
    cell: Dp,
): Dp = Dimens.BoardPadding + (cell + Dimens.TileGap) * (index % Board.WIDTH)

/** Top edge of a cell, in dp, inside the board's own padding. */
internal fun yOf(
    index: Int,
    cell: Dp,
): Dp = Dimens.BoardPadding + (cell + Dimens.TileGap) * (index / Board.WIDTH)

private const val DIM_ALPHA = 0.45f
private const val SPAWN_SCALE = 0.6f
private const val POP_SCALE = 1.06f
