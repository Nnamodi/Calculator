package com.roland.android.calculator.viewmodel

import androidx.lifecycle.ViewModel
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.data.Digits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val digits = Digits()
    private val _stateFlow = MutableStateFlow(digits)
    val stateFlow = _stateFlow.asStateFlow()

    fun onAction(action: CalculatorActions) {
        when (action) {
            is CalculatorActions.Numbers -> { enterNumber(action.number) }
            is CalculatorActions.Clear -> { clearInput() }
            is CalculatorActions.Delete -> { deleteInput() }
            is CalculatorActions.Decimal -> { enterDecimal() }
            is CalculatorActions.Operators -> { enterOperation(action.operator) }
            is CalculatorActions.Calculate -> { calculateInput() }
        }
    }

    private fun calculateInput() {
        if (_stateFlow.value.digit_2.isNotBlank()) {
            val digit1 = _stateFlow.value.digit_1.toDouble()
            val digit2 = _stateFlow.value.digit_2.toDouble()
            val result = when (_stateFlow.value.operator) {
                CalculatorOperations.Add -> digit1 + digit2
                CalculatorOperations.Divide -> digit1 / digit2
                CalculatorOperations.Multiply -> digit1 * digit2
                CalculatorOperations.Subtract -> digit1 - digit2
                null -> return
            }
            _stateFlow.value = Digits(digit_1 = result.toString())
        }
    }

    private fun enterNumber(digit: String) {
        if (_stateFlow.value.operator == null) {
            if (_stateFlow.value.digit_1.count() < 9) {
                _stateFlow.value = Digits(digit_1 = _stateFlow.value.digit_1 + digit)
            }
        } else {
            if (_stateFlow.value.digit_2.count() < 9) {
                _stateFlow.value = Digits(
                    digit_1 = _stateFlow.value.digit_1,
                    digit_2 = _stateFlow.value.digit_2 + digit,
                    operator = _stateFlow.value.operator
                )
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
    }

    private fun clearInput() {
        _stateFlow.value = Digits(digit_1 = "", digit_2 = "", operator = null)
    }

    private fun deleteInput() {
        when {
            _stateFlow.value.operator == null -> {
                _stateFlow.value = Digits(digit_1 = _stateFlow.value.digit_1.dropLast(1))
            }
            _stateFlow.value.operator != null && _stateFlow.value.digit_2.isBlank() -> {
                _stateFlow.value = Digits(
                    digit_1 = _stateFlow.value.digit_1,
                    operator = null
                )
            }
            _stateFlow.value.digit_2.isNotBlank() -> {
                _stateFlow.value = Digits(
                    digit_1 = _stateFlow.value.digit_1,
                    digit_2 = _stateFlow.value.digit_2.dropLast(1),
                    operator = _stateFlow.value.operator
                )
            }
        }
    }

    private fun enterDecimal() {
        if (!_stateFlow.value.digit_1.contains(".") && _stateFlow.value.operator == null) {
            _stateFlow.value = Digits(digit_1 = _stateFlow.value.digit_1 + ".")
        }
        if (!_stateFlow.value.digit_2.contains(".") && _stateFlow.value.operator != null) {
            _stateFlow.value = Digits(
                digit_1 = _stateFlow.value.digit_1,
                digit_2 = _stateFlow.value.digit_2 + ".",
                operator = _stateFlow.value.operator
            )
        }
    }
}