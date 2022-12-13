package com.roland.android.calculator.data

sealed interface CalculatorActions {
    object Clear: CalculatorActions
    object Delete: CalculatorActions
    object Decimal: CalculatorActions
    object Calculate: CalculatorActions
    object Euler: CalculatorActions
    object EulerInv: CalculatorActions
    object Bracket: CalculatorActions
    object Pi : CalculatorActions
    object Log : CalculatorActions
    object LogInv : CalculatorActions
    object LogN : CalculatorActions
    object Square : CalculatorActions
    object SquareInv : CalculatorActions
    object SquareRoot: CalculatorActions
    object DegRad: CalculatorActions
    object Factorial: CalculatorActions
    object ComputeFormat: CalculatorActions
    data class EnterEquation(val equation: String): CalculatorActions
    data class Numbers(val number: String): CalculatorActions
    data class Trigonometry(val function: TrigFunctions): CalculatorActions
    data class Operators(val operator: CalculatorOperations): CalculatorActions
}