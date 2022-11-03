package com.roland.android.calculator.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EquationDao {
    @Query("SELECT * FROM equation ORDER BY id")
    fun getEquations(): LiveData<List<Equation>>

    @Insert
    suspend fun addCalculation(equation: Equation)
}