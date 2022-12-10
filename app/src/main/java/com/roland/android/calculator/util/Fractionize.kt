package com.roland.android.calculator.util

import android.content.Context
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.roland.android.calculator.R
import com.roland.android.calculator.util.Constants.MINUS

class Fractionize(val input: String) {
    private val digit = input.replace(MINUS, "-").toBigDecimal()
    private val wholeNumber = digit.toString().dropLastWhile { it != '.' }.dropLast(1)
    private var fractionNum = digit.minus(wholeNumber.toBigDecimal()).toString()
        .filter { it.isDigit() }.drop(1)
    private var numerator = fractionNum.toInt()
    private var numLength = fractionNum.length
    private var denominator = 1
    private var divisor = 10

    private fun getDenominator(): Int {
        while (numLength > 0) {
            denominator = (denominator.toString() + "0").toInt()
            numLength -= 1
        }
        return denominator
    }

    private fun numerate() {
        denominator = getDenominator()
        while (divisor > 1) {
            if (numerator % divisor == 0 && denominator % divisor == 0) {
                numerator = numerator.div(divisor)
                denominator = denominator.div(divisor)
            } else { divisor -= 1 }
        }
    }

    private fun Context.fraction(wholeNumber: String, numerator: Int, denominator: Int): Spanned {
        wholeNumber.trimStart()
        numerator.toString(); denominator.toString()
        val text = getString(R.string.fractionized)
        val styled = String.format(text, wholeNumber, numerator, denominator).replace("-", MINUS)
        return HtmlCompat.fromHtml(styled, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    fun evaluate(context: Context): Spanned {
        numerate()
        val wholeNumber = when (wholeNumber) {
            "-0" -> { "-" }; "0" -> { "" }
            else -> wholeNumber
        }
        var fraction = HtmlCompat.fromHtml("", HtmlCompat.FROM_HTML_MODE_COMPACT)
        if (divisor == 1) {
            fraction = context.fraction(wholeNumber, numerator, denominator)
        }
        return fraction
    }
}