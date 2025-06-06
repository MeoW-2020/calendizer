package com.meowstudio.calendar_organizer.views;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.meowstudio.calendar_organizer.adapters.UpcomingEventAdapter;
import com.meowstudio.calendar_organizer.database.DBHelper;
import com.meowstudio.calendar_organizer.database.DBTables;
import com.meowstudio.calendar_organizer.models.Event;
import com.meowstudio.calendar_organizer.R;
import com.meowstudio.calendar_organizer.Utils;
import com.meowstudio.calendar_organizer.models.RecurringPattern;

public class UpcomingEventsFragment extends Fragment {

    private ImageButton changePeriodImageButton;
    public TextView periodTextView;
    private RecyclerView eventsRecyclerView;

    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_events, container, false);

        dbHelper = new DBHelper(getActivity());

        defineViews(view);
        initViews();
        defineListeners();

        return view;
    }

    private void defineViews(View view) {
        changePeriodImageButton = (ImageButton) view.findViewById(R.id.UpcomingEventsFragment_ImageButton_Period);
        periodTextView = (TextView) view.findViewById(R.id.UpcomingEventsFragment_TextView_Period);
        eventsRecyclerView = (RecyclerView) view.findViewById(R.id.UpcomingEventsFragment_RecyclerView_Events);
    }

    private void initViews() {
        periodTextView.setText(getContext().getResources().getString(R.string.today));

        setUpRecyclerView();
    }

    private void defineListeners() {
        changePeriodImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_period, popup.getMenu());
                popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
                popup.show();

                setUpRecyclerView();
            }

            class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.PopupPeriod_Item_Today:
                            Utils.CURRENT_FILTER = Utils.TODAY;
                            periodTextView.setText(getContext().getResources().getString(R.string.today));
                            break;
                        case R.id.PopupPeriod_Item_Next7Days:
                            Utils.CURRENT_FILTER = Utils.NEXT_7_DAYS;
                            periodTextView.setText(getContext().getResources().getString(R.string.next_7_days));
                            break;
                        case R.id.PopupPeriod_Item_Next30Days:
                            Utils.CURRENT_FILTER = Utils.NEXT_30_DAYS;
                            periodTextView.setText(getContext().getResources().getString(R.string.next_30_days));
                            break;
                        case R.id.PopupPeriod_Item_Birthdays:
                            Utils.CURRENT_FILTER = Utils.BIRTHDAYS;
                            periodTextView.setText(getContext().getResources().getString(R.string.birthdays));
                            break;
                        case R.id.PopupPeriod_Item_All:
                            Utils.CURRENT_FILTER = Utils.ALL_EVENTS;
                            periodTextView.setText(getContext().getResources().getString(R.string.all_events));
                            break;
                    }
                    setUpRecyclerView();
                    return true;
                }
            }
        });
    }

    public void setUpRecyclerView() {
        eventsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setMeasurementCacheEnabled(false);
        eventsRecyclerView.setLayoutManager(layoutManager);

        UpcomingEventAdapter upcomingEventAdapter = new UpcomingEventAdapter(getActivity(), collectEvents(Calendar.getInstance().getTime()), this);
        eventsRecyclerView.setAdapter(upcomingEventAdapter);
    }

    private List<Event> collectEvents(Date today) {
        List<Event> events = null;

        try {
            switch (Utils.CURRENT_FILTER) {
                case Utils.TODAY:
                    events = collectTodayEvents(today);
                    break;
                case Utils.NEXT_7_DAYS:
                    events = collectNext7DaysEvents(today);
                    break;
                case Utils.NEXT_30_DAYS:
                    events = collectNext30DaysEvents(today);
                    break;
                case Utils.BIRTHDAYS:
                    events = collectBirthdays(today);
                    break;
                case Utils.ALL_EVENTS:
                    events = collectAllEvents(today);
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Collections.sort(events, new Comparator<Event>(){
            @Override
            public int compare(Event ev1, Event ev2) {
                return  Utils.convertStringToDate(ev1.getDate()).compareTo(Utils.convertStringToDate(ev2.getDate()));
            }
        });

        return events;
    }

    private List<Event> collectTodayEvents(Date today) {
        List<Event> eventList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        List<RecurringPattern> recurringPatterns = readRecurringPatterns();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Event event = new Event();
        for (RecurringPattern recurringPattern : recurringPatterns) {
            switch (recurringPattern.getPattern()) {
                case Utils.DAILY:
                    event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                    event.setDate(Utils.eventDateFormat.format(today));
                    eventList.add(event);
                    break;
                case Utils.WEEKLY:
                    if (dayOfWeek == recurringPattern.getDayOfWeek()) {
                        event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                        event.setDate(Utils.eventDateFormat.format(today));
                        eventList.add(event);
                    }
                    break;
                case Utils.MONTHLY:
                    if (dayOfMonth == recurringPattern.getDayOfMonth()) {
                        event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                        event.setDate(Utils.eventDateFormat.format(today));
                        eventList.add(event);
                    }
                    break;
                case Utils.YEARLY:
                    if (month == recurringPattern.getMonthOfYear() && dayOfMonth == recurringPattern.getDayOfMonth()) {
                        event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                        event.setDate(Utils.eventDateFormat.format(today));
                        eventList.add(event);
                    }
                    break;
            }
        }

        Cursor cursor = dbHelper.readEventsByDate(sqLiteDatabase, Utils.eventDateFormat.format(today));
        while (cursor.moveToNext()) {
            int eventID = cursor.getInt(cursor.getColumnIndex(DBTables.EVENT_ID));
            if (!isContains(eventList, eventID)) {
                eventList.add(dbHelper.readEvent(sqLiteDatabase, eventID));
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return eventList;
    }

    private List<Event> collectNext7DaysEvents(Date today) throws ParseException {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(today);

        Calendar toCalendar = (Calendar) fromCalendar.clone();
        toCalendar.add(Calendar.DAY_OF_MONTH, 8);

        Date fromDate = fromCalendar.getTime();
        Date toDate = toCalendar.getTime();

        List<Event> eventList = new ArrayList<>();

        List<RecurringPattern> recurringPatterns = readRecurringPatterns();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Event event = new Event();
        for (RecurringPattern recurringPattern : recurringPatterns) {
            switch (recurringPattern.getPattern()) {
                case Utils.DAILY:
                    Calendar mCalendar = (Calendar) fromCalendar.clone();
                    event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                    for (int i = 0; i < 7; i++) {
                        mCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        event.setDate(Utils.eventDateFormat.format(mCalendar.getTime()));
                        eventList.add(event);
                        event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId()); // TODO: clone the object
                    }
                    break;
                case Utils.WEEKLY:
                    mCalendar = (Calendar) fromCalendar.clone();
                    mCalendar.add(Calendar.DAY_OF_MONTH, 7);
                    mCalendar.set(Calendar.DAY_OF_WEEK, recurringPattern.getDayOfWeek());
                    event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                    event.setDate(Utils.eventDateFormat.format(mCalendar.getTime()));
                    eventList.add(event);
                    break;
                case Utils.MONTHLY:
                    mCalendar = (Calendar) fromCalendar.clone();
                    mCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    if (recurringPattern.getDayOfMonth() >= mCalendar.get(Calendar.DAY_OF_MONTH)) {
                        mCalendar.set(Calendar.DAY_OF_MONTH, recurringPattern.getDayOfMonth());
                        event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                        event.setDate(Utils.eventDateFormat.format(mCalendar.getTime()));
                        eventList.add(event);
                    }
                    break;
                case Utils.THIS_YEAR:
                    mCalendar = (Calendar) fromCalendar.clone();
                    mCalendar.set(Calendar.MONTH, recurringPattern.getMonthOfYear());
                    mCalendar.set(Calendar.DAY_OF_MONTH, recurringPattern.getDayOfMonth());
                    event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                    event.setDate(Utils.eventDateFormat.format(today));
                    eventList.add(event);
            }
        }

        List<Event> allEvents = dbHelper.readAllEvents(sqLiteDatabase);
        for (Event mEvent : allEvents) {
            Date currentDate = Utils.eventDateFormat.parse(mEvent.getDate());
            if (currentDate.after(fromDate) && currentDate.before(toDate) && !isContains(eventList, mEvent.getId())) {
                eventList.add(mEvent);
            }
        }
        sqLiteDatabase.close();
        return eventList;
    }

    private List<Event> collectNext30DaysEvents(Date today) throws ParseException {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(today);

        Calendar toCalendar = (Calendar) fromCalendar.clone();
        toCalendar.add(Calendar.DAY_OF_MONTH, 31);

        Date fromDate = fromCalendar.getTime();
        Date toDate = toCalendar.getTime();

        List<Event> eventList = new ArrayList<>();

        List<RecurringPattern> recurringPatterns = readRecurringPatterns();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Event event = new Event();
        for (RecurringPattern recurringPattern : recurringPatterns) {
            switch (recurringPattern.getPattern()) {
                case Utils.DAILY:
                    Calendar mCalendar = (Calendar) fromCalendar.clone();
                    for (int i = 0; i < 30; i++) {
                        event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());// TODO: clone the object
                        mCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        event.setDate(Utils.eventDateFormat.format(mCalendar.getTime()));
                        eventList.add(event);
                    }
                    break;
                case Utils.WEEKLY:
                    mCalendar = (Calendar) fromCalendar.clone();
                    for (int i = 0; i < 4; i++) {
                        event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId()); // TODO: clone the object
                        mCalendar.add(Calendar.DAY_OF_MONTH, 7);
                        mCalendar.set(Calendar.DAY_OF_WEEK, recurringPattern.getDayOfWeek());
                        if (mCalendar.getTime().before(toDate)) {
                            event.setDate(Utils.eventDateFormat.format(mCalendar.getTime()));
                            eventList.add(event);
                        }
                    }
                    break;
                case Utils.MONTHLY:
                    mCalendar = (Calendar) fromCalendar.clone();
                    mCalendar.set(Calendar.DAY_OF_MONTH, recurringPattern.getDayOfMonth());
                    mCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    if (mCalendar.getTime().before(toDate) && mCalendar.getTime().after(fromDate)) {
                        mCalendar.set(Calendar.DAY_OF_MONTH, recurringPattern.getDayOfMonth());
                        event = dbHelper.readEvent(sqLiteDatabase, recurringPattern.getEventId());
                        event.setDate(Utils.eventDateFormat.format(mCalendar.getTime()));
                        eventList.add(event);
                    }
                    break;
            }
        }

        List<Event> allEvents = dbHelper.readAllEvents(sqLiteDatabase);
        for (Event mEvent : allEvents) {
            Date currentDate = Utils.eventDateFormat.parse(mEvent.getDate());
            if (currentDate.after(fromDate) && currentDate.before(toDate) && !isContains(eventList, mEvent.getId())) {
                eventList.add(mEvent);
            }
        }
        sqLiteDatabase.close();
        return eventList;
    }

    private List<Event> collectBirthdays(Date today) {

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        List<Event> eventList = new ArrayList<>();

        List<Event> allEvents = dbHelper.readAllEvents(sqLiteDatabase);
        for (Event mEvent : allEvents) {
            if (mEvent.isBirthday()) {
                eventList.add(mEvent);
            }
        }
        sqLiteDatabase.close();
        return eventList;
    }

    private List<Event> collectAllEvents(Date today) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        return dbHelper.readAllEvents(sqLiteDatabase);
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

    private boolean isContains(List<Event> events, int eventId) {
        for (Event event : events) {
            if (event.getId() == eventId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            setUpRecyclerView();
            Toast.makeText(getActivity(), getResources().getString(R.string.event_edited), Toast.LENGTH_SHORT).show();
        }
    }
}
