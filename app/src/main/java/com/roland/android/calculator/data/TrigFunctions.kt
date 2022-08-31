package com.roland.android.calculator.data

sealed class TrigFunctions(val symbol: String) {
    object Sine: TrigFunctions("sin(")
    object Cosine: TrigFunctions("cos(")
    object Tangent: TrigFunctions("tan(")
}
