package com.meowstudio.calendar_organizer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.meowstudio.calendar_organizer.database.DBHelper;
import com.meowstudio.calendar_organizer.database.DBTables;
import com.meowstudio.calendar_organizer.models.Event;
import com.meowstudio.calendar_organizer.R;
import com.meowstudio.calendar_organizer.Utils;
import com.meowstudio.calendar_organizer.models.RecurringPattern;

public class GridAdapter extends ArrayAdapter {

    private final String DAILY = "Repeat Daily";
    private final String WEEKLY = "Repeat Weekly";
    private final String MONTHLY = "Repeat Monthly";
    private final String YEARLY = "Repeat Yearly";

    private Utils.AppTheme appTheme;

    private List<Date> dates;
    private Calendar selectedCalendar;
    private List<Event> events;
    private LayoutInflater layoutInflater;
    private TextView dayTextView;
    private TextView eventCountTextView;
    private ArrayList<Integer> colors;
    private DBHelper dbHelper;

    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar selectedCalendar, List<Event> events) {
        super(context, R.layout.layout_cell);

        this.dates = dates;
        this.selectedCalendar = selectedCalendar;
        this.events = events;
        this.layoutInflater = LayoutInflater.from(context);
        dbHelper = new DBHelper(context);

        this.appTheme = getAppTheme();
        colors = getColors();
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Date viewDate = dates.get(position);
        Calendar viewCalendar = Calendar.getInstance();
        viewCalendar.setTime(viewDate);

        int viewMonth = viewCalendar.get(Calendar.MONTH);
        int viewYear = viewCalendar.get(Calendar.YEAR);
        int viewDayOfMonth = viewCalendar.get(Calendar.DAY_OF_MONTH);
        int viewDayOfWeek = viewCalendar.get(Calendar.DAY_OF_WEEK);

        int selectedMonth = selectedCalendar.get(Calendar.MONTH);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_cell, parent, false);
            dayTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_Day);
            eventCountTextView = (TextView) convertView.findViewById(R.id.LayoutCell_TextView_EventCount);

            dayTextView.setText(String.valueOf(viewDayOfMonth));
        }

        TextView dayTextView = convertView.findViewById(R.id.LayoutCell_TextView_Day);
        TextView eventCountTextView = convertView.findViewById(R.id.LayoutCell_TextView_EventCount);
        LinearLayout bgLinearLayout = convertView.findViewById(R.id.LayoutCell_LinearLayout);

        if (viewYear == selectedYear && viewMonth == selectedMonth) {
            //активные даты
            convertView.setBackgroundColor(getContext().getResources().getColor(colors.get(2)));
            dayTextView.setTextColor(getContext().getResources().getColor(colors.get(3)));
            eventCountTextView.setTextColor(getContext().getResources().getColor(colors.get(6)));
        } else {
            //нективные даты
            dayTextView.setTextColor(getContext().getResources().getColor(colors.get(1)));
            eventCountTextView.setVisibility(View.INVISIBLE);
        }

        //текущая дата
        Calendar mCalendar = Calendar.getInstance();
        if (viewYear == mCalendar.get(Calendar.YEAR) && viewMonth == mCalendar.get(Calendar.MONTH) && viewDayOfMonth == mCalendar.get(Calendar.DAY_OF_MONTH)) {
            bgLinearLayout.setBackgroundColor(getContext().getResources().getColor(colors.get(4)));
            dayTextView.setTextColor(getContext().getResources().getColor(colors.get(5)));
            eventCountTextView.setTextColor(getContext().getResources().getColor(colors.get(5)));
        }

        if (viewDayOfWeek == Calendar.SUNDAY || viewDayOfWeek == Calendar.SATURDAY) {
            if (viewYear == selectedYear && viewMonth == selectedMonth) {
                //неактивные даты
                dayTextView.setTextColor(getContext().getResources().getColor(R.color.__1));
            } else {
                //активные даты
                dayTextView.setTextColor(getContext().getResources().getColor(R.color.__1_tr50));
                eventCountTextView.setVisibility(View.INVISIBLE);
            }
        }

        //подсчет событий
        List<Integer> eventIDs = new ArrayList<>();
        List<RecurringPattern> recurringPatterns = readRecurringPatterns();
        for (RecurringPattern recurringPattern : recurringPatterns) {
            switch (recurringPattern.getPattern()) {
                case DAILY:
                    eventIDs.add(recurringPattern.getEventId());
                    break;
                case WEEKLY:
                    if (viewDayOfWeek == recurringPattern.getDayOfWeek()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
                case MONTHLY:
                    if (viewDayOfMonth == recurringPattern.getDayOfMonth()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
                case YEARLY:
                    if (viewMonth == recurringPattern.getMonthOfYear() && viewDayOfMonth == recurringPattern.getDayOfMonth()) {
                        eventIDs.add(recurringPattern.getEventId());
                    }
                    break;
            }
        }

        mCalendar = Calendar.getInstance();
        for (Event event : events) {
            if(event.getDate()!=null){
                mCalendar.setTime(Utils.convertStringToDate(event.getDate()));
                if (viewDayOfMonth == mCalendar.get(Calendar.DAY_OF_MONTH) && viewMonth == mCalendar.get(Calendar.MONTH) && viewYear == mCalendar.get(Calendar.YEAR) && !eventIDs.contains(event.getId())) {
                    eventIDs.add(event.getId());
                }
            }
        }

        if (eventIDs.size() > 0) {
            eventCountTextView.setText(Integer.toString(eventIDs.size()));
        }
        if(!eventCountTextView.getText().toString().equals(" ")){
            eventCountTextView.setBackgroundResource(R.drawable.filled_circle);
        }

        return convertView;
    }

    private List<RecurringPattern> readRecurringPatterns() {
        List<RecurringPattern> recurringPatterns = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readAllRecurringPatterns(sqLiteDatabase);
        while (cursor.moveToNext()) {
            RecurringPattern recurringPattern = new RecurringPattern();
            recurringPattern.setEventId(cursor.getInt(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_EVENT_ID)));
            recurringPattern.setPattern(cursor.getString(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_TYPE)));
            recurringPattern.setMonthOfYear(cursor.getInt(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_MONTH_OF_YEAR)));
            recurringPattern.setDayOfMonth(cursor.getInt(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_DAY_OF_MONTH)));
            recurringPattern.setDayOfWeek(cursor.getInt(cursor.getColumnIndex(DBTables.RECURRING_PATTERN_DAY_OF_WEEK)));
            recurringPatterns.add(recurringPattern);
        }
        return recurringPatterns;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    private Utils.AppTheme getAppTheme() {
        Utils.AppTheme theme = Utils.AppTheme.ORANGE;
        switch (getString()) {
            case "Dark":
                theme = Utils.AppTheme.DARK;
                break;
            case "Orange":
                theme = Utils.AppTheme.ORANGE;
                break;
        }
        return theme;
    }

    private String getString() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences.getString("theme", "Orange");
    }

    private ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        switch (appTheme) {
            case ORANGE:
                colors.add(R.color.transparent); //фон неактивной даты
                colors.add(R.color.transparent_25); //цвет текста неактивной даты
                colors.add(R.color.transparent); //фон ативной даты
                colors.add(R.color.black); //цвет текста неактивной даты
                colors.add(R.color._6); //фон текущей даты
                colors.add(R.color.white); //цвет текста текущей даты
                colors.add(R.color.black); //цвет текста цифры цисла событий
                break;
            case DARK:
                colors.add(R.color.transparent);
                colors.add(R.color.white_transparent_25);
                colors.add(R.color.transparent);
                colors.add(R.color._5);
                colors.add(R.color._6);
                colors.add(R.color.white);
                colors.add(R.color._5);
                break;
        }
        return colors;
    }
}
