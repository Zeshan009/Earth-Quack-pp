package com.relicware.quakes;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;

import android.content.*;
import static android.icu.text.MessageFormat.format;

/**
 * Created by Muhammad on 14-Mar-18.
 */

public class Earthquake {
    private String mMagnitude, mTime, mDate, mLocation, mDistance;
    private int mColorCode;

    public Earthquake(String place, String stamp, String magnitude) {
        int index = place.toLowerCase().indexOf("of");
        if (index == -1) mLocation = place;
        else {
            mDistance = place.substring(0, index + 2);
            mLocation = place.substring(index + 3);
        }

        Double mag = Double.valueOf(magnitude);
        if (mag > 9.9) mMagnitude = new DecimalFormat("0").format(mag);
        else mMagnitude = new DecimalFormat("0.0").format(mag);

        switch (mag.intValue()) {
            case 0: case 1: case 2:
                mColorCode = Color.YELLOW;
                break;
            case 3: case 4:
                mColorCode = Color.CYAN;
                break;
            case 5: case 6:
                mColorCode = Color.MAGENTA;
                break;
            case 7: case 8:
                mColorCode = Color.RED;
                break;
            case 9: case 10:
                mColorCode = Color.LTGRAY;
                break;
            default:
                mColorCode = Color.BLACK;;
                break;
        }

        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(Long.valueOf(stamp) / 1000, 0, ZoneOffset.UTC);
        mDate = localDateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        mTime = localDateTime.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    @Override
    public String toString() {
        return "Place: " + mLocation + ", Time: " + mTime + " " + mDate  + ", Magnitude: " + mMagnitude;
    }

    public int getColorCode() { return mColorCode; }
    public String getDistance() { return mDistance; }
    public String getLocation() { return mLocation; }
    public String getTime() { return mTime; }
    public String getDate() { return mDate; }
    public String getMagnitude() { return mMagnitude; }
}
