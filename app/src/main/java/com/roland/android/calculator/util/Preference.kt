package com.roland.android.calculator.util

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.roland.android.calculator.util.Constants.DEG_RAD
import com.roland.android.calculator.util.Constants.HAPTIC
import com.roland.android.calculator.util.Constants.RAD
import com.roland.android.calculator.util.Constants.THEME

object Preference {
    fun getTheme(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(THEME, 2)
    }

    fun setTheme(context: Context, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putInt(THEME, value)
        }
    }

    fun getDegRad(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(DEG_RAD, RAD)
    }

    fun setDegRad(context: Context, value: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(DEG_RAD, value)
        }
    }

    fun getHaptic(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(HAPTIC, true)
    }

    fun setHaptic(context: Context, vibrate: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(HAPTIC, vibrate)
        }
    }
}