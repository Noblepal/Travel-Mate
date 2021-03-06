package com.trichain.omiinad.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trichain.omiinad.entities.PhotoTable;

import java.util.List;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM PhotoTable WHERE holidayID = :holiday")
    List<PhotoTable> getAllphotos(int holiday);

    @Query("SELECT * FROM PhotoTable where id in (SELECT max(id) FROM PhotoTable GROUP BY photoDate ) order by id desc")
    List<PhotoTable> GetListDatePhotos();


    @Query("SELECT * FROM PhotoTable where id in (SELECT max(id) FROM PhotoTable GROUP BY placeID ) order by id desc")
    List<PhotoTable> GetListPlacePhotos();

    @Query("SELECT * FROM PhotoTable where id in (SELECT max(id) FROM PhotoTable GROUP BY holidayID ) order by id desc")
    List<PhotoTable> GetListHolidayPhotos();

    @Query("SELECT * FROM PhotoTable WHERE 1")
    List<PhotoTable> getAllphotosUnconditional();

    @Query("SELECT * FROM PhotoTable WHERE placeID = :event")
    List<PhotoTable> getAllEventphotos(int event);

    @Query("SELECT COUNT(id) FROM PhotoTable WHERE holidayID = :holiday")
    int getNumberofHolidayphotos(int holiday);

    @Query("SELECT * FROM PhotoTable WHERE placeID = :event order by id desc")
    List<PhotoTable> getEventDescphotos(int event);

    @Query("SELECT * FROM PhotoTable WHERE placeID = :event order by id asc")
    List<PhotoTable> getEventAscphotos(int event);

    @Query("SELECT * FROM PhotoTable WHERE holidayID = :event order by id desc")
    List<PhotoTable> getHolDescphotos(int event);

    @Query("SELECT * FROM PhotoTable WHERE holidayID = :event order by id asc")
    List<PhotoTable> getHolAscphotos(int event);

    @Query("SELECT * FROM PhotoTable WHERE photoDate = :event order by id desc")
    List<PhotoTable> getDateDescphotos(String event);

    @Query("SELECT * FROM PhotoTable WHERE photoDate = :event order by id asc")
    List<PhotoTable> getDateAscphotos(String event);

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
