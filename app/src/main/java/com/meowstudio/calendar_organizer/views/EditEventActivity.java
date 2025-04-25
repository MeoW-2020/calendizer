package com.meowstudio.calendar_organizer.views;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class EditEventActivity extends AppCompatActivity {

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

    private Switch isBirthdaySwitch;;
    private CheckBox isDoneCheckBox;

    private AlertDialog notificationAlertDialog;
    private AlertDialog repetitionAlertDialog;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    private int eventColor;
    private DBHelper dbHelper;
    private List<Notification> currentNotifications;
    private int oldEventId;
    private NotificationAdapter notificationAdapter;
    private Event mEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(getAppTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

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
        addNotificationLayout = (LinearLayout) findViewById(R.id.AddNewEventActivity_Layout_AddNotification);
        repeatTextView = (TextView) findViewById(R.id.AddNewEventActivity_TextView_Repeat);
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
        isDoneCheckBox = (CheckBox) findViewById(R.id.CheckBox_isDone);

    }

    @SuppressLint("ResourceType")
    private void initViews() {

        Intent intent = getIntent();
        int eventId = intent.getIntExtra("eventId", 0);
        mEvent = readEvent(eventId);
        oldEventId = mEvent.getId();

        eventTitleEditText.setText(mEvent.getTitle());

        setDateTextView.setText(intent.getStringExtra("eventDate"));

        if (mEvent.isAllDay()) {
            allDayEventSwitch.setChecked(true);
            setTimeLinearLayout.setVisibility(View.GONE);

        } else {
            allDayEventSwitch.setChecked(false);
            setTimeTextView.setText(mEvent.getTime());
        }

        if (mEvent.isBirthday()) {
            isBirthdaySwitch.setChecked(true);

        } else {
            isBirthdaySwitch.setChecked(false);
        }

        if (mEvent.isDone()) {
            isDoneCheckBox.setChecked(true);

        } else {
            isDoneCheckBox.setChecked(false);
        }

        currentNotifications = new ArrayList<>(readNotifications(mEvent.getId()));
        setUpRecyclerView();

        repeatTextView.setText(mEvent.getRecurringPeriod());

        eventNoteEditText.setText(mEvent.getNote());

        GradientDrawable bgShape = (GradientDrawable) pickNoteColorImgView.getBackground();
        bgShape.setColor(mEvent.getColor());

        eventAddressEditText.setText(mEvent.getAddress());

        phoneNumberEditText.setText(mEvent.getPhoneNumber());
    }

    private void initVariables() {
        Calendar mCal = Calendar.getInstance();
        mCal.setTimeZone(TimeZone.getDefault());
        try {
            mCal.setTime(Utils.eventDateFormat.parse(setDateTextView.getText().toString()));
            alarmYear = mCal.get(Calendar.YEAR);
            alarmMonth = mCal.get(Calendar.MONTH);
            alarmDay = mCal.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createAlertDialogs() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setCancelable(true);

        final View notificationDialogView = LayoutInflater.from(this).inflate(R.layout.layout_alert_dialog_notification, null, false);
        RadioGroup notificationRadioGroup = (RadioGroup) notificationDialogView.findViewById(R.id.AlertDialogLayout_RadioGroup);
        notificationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                currentNotifications.add(new Notification(((RadioButton) notificationDialogView.findViewById(checkedId)).getText().toString()));
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
                repeatTextView.setText(getResources().getString(R.string.repetition) + ": " +((RadioButton) eventRepetitionDialogView.findViewById(checkedId)).getText().toString());
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

    private Event readEvent(int eventId) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Event event = dbHelper.readEvent(sqLiteDatabase, eventId);
        event.setRecurringPeriod(dbHelper.readRecurringPeriod(sqLiteDatabase, event.getId()));
        sqLiteDatabase.close();
        return event;
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
        sqLiteDatabase.close();
        return notifications;
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
                aCal.set(Calendar.YEAR, year);
                aCal.set(Calendar.MONTH, month);
                aCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                aCal.setTimeZone(TimeZone.getDefault());
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
                    public void onCancel() {

                    }
                }).show();
        colorPicker.getNegativeButton().setText("Отмена");
    }

    private void setUpRecyclerView() {
        notificationsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setMeasurementCacheEnabled(false);
        notificationsRecyclerView.setLayoutManager(layoutManager);
        notificationAdapter = new NotificationAdapter(this, currentNotifications);
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
                    if (mEvent.isRecurring()) {

                        new AlertDialog.Builder(this)
                                .setTitle(getResources().getString(R.string.edit_warning_title))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(getResources().getString(R.string.edit_warning_text))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getViewValues();
                                        new UpdateAsyncTask().execute();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    } else {
                        getViewValues();
                        new UpdateAsyncTask().execute();
                    }
                }
                break;
        }
        return true;
    }

    private void cancelAlarms(List<Notification> notifications) {
        for (Notification notification : notifications) {
            cancelAlarm(notification.getId());
            dbHelper.deleteNotificationById(dbHelper.getWritableDatabase(), notification.getId());
        }
    }

    private void cancelAlarm(int requestCode) {
        Intent intent = new Intent(getApplicationContext(), ServiceAutoLauncher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, 0);
        pendingIntent.cancel();
    }

    private void setAlarms(ArrayList<Notification> notifications) {
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
        Intent intent = new Intent(this, ServiceAutoLauncher.class);
        intent.putExtra("eventTitle", mEvent.getTitle());
        intent.putExtra("eventNote", mEvent.getNote());
        intent.putExtra("eventColor", mEvent.getColor());
        intent.putExtra("eventTimeStamp", mEvent.getDate() + ", " + mEvent.getTime());
        intent.putExtra("interval", getInterval());
        intent.putExtra("soundName", getString("ringtone"));
        String asd = getInterval();
        intent.putExtra("notificationId", notification.getChannelId());

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notification.getId(), intent, 0);
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
        mEvent.setTitle(eventTitleEditText.getText().toString().trim());
        mEvent.setAllDay(allDayEventSwitch.isChecked());
        mEvent.setDate(Utils.eventDateFormat.format(aDate));
        mEvent.setMonth(Utils.monthFormat.format(aDate));
        mEvent.setYear(Utils.yearFormat.format(aDate));
        mEvent.setTime(setTimeTextView.getText().toString());
        mEvent.setNotify(!notificationAdapter.getNotifications().isEmpty());
        mEvent.setBirthday(isBirthdaySwitch.isChecked());
        mEvent.setDone(isDoneCheckBox.isChecked());
        mEvent.setRecurring(isRecurring(repeatTextView.getText().toString()));
        mEvent.setRecurringPeriod(repeatTextView.getText().toString());
        mEvent.setNote(eventNoteEditText.getText().toString().trim());
        if (eventColor == 0) {
            eventColor = getResources().getInteger(R.color.__1);
        } else {
            mEvent.setColor(eventColor);
        }
        mEvent.setAddress(eventAddressEditText.getText().toString().trim());
        mEvent.setPhoneNumber(phoneNumberEditText.getText().toString().trim());
        mEvent.setEmail(emailEditText.getText().toString().trim());
    }

    private boolean isRecurring(String toString) {
        return !toString.equals(getResources().getString(R.string.one_time));
    }

    private boolean confirmInputs() {
        if (!validateEventTitle()) {
            return false;
        }

        if (!validateNotifications()) {
            Snackbar.make(addNotificationLayout, getResources().getString(R.string.in_the_past), BaseTransientBottomBar.LENGTH_LONG).show();
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

        for (Notification notification : notificationAdapter.getNotifications()) {
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

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            cancelAlarms(readNotifications(mEvent.getId()));

            dbHelper.updateEvent(dbHelper.getWritableDatabase(), oldEventId, mEvent);
            for (Notification notification : notificationAdapter.getNotifications()) {
                notification.setEventId(mEvent.getId());
                dbHelper.saveNotification(dbHelper.getWritableDatabase(), notification);
            }
            if (mEvent.isNotify()) {
                setAlarms(readNotifications(mEvent.getId()));
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