package com.roland.android.calculator.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Equation::class], version = 1, exportSchema = false)
abstract class EquationDatabase : RoomDatabase() {
    abstract fun equationDao(): EquationDao

    companion object {
        @Volatile
        private var INSTANCE: EquationDatabase? = null

        fun getDatabase(context: Context): EquationDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EquationDatabase::class.java,
                    "equation_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}