package com.trichain.omiinad.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.trichain.omiinad.R;

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

    public static String formatToReadable(Date date) {

        String reformattedStr = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-d hh:mm:ss");
        String strDate = formatter.format(date);
        System.out.println(strDate);
        Log.e(TAG, "formatToReadable: " + strDate);

        reformattedStr = strDate;


        return reformattedStr;
    }

    //2020-02-14 19:42:14
    public static String getDateNumberOnly(String d) {
        String[] x = d.split(" ");
        String[] xD = x[0].split("-");
        Log.e(TAG, "getDateNumberOnly: " + Arrays.toString(x));
        return xD[2];
    }

    public static String getTimeNumberOnly(String date) {
        date = extractDate(date);
        String reformattedStr = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        SimpleDateFormat fromInput = new SimpleDateFormat("yyyy-MMM-d");

        try {
            Date fromUser = fromInput.parse(date);
            reformattedStr = dateFormat.format(fromUser);
        } catch (ParseException e) {
            try {
                //2020-02-14 19:43:29
                dateFormat = new SimpleDateFormat("h:mm a");
                fromInput = new SimpleDateFormat("yyyy-MM-d hh:mm:ss");
                String[] x = date.split(" ");
                String mDate1 = x[1];
                Log.e(TAG, "formatDate: new date :" + mDate1);
                Date fromUser = fromInput.parse(mDate1);
                reformattedStr = dateFormat.format(fromUser);

                Log.e(TAG, "formatDate: " + Arrays.toString(x));

            } catch (Exception e1) {
                Log.e(TAG, "formatDate: Not a valid date: " + date);
            }
            e.printStackTrace();
        }
        return reformattedStr;
    }

    private static String extractDate(String a) {
        try {
            String[] newDate = a.split(" ");
            return newDate[5] + "-" + newDate[1] + "-" + newDate[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            return a;
        }
    }

    private static final float BITMAP_SCALE = 0.4f;
    private static final int BLUR_RADIUS = 8;

    public static Bitmap fastblur(Bitmap sentBitmap) {
        float scale = BITMAP_SCALE;
        int radius = BLUR_RADIUS;
        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);

    }

    public static void loadPhoto(@NonNull Context context, String imageUrl, ImageView target) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .fallback(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(target);
    }
}
