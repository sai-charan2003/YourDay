package com.charan.yourday.data.repository.impl

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import com.charan.yourday.data.model.CalenderItems
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.utils.DateUtils
import java.util.Date

class CalenderEventsImp(val context : Context) : CalenderEventsRepo {
    @SuppressLint("Range")
    override fun getCalenderEvents(): List<CalenderItems> {
        val currentTime = DateUtils.getCurrentTimeInMillis()
        val stateDayTime = DateUtils.getStartOfDay()
        val endDayTime = DateUtils.getEndOfDay()
        val calenderItems = mutableListOf<CalenderItems>()
        val uri: Uri = CalendarContract.Events.CONTENT_URI
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DISPLAY_COLOR
        )
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val startTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTSTART))
                val endTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTEND))
                if ((startTime in stateDayTime..endDayTime) ||
                    (endTime in stateDayTime..endDayTime) ||
                    (currentTime in startTime..endTime)) {
                    val calenderItem = CalenderItems(
                        eventId = it.getString(it.getColumnIndex(CalendarContract.Events._ID)),
                        title = it.getString(it.getColumnIndex(CalendarContract.Events.TITLE)),
                        description = it.getString(it.getColumnIndex(CalendarContract.Events.DESCRIPTION)),
                        stateTime = stateDayTime,
                        endTime = endTime,
                        calenderColor = it.getString(it.getColumnIndex(CalendarContract.Events.DISPLAY_COLOR)),
                    )
                    calenderItems.add(calenderItem)

                }


            }
        }
        return calenderItems
    }
}