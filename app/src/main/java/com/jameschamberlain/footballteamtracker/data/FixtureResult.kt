package com.jameschamberlain.footballteamtracker.data

import android.content.Context
import androidx.core.content.ContextCompat
import com.jameschamberlain.footballteamtracker.R

enum class FixtureResult(val text: String) {
    WIN("W"),
    LOSS("L"),
    DRAW("D"),
    UNPLAYED("");

    companion object {
        fun getColor(result: FixtureResult, context: Context): Int {
            return when(result) {
                WIN -> {
                    ContextCompat.getColor(context, R.color.colorWin)
                }
                LOSS -> {
                    ContextCompat.getColor(context, R.color.colorLoss)
                }
                DRAW -> {
                    ContextCompat.getColor(context, R.color.colorDraw)
                }
                else -> {
                    -1
                }
            }
        }
    }
}