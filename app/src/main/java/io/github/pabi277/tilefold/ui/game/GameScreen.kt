@file:Suppress("MagicNumber")

package io.github.pabi277.tilefold.ui.game

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.domain.Direction
import io.github.pabi277.tilefold.domain.GameStatus
import io.github.pabi277.tilefold.ui.components.ConfirmDialog
import io.github.pabi277.tilefold.ui.components.IconAction
import io.github.pabi277.tilefold.ui.components.InlineBanner
import io.github.pabi277.tilefold.ui.components.OutlineChip
import io.github.pabi277.tilefold.ui.components.TextAction
import io.github.pabi277.tilefold.ui.icons.TilefoldIcons
import io.github.pabi277.tilefold.ui.theme.Corners
import io.github.pabi277.tilefold.ui.theme.Dimens
import io.github.pabi277.tilefold.ui.theme.TextCaps
import io.github.pabi277.tilefold.ui.theme.TilefoldThemeDefaults
import io.github.pabi277.tilefold.ui.theme.TypeScale

internal const val SCORE_TAG = "game-score"
internal const val BEST_TAG = "game-best"
internal const val HINT_TAG = "game-hint"

/** Everything the game screen draws — one value, so a screenshot is one object. */
internal data class GameScreenState(
    val scene: BoardScene,
    val score: Int,
    val best: Int,
    val status: GameStatus,
    val canUndo: Boolean,
    val hintVisible: Boolean,
    val padVisible: Boolean,
    val dimmed: Boolean = false,
    val winDialogVisible: Boolean = false,
    val gameOverDialogVisible: Boolean = false,
    val isNewBest: Boolean = false,
    val canEarnUndo: Boolean = false,
    val storageError: Boolean = false,
    val showRestartConfirm: Boolean = false,
)

/** Everything the player can do on the game screen. */
internal class GameActions(
    val onMove: (Direction) -> Unit,
    val onUndo: () -> Unit,
    val onRewardedUndo: () -> Unit = {},
    val onKeepPlaying: () -> Unit = {},
    val onNewGameFromWin: () -> Unit = {},
    val onNewGameFromGameOver: () -> Unit = {},
    val onDismissGameOver: () -> Unit = {},
    val onRestart: () -> Unit,
    val onConfirmRestart: () -> Unit = onRestart,
    val onDismissRestartConfirm: () -> Unit = {},
    val onHome: () -> Unit,
    val onTogglePad: () -> Unit,
    val onRetryStorage: () -> Unit = {},
)

/**
 * The game screen, exactly the layout of `#game-new` / `#game-mid` in the frozen
 * reference: title row with home and restart, SCORE and BEST boxes, the rounded
 * board, then Undo, the swipe hint and the direction-pad toggle.
 *
 * It is stateless: what it draws comes from the engine via [BoardScene], and every
 * interaction is a callback. That keeps the screenshot goldens deterministic (a
 * fixed state renders a fixed picture) and keeps the rules in one place —
 * `domain/`.
 */
@Composable
internal fun GameScreen(
    state: GameScreenState,
    actions: GameActions,
    modifier: Modifier = Modifier,
    reducedMotion: Boolean = rememberReducedMotion(),
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focusRequester) {
        runCatching { focusRequester.requestFocus() }
    }

    val animatedScore by
        animateIntAsState(
            targetValue = state.score,
            animationSpec = tween(Motion.score(reducedMotion)),
            label = "score",
        )
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val keyHandler: (KeyEvent) -> Boolean = { event ->
        handleKey(event.type, event.key, actions.onMove)
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.ScreenPaddingVertical)
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent(keyHandler)
                    .focusable(),
        ) {
            TitleRow(onHome = actions.onHome, onRestart = actions.onRestart, tint = textPrimary)
            ScoreRow(score = animatedScore, best = state.best)

            if (state.storageError) {
                InlineBanner(
                    message = stringResource(R.string.banner_save_error),
                    detail = stringResource(R.string.banner_save_detail),
                    onRetry = actions.onRetryStorage,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            BoardFrame(
                scene = state.scene,
                onMove = actions.onMove,
                reducedMotion = reducedMotion,
                dim = state.dimmed,
                modifier = Modifier.padding(top = Dimens.BoardControlsGap),
            )

            ControlRow(
                canUndo = state.canUndo,
                hintVisible = state.hintVisible,
                padVisible = state.padVisible,
                tint = textPrimary,
                actions = actions,
            )

            if (state.padVisible) {
                DirectionPad(
                    onMove = actions.onMove,
                    modifier = Modifier.padding(top = Dimens.SameRowGap),
                )
            }
            Spacer(Modifier.height(Dimens.SameRowGap))
        }

        if (state.winDialogVisible) {
            WinDialog(
                score = state.score,
                best = state.best,
                onKeepPlaying = actions.onKeepPlaying,
                onNewGame = actions.onNewGameFromWin,
            )
        }
        if (state.gameOverDialogVisible) {
            GameOverDialog(
                score = state.score,
                best = state.best,
                isNewBest = state.isNewBest,
                canUndo = state.canUndo,
                canEarnUndo = state.canEarnUndo,
                onUndo = actions.onUndo,
                onRewardedUndo = actions.onRewardedUndo,
                onNewGame = actions.onNewGameFromGameOver,
                onHome = actions.onHome,
            )
        }
        if (state.showRestartConfirm) {
            ConfirmDialog(
                title = stringResource(R.string.restart_title),
                body = stringResource(R.string.restart_body),
                confirmLabel = stringResource(R.string.game_restart),
                dismissLabel = stringResource(R.string.cancel),
                onConfirm = actions.onConfirmRestart,
                onDismiss = actions.onDismissRestartConfirm,
            )
        }
    }
}

/** SCORE and BEST, side by side, 12dp apart (the reference's score gap). */
@Composable
private fun ScoreRow(
    score: Int,
    best: Int,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(Dimens.ScoreGap),
    ) {
        ScoreBox(
            label = stringResource(R.string.game_score),
            value = score,
            tag = SCORE_TAG,
            modifier = Modifier.weight(1f),
        )
        ScoreBox(
            label = stringResource(R.string.game_best),
            value = best,
            tag = BEST_TAG,
            modifier = Modifier.weight(1f),
        )
    }
}

/** App name, then the home and restart icon actions, 4dp apart as the reference draws them. */
@Composable
private fun TitleRow(
    onHome: () -> Unit,
    onRestart: () -> Unit,
    tint: Color,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = Dimens.MinTarget),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = tint,
            modifier = Modifier.weight(1f),
        )
        IconAction(
            icon = TilefoldIcons.House,
            label = stringResource(R.string.game_home),
            onClick = onHome,
            tint = tint,
        )
        IconAction(
            icon = TilefoldIcons.RotateCcw,
            label = stringResource(R.string.game_restart),
            onClick = onRestart,
            tint = tint,
        )
    }
}

/** Undo on the left, the hint in the middle, the pad toggle on the right. */
@Composable
private fun ControlRow(
    canUndo: Boolean,
    hintVisible: Boolean,
    padVisible: Boolean,
    tint: Color,
    actions: GameActions,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = Dimens.ControlGap)
                .heightIn(min = Dimens.MinTarget),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextAction(
            label = stringResource(R.string.game_undo),
            onClick = actions.onUndo,
            icon = TilefoldIcons.Undo2,
            enabled = canUndo,
        )
        if (hintVisible) {
            OutlineChip(stringResource(R.string.game_swipe_hint), Modifier.testTag(HINT_TAG))
        }
        IconAction(
            icon = TilefoldIcons.ArrowUp,
            label = stringResource(R.string.game_direction_pad),
            onClick = actions.onTogglePad,
            tint = if (padVisible) MaterialTheme.colorScheme.primary else tint,
        )
    }
}

/**
 * One taupe score box: an uppercase label over a tabular value whose numerals
 * stop growing at 1.5×, so a 2.0× font scale keeps them on one line.
 */
@Composable
private fun ScoreBox(
    label: String,
    value: Int,
    tag: String,
    modifier: Modifier = Modifier,
) {
    val palette = TilefoldThemeDefaults.palette
    val fontScale = LocalDensity.current.fontScale
    val valueStyle =
        MaterialTheme.typography.headlineMedium.copy(
            fontSize = TextCaps.capped(TypeScale.ScoreValue, TypeScale.IN_SCREEN_SCORE_MAX_SCALE, fontScale),
            lineHeight = TextCaps.capped(TypeScale.ScoreValueLine, TypeScale.IN_SCREEN_SCORE_MAX_SCALE, fontScale),
            fontWeight = FontWeight.Bold,
            fontFeatureSettings = "tnum",
        )

    Column(
        modifier =
            modifier
                .background(palette.scoreBox, RoundedCornerShape(Corners.ScoreBox))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = palette.scoreLabel,
            maxLines = 1,
        )
        Text(
            text = ScoreFormat.of(value),
            style = valueStyle,
            color = palette.scoreValue,
            maxLines = 1,
            modifier = Modifier.testTag(tag),
        )
    }
}

/**
 * Arrow keys are the third way to play (with a swipe and the pad), which is what
 * makes the game usable on a Chromebook or with a keyboard attached.
 */
internal fun handleKey(
    type: KeyEventType,
    key: Key,
    onMove: (Direction) -> Unit,
): Boolean {
    if (type != KeyEventType.KeyDown) return false
    val direction = directionFor(key) ?: return false
    onMove(direction)
    return true
}

/** The arrow-key map, kept pure so it is testable without a keyboard. */
internal fun directionFor(key: Key): Direction? =
    when (key) {
        Key.DirectionUp -> Direction.UP
        Key.DirectionDown -> Direction.DOWN
        Key.DirectionLeft -> Direction.LEFT
        Key.DirectionRight -> Direction.RIGHT
        else -> null
    }
