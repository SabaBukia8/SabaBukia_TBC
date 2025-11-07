package util

import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object DatePickerUtil {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun showDatePicker(
        context: Context,
        currentDate: String? = null,
        onDateSelected: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance()

        // Parse current date if provided
        currentDate?.let { date ->
            try {
                val parsedDate = dateFormat.parse(date)
                parsedDate?.let { calendar.time = it }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val formattedDate = dateFormat.format(calendar.time)
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set maximum date to today (no future dates for birthday)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // Set minimum date to 100 years ago
        val minCalendar = Calendar.getInstance()
        minCalendar.add(Calendar.YEAR, -100)
        datePickerDialog.datePicker.minDate = minCalendar.timeInMillis

        datePickerDialog.show()
    }
}