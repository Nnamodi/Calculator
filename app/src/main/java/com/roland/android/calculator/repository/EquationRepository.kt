package com.roland.android.calculator.repository

import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.data.database.EquationDao
import kotlinx.coroutines.flow.Flow

class EquationRepository(private val equationDao: EquationDao) {
    val getEquations: Flow<List<Equation>> = equationDao.getEquations()

    suspend fun addCalculation(equation: Equation) {
        equationDao.addCalculation(equation)
    }
}