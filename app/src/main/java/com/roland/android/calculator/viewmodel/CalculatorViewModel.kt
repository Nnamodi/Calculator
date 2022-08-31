package com.roland.android.calculator.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.data.Digits
import com.roland.android.calculator.data.TrigFunctions
import com.udojava.evalex.Expression
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val _stateFlow = MutableStateFlow(Digits())
    val stateFlow = _stateFlow.asStateFlow()
    private var inputIsAnswer = false
    private var currentDigit = ""

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
            is CalculatorActions.Trigonometry -> { trigonometricFunction(action.function) }
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
        if (input.isNotBlank() && input.last() != '.') {
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
            input.endsWith(")") && openBrackets == closeBrackets -> { _stateFlow.value = Digits(input = "$input(") }
            input.endsWith("(") -> { _stateFlow.value = Digits(input = "$input(") }
            openBrackets > closeBrackets && input.last() == ')' -> { _stateFlow.value = Digits(input = "$input)") }
            openBrackets > closeBrackets && input.last().isDigit() -> { _stateFlow.value = Digits(input = "$input)") }
            input.isBlank() -> { _stateFlow.value = Digits(input = "(") }
            input.last().isDigit() && openBrackets == closeBrackets -> { _stateFlow.value = Digits(input = "$input(") }
        }
        apply {
            val openedBrackets: Int = _stateFlow.value.input.count { it == '(' }
            val closedBrackets: Int = _stateFlow.value.input.count { it == ')' }
            if (openedBrackets == closedBrackets) { calculateInput() }
        }; inputIsAnswer = false
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
                val expression = Expression(input).setPrecision(20)
                val calcResult = expression.eval(false).toString()
                val result = if (calcResult.endsWith(".0")) { calcResult.dropLast(2) } else { calcResult }
                _stateFlow.value = Digits(input = _stateFlow.value.input, result = result)
            }
            // if `equal button` is pressed and there's a result already, show only result
            if (equalled && _stateFlow.value.result.isNotBlank()) {
                _stateFlow.value = Digits(input = _stateFlow.value.result)
                inputIsAnswer = true; currentDigit = ""
            }
        } catch (e: Exception) {
            // if `equal button` is pressed, show error message
            if (equalled) { _stateFlow.value = Digits(input = _stateFlow.value.input, error = true) }
            Log.e("SyntaxError", "Input error: ", e)
        }
    }

    private fun enterNumber(digit: String) {
        if (inputIsAnswer) { _stateFlow.value = Digits(input = digit); inputIsAnswer = false }
        else {
            if (_stateFlow.value.input.endsWith(")")) {
                _stateFlow.value = Digits(input = _stateFlow.value.input + "×" + digit)
            }
            else { _stateFlow.value = Digits(input = _stateFlow.value.input + digit) }
        }
        // to get the numbers after the last operator
        currentDigit += digit
        val input = _stateFlow.value.input
        val operator = input.filter { !it.isDigit() }.toList()
        // calculate input only if an operator has been entered.
        if (_stateFlow.value.input.isNotBlank()) {
            if (input.all { !it.isDigit() } && !input.startsWith("-") || input.first() == '(') { calculateInput() }
            if (!input.first().isDigit() && operator.size > 1) { calculateInput() }
            if (input.first().isDigit() && (input.all { !it.isDigit() } || operator.isNotEmpty())) { calculateInput() }
        }
        Log.d("CurrentDigit", "enterNumber: $currentDigit")
    }

    private fun enterOperation(operator: CalculatorOperations) {
        val input = _stateFlow.value.input
        if (input.isNotBlank()) {
            val lastTwoAreSymbols = when {
                input.endsWith("÷-") -> true
                input.endsWith("×-") -> true
                input.endsWith("+-") -> true
                input.endsWith("--") -> true
                input.endsWith(".") -> true
                else -> false
            }
            val lastDigit = input.last()
            if (!lastDigit.isDigit()) {
                if (!lastDigit.isDigit() && input != "-" && !input.endsWith("(-") && !lastTwoAreSymbols) {
                    // if lastDigit is an operator and end with neither `(` nor `)`
                    if (lastDigit != '(' && operator.symbol != "%") {
                        if (lastDigit != ')') { _stateFlow.value = Digits(input = input.dropLast(1)) }
                        _stateFlow.value = Digits(input = _stateFlow.value.input + operator.symbol)
                    }
                }
            } else {
                if (lastDigit != '(' && operator.symbol != "%") {
                    _stateFlow.value = Digits(input = input + operator.symbol)
                }
            }
            if (operator.symbol == "%" && lastDigit.isDigit() || lastDigit == '%' || lastDigit == ')') {
                _stateFlow.value = Digits(input = input + operator.symbol); calculateInput()
            }
            currentDigit = ""
        }
        // if input contains digits and operator is `%`, calculate input
        if (_stateFlow.value.input.all { !it.isDigit() } && operator.symbol == "%") { calculateInput() }
        inputIsAnswer = false
    }

    private fun trigonometricFunction(functions: TrigFunctions) {
        val input = _stateFlow.value.input
        if (input.isNotBlank()) {
            if (input.endsWith(")") || input.last().isDigit() || input.endsWith("%")) {
                _stateFlow.value = Digits(input = input + "×" + functions.symbol)
            } else { _stateFlow.value = Digits(input = input + functions.symbol) }
            inputIsAnswer = false
        } else { _stateFlow.value = Digits(input = functions.symbol) }
    }

    private fun clearInput() { _stateFlow.value = Digits(); currentDigit = "" }

    private fun deleteInput() {
        if (!inputIsAnswer) {
            if (_stateFlow.value.input.last().isDigit()) { currentDigit = currentDigit.dropLast(1) }
            val input: String = when {
                _stateFlow.value.input.endsWith("sin(") -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith("cos(") -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith("tan(") -> _stateFlow.value.input.dropLast(4)
                else -> _stateFlow.value.input.dropLast(1)
            }
            _stateFlow.value = Digits(input = input)
        }
        val input = _stateFlow.value.input
        val operator = input.filter { !it.isDigit() }.toList()
        // calculate input if it contains operator and ends with digit
        if (input.isNotBlank()) {
            if (input.all { !it.isDigit() } && input.first() != '-' || input.first() == '(') { calculateInput() }
            if (!input.first().isDigit() && operator.size > 1) { calculateInput() }
            if (input.first().isDigit() && (input.all { !it.isDigit() } || operator.isNotEmpty())) { calculateInput() }
        }
    }

    private fun enterDecimal() {
        if (!currentDigit.contains(".")) {
            if (inputIsAnswer) {
                _stateFlow.value = Digits(input = "."); inputIsAnswer = false
            } else {
                if (_stateFlow.value.input.endsWith(")")) {
                    _stateFlow.value = Digits(input = _stateFlow.value.input + "×" + ".")
                } else { _stateFlow.value = Digits(input = _stateFlow.value.input + ".") }
            }
            // update variable holding current digit
            currentDigit += "."
        }
    }
}