package com.trichain.omiinad.room;

import java.util.Calendar;

public interface CalendarListener {

    void onFirstDateSelected(Calendar startDate);
    void onDateRangeSelected(Calendar startDate, Calendar endDate);
}
