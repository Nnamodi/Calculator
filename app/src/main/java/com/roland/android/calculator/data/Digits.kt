package com.roland.android.calculator.data

data class Digits(
    var input: String = "",
    var result: String = "",
    var error: Boolean = false,
    var errorMessage: String = ""
)