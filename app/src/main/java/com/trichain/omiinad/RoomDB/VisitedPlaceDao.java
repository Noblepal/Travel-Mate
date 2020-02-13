package com.trichain.omiinad.RoomDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trichain.omiinad.Entities.VisitedPlaceTable;

import java.util.List;

@Dao
public interface VisitedPlaceDao {

    @Query("SELECT * FROM VisitedPlaceTable WHERE holidayID= :holidayID")
    List<VisitedPlaceTable> getAllVisitedplace(int holidayID);

    @Query("SELECT * FROM VisitedPlaceTable WHERE id= :visitedPlace")
    VisitedPlaceTable getHolidayIdofplace(int visitedPlace);

    @Query("SELECT * FROM VisitedPlaceTable WHERE 1")
    List<VisitedPlaceTable> getAllVisitedplacesAnywhere();

    @Query("SELECT * FROM VisitedPlaceTable WHERE name LIKE :search OR text LIKE :search")
    List<VisitedPlaceTable> getSearchResultsofplace(String search);

    @Insert
    long insert(VisitedPlaceTable visitedPlaceTable);

    @Delete
    void delete(VisitedPlaceTable visitedPlaceTable);

    @Update
    void update(VisitedPlaceTable visitedPlaceTable);
}
