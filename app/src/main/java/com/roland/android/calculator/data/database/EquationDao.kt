package com.roland.android.calculator.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EquationDao {
    @Query("SELECT * FROM equation ORDER BY id")
    fun getEquations(): Flow<List<Equation>>

    @Insert
    suspend fun addCalculation(equation: Equation)
}