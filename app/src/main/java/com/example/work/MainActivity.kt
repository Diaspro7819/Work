package com.example.work

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var btnCalendar: Button
    private lateinit var btnAdd: Button
    private lateinit var txtAll2: TextView
    private lateinit var txtMonth2: TextView
    private lateinit var spinner: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var dbHelper: WorkDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnCalendar = findViewById(R.id.btnCalendar)
        btnAdd = findViewById(R.id.btnAdd)
        txtAll2 = findViewById(R.id.txtAll2) // Инициализация txtAll2
        txtMonth2 = findViewById(R.id.txtMonth2)
        spinner = findViewById(R.id.spinner)
        spinner2 = findViewById(R.id.spinner2)
        val spinner3 = findViewById<Spinner>(R.id.spinner3)

        dbHelper = WorkDbHelper(this)

        val calendar = Calendar.getInstance() // Инициализация календаря
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        btnCalendar.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
        btnAdd.setOnClickListener {
            val intent = Intent(this, Add_Work::class.java)
            startActivity(intent)
        }

        // Создание списка месяцев
        val months = arrayOf(
            "январь", "февраль", "март", "апрель", "май", "июнь",
            "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"
        )

        // Создание списка годов (от текущего года до 2024)
        val years = Array(currentYear - 2024 + 1) { (currentYear - it).toString() }

        // Инициализация адаптера для спиннеров
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        val spinner2Adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        spinner2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = spinner2Adapter

        spinner.setSelection(currentMonth)
        spinner2.setSelection(years.size - 1)
// Получение суммы всех значений из столбца total
        val totalSum = getTotalSumFromDatabase()
        txtAll2.text = totalSum.toString()
        // Установка слушателя на выбор элемента в спиннере месяцев
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = position + 1 // +1 для учета нулевого индекса
                val selectedYear = spinner2.selectedItem.toString().toInt()
                val selectedWork = spinner3.selectedItem.toString()
                val totalSumForSelectedMonth = getTotalSumForCurrentMonthFromDatabase(selectedMonth, selectedYear, selectedWork)
                txtMonth2.text = totalSumForSelectedMonth.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Здесь можно описать действия при отсутствии выбора
            }
        }

        // Установка слушателя на выбор элемента в спиннере работы
        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = spinner.selectedItemPosition + 1 // +1 для учета нулевого индекса
                val selectedYear = spinner2.selectedItem.toString().toInt()
                val selectedWork = spinner3.selectedItem.toString()
                val totalSumForSelectedMonth = getTotalSumForCurrentMonthFromDatabase(selectedMonth, selectedYear, selectedWork)
                txtMonth2.text = totalSumForSelectedMonth.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Здесь можно описать действия при отсутствии выбора
            }
        }

    }

    // Метод для получения суммы всех значений из столбца total
    private fun getTotalSumFromDatabase(): Double {
        var totalSum = 0.0
        val db = dbHelper.readableDatabase
        val projection = arrayOf(WorkContract.Columns.TOTAL)
        val cursor = db.query(
            WorkContract.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkContract.Columns.TOTAL))
            totalSum += total
        }
        cursor.close()
        return totalSum
    }

    // Метод для получения суммы из столбца total за определенный месяц и год
    private fun getTotalSumForCurrentMonthFromDatabase(month: Int, year: Int, selectedWork: String): Double {
        var totalSum = 0.0
        val db = dbHelper.readableDatabase

        val selection = if (selectedWork == "все") {
            "${WorkContract.Columns.DATE} LIKE ?"
        } else {
            "${WorkContract.Columns.DATE} LIKE ? AND ${WorkContract.Columns.WORK} = ?"
        }

        val selectionArgs = if (selectedWork == "все") {
            arrayOf("%.${String.format("%02d", month)}.$year")
        } else {
            arrayOf("%.${String.format("%02d", month)}.$year", selectedWork)
        }

        val projection = arrayOf(WorkContract.Columns.TOTAL)
        val cursor = db.query(
            WorkContract.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(WorkContract.Columns.TOTAL))
            totalSum += total
        }
        cursor.close()
        return totalSum
    }

}