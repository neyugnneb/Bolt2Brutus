package com.example.bolt2brutus.ui

import java.util.Locale

/** Formats a race time as minutes, seconds, and hundredths: 83570 -> "1:23.57". */
fun formatElapsed(ms: Long): String {
    val minutes = ms / 60_000
    val seconds = (ms % 60_000) / 1000
    val hundredths = (ms % 1000) / 10
    return String.format(Locale.US, "%d:%02d.%02d", minutes, seconds, hundredths)
}
