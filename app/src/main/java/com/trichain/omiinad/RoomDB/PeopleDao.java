package com.trichain.omiinad.RoomDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trichain.omiinad.Entities.PeopleTable;
import com.trichain.omiinad.Entities.PhotoTable;

import java.util.List;

@Dao
public interface PeopleDao {
    @Query("SELECT * FROM PeopleTable")
    List<PeopleTable> getAllpeople();

    @Query("SELECT COUNT(id) FROM PeopleTable WHERE placeID = :event")
    int getNumberofEventPeople(int event);

    @Query("SELECT * FROM PeopleTable WHERE placeID = :event")
    List<PeopleTable> getAllofEventPeople(int event);

    @Insert
    void insert(PeopleTable peopleTable);

    @Delete
    void delete(PeopleTable peopleTable);

    @Update
    void update(PeopleTable peopleTable);
}
