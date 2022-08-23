package com.roland.android.calculator.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.data.Digits
import com.udojava.evalex.Expression
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val _stateFlow = MutableStateFlow(Digits())
    val stateFlow = _stateFlow.asStateFlow()
    private var inputIsAnswer = false

    fun onAction(action: CalculatorActions) {
        when (action) {
            is CalculatorActions.Numbers -> { enterNumber(action.number) }
            is CalculatorActions.Clear -> { clearInput() }
            is CalculatorActions.Delete -> { deleteInput() }
            is CalculatorActions.Decimal -> { enterDecimal() }
            is CalculatorActions.Operators -> { enterOperation(action.operator) }
            is CalculatorActions.Calculate -> { calculateInput(true) }
            is CalculatorActions.Bracket -> { addBracket() }
            is CalculatorActions.PlusMinus -> { addPlusMinus() }
        }
    }

    private fun addPlusMinus() {
        val input = _stateFlow.value.input
        val digitIsNegative = when {
            _stateFlow.value.input.endsWith("+-") -> { true }
            _stateFlow.value.input.endsWith("%-") -> { true }
            _stateFlow.value.input.endsWith("×-") -> { true }
            _stateFlow.value.input.endsWith("÷-") -> { true }
            _stateFlow.value.input.endsWith("--") -> { true }
            _stateFlow.value.input.endsWith("(-") -> { true }
            _stateFlow.value.input == "-" -> { true }
            else -> { false }
        }
        if (input.isNotBlank()) {
            if (!input.last().isDigit() && !digitIsNegative) { _stateFlow.value = Digits(input = "$input-") }
        } else { _stateFlow.value = Digits(input = "-") }
        if (digitIsNegative) {
            _stateFlow.value = Digits(input = _stateFlow.value.input.dropLast(1))
        }
        if (_stateFlow.value.input.isNotBlank()) {
            if (_stateFlow.value.input.last() == '%') {
                calculateInput()
            }
        }
    }

    private fun addBracket() {
        val input = _stateFlow.value.input
        val openBrackets: Int = input.count { it == '(' }
        val closeBrackets: Int = input.count { it == ')' }
        when {
            input.endsWith(")") || openBrackets == closeBrackets -> { _stateFlow.value = Digits(input = "$input(") }
            input.endsWith("(") -> { _stateFlow.value = Digits(input = "$input(") }
            openBrackets > closeBrackets && !input.last().isDigit() -> { _stateFlow.value = Digits(input = "$input)") }
            input.isBlank() -> { _stateFlow.value = Digits(input = "(") }
            input.last().isDigit() -> { _stateFlow.value = Digits(input = "$input(") }
        }
        apply {
            val openedBrackets: Int = _stateFlow.value.input.count { it == '(' }
            val closedBrackets: Int = _stateFlow.value.input.count { it == ')' }
            if (openedBrackets == closedBrackets) { calculateInput() }
        }
    }

    private fun calculateInput(equalled: Boolean = false) {
        try {
            val input = _stateFlow.value.input.replace(Regex("[÷×%]")) {
                when (it.value) {
                    "÷" -> "/"
                    "×" -> "*"
                    "%" -> "/100"
                    else -> it.value
                }
            }
            if (input.last().isDigit() || input.endsWith(")")) {
                val calcResult = Expression(input).eval()
                    .toString().take(15)
                val result = if (calcResult.endsWith(".0")) { calcResult.dropLast(2) } else { calcResult }
                apply {
                    _stateFlow.value = Digits(input = _stateFlow.value.input, result = result)
                }
            }
            // if `equal button` is pressed and there's a result already, show only result
            if (equalled && _stateFlow.value.result.isNotBlank()) {
                _stateFlow.value = Digits(input = _stateFlow.value.result)
                inputIsAnswer = true
            }
        } catch (e: Exception) {
            // if `equal button` is pressed, show error message
            if (equalled) { _stateFlow.value = Digits(input = _stateFlow.value.input, error = true) }
            Log.e("SyntaxError", "Input error: ", e)
        }
    }

    private fun enterNumber(digit: String) {
        val input = _stateFlow.value.input
        _stateFlow.value = Digits(input = _stateFlow.value.input + digit)
        // calculate input only if an operator has been entered.
        if (!input.all { it.isDigit() } &&
            (input.first().isDigit() || input.first() == '(')) { calculateInput() }
    }

    private fun enterOperation(operator: CalculatorOperations) {
        if (_stateFlow.value.input.isNotBlank()) {
            val lastDigit = _stateFlow.value.input.last()
            if (!lastDigit.isDigit()) {
                // if lastDigit is an operator and end with neither `(` nor `)`
                if (lastDigit != '(' && lastDigit != ')') {
                    _stateFlow.value = Digits(
                        input = _stateFlow.value.input.replace(lastDigit.toString(), operator.symbol)
                    )
                }
            } else {
                if (lastDigit != '(') {
                    _stateFlow.value = Digits(input = _stateFlow.value.input + operator.symbol)
                }
            }
        }
//        if (operator.symbol == "%") {
//            _stateFlow.value = Digits(input = _stateFlow.value.input + operator.symbol)
//        }
        // if input contains digits and operator is `%`, calculate input
        if (!_stateFlow.value.input.all { it.isDigit() } && operator.symbol == "%") { calculateInput() }
        inputIsAnswer = false
    }

    private fun clearInput() { _stateFlow.value = Digits() }

    private fun deleteInput() {
        val zeroToNine = (0..9).toString().toList()
        if (!inputIsAnswer) {
            _stateFlow.value = Digits(input = _stateFlow.value.input.dropLast(1))
        }
        val input = _stateFlow.value.input
        val number = input.findLast { it in zeroToNine; true }.toString()
        // calculate input if it contains operator and ends with digit
        if (!input.all { it.isDigit() } && input.endsWith(number) && !inputIsAnswer) { calculateInput() }
    }

    // TODO()
    private fun enterDecimal() {
        val operator = _stateFlow.value.input.filter { !it.isDigit() }
        val lastOperator = operator.last().toString().lastIndex.until(_stateFlow.value.input.toInt())
        if (!lastOperator.contains(".".toInt())) {
            if (inputIsAnswer) { _stateFlow.value = Digits(input = "."); inputIsAnswer = false }
            else { _stateFlow.value = Digits(input = _stateFlow.value.input + ".") }
        }
    }
}