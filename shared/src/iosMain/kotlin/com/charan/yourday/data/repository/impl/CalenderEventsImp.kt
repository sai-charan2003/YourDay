package com.charan.yourday.data.repository.impl

import com.charan.yourday.data.model.CalenderItems
import com.charan.yourday.data.repository.CalenderEventsRepo
import com.charan.yourday.utils.DateUtils
import kotlinx.cinterop.ExperimentalForeignApi
import platform.EventKit.EKEntityType
import platform.EventKit.EKEvent
import platform.EventKit.EKEventStore
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarOptions
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSDate
import platform.Foundation.dateByAddingTimeInterval
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIColor

class CalenderEventsImp : CalenderEventsRepo {
    private val eventStore = EKEventStore()


    @OptIn(ExperimentalForeignApi::class)
    override fun getCalenderEvents(): List<CalenderItems> {
        val calendars = eventStore.calendarsForEntityType(EKEntityType.EKEntityTypeEvent)
        val now = NSDate()


        val calendar = NSCalendar.currentCalendar
        val startOfDay = calendar.startOfDayForDate(now)
        val oneDayFromNow = calendar.dateByAddingUnit(
            unit = NSCalendarUnitDay,
            value = 1,
            toDate = startOfDay,
            NSCalendarOptions.MIN_VALUE
        )?.dateByAddingTimeInterval(-1.0)
        val range = eventStore.predicateForEventsWithStartDate(
            now,
            oneDayFromNow!!,
            calendars
        )
        println(eventStore.eventsMatchingPredicate(range))
       return eventStore.eventsMatchingPredicate(range).mapNotNull { event ->
            event as? EKEvent
        }.map {
            CalenderItems(
                eventId = it.eventIdentifier,
                title = it.title,
                stateTime = it.startDate?.timeIntervalSince1970?.toLong(),
                endTime = it.endDate?.timeIntervalSince1970?.toLong(),
                calenderColor = UIColor(it.calendar?.CGColor).toString()

            )
        }



    }

}