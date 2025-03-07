package com.charan.yourday.utils


import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

object DateUtils {

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun getDateInDDMMYYYY(): String {
        val formatPattern = "dd/MM/yyyy"
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateTimeFormat = LocalDateTime.Format {
            byUnicodePattern(formatPattern)
        }
        return currentDate.format(dateTimeFormat)
    }

    fun getGreeting(): String {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
        return when (currentTime.hour) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun getCurrentTimeInMillis() : Long {
        return Clock.System.now().toEpochMilliseconds()
    }

    fun getStartOfDay(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        return today.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    fun getEndOfDay(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        return tomorrow.atStartOfDayIn(timeZone).toEpochMilliseconds() - 1
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun getTimeFromTimeMillis(timeMills: Long): String {
        val instant = Instant.fromEpochMilliseconds(timeMills)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = localDateTime.hour % 12
        val adjustedHour = if (hour == 0) 12 else hour
        val amPm = if (localDateTime.hour < 12) "AM" else "PM"

        return "${adjustedHour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}$amPm"
    }

    fun String.convertToMMMDYYYY(): String {
        val parsedDate = LocalDate.parse(this)

        val monthNames = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        val month = monthNames[parsedDate.month.ordinal]
        val day = parsedDate.dayOfMonth
        val year = parsedDate.year

        return "$month $day, $year"
    }
    fun String.convertToMMMDYYYYWithTime(): String {
        val localDateTime: LocalDateTime = try {

            val instant = Instant.parse(this)
            instant.toLocalDateTime(TimeZone.currentSystemDefault())
        } catch (e: Exception) {

            LocalDateTime.parse(this)
        }

        val monthNames = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        val month = monthNames[localDateTime.month.ordinal]
        val day = localDateTime.dayOfMonth
        val year = localDateTime.year
        val hour = localDateTime.hour
        val minute = localDateTime.minute

        // Format hours into 12-hour format with AM/PM
        val amPm = if (hour < 12) "AM" else "PM"
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12

        return "$month $day, $year, $formattedHour:${minute.toString().padStart(2, '0')} $amPm"
    }

    fun String.isOverDue() : Boolean {
        return LocalDate.parse(this) < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
}