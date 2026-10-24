package io.github.pabi277.tilefold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.pabi277.tilefold.ui.game.GameRoute
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme

/**
 * Single activity for the whole app (single-activity + Compose per
 * `knowledge/app-engineering.md` §1).
 *
 * Phase 2 puts the real game screen in front of the player: [GameRoute] draws the
 * approved layout, plays the board and wires the swipe, the pad and the arrow
 * keys. Home, dialogs and navigation arrive with their own phases
 * (`docs/ROADMAP.md`); until then the game screen is the app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TilefoldTheme {
                GameRoute()
            }
        }
    }
}
