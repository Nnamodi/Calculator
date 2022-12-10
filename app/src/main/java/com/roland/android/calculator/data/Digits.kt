package com.roland.android.calculator.data

import android.text.Spanned
import androidx.core.text.HtmlCompat

data class Digits(
    var input: String = "",
    var result: Spanned = HtmlCompat.fromHtml("", HtmlCompat.FROM_HTML_MODE_COMPACT),
    var error: Boolean = false,
    var errorMessage: String = ""
)