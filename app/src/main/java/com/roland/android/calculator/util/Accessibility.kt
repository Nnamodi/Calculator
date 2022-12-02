package com.roland.android.calculator.util

import android.util.Log
import com.roland.android.calculator.R
import com.roland.android.calculator.databinding.FragmentCalculatorBinding
import com.roland.android.calculator.util.Constants.DEG
import com.roland.android.calculator.util.Constants.DIVIDE
import com.roland.android.calculator.util.Constants.EULER
import com.roland.android.calculator.util.Constants.EULER_INV
import com.roland.android.calculator.util.Constants.INV_LOG
import com.roland.android.calculator.util.Constants.MULTIPLY
import com.roland.android.calculator.util.Constants.RAD
import com.roland.android.calculator.util.Constants.SQUARE
import com.roland.android.calculator.util.Regex.accessibilityRegex

object Accessibility {
    fun FragmentCalculatorBinding.accessCalculator(inverse: Boolean = false) {
        val string: (Int) -> String = { root.context.getString(it) }
        setOf(input, wrongInput, resultContainer, landResultContainer).forEach {
            it?.contentDescription = when (it) {
                resultContainer -> if (result?.text?.isEmpty() == true) { string(R.string.a_no_result) } else { result?.text }
                landResultContainer -> if (landResult?.text?.isEmpty() == true) { string(R.string.a_no_result) } else { landResult?.text }
                else -> if (input.text.isEmpty()) { string(R.string.a_no_equation) } else {
                    input.text.toString().accessibilityRegex(string)
                }
            }
            Log.d("AccessibilityRegex", "accessCalculator: ${input.contentDescription}")
        }

        setOf(divide, multiply, equals, button00, euler, factorial, bracket, sin, cos, tan, square, squareRoot,
            log, naturalLog, buttonAc, decimal, delButton, degRad, inv, expandButton).forEach {
            it?.contentDescription = when (it?.text) {
                DIVIDE -> string(R.string.a_divide)
                MULTIPLY -> string(R.string.a_multiply)
                "=" -> string(R.string.a_equals)
                "00" -> string(R.string.a_double_zero)
                EULER -> string(R.string.a_euler)
                EULER_INV -> string(R.string.a_euler_inverse)
                "!" -> string(R.string.a_factorial)
                "( )" -> string(R.string.a_parenthesis)
                "sin" -> string(R.string.a_sine)
                "sin⁻¹" -> string(R.string.a_sine_inverse)
                "cos" -> string(R.string.a_cosine)
                "cos⁻¹" -> string(R.string.a_cosine_inverse)
                "tan" -> string(R.string.a_tangent)
                "tan⁻¹" -> string(R.string.a_tangent_inverse)
                SQUARE -> string(R.string.a_power)
                "√" -> string(R.string.a_square_root)
                "x²" -> string(R.string.a_square)
                "log" -> string(R.string.a_log)
                INV_LOG -> string(R.string.a_log_inverse)
                "ln" -> string(R.string.a_natural_log)
                "AC" -> string(R.string.a_clear)
                "C" -> string(R.string.a_delete)
                "·" -> string(R.string.a_point)
                DEG -> string(R.string.a_deg_mode)
                RAD -> string(R.string.a_rad_mode)
                "INV" -> if (inverse) { string(R.string.a_from_inverse) } else { string(R.string.a_to_inverse) }
                else -> if (expandButton?.isChecked == true) { string(R.string.a_collapse) } else { string(R.string.a_expand) }
            }
        }

        toolbarDegRad.contentDescription = root.context.getString(R.string.a_deg_rad_mode, toolbarDegRad.text)
    }
}