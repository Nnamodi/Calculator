package com.roland.android.calculator.util

object Regex {
    fun regex(input: String) = input
        .replace("÷", "/")
        .replace("×", "*")
        .replace("−", "-")
        .replace("π", "PI")
        .replace("²", "^2")
        .replace("√", "sqrt")
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
}