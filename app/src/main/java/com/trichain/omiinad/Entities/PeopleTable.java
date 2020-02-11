package com.trichain.omiinad.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class PeopleTable implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "holidayID")
    private int holidayID;

    @ColumnInfo(name = "placeID")
    private int placeID;

    @ColumnInfo(name = "personName")
    private String personName;


    //getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHolidayID() {
        return holidayID;
    }

    public void setHolidayID(int holidayID) {
        this.holidayID = holidayID;
    }

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
