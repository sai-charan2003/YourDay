package com.charan.yourday.data.repository

import com.charan.yourday.data.model.CalenderItems

interface CalenderEventsRepo {

    fun getCalenderEvents() : List<CalenderItems>
}