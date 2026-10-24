package io.github.pabi277.tilefold.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.pabi277.tilefold.R
import io.github.pabi277.tilefold.ui.theme.TilefoldTheme

/**
 * Phase-0 hello screen: proves the build, the theme seed, the localisation
 * plumbing and the screenshot lane. It is not a game screen and is replaced as
 * soon as the approved reference exists (phase 0-D / phase 2).
 */
@Composable
fun HelloScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.hello_title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.hello_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .padding(top = 24.dp)
                .widthIn(min = 200.dp),
        ) {
            Text(
                text = stringResource(R.string.hello_phase),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
        Text(
            text = stringResource(R.string.hello_footer),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 32.dp),
        )
    }
}

@Preview(name = "Hello light", showBackground = true)
@Composable
internal fun HelloScreenLightPreview() {
    TilefoldTheme(darkTheme = false) { HelloScreen() }
}

@Preview(name = "Hello dark", showBackground = true)
@Composable
internal fun HelloScreenDarkPreview() {
    TilefoldTheme(darkTheme = true) { HelloScreen() }
}

@Preview(name = "Hello large font", fontScale = 2.0f, showBackground = true)
@Composable
internal fun HelloScreenLargeFontPreview() {
    TilefoldTheme(darkTheme = false) { HelloScreen() }
}
