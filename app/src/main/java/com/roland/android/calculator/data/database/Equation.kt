package com.roland.android.calculator.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Equation(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var input: String = "",
    var result: String = ""
)
