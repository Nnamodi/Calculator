package com.roland.android.calculator.util

class Fractionize(input: String) {
    private val digit = input.replace(Constants.MINUS, "-").toBigDecimal()
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

    fun evaluate(): String {
        numerate()
        val wholeNumber = when (wholeNumber) {
            "-0" -> { "-" }; "0" -> { "" }
            else -> "$wholeNumber "
        }
        var fraction = ""
        if (divisor == 1) {
            fraction = "$wholeNumber$numerator/$denominator".trimStart()
        }
        return fraction.replace("-", Constants.MINUS)
    }
}

fun main() {
    while (true) {
        print("> Enter any decimal digit: ")
        try { println(Fractionize(readln()).evaluate()) }
        catch (e: Exception) { println("! Input error") }
    }
}