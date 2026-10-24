package io.github.pabi277.tilefold.data

import androidx.datastore.preferences.core.stringPreferencesKey

internal object PreferencesKeys {
    val SaveGame = stringPreferencesKey("save.v1.game")
    val Stats = stringPreferencesKey("stats.v1.blob")
    val Settings = stringPreferencesKey("settings.v1.blob")
    val InterstitialCount = stringPreferencesKey("ads.interstitial.count")
    val InterstitialLastShown = stringPreferencesKey("ads.interstitial.last")
}
