package com.trichain.omiinad.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.trichain.omiinad.entities.HolidayTable;
import com.trichain.omiinad.entities.PeopleTable;
import com.trichain.omiinad.entities.PhotoTable;
import com.trichain.omiinad.entities.VisitedPlaceTable;

@Database(entities = {HolidayTable.class, PeopleTable.class, PhotoTable.class, VisitedPlaceTable.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HolidayDao holidayDao();

    public abstract PeopleDao peopleDao();

    public abstract PhotoDao photoDao();

    public abstract VisitedPlaceDao visitedPlaceDao();
}
