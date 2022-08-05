package com.roland.android.calculator.data

sealed interface CalculatorActions {
    data class Numbers(val number: String): CalculatorActions
    object Decimal: CalculatorActions
    data class Operators(val operator: CalculatorOperations): CalculatorActions
}