package com.roland.android.calculator.util

import com.roland.android.calculator.R

object Regex {
    fun regex(input: String) = input
        .replace("÷", "/")
        .replace("×", "*")
        .replace("−", "-")
        .replace("π", "PI")
        .replace("²", "^2")
        .replace("√", "sqrt")
        .replace("%", "/100")
        .replace("2e", "2*e") // ²e -> ²×e
        .replace("2P", "2*P") // ²π -> ²×π
        .replace("IP", "I*P") // ππ -> π×π
        .replace("eP", "e*P") // eπ -> e×π
        .replace("Ie", "I*e") // πe -> π×e
        .replace("ee", "e*e") // ee -> e×e
        .replace("2(", "2*(") // ²() -> ²×()
        .replace("I(", "I*(") // π() -> π×()
        .replace(")P", ")*P") // ()π -> ()×π
        .replace("e(", "e*(") // e() -> e×()
        .replace(")e", ")*e") // ()e -> ()×e
        .replace("*ee", "*e*e") // *ee -> *e×e
        .replace("log", "log10")
        .replace("ln", "log")
        .replace(Constants.SIN_INV, Constants.ASIN)
        .replace(Constants.COS_INV, Constants.ACOS)
        .replace(Constants.TAN_INV, Constants.ATAN)

    fun regexR(input: String) = input
        .replace(Constants.SIN, Constants.SINR)
        .replace(Constants.COS, Constants.COSR)
        .replace(Constants.TAN, Constants.TANR)
        .replace(Constants.ASIN, Constants.ASINR)
        .replace(Constants.ACOS, Constants.ACOSR)
        .replace(Constants.ATAN, Constants.ATANR)

    fun String.accessibilityRegex(string: (Int) -> String) = this
        .replace("e", string(R.string.a_euler))
        .replace("÷", string(R.string.a_divided_by))
        .replace("×", string(R.string.a_multiply))
        .replace("−", string(R.string.a_minus))
        .replace("(", string(R.string.a_left_parenthesis))
        .replace(")", string(R.string.a_right_parenthesis))
        .replace("sin⁻¹", string(R.string.a_sine_inverse))
        .replace("sin", string(R.string.a_sine))
        .replace("cos⁻¹", string(R.string.a_cosine_inverse))
        .replace("cos", string(R.string.a_cosine))
        .replace("tan⁻¹", string(R.string.a_tangent_inverse))
        .replace("tan", string(R.string.a_tangent))
        .replace("log", string(R.string.a_log))
        .replace("ln", string(R.string.a_natural_log))
        .replace("fact", string(R.string.a_factorial))
        .replace("√", string(R.string.a_square_root))
        .replace("^", string(R.string.a_exponent))
}