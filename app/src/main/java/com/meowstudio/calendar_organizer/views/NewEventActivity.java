package com.meowstudio.calendar_organizer.views;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.meowstudio.calendar_organizer.adapters.NotificationAdapter;
import com.meowstudio.calendar_organizer.database.DBHelper;
import com.meowstudio.calendar_organizer.database.DBTables;
import com.meowstudio.calendar_organizer.models.Event;
import com.meowstudio.calendar_organizer.models.Notification;
import com.meowstudio.calendar_organizer.R;
import com.meowstudio.calendar_organizer.Utils;
import com.meowstudio.calendar_organizer.other.ServiceAutoLauncher;
import petrov.kristiyan.colorpicker.ColorPicker;

public class NewEventActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EditText eventTitleEditText;
    private Switch allDayEventSwitch;
    private LinearLayout setDateLinearLayout;
    private TextView setDateTextView;
    private LinearLayout setTimeLinearLayout;
    private TextView setTimeTextView;
    private RecyclerView notificationsRecyclerView;
    private LinearLayout addNotificationLayout;
    private LinearLayout repeatLayout;
    private TextView repeatTextView;
    private EditText eventNoteEditText;
    private ImageView pickNoteColorImgView;
    private LinearLayout pickNoteColorLayout;
    private EditText eventAddressEditText;
    private EditText phoneNumberEditText;
    private EditText emailEditText;

    private Switch isBirthdaySwitch;
    private LinearLayout isDoneLayout;
    private CheckBox isDoneCheckBox;

    private AlertDialog notificationAlertDialog;
    private AlertDialog repetitionAlertDialog;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    private int eventColor;
    private DBHelper dbHelper;
    private List<Notification> notifications;
    private Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(getAppTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        event = new Event();
        notifications = new ArrayList<>();
        dbHelper = new DBHelper(this);

        defineViews();
        initViews();
        initVariables();
        createAlertDialogs();
        defineListeners();

        setSupportActionBar(toolbar);
    }

    private void defineViews() {
        eventTitleEditText = (EditText) findViewById(R.id.AddNewEventActivity_EditText_EventTitle);
        allDayEventSwitch = (Switch) findViewById(R.id.AddNewEventActivity_Switch_AllDayEvent);
        setDateLinearLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_LinearLayout_SetDate);
        setDateTextView = (TextView) findViewById(R.id.AddNewEventActivity_TexView_SetDate);
        setTimeLinearLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_LinearLayout_SetTime);
        setTimeTextView = (TextView) findViewById(R.id.AddNewEventActivity_TexView_SetTime);
        notificationsRecyclerView = (RecyclerView) findViewById(R.id.AddNewEventActivity_RecyclerView_Notifications);
        repeatTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_Repeat);
        addNotificationLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_Layout_AddNotification);
        repeatLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_Layout_Repeat);
        eventNoteEditText = (EditText) findViewById(R.id.AddNewEventActivity_EditText_Note);
        pickNoteColorImgView = (ImageView) findViewById(R.id.AddNewEventActivity_ImgView_PickNoteColor);
        eventAddressEditText = (EditText) findViewById(R.id.AddNewEventActivity_EditText_Location);
        phoneNumberEditText = (EditText) findViewById(R.id.AddNewEventActivity_EditText_PhoneNumber);
        emailEditText = (EditText) findViewById(R.id.AddNewEventActivity_EditText_Mail);
        pickNoteColorLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_Layout_PickNoteColor);

        progressBar = (ProgressBar) findViewById(R.id.AddNewEventActivity_ProgressBar);
        toolbar = (Toolbar) findViewById(R.id.AddNewEventActivity_Toolbar);

        isBirthdaySwitch = (Switch) findViewById(R.id.AddNewEventActivity_CheckBox_IsBirthday);
        isDoneLayout = (LinearLayout) findViewById(R.id.isDone_Layout);
        isDoneCheckBox = (CheckBox) findViewById(R.id.CheckBox_isDone);
    }

    @SuppressLint("ResourceType")
    private void initViews() {
        Intent intent = getIntent();
        setDateTextView.setText(intent.getStringExtra("date"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        setTimeTextView.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));

        isDoneLayout.setVisibility(View.GONE);

        GradientDrawable bgShape = (GradientDrawable) pickNoteColorImgView.getBackground();
        bgShape.setColor(getResources().getInteger(R.color.__1));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        repeatTextView.setText(sharedPreferences.getString("frequency", getResources().getString(R.string.repetition) + ": " + getResources().getString(R.string.one_time)));
        notifications.add(new Notification(sharedPreferences.getString("reminder", getResources().getString(R.string.at_the_time_of_event))));
        setUpRecyclerView();
    }

    private void initVariables() {
        Calendar mCal = Calendar.getInstance();
        mCal.setTimeZone(TimeZone.getDefault());
        alarmHour = mCal.get(Calendar.HOUR_OF_DAY);
        alarmMinute = mCal.get(Calendar.MINUTE);

        try {
            mCal.setTime(Utils.eventDateFormat.parse(getIntent().getStringExtra("date")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        alarmYear = mCal.get(Calendar.YEAR);
        alarmMonth = mCal.get(Calendar.MONTH);
        alarmDay = mCal.get(Calendar.DAY_OF_MONTH);
    }

    private void createAlertDialogs() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setCancelable(true);

        final View notificationDialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_notification, null, false);
        RadioGroup notificationRadioGroup = (RadioGroup) notificationDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        notificationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                notifications.add(new Notification(((RadioButton) notificationDialogView.findViewById(checkedId)).getText().toString()));
                notificationAlertDialog.dismiss();
                setUpRecyclerView();
            }
        });
        builder.setView(notificationDialogView);
        notificationAlertDialog = builder
                .setPositiveButton("OK", null)
                .create();

        final View eventRepetitionDialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_repeat, null, false);
        RadioGroup eventRepetitionRadioGroup = (RadioGroup) eventRepetitionDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        eventRepetitionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                repeatTextView.setText(getResources().getString(R.string.repetition) + ": " + ((RadioButton) eventRepetitionDialogView.findViewById(checkedId)).getText().toString());
                repetitionAlertDialog.dismiss();
            }
        });
        builder.setView(eventRepetitionDialogView);
        repetitionAlertDialog = builder
                .setPositiveButton("OK", null)
                .create();
    }

    private void defineListeners() {
        allDayEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    setTimeLinearLayout.setVisibility(View.GONE);
                    String eventTime = "";
                    setTimeTextView.setText(eventTime);
                } else {
                    setTimeLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        setDateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(view);
            }
        });

        setTimeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(view);
            }
        });

        addNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationAlertDialog.show();
            }
        });

        repeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repetitionAlertDialog.show();
            }
        });

        pickNoteColorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickNoteColor(view);
            }
        });
    }

    public void setTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar aCal = Calendar.getInstance();
                aCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                aCal.set(Calendar.MINUTE, minute);
                aCal.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                String eventTime = simpleDateFormat.format(aCal.getTime());

                alarmHour = hourOfDay;
                alarmMinute = minute;

                setTimeTextView.setText(eventTime);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    public void setDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar aCal = Calendar.getInstance();
                aCal.setTimeZone(TimeZone.getDefault());
                aCal.set(Calendar.YEAR, year);
                aCal.set(Calendar.MONTH, month);
                aCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String eventTime = simpleDateFormat.format(aCal.getTime());

                alarmYear = year;
                alarmMonth = month;
                alarmDay = dayOfMonth;

                setDateTextView.setText(eventTime);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    public void pickNoteColor(View view) {
        final ArrayList<String> colors = Utils.getColors(this);
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker
                .setTitle(getString(R.string.choose_color))
                .setColorButtonTickColor(getResources().getColor(R.color.white))
                .setDefaultColorButton(getResources().getColor(R.color.__1))
                .setColors(colors)
                .setColumns(5)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        eventColor = color;
                        GradientDrawable bgShape = (GradientDrawable) pickNoteColorImgView.getBackground();
                        bgShape.setColor(color);
                    }

                    @Override
                    public void onCancel() { }
                }).show();
        colorPicker.getNegativeButton().setText("Отмена");
    }

    private void setUpRecyclerView() {
        notificationsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setMeasurementCacheEnabled(false);
        notificationsRecyclerView.setLayoutManager(layoutManager);
        NotificationAdapter notificationAdapter = new NotificationAdapter(this, notifications);
        notificationsRecyclerView.setAdapter(notificationAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.ToolBar_Item_Save:
                if (confirmInputs()) {
                    getViewValues();
                    new SaveAsyncTask().execute();
                }
                break;
        }
        return true;
    }

    private void setAlarms() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(alarmYear, alarmMonth, alarmDay);

        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, 0);

        for (Notification notification : notifications) {
            Calendar aCal = (Calendar) calendar.clone();
            String notificationPreference = notification.getTime();

            if (notificationPreference.equals(getString(R.string._10_minutes_before))) {
                aCal.add(Calendar.MINUTE, -10);
            } else if (notificationPreference.equals(getString(R.string._1_hour_before))) {
                aCal.add(Calendar.HOUR_OF_DAY, -1);
            } else if (notificationPreference.equals(getString(R.string._1_day_before))) {
                aCal.add(Calendar.DAY_OF_MONTH, -1);
            }

            setAlarm(notification, aCal.getTimeInMillis());
        }
    }

    private void setAlarm(Notification notification, long triggerAtMillis) {
        Intent intent = new Intent(getApplicationContext(), ServiceAutoLauncher.class);
        intent.putExtra("eventTitle", event.getTitle());
        intent.putExtra("eventNote", event.getNote());
        intent.putExtra("eventColor", event.getColor());
        intent.putExtra("eventTimeStamp", event.getDate() + ", " + event.getTime());
        intent.putExtra("interval", getInterval());
        intent.putExtra("notificationId", notification.getChannelId());
        intent.putExtra("soundName", getString("ringtone"));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notification.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }

    private String getInterval() {
        String interval = getString(R.string.one_time);
        String repeatingPeriod = repeatTextView.getText().toString();
        if (repeatingPeriod.equals(getString(R.string.daily))) {
            interval = getString(R.string.daily);
        } else if (repeatingPeriod.equals(getString(R.string.weekly))) {
            interval = getString(R.string.weekly);
        } else if (repeatingPeriod.equals(getString(R.string.monthly))) {
            interval = getString(R.string.monthly);
        } else if (repeatingPeriod.equals(getString(R.string.yearly))) {
            interval = getString(R.string.yearly);
        }
        return interval;
    }

    @SuppressLint("ResourceType")
    private void getViewValues() {
        Date aDate = null;
        try {
            aDate = Utils.eventDateFormat.parse((String) setDateTextView.getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        event.setTitle(eventTitleEditText.getText().toString().trim());
        event.setAllDay(allDayEventSwitch.isChecked());
        event.setDate(Utils.eventDateFormat.format(aDate));
        event.setMonth(Utils.monthFormat.format(aDate));
        event.setYear(Utils.yearFormat.format(aDate));
        event.setTime(setTimeTextView.getText().toString());
        event.setNotify(!notifications.isEmpty());
        event.setBirthday(isBirthdaySwitch.isChecked());
        event.setDone(isDoneCheckBox.isChecked());
        event.setRecurring(isRecurring(repeatTextView.getText().toString()));
        event.setRecurringPeriod(repeatTextView.getText().toString());
        event.setNote(eventNoteEditText.getText().toString().trim());
        if (eventColor == 0) {
            eventColor = getResources().getInteger(R.color.__1);
            event.setColor(eventColor);
        }
        else{
            event.setColor(eventColor);
        }
        event.setAddress(eventAddressEditText.getText().toString().trim());
        event.setPhoneNumber(phoneNumberEditText.getText().toString().trim());
        event.setEmail(emailEditText.getText().toString().trim());
    }

    private boolean isRecurring(String toString) {
        return !toString.equals(getResources().getString(R.string.repetition) + " " + getResources().getString(R.string.one_time));
    }

    private boolean confirmInputs() {
        if (!validateEventTitle()) {
            return false;
        }

        if (!validateNotifications()) {
            Snackbar.make(addNotificationLayout, getResources().getString(R.string.in_the_past), BaseTransientBottomBar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateEventTitle() {
        String eventTitleString = eventTitleEditText.getText().toString().trim();
        if (eventTitleString.isEmpty()) {
            eventTitleEditText.setError(getResources().getString(R.string.field_is_empty));
            return false;
        } else {
            eventTitleEditText.setError(null);
            return true;
        }
    }

    private boolean validateNotifications() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(alarmYear, alarmMonth, alarmDay);

        calendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        calendar.set(Calendar.MINUTE, alarmMinute);
        calendar.set(Calendar.SECOND, 0);

        for (Notification notification : notifications) {
            Calendar aCal = (Calendar) calendar.clone();
            String notificationPreference = notification.getTime();

            if (notificationPreference.equals(getString(R.string._10_minutes_before))) {
                aCal.add(Calendar.MINUTE, -10);
            } else if (notificationPreference.equals(getString(R.string._1_hour_before))) {
                aCal.add(Calendar.HOUR_OF_DAY, -1);
            } else if (notificationPreference.equals(getString(R.string._1_day_before))) {
                aCal.add(Calendar.DAY_OF_MONTH, -1);
            }

            if (aCal.before(Calendar.getInstance())) {
                return false;
            }
        }
        return true;
    }

    private class SaveAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dbHelper.saveEvent(dbHelper.getWritableDatabase(), event);
            int event_id = getEventId(event.getTitle(), event.getDate(), event.getTime());
            for (Notification notification : notifications) {
                notification.setEventId(event_id);
                dbHelper.saveNotification(dbHelper.getWritableDatabase(), notification);
            }
            notifications = readNotifications(event_id);
            if (event.isNotify()) {
                setAlarms();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dbHelper.close();
            setResult(RESULT_OK);
            finish();
        }
    }

    private ArrayList<Notification> readNotifications(int eventId) {
        ArrayList<Notification> notifications = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readEventNotifications(sqLiteDatabase, eventId);
        while (cursor.moveToNext()) {
            Notification notification = new Notification();
            notification.setId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_ID)));
            notification.setEventId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_EVENT_ID)));
            notification.setTime(cursor.getString(cursor.getColumnIndex(DBTables.NOTIFICATION_TIME)));
            notification.setChannelId(cursor.getInt(cursor.getColumnIndex(DBTables.NOTIFICATION_CHANNEL_ID)));
            notifications.add(notification);
        }
        return notifications;
    }

    private int getEventId(String eventTitle, String eventDate, String eventTime) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Event event = dbHelper.readEventByTimestamp(sqLiteDatabase, eventTitle, eventDate, eventTime);
        sqLiteDatabase.close();
        return event.getId();
    }

    private int getAppTheme() {
        switch (getString("theme")) {
            case "Dark":
                return R.style.DarkTheme;
            case "Orange":
                return R.style.OrangeTheme;
        }

        return R.style.OrangeTheme;
    }

    private String getString(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(key, "Orange");
    }
}