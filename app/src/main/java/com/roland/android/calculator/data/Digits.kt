package com.roland.android.calculator.data

data class Digits(
    var digit_1: String = "",
    var digit_2: String = "",
    var result: String = "",
    var operator: CalculatorOperations? = null,
    var error: Boolean = false
)