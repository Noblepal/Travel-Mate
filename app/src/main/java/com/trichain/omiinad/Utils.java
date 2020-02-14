package com.trichain.omiinad;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final String TAG = "Utils";

    public static void setGoogleMapStyle(Context context, GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.silver_map));
    }


    public static String formatDate(String date) {
        date = extractDate(date);
        String reformattedStr = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
        SimpleDateFormat fromInput = new SimpleDateFormat("yyyy-MMM-d");

        try {
            Date fromUser = fromInput.parse(date);
            reformattedStr = dateFormat.format(fromUser);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    private static String extractDate(String a) {
        String[] newDate = a.split(" ");
        return newDate[5] + "-" + newDate[1] + "-" + newDate[2];
    }
}
