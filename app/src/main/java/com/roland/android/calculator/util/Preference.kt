package com.roland.android.calculator.util

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.roland.android.calculator.R
import com.roland.android.calculator.util.Constants.DEG_RAD
import com.roland.android.calculator.util.Constants.HAPTIC
import com.roland.android.calculator.util.Constants.RAD
import com.roland.android.calculator.util.Constants.THEME
import com.roland.android.calculator.util.Constants.COMPUTE_FORMAT
import com.roland.android.calculator.util.Constants.SAVE_ERROR
import com.roland.android.calculator.util.Constants.SAVE_HISTORY

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

    fun getComputeFormat(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(COMPUTE_FORMAT, R.id.plain)
    }

    fun setComputeFormat(context: Context, showZeros: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putInt(COMPUTE_FORMAT, showZeros)
        }
    }

    fun getSaveHistory(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(SAVE_HISTORY, true)
    }

    fun setSaveHistory(context: Context, save: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(SAVE_HISTORY, save)
        }
    }

    fun getSaveErrors(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(SAVE_ERROR, true)
    }

    fun setSaveErrors(context: Context, save: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(SAVE_ERROR, save)
        }
    }
}