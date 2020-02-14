package com.trichain.omiinad.roomDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trichain.omiinad.entities.VisitedPlaceTable;

import java.util.List;

@Dao
public interface VisitedPlaceDao {

    @Query("SELECT * FROM VisitedPlaceTable WHERE holidayID= :holidayID")
    List<VisitedPlaceTable> getAllVisitedplace(int holidayID);

    @Query("SELECT * FROM VisitedPlaceTable WHERE id= :visitedPlace")
    VisitedPlaceTable getHolidayIdofplace(int visitedPlace);

    @Query("SELECT * FROM VisitedPlaceTable WHERE 1")
    List<VisitedPlaceTable> getAllVisitedplacesAnywhere();

    @Query("SELECT * FROM VisitedPlaceTable WHERE name LIKE :search or text LIKE :search")
    List<VisitedPlaceTable> getSearchResultsOfPlace(String search);

    @Insert
    long insert(VisitedPlaceTable visitedPlaceTable);

    @Delete
    void delete(VisitedPlaceTable visitedPlaceTable);

    @Update
    void update(VisitedPlaceTable visitedPlaceTable);
}
