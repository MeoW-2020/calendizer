package com.meowstudio.calendar_organizer.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceAutoLauncher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mIntent = new Intent(context, AlarmService.class);
        mIntent.putExtras(intent.getExtras());
        context.startService(mIntent);
    }
}
