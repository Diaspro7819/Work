package com.example.work

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


// Создаем объект для определения структуры таблицы
object WorkContract {
    // Название таблицы
    const val TABLE_NAME = "work_entries"

    // Названия столбцов
    object Columns {
        const val DATE = "date"
        const val WORK = "work" // кейт, банкет
        const val WORKPLACE = "workplace" // место где
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
        const val HOURLY_RATE = "hourly_rate" // ставка час
        const val TOTAL = "total" // всего денег за смену
        const val LIKED = "liked" // понравилось
        const val HARD = "hard" // тяжело или не очень
    }
}

class WorkDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_ENTRIES = "CREATE TABLE ${WorkContract.TABLE_NAME} (" +
                "${WorkContract.Columns.DATE} TEXT, " +
                "${WorkContract.Columns.WORK} TEXT, " +
                "${WorkContract.Columns.WORKPLACE} TEXT, " +
                "${WorkContract.Columns.START_TIME} TEXT, " +
                "${WorkContract.Columns.END_TIME} TEXT, " +
                "${WorkContract.Columns.HOURLY_RATE} REAL, " +
                "${WorkContract.Columns.TOTAL} REAL, " +
                "${WorkContract.Columns.LIKED} INTEGER, " +
                "${WorkContract.Columns.HARD} INTEGER)"

        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Метод onUpgrade вызывается, когда база данных нуждается в обновлении
        // Здесь можно описать логику обновления структуры базы данных
        // Например, можно удалить текущую таблицу и создать новую
        db.execSQL("DROP TABLE IF EXISTS ${WorkContract.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        // Версия базы данных. При изменении структуры базы данных увеличьте эту версию
        const val DATABASE_VERSION = 1
        // Имя файла базы данных
        const val DATABASE_NAME = "Work.db"
    }
}