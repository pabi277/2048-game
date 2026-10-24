package io.github.pabi277.tilefold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.pabi277.tilefold.ui.EnginePreview
import io.github.pabi277.tilefold.ui.HelloScreen
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme

/**
 * Single activity for the whole app (single-activity + Compose per
 * `knowledge/app-engineering.md` §1).
 *
 * The debug build shows the phase-1 [EnginePreview] so the progress APK has a
 * board that actually moves; the release build still starts on the phase-0 hello
 * screen, because the designed screens arrive in phase 2 from the frozen
 * reference. R8 removes the unused branch from a release build.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TilefoldTheme {
                if (BuildConfig.DEBUG) {
                    EnginePreview()
                } else {
                    HelloScreen()
                }
            }
        }
    }
}
