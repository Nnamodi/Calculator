package com.roland.android.calculator.data

import com.roland.android.calculator.util.Constants.COS
import com.roland.android.calculator.util.Constants.COS_INV
import com.roland.android.calculator.util.Constants.SIN
import com.roland.android.calculator.util.Constants.SIN_INV
import com.roland.android.calculator.util.Constants.TAN
import com.roland.android.calculator.util.Constants.TAN_INV

sealed class TrigFunctions(val symbol: String) {
    object Sine: TrigFunctions(SIN)
    object Cosine: TrigFunctions(COS)
    object Tangent: TrigFunctions(TAN)
    object ASine: TrigFunctions(SIN_INV)
    object ACosine: TrigFunctions(COS_INV)
    object ATangent: TrigFunctions(TAN_INV)
}
