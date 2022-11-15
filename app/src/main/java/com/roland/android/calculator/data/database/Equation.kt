package com.roland.android.calculator.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Equation(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: Date = Date(),
    var input: String = "",
    var result: String = "",
    var degRad: String = ""
)
