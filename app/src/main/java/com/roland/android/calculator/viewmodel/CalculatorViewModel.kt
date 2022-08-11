package com.roland.android.calculator.viewmodel

import androidx.lifecycle.ViewModel
import com.roland.android.calculator.data.CalculatorActions
import com.roland.android.calculator.data.CalculatorOperations
import com.roland.android.calculator.data.Digits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val _stateFlow = MutableStateFlow(Digits())
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
            val calcResult = when (_stateFlow.value.operator) {
                CalculatorOperations.Add -> digit1 + digit2
                CalculatorOperations.Divide -> digit1 / digit2
                CalculatorOperations.Multiply -> digit1 * digit2
                CalculatorOperations.Subtract -> digit1 - digit2
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
                    operator = _stateFlow.value.operator,
                    result = _stateFlow.value.result
                )
                if (_stateFlow.value.result.isNotBlank()) { calculateInput() }
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

    private fun clearInput() { _stateFlow.value = Digits() }

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
            _stateFlow.value = Digits(digit_1 = _stateFlow.value.digit_1 + ".")
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