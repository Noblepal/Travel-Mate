package com.trichain.omiinad.RoomDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trichain.omiinad.Entities.PhotoTable;

import java.util.List;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM PhotoTable WHERE holidayID = :holiday")
    List<PhotoTable> getAllphotos(int holiday);


    @Query("SELECT * FROM PhotoTable WHERE 1")
    List<PhotoTable> getAllphotosUnconditional();

    @Query("SELECT * FROM PhotoTable WHERE placeID = :event")
    List<PhotoTable> getAllEventphotos(int event);

    @Query("SELECT COUNT(id) FROM PhotoTable WHERE holidayID = :holiday")
    int getNumberofHolidayphotos(int holiday);

    @Query("SELECT COUNT(id) FROM PhotoTable WHERE placeID = :event")
    int getNumberofEventphotos(int event);

    @Query("SELECT  photoName FROM PhotoTable WHERE holidayID = :event LIMIT 1")
    String getLatestHolydayphotos(int event);

    @Query("SELECT  photoName FROM PhotoTable WHERE placeID = :event LIMIT 1")
    String getLatestEventphotos(int event);

    @Insert
    void insert(PhotoTable photoTable);

    @Delete
    void delete(PhotoTable photoTable);

    @Update
    void update(PhotoTable photoTable);
}
