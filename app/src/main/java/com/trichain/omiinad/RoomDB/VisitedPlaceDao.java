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
    @Query("SELECT * FROM VisitedPlaceTable")
    List<VisitedPlaceTable> getAllVisitedplace();

    @Insert
    void insert(VisitedPlaceTable visitedPlaceTable);

    @Delete
    void delete(VisitedPlaceTable visitedPlaceTable);

    @Update
    void update(VisitedPlaceTable visitedPlaceTable);
}
