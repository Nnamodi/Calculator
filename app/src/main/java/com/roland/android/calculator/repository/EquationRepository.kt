package com.roland.android.calculator.repository

import androidx.lifecycle.LiveData
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.data.database.EquationDao

class EquationRepository(private val equationDao: EquationDao) {
    val getEquations: LiveData<List<Equation>> = equationDao.getEquations()

    suspend fun addCalculation(equation: Equation) {
        equationDao.addCalculation(equation)
    }
}