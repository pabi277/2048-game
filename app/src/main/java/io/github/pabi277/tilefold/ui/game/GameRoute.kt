package io.github.pabi277.tilefold.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * The game, wired together: one [GameSession] plus the pause behaviour.
 *
 * Three things live here and nowhere else:
 * - **auto-pause**: the app going to the background pauses the game, so a phone
 *   call never leaves a running timer or an accidental swipe half-applied;
 * - **the pause sheet** drawn over the dimmed board, dismissed by Resume or the
 *   system back gesture;
 * - **cleanup of absorbed tiles**, so a merge leaves no invisible remnants
 *   behind after its slide finishes.
 */
@Composable
fun GameRoute(
    modifier: Modifier = Modifier,
    seed: Long? = null,
) {
    // A real game is seeded from the clock; a test passes a seed and gets the
    // same board every run (`rule.md` §5 — a board is always reproducible).
    val startSeed = remember(seed) { seed ?: Random.nextLong() }
    val session = remember(startSeed) { GameSession(startSeed) }
    val actions = remember(session) { session.actions() }
    val reducedMotion = rememberReducedMotion()
    val lifecycleOwner = LocalLifecycleOwner.current
    val leaving = session.scene.leaving

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) session.pause()
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // A faded-out tile is only needed while its slide runs.
    LaunchedEffect(leaving) {
        if (leaving.isEmpty()) return@LaunchedEffect
        delay((Motion.slide(reducedMotion) + Motion.pop(reducedMotion)).toLong())
        session.clearLeaving()
    }

    BackHandler(enabled = session.paused) { session.resume() }

    Box(modifier = modifier.fillMaxSize()) {
        GameScreen(
            state = session.screenState,
            actions = actions,
            reducedMotion = reducedMotion,
        )
        if (session.paused) {
            PauseSheet(
                score = session.game.score,
                highestTile = session.highestTile,
                onResume = { session.resume() },
                onRestart = { session.restart() },
            )
        }
    }
}
