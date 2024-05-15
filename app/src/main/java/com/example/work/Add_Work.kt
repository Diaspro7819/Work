package com.example.work

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class Add_Work : AppCompatActivity() {

    private lateinit var dbHelper: WorkDbHelper
    private lateinit var txtAll2: TextView
    private lateinit var txtMonth2: TextView
    private var currentMonth: Int = 0
    private var currentYear: Int = 0
    private lateinit var btnCalendar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work)
        // бд моя
        dbHelper = WorkDbHelper(this)

        btnCalendar = findViewById(R.id.btnCalendar)

        val buttonPickTime = findViewById<Button>(R.id.buttonPickTime)
        buttonPickTime.setOnClickListener {
            // Получаем текущее время
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            // диалог выбора времени и установка слушателя для обработки
            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->

                    // отображение выбранного времени в тексте кнопки
                    buttonPickTime.text = "$selectedHour:$selectedMinute"
                },
                hour,
                minute,
                true // true, если 24-часовой формат времени
            )

            //диалог выбора времени
            timePickerDialog.show()
        }
        val buttonPickTime2 = findViewById<Button>(R.id.buttonPickTime2)
        buttonPickTime2.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    buttonPickTime2.text = "$selectedHour:$selectedMinute"
                },
                hour,
                minute,
                true
            )
            timePickerDialog.show()
        }
        // В вашей активности или фрагменте после инициализации кнопки "Сохранить"
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            // Получаем данные из элементов пользовательского интерфейса
            val date = findViewById<EditText>(R.id.editTextDate).text.toString()
            val work = findViewById<Spinner>(R.id.spinner2).selectedItem.toString()
            val workplace = findViewById<EditText>(R.id.editTextText2).text.toString()
            val startTime = findViewById<Button>(R.id.buttonPickTime).text.toString()
            val endTime = findViewById<Button>(R.id.buttonPickTime2).text.toString()
            val hourlyRate = findViewById<EditText>(R.id.editTextNumber).text.toString().toDoubleOrNull() ?: 0.0
            val total = findViewById<EditText>(R.id.editTextNumber2).text.toString().toDoubleOrNull() ?: 0.0
            val liked = findViewById<SeekBar>(R.id.seekBar).progress
            val hard = findViewById<SeekBar>(R.id.seekBar2).progress

            // Записываем данные в базу данных
            val dbHelper = WorkDbHelper(this)
            val db = dbHelper.writableDatabase

            val values = ContentValues().apply {
                put(WorkContract.Columns.DATE, date)
                put(WorkContract.Columns.WORK, work)
                put(WorkContract.Columns.WORKPLACE, workplace)
                put(WorkContract.Columns.START_TIME, startTime)
                put(WorkContract.Columns.END_TIME, endTime)
                put(WorkContract.Columns.HOURLY_RATE, hourlyRate)
                put(WorkContract.Columns.TOTAL, total)
                put(WorkContract.Columns.LIKED, liked)
                put(WorkContract.Columns.HARD, hard)
            }

            val newRowId = db.insert(WorkContract.TABLE_NAME, null, values)

            // Проверяем, успешно ли записали данные
            if (newRowId != -1L) {
                // Выводим сообщение о успешном сохранении
                Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show()

                // Показываем диалоговое окно с данными, которые были сохранены
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.apply {
                    setTitle("Сохраненные данные")
                    setMessage("Дата: $date\n" +
                            "Работа: $work\n" +
                            "Место работы: $workplace\n" +
                            "Время начала: $startTime\n" +
                            "Время окончания: $endTime\n" +
                            "Ставка в час: $hourlyRate\n" +
                            "Всего: $total\n" +
                            "Понравилось: $liked\n" +
                            "Тяжело: $hard")
                    setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    create().show()
                }

            } else {
                Toast.makeText(this, "Ошибка при сохранении данных", Toast.LENGTH_SHORT).show()
            }

            // Выводим путь к файлу базы данных в логи
            val databasePath = getDatabasePath(WorkDbHelper.DATABASE_NAME).absolutePath
            println("Путь к файлу базы данных: $databasePath")

            // Проверяем, что ставка за час была введена корректно
            if (hourlyRate <= 0) {
                Toast.makeText(this, "Введите корректную ставку за час", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startHour = startTime.split(":")[0].toDouble()
            val startMinute = startTime.split(":")[1].toDouble()
            val endHour = endTime.split(":")[0].toDouble()
            val endMinute = endTime.split(":")[1].toDouble()

            // Рассчитываем общее время работы в часах
            val totalHours = endHour - startHour + (endMinute - startMinute) / 60

            // Рассчитываем общую сумму оплаты
            val totalPayment = totalHours * hourlyRate

            // Устанавливаем значение в editTextNumber2
            findViewById<EditText>(R.id.editTextNumber2).setText(totalPayment.toString())


        }
        btnCalendar.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

    }

}

