package com.roland.android.calculator.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.data.Digits
import com.roland.android.calculator.data.TrigFunctions
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.data.database.EquationDatabase
import com.roland.android.calculator.repository.EquationRepository
import com.roland.android.calculator.util.Constants.ADD
import com.roland.android.calculator.util.Constants.COS
import com.roland.android.calculator.util.Constants.COS_INV
import com.roland.android.calculator.util.Constants.DEG
import com.roland.android.calculator.util.Constants.DIVIDE
import com.roland.android.calculator.util.Constants.DOT
import com.roland.android.calculator.util.Constants.EULER
import com.roland.android.calculator.util.Constants.EULER_INV
import com.roland.android.calculator.util.Constants.FACT
import com.roland.android.calculator.util.Constants.INV_LOG
import com.roland.android.calculator.util.Constants.LOG
import com.roland.android.calculator.util.Constants.LOG_N
import com.roland.android.calculator.util.Constants.MINUS
import com.roland.android.calculator.util.Constants.MOD
import com.roland.android.calculator.util.Constants.MULTIPLY
import com.roland.android.calculator.util.Constants.PI
import com.roland.android.calculator.util.Constants.RAD
import com.roland.android.calculator.util.Constants.ROOT
import com.roland.android.calculator.util.Constants.SIN
import com.roland.android.calculator.util.Constants.SIN_INV
import com.roland.android.calculator.util.Constants.SQUARE
import com.roland.android.calculator.util.Constants.SQUARED
import com.roland.android.calculator.util.Constants.TAN
import com.roland.android.calculator.util.Constants.TAN_INV
import com.roland.android.calculator.util.Fractionize
import com.roland.android.calculator.util.Preference
import com.roland.android.calculator.util.Regex.regex
import com.roland.android.calculator.util.Regex.regexR
import com.roland.android.calculator.util.Utility.e
import com.roland.android.calculator.util.Utility.format
import com.roland.android.calculator.util.Utility.optimizedInput
import com.udojava.evalex.Expression
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class CalculatorViewModel(private val app: Application) : AndroidViewModel(app) {
    // database
    private val repository: EquationRepository

    init {
        val equationDao = EquationDatabase.getDatabase(app).equationDao()
        repository = EquationRepository(equationDao, viewModelScope)
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
    var equalled = false
    var inputIsAnswer = false

    fun onAction(action: CalculatorActions) {
        if (action !is CalculatorActions.Delete && action !is CalculatorActions.Numbers &&
            action !is CalculatorActions.Decimal && action !is CalculatorActions.ComputeFormat) { inputIsAnswer = false }
        if (action !is CalculatorActions.Calculate) { equalled = false }
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
            is CalculatorActions.EnterEquation -> { enterEquation(action.equation) }
            is CalculatorActions.ComputeFormat -> { computeFormat() }
        }
        if (action !is CalculatorActions.Calculate) { calculateInput() }
    }

    private fun computeFormat() {
        if (inputIsAnswer) {
            _stateFlow.value = Digits(input = previousEquation)
            inputIsAnswer = false
        }
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
        if (inputIsAnswer) { _stateFlow.value = Digits(input = digit); inputIsAnswer = false }
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
        if (inputIsAnswer) {
            _stateFlow.value = Digits(input = DOT)
        } else {
            if (input.isDigitsOnly()) {
                _stateFlow.value = Digits(input = _stateFlow.value.input + DOT)
            } else {
                val lastSymbol = _stateFlow.value.input.last { !it.isDigit() }
                if (lastSymbol != '.' || lastSymbol == '^') {
                    val signum = setOf(")", PI, EULER)
                    if (signum.any { input.endsWith(it) }) {
                        _stateFlow.value = Digits(input = "$input×.")
                    } else {
                        _stateFlow.value = Digits(input = input + DOT)
                    }
                }
            }
        }; inputIsAnswer = false
    }

    private fun enterEquation(equation: String) {
        val input = _stateFlow.value.input
        _stateFlow.value = if (input.isNotBlank()) {
            Digits(input = "($input)×$equation")
        } else { Digits(input = equation) }
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
                val expression = Expression(optimizedInput(input)).setPrecision(50)
                val calcResult = expression.eval().toPlainString()
                val result = if (calcResult.contains(DOT)) {
                    calcResult.dropLastWhile { it == '0' || it == '.' }
                } else { calcResult }
                _stateFlow.value = Digits(
                    input = _stateFlow.value.input,
                    result = result.replace("-", MINUS).format(app)
                )
            }
            // if `equal button` is pressed and there's a result (that isn't a fraction) already, show only result
            // if result is decimal, fractionize.
            val result = _stateFlow.value.result
            if (equalled) {
                inputIsAnswer = true; this.equalled = true
                // temporarily save previous equation
                previousEquation = _stateFlow.value.input
                if (result.isNotBlank() && "/" !in result && "×" !in result) {
                    // save to history if set in settings
                    if (Preference.getSaveHistory(app)) {
                        val equation = stateFlow.value.input
                        val trigFunctions = setOf(SIN, COS, TAN, SIN_INV, COS_INV, TAN_INV)
                        val radDeg = if (trigFunctions.any { equation.contains(it) }) {
                            Preference.getDegRad(app)!! } else { "" }
                        addCalculation(Equation(
                            date = Calendar.getInstance().time,
                            input = stateFlow.value.input,
                            result = stateFlow.value.result.toString(),
                            degRad = radDeg
                        ))
                    }
                    // convert answer to fraction if result is decimal and if bits <= 9
                    if (DOT in result) {
                        val fraction = try { Fractionize(result.toString()).evaluate(app) }
                        catch (e: Exception) { HtmlCompat.fromHtml("", FROM_HTML_MODE_COMPACT) }
                        _stateFlow.value = Digits(input = result.toString(), result = fraction)
                    } else {
                        _stateFlow.value = Digits(input = result.toString())
                    }
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
                // save to history if set in settings
                if (Preference.getSaveHistory(app) && Preference.getSaveErrors(app)) {
                    addCalculation(Equation(
                        date = Calendar.getInstance().time,
                        input = stateFlow.value.input,
                        error = true,
                        errorMessage = e(e.message!!)
                    ))
                }
            }; inputIsAnswer = false
            Log.e("SyntaxError", "Input error: ", e)
        }
    }
}