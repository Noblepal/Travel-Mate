package com.trichain.omiinad.RoomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.trichain.omiinad.Entities.HolidayTable;
import com.trichain.omiinad.Entities.PeopleTable;
import com.trichain.omiinad.Entities.PhotoTable;
import com.trichain.omiinad.Entities.VisitedPlaceTable;

@Database(entities = {HolidayTable.class, PeopleTable.class, PhotoTable.class, VisitedPlaceTable.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HolidayDao holidayDao();

    public abstract PeopleDao peopleDao();

    public abstract PhotoDao photoDao();

    public abstract VisitedPlaceDao visitedPlaceDao();
}
