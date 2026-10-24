package io.github.pabi277.tilefold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.pabi277.tilefold.ui.HelloScreen
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme

/**
 * Single activity for the whole app (single-activity + Compose per
 * `knowledge/app-engineering.md` §1). Screens and navigation arrive with the
 * approved reference; phase 0 shows the hello screen only.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TilefoldTheme {
                HelloScreen()
            }
        }
    }
}
