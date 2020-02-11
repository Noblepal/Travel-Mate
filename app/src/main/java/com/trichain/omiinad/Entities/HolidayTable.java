package com.trichain.omiinad.Entities;

import android.os.Build;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class HolidayTable implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "startDate")
    private String startDate;

    @ColumnInfo(name = "startTime")
    private String startTime;

    @ColumnInfo(name = "endDate")
    private String endDate;

    @ColumnInfo(name = "endTime")
    private String endTime;

    @ColumnInfo(name = "name")
    private String name;


    @TypeConverter
    public static LocalDate toDate(String dateString) {
        if (dateString == null) {
            return null;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return LocalDate.parse(dateString);
            }else
                return null;
        }
    }

    //getters and setters

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
