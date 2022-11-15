package com.roland.android.calculator.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Equation::class], version = 2, exportSchema = false)
@TypeConverters(TypeConverter::class)
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
                ).addMigrations(migration)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

val migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Equation ADD COLUMN date INTEGER NOT NULL DEFAULT ''"
        )
    }

}