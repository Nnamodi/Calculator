package com.roland.android.calculator.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EquationDao {
    @Query("SELECT * FROM equation ORDER BY id")
    suspend fun getEquations(): List<Equation>

    @Insert
    suspend fun addCalculation(equation: Equation)
}