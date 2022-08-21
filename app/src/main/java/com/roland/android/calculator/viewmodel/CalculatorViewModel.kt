package com.roland.android.calculator.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.data.Digits
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
        if (_stateFlow.value.operator == null) {
            if (!_stateFlow.value.digit_1.contains("-")) {
                _stateFlow.value = Digits(digit_1 = "-" + _stateFlow.value.digit_1)
            } else {
                _stateFlow.value = Digits(digit_1 = _stateFlow.value.digit_1.drop(1))
            }
        } else {
            if (!_stateFlow.value.digit_2.contains("-")) {
                _stateFlow.value = Digits(
                    digit_1 = _stateFlow.value.digit_1,
                    operator = _stateFlow.value.operator,
                    digit_2 = "-" + _stateFlow.value.digit_2
                )
            } else {
                _stateFlow.value = Digits(
                    digit_1 = _stateFlow.value.digit_1,
                    operator = _stateFlow.value.operator,
                    digit_2 = _stateFlow.value.digit_2.drop(1)
                )
            }
        }
        if (_stateFlow.value.digit_2.isNotBlank() && _stateFlow.value.digit_2 != "-") { calculateInput() }
    }

    private fun addBracket() {}

    private fun calculateInput(equalled: Boolean = false) {
        try {
            if (_stateFlow.value.digit_2.isNotBlank()) {
                val digit1 = _stateFlow.value.digit_1.toDouble()
                val digit2 = _stateFlow.value.digit_2.toDouble()
                val calcResult = when (_stateFlow.value.operator) {
                    CalculatorOperations.Add -> digit1 + digit2
                    CalculatorOperations.Divide -> digit1 / digit2
                    CalculatorOperations.Multiply -> digit1 * digit2
                    CalculatorOperations.Subtract -> digit1 - digit2
                    CalculatorOperations.Modulus -> TODO()
                    null -> return
                }.toString().take(15)
                apply {
                    val result = if (calcResult.endsWith(".0")) { calcResult.dropLast(2) } else { calcResult }
                    _stateFlow.value = Digits(
                        digit_1 = _stateFlow.value.digit_1,
                        digit_2 = _stateFlow.value.digit_2,
                        operator = _stateFlow.value.operator,
                        result = result
                    )
                }
                if (equalled) { _stateFlow.value = Digits(digit_1 = _stateFlow.value.result); inputIsAnswer = true }
            }
        } catch (e: Exception) {
            _stateFlow.value = Digits(
                digit_1 = _stateFlow.value.digit_1,
                digit_2 = _stateFlow.value.digit_2,
                operator = _stateFlow.value.operator,
                error = true
            )
            Log.e("SyntaxError", "calculateInput: Format Error: $e", e)
        }
    }

    private fun enterNumber(digit: String) {
        if (_stateFlow.value.operator == null) {
            if (inputIsAnswer) { inputIsAnswer = false; _stateFlow.value = Digits(digit_1 = digit) }
            else if (_stateFlow.value.digit_1.count() < 9) {
                _stateFlow.value = Digits(digit_1 = _stateFlow.value.digit_1 + digit)
            }; inputIsAnswer = false
        } else {
            if (_stateFlow.value.digit_2.count() < 9) {
                _stateFlow.value = Digits(
                    digit_1 = _stateFlow.value.digit_1,
                    digit_2 = _stateFlow.value.digit_2 + digit,
                    operator = _stateFlow.value.operator,
                    result = _stateFlow.value.result
                )
                calculateInput()
            }
        }
    }

    private fun enterOperation(operator: CalculatorOperations) {
        if (_stateFlow.value.digit_1.isNotBlank() && _stateFlow.value.digit_2.isBlank()) {
            _stateFlow.value = Digits(
                digit_1 = _stateFlow.value.digit_1,
                operator = operator
            )
        }
        if (_stateFlow.value.result.isNotBlank()) {
            _stateFlow.value = Digits(
                digit_1 = _stateFlow.value.result,
                operator = operator
            )
        }; inputIsAnswer = false
    }

    private fun clearInput() { _stateFlow.value = Digits() }

    private fun deleteInput() {
        when {
            _stateFlow.value.operator == null && !inputIsAnswer -> {
                _stateFlow.value = Digits(digit_1 = _stateFlow.value.digit_1.dropLast(1))
            }
            _stateFlow.value.operator != null && _stateFlow.value.digit_2.isBlank() -> {
                _stateFlow.value = Digits(
                    digit_1 = _stateFlow.value.digit_1,
                    operator = null
                )
            }
            _stateFlow.value.digit_2.isNotBlank() -> {
                val result = _stateFlow.value.result
                _stateFlow.value = Digits(
                    digit_1 = _stateFlow.value.digit_1,
                    digit_2 = _stateFlow.value.digit_2.dropLast(1),
                    operator = _stateFlow.value.operator,
                )
                if (result.isNotBlank() && _stateFlow.value.digit_2.isNotBlank()) { calculateInput() }
            }
        }
    }

    private fun enterDecimal() {
        if (!_stateFlow.value.digit_1.contains(".") && _stateFlow.value.operator == null) {
            if (inputIsAnswer) { _stateFlow.value = Digits(digit_1 = "."); inputIsAnswer = false }
            else { _stateFlow.value = Digits(digit_1 = _stateFlow.value.digit_1 + ".") }
        }
        if (!_stateFlow.value.digit_2.contains(".") && _stateFlow.value.operator != null) {
            _stateFlow.value = Digits(
                digit_1 = _stateFlow.value.digit_1,
                digit_2 = _stateFlow.value.digit_2 + ".",
                operator = _stateFlow.value.operator,
                result = _stateFlow.value.result
            )
        }
    }
}