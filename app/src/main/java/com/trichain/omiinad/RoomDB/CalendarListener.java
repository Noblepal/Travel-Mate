package com.trichain.omiinad.RoomDB;

import java.util.Calendar;

public interface CalendarListener {

    void onFirstDateSelected(Calendar startDate);
    void onDateRangeSelected(Calendar startDate, Calendar endDate);
}
