package com.meowstudio.calendar_organizer;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {

    public static final int MAX_CALENDAR_DAYS = 42;

    public static final String DAILY = "Repeat Daily";
    public static final String WEEKLY = "Repeat Weekly";
    public static final String MONTHLY = "Repeat Monthly";
    public static final String YEARLY = "Repeat Yearly";

    public static int CURRENT_FILTER = 0;
    public static final int TODAY = 0;
    public static final int NEXT_7_DAYS = 1;
    public static final int NEXT_30_DAYS = 2;
    public static final int BIRTHDAYS = 3;
    public static final int ALL_EVENTS = 4;
    public static final String THIS_YEAR = "This Year";

    public enum AppTheme {
        ORANGE,
        DARK,
    }

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL yyyy");
    public static final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL");
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    public static final SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat birthdayFormat = new SimpleDateFormat("dd MMMM");

    public static Date convertStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date aDate = null;
        try {
            aDate = simpleDateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aDate;
    }

    public static Date convertStringToTime(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date aDate = null;
        try {
            aDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return aDate;
    }

    @SuppressLint("ResourceType")
    public static ArrayList<String> getColors(Context context) {
        ArrayList<String> colors = new ArrayList<>();

        colors.add(context.getResources().getString(R.color.__1));
        colors.add(context.getResources().getString(R.color.__16));
        colors.add(context.getResources().getString(R.color.__9));
        colors.add(context.getResources().getString(R.color.__5));
        colors.add(context.getResources().getString(R.color.__17));

        colors.add(context.getResources().getString(R.color.__2));
        colors.add(context.getResources().getString(R.color.__15));
        colors.add(context.getResources().getString(R.color.__10));
        colors.add(context.getResources().getString(R.color.__6));
        colors.add(context.getResources().getString(R.color.__18));

        colors.add(context.getResources().getString(R.color.__3));
        colors.add(context.getResources().getString(R.color.__14));
        colors.add(context.getResources().getString(R.color.__11));
        colors.add(context.getResources().getString(R.color.__7));
        colors.add(context.getResources().getString(R.color.__19));

        colors.add(context.getResources().getString(R.color.__4));
        colors.add(context.getResources().getString(R.color.__13));
        colors.add(context.getResources().getString(R.color.__12));
        colors.add(context.getResources().getString(R.color.__8));
        colors.add(context.getResources().getString(R.color.__20));
        
        return colors;
    }
}
