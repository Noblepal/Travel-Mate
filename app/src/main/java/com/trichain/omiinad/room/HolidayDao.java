package com.trichain.omiinad.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.trichain.omiinad.entities.HolidayTable;

import java.util.List;

@Dao
public interface HolidayDao {
    @Query("SELECT * FROM HolidayTable")
    List<HolidayTable> getAllHolidays();

    @Insert
    void insert(HolidayTable holidayTable);

    @Delete
    void delete(HolidayTable holidayTable);

    @Update
    void update(HolidayTable holidayTable);
}
