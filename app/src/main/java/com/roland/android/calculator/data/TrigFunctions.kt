package com.roland.android.calculator.data

import com.roland.android.calculator.util.Constants.COS
import com.roland.android.calculator.util.Constants.SIN
import com.roland.android.calculator.util.Constants.TAN

sealed class TrigFunctions(val symbol: String) {
    object Sine: TrigFunctions(SIN)
    object Cosine: TrigFunctions(COS)
    object Tangent: TrigFunctions(TAN)
}
