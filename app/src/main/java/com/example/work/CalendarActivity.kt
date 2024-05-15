package com.example.work

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CalendarActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var dbHelper: WorkDbHelper // Правильное имя переменной

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        listView = findViewById(R.id.listView)
        dbHelper = WorkDbHelper(this) // Используем переменную класса

        val db: SQLiteDatabase = dbHelper.readableDatabase
        val projection = arrayOf(
            WorkContract.Columns.DATE,
            WorkContract.Columns.WORK,
            WorkContract.Columns.WORKPLACE,
            WorkContract.Columns.START_TIME,
            WorkContract.Columns.END_TIME,
            WorkContract.Columns.HOURLY_RATE,
            WorkContract.Columns.TOTAL,
            WorkContract.Columns.LIKED,
            WorkContract.Columns.HARD
        )

        val cursor: Cursor = db.query(
            WorkContract.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val entries = mutableListOf<String>()

        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.DATE))
            val work = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.WORK))
            val workplace = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.WORKPLACE))
            val startTime = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.START_TIME))
            val endTime = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.END_TIME))
            val hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkContract.Columns.HOURLY_RATE))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkContract.Columns.TOTAL))
            val liked = cursor.getInt(cursor.getColumnIndexOrThrow(WorkContract.Columns.LIKED))
            val hard = cursor.getInt(cursor.getColumnIndexOrThrow(WorkContract.Columns.HARD))

            val entry = "\n$date \n$work | $workplace \n$startTime - $endTime \n$hourlyRate (ч) \nвсего $total \nLiked: $liked | Hard: $hard\n"
            entries.add(entry)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
        listView.adapter = adapter

        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            deleteSelectedItems()
        }
    }

    private fun deleteSelectedItems() {
        val selectedItemPosition = listView.checkedItemPosition
        if (selectedItemPosition != ListView.INVALID_POSITION) {
            val db: SQLiteDatabase = dbHelper.writableDatabase

            val cursor: Cursor = db.query(
                WorkContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )

            cursor.moveToPosition(selectedItemPosition)
            val date = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.DATE))

            val selection = "${WorkContract.Columns.DATE} = ?"
            val selectionArgs = arrayOf(date)
            val deletedRows = db.delete(WorkContract.TABLE_NAME, selection, selectionArgs)

            if (deletedRows > 0) {
                Toast.makeText(this, "Элемент удален", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ошибка при удалении элемента", Toast.LENGTH_SHORT).show()
            }

            cursor.close()

            // Обновляем список после удаления
            updateListView()
        } else {
            Toast.makeText(this, "Выберите элемент для удаления", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateListView() {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val projection = arrayOf(
            WorkContract.Columns.DATE,
            WorkContract.Columns.WORK,
            WorkContract.Columns.WORKPLACE,
            WorkContract.Columns.START_TIME,
            WorkContract.Columns.END_TIME,
            WorkContract.Columns.HOURLY_RATE,
            WorkContract.Columns.TOTAL,
            WorkContract.Columns.LIKED,
            WorkContract.Columns.HARD
        )

        val cursor: Cursor = db.query(
            WorkContract.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val entries = mutableListOf<String>()

        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.DATE))
            val work = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.WORK))
            val workplace = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.WORKPLACE))
            val startTime = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.START_TIME))
            val endTime = cursor.getString(cursor.getColumnIndexOrThrow(WorkContract.Columns.END_TIME))
            val hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkContract.Columns.HOURLY_RATE))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkContract.Columns.TOTAL))
            val liked = cursor.getInt(cursor.getColumnIndexOrThrow(WorkContract.Columns.LIKED))
            val hard = cursor.getInt(cursor.getColumnIndexOrThrow(WorkContract.Columns.HARD))

            val entry = "\n$date \n$work | $workplace \n$startTime - $endTime \n$hourlyRate (ч) \nвсего $total \nLiked: $liked | Hard: $hard\n"
            entries.add(entry)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
        listView.adapter = adapter
    }
}
