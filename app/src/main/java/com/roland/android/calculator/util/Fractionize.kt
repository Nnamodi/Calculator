package com.roland.android.calculator.util

class Fractionize(input: String) {
    private val digit = input.toBigDecimal()
    private val defraction = digit.toString().dropLastWhile { it != '.' }
    private val wholeNumber = defraction.toBigDecimal()
    private var fractionNum = digit.minus(wholeNumber).toString().filter { it.isDigit() }.drop(1)
    private var numerator = fractionNum.toInt()
    private var numLength = fractionNum.length
    private var denominator = 1
    private var divisor = 10

    // numerator must be >= 9 digits
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
        val wholeNumber = if (wholeNumber.toInt() > 0) { wholeNumber } else { "" }
        var fraction = ""
        if (divisor == 1) {
            fraction = "$wholeNumber $numerator/$denominator".trimStart()
        }
        return fraction
    }
}

fun main() {
    while (true) {
        print("> Enter any decimal digit: ")
        println(Fractionize(readLine()!!).evaluate())
    }
}