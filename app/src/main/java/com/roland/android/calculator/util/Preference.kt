package com.roland.android.calculator.util

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

object Preference {
    private const val THEME = "theme"

    fun getTheme(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(THEME, 2)
    }

    fun setTheme(context: Context, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putInt(THEME, value)
        }
    }
}