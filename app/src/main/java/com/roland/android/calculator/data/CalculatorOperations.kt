package com.roland.android.calculator.data

import com.roland.android.calculator.util.Constants.ADD
import com.roland.android.calculator.util.Constants.DIVIDE
import com.roland.android.calculator.util.Constants.MOD
import com.roland.android.calculator.util.Constants.MULTIPLY
import com.roland.android.calculator.util.Constants.MINUS

sealed class CalculatorOperations(val symbol: String) {
    object Add: CalculatorOperations(ADD)
    object Subtract: CalculatorOperations(MINUS)
    object Divide: CalculatorOperations(DIVIDE)
    object Multiply: CalculatorOperations(MULTIPLY)
    object Modulus: CalculatorOperations(MOD)
}
