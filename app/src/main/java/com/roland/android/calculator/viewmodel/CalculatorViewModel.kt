package com.roland.android.calculator.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.roland.android.calculator.data.*
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.data.database.EquationDatabase
import com.roland.android.calculator.repository.EquationRepository
import com.roland.android.calculator.util.Constants.ADD
import com.roland.android.calculator.util.Constants.COS
import com.roland.android.calculator.util.Constants.COS_INV
import com.roland.android.calculator.util.Constants.DEG
import com.roland.android.calculator.util.Constants.DIVIDE
import com.roland.android.calculator.util.Constants.DIVIDE_0
import com.roland.android.calculator.util.Constants.DOT
import com.roland.android.calculator.util.Constants.EULER
import com.roland.android.calculator.util.Constants.EULER_INV
import com.roland.android.calculator.util.Constants.FACT
import com.roland.android.calculator.util.Constants.INFINITY
import com.roland.android.calculator.util.Constants.INV_LOG
import com.roland.android.calculator.util.Constants.LOG
import com.roland.android.calculator.util.Constants.LOG_N
import com.roland.android.calculator.util.Constants.MINUS
import com.roland.android.calculator.util.Constants.MISMATCHED_PAR
import com.roland.android.calculator.util.Constants.MISSING_PARAM
import com.roland.android.calculator.util.Constants.MOD
import com.roland.android.calculator.util.Constants.MULTIPLY
import com.roland.android.calculator.util.Constants.NEGATIVE_SQRT
import com.roland.android.calculator.util.Constants.PI
import com.roland.android.calculator.util.Constants.RAD
import com.roland.android.calculator.util.Constants.ROOT
import com.roland.android.calculator.util.Constants.ROUNDING_NEC
import com.roland.android.calculator.util.Constants.SIN
import com.roland.android.calculator.util.Constants.SIN_INV
import com.roland.android.calculator.util.Constants.SQUARE
import com.roland.android.calculator.util.Constants.SQUARED
import com.roland.android.calculator.util.Constants.TAN
import com.roland.android.calculator.util.Constants.TAN_INV
import com.roland.android.calculator.util.Constants.UNKNOWN_OPERATOR
import com.roland.android.calculator.util.Constants.UNKNOWN_UNARY
import com.roland.android.calculator.util.Fractionize
import com.roland.android.calculator.util.Preference
import com.roland.android.calculator.util.Regex.regex
import com.roland.android.calculator.util.Regex.regexR
import com.udojava.evalex.Expression
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel(private val app: Application) : AndroidViewModel(app) {
    // database
    private val repository: EquationRepository
    val getEquation: Flow<List<Equation>>

    init {
        val equationDao = EquationDatabase.getDatabase(app).equationDao()
        repository = EquationRepository(equationDao)
        getEquation = repository.getEquations
    }

    private fun addCalculation(equation: Equation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCalculation(equation)
        }
    }

    // calculation logic
    private val _stateFlow = MutableStateFlow(Digits())
    val stateFlow = _stateFlow.asStateFlow()
    private var previousEquation = ""
    var inputIsAnswer = false

    fun onAction(action: CalculatorActions) {
        if (action !is CalculatorActions.Delete) { inputIsAnswer = false }
        when (action) {
            is CalculatorActions.Numbers -> { enterNumber(action.number) }
            is CalculatorActions.Clear -> { clearInput() }
            is CalculatorActions.Delete -> { deleteInput() }
            is CalculatorActions.Decimal -> { enterDecimal() }
            is CalculatorActions.Operators -> { enterOperation(action.operator) }
            is CalculatorActions.Calculate -> { calculateInput(true) }
            is CalculatorActions.Bracket -> { addBracket() }
            is CalculatorActions.Trigonometry -> { trigonometricFunction(action.function) }
            is CalculatorActions.Pi -> { addSymbol(PI) }
            is CalculatorActions.Euler -> { addSymbol(EULER) }
            is CalculatorActions.EulerInv -> { addSymbol(EULER_INV) }
            is CalculatorActions.Factorial -> { addSymbol(FACT) }
            is CalculatorActions.Log -> { addLog() }
            is CalculatorActions.LogInv -> { addLog(INV_LOG) }
            is CalculatorActions.LogN -> { addLog(LOG_N) }
            is CalculatorActions.Square -> { addSquare() }
            is CalculatorActions.SquareInv -> { addSquare(SQUARED) }
            is CalculatorActions.SquareRoot -> { addSquareRoot() }
            is CalculatorActions.DegRad -> { degRad() }
        }
        if (action !is CalculatorActions.Calculate) { calculateInput() }
    }

    private fun degRad() {
        val degRad = Preference.getDegRad(app) == RAD
        val value = if (degRad) { DEG } else { RAD }
        Preference.setDegRad(app, value)
    }

    private fun addSquareRoot() {
        val input = _stateFlow.value.input
        val symbols = setOf(DIVIDE, MULTIPLY, MINUS, ADD, SQUARE, "(")
        if (symbols.any { input.endsWith(it) } || input.isBlank()) { _stateFlow.value = Digits(input = input + ROOT) }
        else { _stateFlow.value = Digits(input = "$input×$ROOT") }
    }

    private fun addSquare(square: String = SQUARE) {
        val input = _stateFlow.value.input
        val symbols = setOf(')', 'π', '%', 'e', '²')
        val addSquare = { _stateFlow.value = Digits(input = "$input$square") }
        if (input.isNotBlank()) {
            when {
                input.last().isDigit() -> { addSquare() }
                symbols.any { input.last() == it } -> { addSquare() }
            }
        }
    }

    private fun addLog(log: String = LOG) {
        val input = _stateFlow.value.input
        val symbols = setOf(")", MOD, PI)
        if (input.isNotBlank()) {
            if (symbols.any { input.endsWith(it) } || input.last().isDigit()) {
                _stateFlow.value = Digits(input = "$input×$log")
            } else { _stateFlow.value = Digits(input = input + log) }
        } else { _stateFlow.value = Digits(input = log) }
    }

    private fun addSymbol(symbol: String) {
        val input = _stateFlow.value.input
        when {
            input.isBlank() -> { _stateFlow.value = Digits(input = symbol) }
            input.last().isDigit() -> { _stateFlow.value = Digits(input = "$input×$symbol") }
            else -> { _stateFlow.value = Digits(input = input + symbol) }
        }
    }

    private fun addBracket() {
        val input = _stateFlow.value.input
        val normalOperators = listOf(DIVIDE, MULTIPLY, ADD, MINUS, SQUARE)
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
    }

    private fun enterNumber(digit: String) {
        if (inputIsAnswer) { _stateFlow.value = Digits(input = digit) }
        else {
            val symbols = setOf(')', 'π', 'e', '²')
            if (symbols.any { _stateFlow.value.input.endsWith(it) }) {
                _stateFlow.value = Digits(input = _stateFlow.value.input + "×" + digit)
            }
            else { _stateFlow.value = Digits(input = _stateFlow.value.input + digit) }
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
                input.endsWith("(−") -> true
                input == MINUS -> true
                else -> false
            }
            val lastDigit = input.last()
            val signum = setOf('π', ')', 'e', '%', '²', '.')
            if (operator.symbol != MINUS && operator.symbol != MOD) {
                if (!lastDigit.isDigit()) {
                    if (!lastTwoAreSymbols) {
                        // if lastDigit is an operator and end with neither `(`, `)`, `e` nor `π`, replace last operator.
                        if (signum.any { lastDigit != it } && lastDigit != '(') {
                            _stateFlow.value =
                                Digits(input = input.dropLast(1) + operator.symbol)
                        }
                        if (signum.any { lastDigit == it }) {
                            _stateFlow.value = Digits(input = input + operator.symbol)
                        }
                    }
                } else {
                    // if lastDigit isn't `(` and operator entered isn't `%`, add operator to input
                    if (lastDigit != '(') {
                        _stateFlow.value = Digits(input = input + operator.symbol)
                    }
                }
            }
            if (operator.symbol == MOD && (lastDigit.isDigit() || signum.any { lastDigit == it })) {
                _stateFlow.value = Digits(input = input + operator.symbol)
            }
        }
        if (operator.symbol == MINUS) { addMinus() }
    }

    private fun addMinus() {
        val input = _stateFlow.value.input
        val digitIsNegative = when {
            input.endsWith("+−") -> { true }
            input.endsWith("×−") -> { true }
            input.endsWith("÷−") -> { true }
            input.endsWith("−−") -> { true }
            input.endsWith("(−") -> { true }
            input.endsWith("^−") -> { true }
            input == MINUS -> { true }
            else -> { false }
        }
        if (input.isNotBlank()) {
            if (digitIsNegative) {
                _stateFlow.value = Digits(input = input.dropLast(1))
            } else { _stateFlow.value = Digits(input = "$input−") }
        } else { _stateFlow.value = Digits(input = "−") }
    }

    private fun trigonometricFunction(functions: TrigFunctions) {
        val input = _stateFlow.value.input
        val symbols = setOf(")", MOD, PI, EULER, SQUARED)
        if (input.isNotBlank()) {
            if (symbols.any { input.endsWith(it) } || input.last().isDigit()) {
                _stateFlow.value = Digits(input = input + "×" + functions.symbol)
            } else { _stateFlow.value = Digits(input = input + functions.symbol) }
        } else { _stateFlow.value = Digits(input = functions.symbol) }
    }

    private fun clearInput() { _stateFlow.value = Digits() }

    private fun deleteInput() {
        if (!inputIsAnswer) {
            val input: String = when {
                _stateFlow.value.input.endsWith(ROOT) -> _stateFlow.value.input.dropLast(2)
                _stateFlow.value.input.endsWith(LOG_N) -> _stateFlow.value.input.dropLast(3)
                _stateFlow.value.input.endsWith(SIN) -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith(COS) -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith(TAN) -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith(LOG) -> _stateFlow.value.input.dropLast(4)
                _stateFlow.value.input.endsWith(FACT) -> _stateFlow.value.input.dropLast(5)
                _stateFlow.value.input.endsWith(SIN_INV) -> _stateFlow.value.input.dropLast(6)
                _stateFlow.value.input.endsWith(COS_INV) -> _stateFlow.value.input.dropLast(6)
                _stateFlow.value.input.endsWith(TAN_INV) -> _stateFlow.value.input.dropLast(6)
                else -> _stateFlow.value.input.dropLast(1)
            }
            _stateFlow.value = Digits(input = input)
        } else {
            val input = previousEquation.dropLast(1)
            inputIsAnswer = false
            _stateFlow.value = Digits(input = input)
        }
    }

    private fun enterDecimal() {
        val input = _stateFlow.value.input
        if (input.isDigitsOnly()) {
            _stateFlow.value = Digits(input = _stateFlow.value.input + DOT)
        } else {
            val lastSymbol = _stateFlow.value.input.last { !it.isDigit() }
            if (lastSymbol != '.' || lastSymbol == '^') {
                if (inputIsAnswer) {
                    _stateFlow.value = Digits(input = DOT)
                } else {
                    val signum = setOf(")", PI, EULER)
                    if (signum.any { input.endsWith(it) }) {
                        _stateFlow.value = Digits(input = "$input×.")
                    } else { _stateFlow.value = Digits(input = input + DOT) }
                }
            }
        }
    }

    private fun calculateInput(equalled: Boolean = false) {
        try {
            var input = regex(_stateFlow.value.input)
            // convert trigonometric equation to radian
            val degRad = Preference.getDegRad(app) == RAD
            if (degRad) { input = regexR(input) }
            // if input ends with operator/decimal, ignore last
            val signs = setOf("/", "*", ADD, "-", DOT, SQUARE)
            if (signs.any { input.endsWith(it) }) {
                input = input.dropLast(1)
            }

            val signum = input.filter { !it.isDigit() }
            val symbols = setOf(")", "PI", DOT, MOD, EULER, SQUARED)
            if ((input.isNotBlank() && input != DOT) && ((input.last().isDigit() &&
                    !input.isDigitsOnly()) || symbols.any { input.endsWith(it) }) &&
                    (setOf(".", "-", "-.").all { signum != it } ||
                    setOf("-.", "-(", "-").all { !input.startsWith(it) })
            ) {
                val expression = Expression(optimizedInput(input)).setPrecision(11)
                val calcResult = expression.eval(false).toPlainString()
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
                inputIsAnswer = true
                // temporarily save previous equation
                previousEquation = _stateFlow.value.input
                // save to history
                val equation = stateFlow.value.input
                val trigFunctions = setOf(SIN, COS, TAN, SIN_INV, COS_INV, TAN_INV)
                val radDeg = if (trigFunctions.any { equation.contains(it) }) {
                    Preference.getDegRad(app)!! } else { "" }
                addCalculation(Equation(
                    input = stateFlow.value.input,
                    result = stateFlow.value.result,
                    degRad = radDeg
                ))
                // convert answer to fraction if result is decimal and if bits <= 5
                if (result.contains(DOT) && decimal <= 5) {
                    val fraction = Fractionize(result).evaluate()
                    _stateFlow.value = Digits(input = result, result = fraction)
                } else {
                    _stateFlow.value = Digits(input = result)
                }
            }
            Log.d("FinalInput", "calculateInput: $input")
        } catch (e: Exception) {
            // if `equal button` is pressed, show error message
            if (equalled) {
                _stateFlow.value = Digits(
                    input = _stateFlow.value.input,
                    error = true,
                    errorMessage = e(e.message!!)
                )
            }; inputIsAnswer = false
            Log.e("SyntaxError", "Input error: ", e)
        }
    }

    // equate closeBrackets with openBrackets if need be, before calculating.
    private fun optimizedInput(input: String): String {
        var optimized = input
        val openBrackets: Int = input.count { it == '(' }
        var closeBrackets: Int = input.count { it == ')' }
        while (openBrackets > closeBrackets) {
            optimized += ")"; closeBrackets += 1
        }
        return optimized
    }

    private fun e(e: String): String {
        val error = when {
            UNKNOWN_UNARY in e -> ErrorMessage.Unknown
            UNKNOWN_OPERATOR in e -> ErrorMessage.Unknown
            DIVIDE_0 in e -> ErrorMessage.Division0
            MISSING_PARAM in e -> ErrorMessage.MissingParam
            MISMATCHED_PAR in e -> ErrorMessage.Mismatched
            ROUNDING_NEC in e -> ErrorMessage.RoundNec
            INFINITY in e -> ErrorMessage.Infinity
            NEGATIVE_SQRT in e -> ErrorMessage.NegSqrt
            else -> ErrorMessage.Undefined
        }
        return error.message
    }
}