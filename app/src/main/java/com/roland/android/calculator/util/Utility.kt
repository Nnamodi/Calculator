package com.roland.android.calculator.util

import android.content.Context
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import com.roland.android.calculator.R
import com.roland.android.calculator.data.ErrorMessage
import com.roland.android.calculator.ui.CalculatorFragment
import com.roland.android.calculator.ui.dialog.SettingsSheet
import com.roland.android.calculator.util.Constants.COMPUTE_FORMAT
import com.roland.android.calculator.util.Constants.HISTORY
import com.roland.android.calculator.util.Constants.NAVIGATE
import com.roland.android.calculator.util.Constants.SET_THEME
import com.roland.android.calculator.util.Constants.THEME
import com.roland.android.calculator.util.Preference.getComputeFormat

object Utility {
    // equate closeBrackets with openBrackets if need be, before calculating.
    fun optimizedInput(input: String): String {
        var optimized = input
        val openBrackets: Int = input.count { it == '(' }
        var closeBrackets: Int = input.count { it == ')' }
        while (openBrackets > closeBrackets) {
            optimized += ")"; closeBrackets += 1
        }
        return optimized
    }

    fun e(e: String): String {
        val error = when {
            Constants.UNKNOWN_UNARY in e -> ErrorMessage.Unknown
            Constants.UNKNOWN_OPERATOR in e -> ErrorMessage.Unknown
            Constants.DIVIDE_0 in e -> ErrorMessage.Division0
            Constants.MISSING_PARAM in e -> ErrorMessage.MissingParam
            Constants.MISMATCHED_PAR in e -> ErrorMessage.Mismatched
            Constants.ROUNDING_NEC in e -> ErrorMessage.RoundNec
            Constants.INFINITY in e -> ErrorMessage.Infinity
            Constants.NEGATIVE_SQRT in e -> ErrorMessage.NegSqrt
            else -> ErrorMessage.Undefined
        }
        return error.message
    }

    fun String.format(context: Context): Spanned {
        val strippedInput = dropLastWhile { it == '0' }
        val trailingZeros = takeLastWhile { it == '0' }
        val text = context.getString(R.string.exponent_result)
        val dynamicResult = String.format(text, "$strippedInput×10", trailingZeros.count())
        return when (getComputeFormat(context)) {
            R.id.plain -> { HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT) }
            R.id.exponent -> if (strippedInput.isNotBlank() && trailingZeros.count() > 0) {
                HtmlCompat.fromHtml(dynamicResult, HtmlCompat.FROM_HTML_MODE_COMPACT)
            } else {
                HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
            else -> { try {
                Fractionize(this).evaluate(context)
            } catch (e: Exception) {
                HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
            } }
        }
    }

    fun String.string(context: Context, resource: Int = R.string.arc_trig): Spanned {
        val text = context.getString(resource)
        val styledText = String.format(text, this)
        return HtmlCompat.fromHtml(styledText, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    fun Fragment.lifecycleObserver() {
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (this is CalculatorFragment) {
                if (event == Lifecycle.Event.ON_STOP) {
                    findNavController().currentBackStackEntry?.apply {
                        savedStateHandle[HISTORY] = ""; savedStateHandle[NAVIGATE] = 0
                        savedStateHandle[THEME] = false; savedStateHandle[SET_THEME] = false
                        savedStateHandle[COMPUTE_FORMAT] = 0
                    }
                }
            } else {
                if (event == Lifecycle.Event.ON_START) {
                    findNavController().previousBackStackEntry?.apply {
                        savedStateHandle[HISTORY] = ""; savedStateHandle[NAVIGATE] = 0
                        savedStateHandle[THEME] = false; savedStateHandle[SET_THEME] = false
                        savedStateHandle[COMPUTE_FORMAT] = 0
                    }
                }
                if (this is SettingsSheet) {
                    if (event == Lifecycle.Event.ON_STOP) {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(THEME, false)
                    }
                }
            }
            Log.d("LifecycleEventObserver", "lifecycleObserver for $this at $event")
        })
    }
}