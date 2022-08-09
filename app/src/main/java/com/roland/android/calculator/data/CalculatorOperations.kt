package com.roland.android.calculator.data

sealed class CalculatorOperations(val symbol: String) {
    object Add: CalculatorOperations("+")
    object Subtract: CalculatorOperations("-")
    object Divide: CalculatorOperations("÷")
    object Multiply: CalculatorOperations("×")
}
