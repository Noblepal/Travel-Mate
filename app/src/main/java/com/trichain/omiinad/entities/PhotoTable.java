package com.trichain.omiinad.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class PhotoTable implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "holidayID")
    private int holidayID;

    @ColumnInfo(name = "placeID")
    private int placeID;

    @ColumnInfo(name = "photoName")
    private String photoName;

    @ColumnInfo(name = "photoDate")
    private String photoDate;

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

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoDate() {
        return photoDate;
    }

    public void setPhotoDate(String photoDate) {
        this.photoDate = photoDate;
    }
}
