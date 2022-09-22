package com.roland.android.calculator.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.data.Digits
import com.roland.android.calculator.data.TrigFunctions
import com.roland.android.calculator.util.Constants.ADD
import com.roland.android.calculator.util.Constants.COS
import com.roland.android.calculator.util.Constants.COSR
import com.roland.android.calculator.util.Constants.DEG
import com.roland.android.calculator.util.Constants.DIVIDE
import com.roland.android.calculator.util.Constants.DOT
import com.roland.android.calculator.util.Constants.LOG
import com.roland.android.calculator.util.Constants.MINUS
import com.roland.android.calculator.util.Constants.MOD
import com.roland.android.calculator.util.Constants.MULTIPLY
import com.roland.android.calculator.util.Constants.PI
import com.roland.android.calculator.util.Constants.RAD
import com.roland.android.calculator.util.Constants.ROOT
import com.roland.android.calculator.util.Constants.SIN
import com.roland.android.calculator.util.Constants.SINR
import com.roland.android.calculator.util.Constants.TAN
import com.roland.android.calculator.util.Constants.TANR
import com.roland.android.calculator.util.Fractionize
import com.roland.android.calculator.util.Preference
import com.udojava.evalex.Expression
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel(private val app: Application) : AndroidViewModel(app) {
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
            is CalculatorActions.Trigonometry -> { trigonometricFunction(action.function) }
            is CalculatorActions.Pi -> { addPi() }
            is CalculatorActions.Log -> { addLog() }
            is CalculatorActions.Square -> { addSquare() }
            is CalculatorActions.SquareRoot -> { addSquareRoot() }
            is CalculatorActions.DegRad -> { degRad() }
        }
    }

    private fun degRad() {
        val degRad = Preference.getDegRad(app) == RAD
        val value = if (degRad) { DEG } else { RAD }
        Preference.setDegRad(app, value)
        calculateInput()
    }

    private fun addSquareRoot() {
        val input = _stateFlow.value.input
        val symbols = setOf(DIVIDE, MULTIPLY, MINUS, ADD, "(")
        if (symbols.any { input.endsWith(it) } || input.isBlank()) { _stateFlow.value = Digits(input = input + ROOT) }
        else { _stateFlow.value = Digits(input = "$input×$ROOT"); inputIsAnswer = false }
    }

    private fun addSquare() {
        val input = _stateFlow.value.input
        val symbols = setOf(')', 'π', '%')
        val addSquare = { _stateFlow.value = Digits(input = "$input^"); inputIsAnswer = false }
        if (input.isNotBlank()) {
            when {
                input.last().isDigit() -> { addSquare() }
                symbols.any { input.last() == it } -> { addSquare() }
            }
        }
    }

    private fun addLog() {
        val input = _stateFlow.value.input
        val symbols = setOf(")", MOD, PI)
        if (input.isNotBlank()) {
            if (symbols.any { input.endsWith(it) } || input.last().isDigit()) {
                _stateFlow.value = Digits(input = "$input×$LOG")
            } else { _stateFlow.value = Digits(input = input + LOG) }
            inputIsAnswer = false
        } else { _stateFlow.value = Digits(input = LOG) }
    }

    private fun addPi() {
        val input = _stateFlow.value.input
        when {
            input.isBlank() -> { _stateFlow.value = Digits(input = PI) }
            input.last().isDigit() -> { _stateFlow.value = Digits(input = "$input×$PI") }
            else -> { _stateFlow.value = Digits(input = input + PI) }
        }
        calculateInput()
    }

    private fun addPlusMinus() {
        val input = _stateFlow.value.input
        val digitIsNegative = when {
            _stateFlow.value.input.endsWith("+−") -> { true }
            _stateFlow.value.input.endsWith("%−") -> { true }
            _stateFlow.value.input.endsWith("×−") -> { true }
            _stateFlow.value.input.endsWith("÷−") -> { true }
            _stateFlow.value.input.endsWith("−−") -> { true }
            _stateFlow.value.input.endsWith("(−") -> { true }
            _stateFlow.value.input == "−" -> { true }
            else -> { false }
        }
        if (input.isNotBlank() && input.last() != '.') {
            if (!input.last().isDigit() && !digitIsNegative) { _stateFlow.value = Digits(input = "$input−") }
        } else { _stateFlow.value = Digits(input = "−") }
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
        val normalOperators = listOf(DIVIDE, MULTIPLY, ADD, MINUS)
        val openBrackets: Int = input.count { it == '(' }
        val closeBrackets: Int = input.count { it == ')' }
        // bracket to add, conditionally
        val addCloseBracket = { _stateFlow.value = Digits(input = "$input)") }
        val addOpenBracket = { _stateFlow.value = Digits(input = "$input(") }
        when {
            input.isBlank() -> { _stateFlow.value = Digits(input = "(") }
            input.endsWith(")") && openBrackets == closeBrackets -> { addOpenBracket() }
            input.endsWith("(") -> { addOpenBracket() }
            input.last() == ')' && openBrackets > closeBrackets -> { addCloseBracket() }
            input.last().isDigit() && openBrackets > closeBrackets -> { addCloseBracket() }
            input.last().isDigit() && openBrackets == closeBrackets -> { addOpenBracket() }
            normalOperators.any{ input.endsWith(it) } -> { addOpenBracket() }
            !input.last().isDigit() && openBrackets == closeBrackets -> { addOpenBracket() }
            !input.last().isDigit() && openBrackets >= closeBrackets -> { addCloseBracket() }
        }
        apply {
            val openedBrackets: Int = _stateFlow.value.input.count { it == '(' }
            val closedBrackets: Int = _stateFlow.value.input.count { it == ')' }
            if (openedBrackets == closedBrackets) { calculateInput() }
        }; inputIsAnswer = false
    }

    private fun enterNumber(digit: String) {
        if (inputIsAnswer) { _stateFlow.value = Digits(input = digit); inputIsAnswer = false }
        else {
            val symbols = setOf(')', '%', 'π')
            if (symbols.any { _stateFlow.value.input.endsWith(it) }) {
                _stateFlow.value = Digits(input = _stateFlow.value.input + "×" + digit)
            }
            else { _stateFlow.value = Digits(input = _stateFlow.value.input + digit) }
        }
        val input = _stateFlow.value.input
        val operator = input.filter { !it.isDigit() }.toList()
        // calculate input only if an operator has been entered.
        if (_stateFlow.value.input.isNotBlank()) {
            if (input.all { !it.isDigit() } && !input.startsWith(MINUS) || input.first() == '(') { calculateInput() }
            if (!input.first().isDigit() && operator.size > 1) { calculateInput() }
            if (input.first().isDigit() && (input.all { !it.isDigit() } || operator.isNotEmpty())) { calculateInput() }
        }
    }

    private fun enterOperation(operator: CalculatorOperations) {
        val input = _stateFlow.value.input
        if (input.isNotBlank()) {
            val lastTwoAreSymbols = when {
                input.endsWith("÷−") -> true
                input.endsWith("×−") -> true
                input.endsWith("+−") -> true
                input.endsWith("−−") -> true
                input.endsWith(DOT) -> true
                else -> false
            }
            val lastDigit = input.last()
            if (!lastDigit.isDigit()) {
                if (!lastDigit.isDigit() && input != MINUS && !input.endsWith("(−") && !lastTwoAreSymbols) {
                    // if lastDigit is an operator and end with neither `(` nor `)` nor `π`
                        // and operator entered isn't `%`, replace lase operator.
                    if (lastDigit != '(' && operator.symbol != MOD && lastDigit != 'π') {
                        if (lastDigit != ')') { _stateFlow.value = Digits(input = input.dropLast(1)) }
                        _stateFlow.value = Digits(input = _stateFlow.value.input + operator.symbol)
                    }
                    if (lastDigit == 'π') { _stateFlow.value = Digits(input = input + operator.symbol) }
                }
            } else {
                // if lastDigit isn't `(` and operator entered isn't `%`, add operator to input
                if (lastDigit != '(' && operator.symbol != MOD) {
                    _stateFlow.value = Digits(input = input + operator.symbol)
                }
            }
            if (operator.symbol == MOD && lastDigit.isDigit() || lastDigit == '%' || lastDigit == ')') {
                _stateFlow.value = Digits(input = input + operator.symbol); calculateInput()
            }
        }
        // if input contains digits and operator is `%`, calculate input
        if (_stateFlow.value.input.all { !it.isDigit() } && operator.symbol == MOD) { calculateInput() }
        inputIsAnswer = false
    }

    private fun trigonometricFunction(functions: TrigFunctions) {
        val input = _stateFlow.value.input
        val symbols = setOf(")", MOD, PI)
        if (input.isNotBlank()) {
            if (symbols.any { input.endsWith(it) } || input.last().isDigit()) {
                _stateFlow.value = Digits(input = input + "×" + functions.symbol)
            } else { _stateFlow.value = Digits(input = input + functions.symbol) }
            inputIsAnswer = false
        } else { _stateFlow.value = Digits(input = functions.symbol) }
    }

    private fun clearInput() { _stateFlow.value = Digits(); inputIsAnswer = false }

    private fun deleteInput() {
        if (!inputIsAnswer) {
            val input: String = when {
                _stateFlow.value.input.endsWith(ROOT) -> _stateFlow.value.input.dropLast(2)
                _stateFlow.value.input.endsWith(SIN) -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith(COS) -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith(TAN) -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith(LOG) -> _stateFlow.value.input.dropLast(4)
                else -> _stateFlow.value.input.dropLast(1)
            }
            _stateFlow.value = Digits(input = input)
        }
        val input = _stateFlow.value.input
        val operator = input.filter { !it.isDigit() }.toList()
        // calculate input if it contains operator and ends with digit
        if (input.isNotBlank()) {
            if (input.all { !it.isDigit() } && input.first() != '−' || input.first() == '(') { calculateInput() }
            if (!input.first().isDigit() && operator.size > 1) { calculateInput() }
            if (input.first().isDigit() && (input.all { !it.isDigit() } || operator.isNotEmpty())) { calculateInput() }
        }
    }

    private fun enterDecimal() {
        val input = _stateFlow.value.input
        if (input.isDigitsOnly()) {
            _stateFlow.value = Digits(input = _stateFlow.value.input + ".")
        } else {
            val lastSymbol = _stateFlow.value.input.last { !it.isDigit() }
            if (lastSymbol != '.' || lastSymbol == '^') {
                if (inputIsAnswer) {
                    _stateFlow.value = Digits(input = "."); inputIsAnswer = false
                } else {
                    if (input.endsWith(")") || input.endsWith("π")) {
                        _stateFlow.value = Digits(input = "$input×.")
                    } else { _stateFlow.value = Digits(input = "$input.") }
                }
                calculateInput()
            }
        }
    }

    private fun calculateInput(equalled: Boolean = false) {
        try {
            var input = _stateFlow.value.input.replace(Regex("[÷×−π]")) {
                when (it.value) {
                    "÷" -> "/"
                    "×" -> "*"
                    "−" -> "-"
                    "π" -> "PI"
                    else -> it.value
                }
            }.replace("log", "log10")
                .replace("IP", "I*P") // ππ -> π×π
                .replace("I(", "I*(") // π() -> π×()
                .replace(")P", ")*P") // ()π -> ()×π
                .replace("√", "sqrt")
            // convert trigonometric equation to degree or radian
            val degRad = Preference.getDegRad(app) == RAD
            if (degRad) {
                input = input.replace(SIN, SINR)
                    .replace(COS, COSR)
                    .replace(TAN, TANR)
            }

            val symbols = setOf(")", "PI", DOT, MOD)
            if ((input.last().isDigit() && !input.isDigitsOnly()) ||
                symbols.any { input.endsWith(it) }) {
                val expression = Expression(input).setPrecision(12)
                val calcResult = expression.eval(false).toString()
                val result = if (calcResult.contains(DOT)) {
                    calcResult.dropLastWhile { it == '0' || it == '.' }
                } else { calcResult }
                _stateFlow.value = Digits(
                    input = _stateFlow.value.input,
                    result = result.replace("-", MINUS)
                )
            }
            // if `equal button` is pressed and there's a result (that isn't a fraction) already, show only result
            // if result is decimal, fractionize.
            val result = _stateFlow.value.result
            if (equalled && result.isNotBlank() && "/" !in result) {
                val decimal = result.takeLastWhile { it.isDigit() }.length
                // convert answer to fraction if decimal and if bits <= 9
                if (result.contains(DOT) && decimal <= 5) {
                    val fraction = Fractionize(result).evaluate()
                    _stateFlow.value = Digits(input = result, result = fraction)
                } else {
                    _stateFlow.value = Digits(input = result)
                }
                inputIsAnswer = true
            }
            Log.d("FinalInput", "calculateInput: $input")
        } catch (e: Exception) {
            // if `equal button` is pressed, show error message
            if (equalled) { _stateFlow.value = Digits(input = _stateFlow.value.input, error = true) }
            Log.e("SyntaxError", "Input error: ", e)
        }
    }
}