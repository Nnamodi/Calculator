package com.roland.android.calculator.util

object Regex {
    fun regex(input: String) = input
        .replace("÷", "/")
        .replace("×", "*")
        .replace("−", "-")
        .replace("π", "PI")
        .replace("√", "sqrt")
        .replace("IP", "I*P") // ππ -> π×π
        .replace("eP", "e*P") // eπ -> e×π
        .replace("Ie", "I*e") // πe -> π×e
        .replace("ee", "e*e") // ee -> e×e
        .replace("I(", "I*(") // π() -> π×()
        .replace(")P", ")*P") // ()π -> ()×π
        .replace("e(", "e*(") // e() -> e×()
        .replace(")e", ")*e") // ()e -> ()×e
        .replace("*ee", "*e*e") // *ee -> *e×e
        .replace("log", "log10")

    fun regexR(input: String) = input
        .replace(Constants.SIN, Constants.SINR)
        .replace(Constants.COS, Constants.COSR)
        .replace(Constants.TAN, Constants.TANR)
}