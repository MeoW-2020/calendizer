package com.meowstudio.calendar_organizer.other;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;

import com.meowstudio.calendar_organizer.views.MainActivity;
import com.meowstudio.calendar_organizer.R;

public class AlarmService extends Service {

    private Bundle bundle;
    private String eventTitle;
    private String eventNote;
    private int eventColor;
    private String interval;
    private String eventTimeStamp;
    private int notificationId;
    private String soundName;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            bundle = intent.getExtras();
            eventTitle = bundle.getString("eventTitle", "No title");
            eventNote = bundle.getString("eventNote", "No note");
            eventColor = bundle.getInt("eventColor", -49920);
            eventTimeStamp = bundle.getString("eventTimeStamp", "No Timestamp");
            interval = bundle.getString("interval", "");
            notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            soundName = bundle.getString("soundName", "Nice message tone");
            showNotification();
            setNewAlarm();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void showNotification(){

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, activityIntent, PendingIntent.FLAG_ONE_SHOT);

        String CHANNEL_ID="0";

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + getSoundResourceId(soundName));
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //For API 26+ you need to put some additional code like below:
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("0", "Event Notification", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(eventColor);
            mChannel.enableLights(true);
            mChannel.setDescription(eventNote);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(soundUri, audioAttributes);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel( mChannel );
            }
        }

        //General code:
        NotificationCompat.Builder status = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        status.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_event_note_24)
                .setContentTitle(eventTitle)
                .setContentText(eventNote)
                .setColor(eventColor)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setDefaults(Notification.DEFAULT_LIGHTS )
                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + getSoundResourceId(soundName)))
                .setContentIntent(pendingIntent);

        if (mNotificationManager != null) {
            mNotificationManager.notify(notificationId, status.build());
        }
    }

    private void setNewAlarm() {
        Intent intent = new Intent(this, ServiceAutoLauncher.class);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        long triggerAtMillis = getNextEventTriggerMillis();
        if (triggerAtMillis != 0) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
            Log.d("APP_TEST", "repeatingAlarm: Alarm at " + Long.toString(triggerAtMillis));
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    private long getNextEventTriggerMillis() {
        Calendar calendar = Calendar.getInstance();
        if (interval.equals(getString(R.string.daily))) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else if (interval.equals(getString(R.string.weekly))) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        } else if (interval.equals(getString(R.string.monthly))) {
            return getNextMonthMillis();
        } else if (interval.equals(getString(R.string.yearly))) {
            calendar.add(Calendar.YEAR, 1);
        } else {
            return 0;
        }
        return calendar.getTimeInMillis();
    }

    private long getNextMonthMillis() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        currentMonth++;

        if (currentMonth > Calendar.DECEMBER) {
            currentMonth = Calendar.JANUARY;
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        }

        cal.set(Calendar.MONTH, currentMonth);
        int maximumDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.set(Calendar.DAY_OF_MONTH, maximumDay);
        return cal.getTimeInMillis();
    }

    private int getSoundResourceId(String soundName) {
        switch (soundName) {
            case "Nice message tone":
                return R.raw.r1_nice_message_tone;
            case "Point blank":
                return R.raw.r2_point_blank;
            case "Tiru-tiru":
                return R.raw.r3_tiru_tiru;
            case "Android cool":
                return R.raw.r4_android_cool;
            case "Pi-pi-pi":
                return R.raw.r5_pi_pi_pi;
            case "Cat ring vibration":
                return R.raw.r6_cat_ring_vibration;
            case "Cat meow meow":
                return R.raw.r7_cat_meow_meow;
            case "Kitten":
                return R.raw.r8_kitten;
            case "Cat call":
                return R.raw.r9_cat_call;
        }

        return R.raw.r1_nice_message_tone;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}