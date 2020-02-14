package com.trichain.omiinad;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
            try {
                //2020-02-14 19:43:29
                dateFormat = new SimpleDateFormat("MMM h:mm a");
                fromInput = new SimpleDateFormat("yyyy-MM-d hh:mm:ss");
                String[] x = date.split(" ");
                String[] xD = x[0].split("-");
                String mDate1 = x[0] + " " + x[1];
                Log.e(TAG, "formatDate: new date :" + mDate1);
                Date fromUser = fromInput.parse(mDate1);
                reformattedStr = dateFormat.format(fromUser);

                Log.e(TAG, "formatDate: " + Arrays.toString(xD));

            } catch (Exception e1) {
                Log.e(TAG, "formatDate: Not a valid date: " + date);
            }
            e.printStackTrace();
        }
        return reformattedStr;
    }

    //2020-02-14 19:42:14
    public static String getDateNumberOnly(String d) {
        String[] x = d.split(" ");
        String[] xD = x[0].split("-");
        Log.e(TAG, "getDateNumberOnly: " + Arrays.toString(x));
        return xD[2];
    }

    private static String extractDate(String a) {
        try {
            String[] newDate = a.split(" ");
            return newDate[5] + "-" + newDate[1] + "-" + newDate[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            return a;
        }
    }
}
