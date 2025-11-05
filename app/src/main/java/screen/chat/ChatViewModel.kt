package screen.chat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import model.Message
import kotlin.time.Duration.Companion.days

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val timeZone = TimeZone.currentSystemDefault()

    fun send(textRaw: String) {
        val text = textRaw.trim()
        if (text.isEmpty()) return
        val now = Clock.System.now()
        val count = _messages.value.size
        val onLeft = count % 2 == 0
        val message = Message(
            id = now.toEpochMilliseconds(),
            text = text,
            timestamp = now,
            onLeft = onLeft
        )
        _messages.update { it + message }
    }

    fun formatTimestamp(instant: Instant): String {
        val now = Clock.System.now().toLocalDateTime(timeZone)
        val today: LocalDate = now.date
        val yesterday: LocalDate = yesterdayOf(today)

        val dt = instant.toLocalDateTime(timeZone)
        val datePart = when (dt.date) {
            today -> TODAY
            yesterday -> YESTERDAY
            else -> monthAbbrev(dt.monthNumber) + " " + dt.dayOfMonth
        }
        val time = format12Hour(dt.hour, dt.minute)
        return "$datePart, $time"
    }

    private fun yesterdayOf(date: LocalDate): LocalDate {
        val startOfDay = LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, 0, 0)
            .toInstant(timeZone)
        val yInstant = startOfDay - 1.days
        return yInstant.toLocalDateTime(timeZone).date
    }

    private fun format12Hour(hour24: Int, minute: Int): String {
        val am = hour24 < 12
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 <= 12 -> hour24
            else -> hour24 - 12
        }
        val mm = minute.toString().padStart(2, '0')
        val suffix = if (am) AM else PM
        return "$hour12:$mm$suffix"
    }

    private fun monthAbbrev(month: Int): String = when (month) {
        1 -> MONTH_JAN
        2 -> MONTH_FEB
        3 -> MONTH_MAR
        4 -> MONTH_APR
        5 -> MONTH_MAY
        6 -> MONTH_JUN
        7 -> MONTH_JUL
        8 -> MONTH_AUG
        9 -> MONTH_SEP
        10 -> MONTH_OCT
        11 -> MONTH_NOV
        else -> MONTH_DEC
    }

    companion object {
        private const val TODAY = "Today"
        private const val YESTERDAY = "Yesterday"
        private const val AM = "am"
        private const val PM = "pm"
        private const val MONTH_JAN = "Jan"
        private const val MONTH_FEB = "Feb"
        private const val MONTH_MAR = "Mar"
        private const val MONTH_APR = "Apr"
        private const val MONTH_MAY = "May"
        private const val MONTH_JUN = "Jun"
        private const val MONTH_JUL = "Jul"
        private const val MONTH_AUG = "Aug"
        private const val MONTH_SEP = "Sep"
        private const val MONTH_OCT = "Oct"
        private const val MONTH_NOV = "Nov"
        private const val MONTH_DEC = "Dec"
    }
}