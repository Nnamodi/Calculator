package com.roland.android.calculator.data

import com.roland.android.calculator.util.Constants.CANT_CALC
import com.roland.android.calculator.util.Constants.CANT_DIVIDE
import com.roland.android.calculator.util.Constants.DOMAIN_ERROR
import com.roland.android.calculator.util.Constants.FORMAT_ERROR
import com.roland.android.calculator.util.Constants.MISMATCHED
import com.roland.android.calculator.util.Constants.NEG_SQRT
import com.roland.android.calculator.util.Constants.UNDEFINED
import com.roland.android.calculator.util.Constants.UNKNOWN

enum class ErrorMessage(val message: String) {
    Unknown(UNKNOWN),
    Division0(CANT_DIVIDE),
    MissingParam(FORMAT_ERROR),
    Mismatched(MISMATCHED),
    RoundNec(CANT_CALC),
    Infinity(DOMAIN_ERROR),
    NegSqrt(NEG_SQRT),
    Undefined(UNDEFINED)
}
