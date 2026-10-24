package io.github.pabi277.tilefold.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.pabi277.tilefold.data.AppContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The game, wired together: one [GameSession] plus the pause behaviour and
 * persistence.
 *
 * Three things live here and nowhere else:
 * - **auto-pause**: the app going to the background pauses the game, so a phone
 *   call never leaves a running timer or an accidental swipe half-applied;
 * - **the pause sheet** drawn over the dimmed board, dismissed by Resume or the
 *   system back gesture;
 * - **cleanup of absorbed tiles**, so a merge leaves no invisible remnants
 *   behind after its slide finishes.
 *
 * Persistence: on every valid move debounced ≤250 ms and on `ON_STOP` immediately,
 * resuming on launch through [io.github.pabi277.tilefold.domain.SaveCodec].
 */
@Composable
fun GameRoute(
    modifier: Modifier = Modifier,
    seed: Long? = null,
    container: AppContainer? = null,
) {
    val context = LocalContext.current
    val appContainer = container ?: remember { AppContainer(context.applicationContext) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val session = remember(seed, appContainer) {
        val s = GameSession(seed = seed ?: kotlin.random.Random.nextLong())
        s.onGameChanged = { game ->
            appContainer.gameSaveRepository.saveDebounced(scope, game)
        }
        s.onStatsChanged = { stats ->
            scope.launch { appContainer.statsRepository.save(stats) }
        }
        s
    }
    var isRestoring by remember { mutableStateOf(true) }
    val actions = remember(session) { session.actions() }
    val reducedMotion = rememberReducedMotion()
    val leaving = session.scene.leaving

    LaunchedEffect(appContainer) {
        val restored = appContainer.gameSaveRepository.load()
        val stats = appContainer.statsRepository.load()
        if (appContainer.gameSaveRepository.loadError || appContainer.statsRepository.loadError) {
            session.setStorageError(true)
        }
        if (restored != null) {
            session.restore(restored, stats)
        } else {
            session.updateStatsExplicit(stats)
        }
        isRestoring = false
    }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) {
                    session.pause()
                    scope.launch { appContainer.gameSaveRepository.saveNow(session.game) }
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(leaving) {
        if (leaving.isEmpty()) return@LaunchedEffect
        delay((Motion.slide(reducedMotion) + Motion.pop(reducedMotion)).toLong())
        session.clearLeaving()
    }

    BackHandler(enabled = session.paused) { session.resume() }

    if (isRestoring) {
        Box(modifier = modifier.fillMaxSize())
    } else {
        GameContent(
            session = session,
            actions = actions,
            reducedMotion = reducedMotion,
            modifier = modifier.fillMaxSize(),
        )
    }
}

/**
 * The screen plus its pause sheet, without the lifecycle wiring.
 *
 * [GameRoute] owns the session and the auto-pause; this is what both the route
 * and the Robolectric tests draw, so the tests exercise the real overlay instead
 * of a re-implementation of it.
 */
@Composable
internal fun GameContent(
    session: GameSession,
    actions: GameActions,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
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
