package com.roland.android.calculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.roland.android.calculator.data.database.Equation
import com.roland.android.calculator.data.database.EquationDatabase
import com.roland.android.calculator.repository.EquationRepository
import kotlinx.coroutines.flow.Flow

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    // database
    private val repository: EquationRepository
    val getEquation: Flow<List<Equation>>

    init {
        val equationDao = EquationDatabase.getDatabase(app).equationDao()
        repository = EquationRepository(equationDao)
        getEquation = repository.getEquations
    }
}