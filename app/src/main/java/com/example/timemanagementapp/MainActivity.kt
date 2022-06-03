package com.example.timemanagementapp

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.KeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getRemindTime()

        val startDateBtn = findViewById<Button>(R.id.start_date_btn)
        val endDateBtn = findViewById<Button>(R.id.end_date_btn)
        val sleepHoursBtn = findViewById<Button>(R.id.enter_sleep_hours)
        val enterButton = findViewById<Button>(R.id.enter_button)

        val inputSleepHours = findViewById<EditText>(R.id.input_sleep_hours)
        val originalKeyListener = inputSleepHours.keyListener
        inputSleepHours.keyListener = null

        startDateBtn.setOnClickListener {
            clickStartDatePicker()
        }

        endDateBtn.setOnClickListener {
            clickEndDatePicker()
        }

        sleepHoursBtn.setOnClickListener {
            onClick(inputSleepHours, originalKeyListener)

            sleepHoursBtn.setOnFocusChangeListener { _, _ ->
                onFocusChange(inputSleepHours, inputSleepHours.hasFocus())
            }
        }

        enterButton.setOnClickListener {
            clickHours(inputSleepHours)
        }
    }

    private fun clickStartDatePicker () {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val selectedStartDate = findViewById<TextView>(R.id.selected_start_date)
        val selectedEndDate = findViewById<TextView>(R.id.selected_end_date)
        val selectedDateInHours = findViewById<TextView>(R.id.hours_number)

        val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth,
                                           selectedDayOfMonth -> Toast.makeText(this,
            "the chosen year is $selectedYear, " +
                    "the month is ${getMonth(selectedMonth+1)} " +
                    "and the selected day is $selectedDayOfMonth", Toast.LENGTH_LONG).show()

            val selectedDate = "$selectedDayOfMonth/${selectedMonth+1}/$selectedYear"
            val dateInHoursOutput = "Number of hours: "

            selectedStartDate.text = selectedDate
            selectedEndDate.text = ""
            selectedDateInHours.text = dateInHoursOutput
        },
            year,
            month,
            day)
        dpd.show()
    }

    private fun clickEndDatePicker() {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val selectedStartDate = findViewById<TextView>(R.id.selected_start_date)
        val selectedEndDate = findViewById<TextView>(R.id.selected_end_date)
        val selectedDateInHours = findViewById<TextView>(R.id.hours_number)

        val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth,
                                           selectedDayOfMonth -> Toast.makeText(this,
            "the chosen year is $selectedYear, " +
                    "the month is ${getMonth(selectedMonth+1)} " +
                    "and the selected day is $selectedDayOfMonth", Toast.LENGTH_LONG).show()
            val selectedDate = "$selectedDayOfMonth/${selectedMonth+1}/$selectedYear"

            val dateInHoursOutput = "Number of hours: " +
                    "${calculateNumberOfDaysBetweenTwoDates(selectedStartDate.text.toString(),
                        selectedDate)}"

            selectedEndDate.text = selectedDate
            selectedDateInHours.text = ""
            selectedDateInHours.text = dateInHoursOutput

        },
            year,
            month,
            day)

        dpd.show()
        getRemindTime()
    }

    private fun calculateNumberOfDaysBetweenTwoDates(selectedStartDate: String,
                                                     selectedEndDate: String): Int {
        println("First date: $selectedStartDate")
        println("Second date: $selectedEndDate")
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val endDate = sdf.parse(selectedEndDate)
        endDate?.let {
            val endDateInHours = endDate.time / 3600000

            val startDate = sdf.parse(selectedStartDate)

            startDate?.let {
                val startedDateInHours = startDate.time / 3600000

                return (endDateInHours - startedDateInHours).toInt()
            }
        }
        return 0
    }

    private fun getRemindTime() {
        val numberOfHoursTextView = findViewById<TextView>(R.id.hours_number)

        val check: Boolean = containsDigit(numberOfHoursTextView.text.toString())

        val numberOfDays = if (check) {
            7
        } else {
            0
        }

        println("Number of days: $numberOfDays")
        val currentDate = findViewById<TextView>(R.id.currentDate)
        val myCalendar = Calendar.getInstance()
        myCalendar.set(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH))
        val currentDayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK)

        val selectedStartDate = findViewById<TextView>(R.id.selected_start_date)
        val selectedEndDate = findViewById<TextView>(R.id.selected_end_date)

        var differenceBetweenTodayAndStartDate = 0
        var differenceBetweenTodayAndEndDate = 0

        val today = "${myCalendar.get(Calendar.DAY_OF_MONTH)}/" +
                "${myCalendar.get(Calendar.MONTH)+1}/" +
                "${myCalendar.get(Calendar.YEAR)}"

        if (selectedStartDate.text.isNotEmpty() && selectedEndDate.text.isNotEmpty()) {
            differenceBetweenTodayAndStartDate =
                calculateNumberOfDaysBetweenTwoDates(selectedStartDate.text.toString(), today)
            println("differenceBetweenTodayAndStartDate $differenceBetweenTodayAndStartDate")
            differenceBetweenTodayAndEndDate =
                calculateNumberOfDaysBetweenTwoDates(today, selectedEndDate.text.toString())
            println("differenceBetweenTodayAndEndDate $differenceBetweenTodayAndEndDate")
        }

        val sleepingHoursTextView = findViewById<TextView>(R.id.textView)
        val sleepingHoursInDay = if (sleepingHoursTextView.text.isEmpty()) {
            0
        } else {
            val numberOfSleepingHours = sleepingHoursTextView.text.toString().filter { it.isDigit() }
            numberOfSleepingHours.toInt()
        }

        println("Sleeping Hours in Day: $sleepingHoursInDay")
        val remindTime = if (numberOfDays == 0 || differenceBetweenTodayAndEndDate <= 0) {
            0
        } else if(differenceBetweenTodayAndStartDate > 0 && differenceBetweenTodayAndEndDate > 0) {
            (numberOfDays - currentDayOfWeek + 2) * (24 - sleepingHoursInDay - 8)
        } else {
            168
        }
        println("Remind time: $remindTime")
        val remindTimeOutput = "Today is ${getDayOfWeek(currentDayOfWeek-1)}\n" +
                "${myCalendar.get(Calendar.DAY_OF_MONTH)} of " +
                "${getMonth(myCalendar.get(Calendar.MONTH)+1)} " +
                "${myCalendar.get(Calendar.YEAR)}\n$remindTime hours left."
        currentDate.text = remindTimeOutput
    }

    private fun containsDigit(str: String): Boolean {
        for (element in str) {
            if (element.isDigit()) {
                return true
            }
        }
        return false
    }
    
    private fun getMonth(n: Int): String {
        return when(n) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            else -> "December"
        }
    }

    private fun getDayOfWeek(n: Int): String {
        return when(n) {
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            else -> "Sunday"
        }
    }

    private fun onClick(inputSleepHours: EditText, originalKeyListener: KeyListener) {
        inputSleepHours.keyListener = originalKeyListener
        inputSleepHours.requestFocus()

        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputSleepHours, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun onFocusChange(inputSleepHours: EditText, hasFocus: Boolean) {
        if (!hasFocus) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputSleepHours.windowToken, 0)
            inputSleepHours.keyListener = null
        }
    }

    private fun clickHours(inputSleepHours: EditText) {
        val inputSleep = findViewById<TextView>(R.id.input_sleep)
        inputSleep.text = ""
        if (inputSleepHours.text.toString().toInt() > 16) {
            val output = "Enter number that is less or equal to 16"
            inputSleep.text = output
            println(output)
        } else {
            val routineWorksInDay = 8
            val personalGrowthInDay = 24 - routineWorksInDay - inputSleepHours.text.toString().toInt()
            getTextViews(inputSleepHours.text.toString().toInt(), personalGrowthInDay * 7)
        }
        inputSleepHours.setText("")
    }

    private fun getTextViews(sleepingHours: Int, personalGrowthHoursInWeek: Int) {
        var personalGrowthHours = personalGrowthHoursInWeek
        val personalGrowthHoursInDay = personalGrowthHoursInWeek / 7

        val sleepingTextViewArray = arrayOf (
            R.id.textView,
            R.id.textView4,
            R.id.textView7,
            R.id.textView10,
            R.id.textView13,
            R.id.textView16,
            R.id.textView19
        )
        val routingTextViewArray = arrayOf (
            R.id.textView2,
            R.id.textView5,
            R.id.textView8,
            R.id.textView11,
            R.id.textView14,
            R.id.textView17,
            R.id.textView20
        )
        val personalTextViewArray = arrayOf(
            R.id.textView3,
            R.id.textView6,
            R.id.textView9,
            R.id.textView12,
            R.id.textView15,
            R.id.textView18,
            R.id.textView21
        )

        var i = 0

        while (i < 7) {
            val sleepingTextView = findViewById<TextView>(sleepingTextViewArray[i])
            val routineTextView = findViewById<TextView>(routingTextViewArray[i])
            val growthTextView = findViewById<TextView>(personalTextViewArray[i])

            val sleepingTextViewOutput = "$sleepingHours hours"
            val routineTextViewOutput = "8 hours"
            val growthTextViewOutput = "$personalGrowthHours hours"

            sleepingTextView.text = sleepingTextViewOutput
            routineTextView.text = routineTextViewOutput
            growthTextView.text = growthTextViewOutput

            personalGrowthHours -= personalGrowthHoursInDay
            i++
        }

        getRemindTime()
    }
}