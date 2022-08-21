package com.roland.android.calculator.data

sealed interface CalculatorActions {
    data class Numbers(val number: String): CalculatorActions
    object Clear: CalculatorActions
    object Delete: CalculatorActions
    object Decimal: CalculatorActions
    object Calculate: CalculatorActions
    object PlusMinus: CalculatorActions
    object Bracket: CalculatorActions
    data class Operators(val operator: CalculatorOperations): CalculatorActions
}