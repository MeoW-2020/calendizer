package com.meowstudio.calendar_organizer.adapters;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.meowstudio.calendar_organizer.database.DBHelper;
import com.meowstudio.calendar_organizer.database.DBTables;
import com.meowstudio.calendar_organizer.models.Event;
import com.meowstudio.calendar_organizer.R;
import com.meowstudio.calendar_organizer.models.Notification;
import com.meowstudio.calendar_organizer.other.ServiceAutoLauncher;
import com.meowstudio.calendar_organizer.views.CalendarFragment;
import com.meowstudio.calendar_organizer.views.EditEventActivity;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private static final int EDIT_EVENT_ACTIVITY_REQUEST_CODE = 1;

    private Context context;
    private List<Event> eventList;
    private DBHelper dbHelper;
    private CalendarFragment calendarFragment;
    private AlertDialog alertDialog;

    public EventAdapter(Context context, List<Event> eventList, AlertDialog alertDialog, CalendarFragment calendarFragment) {
        this.context = context;
        this.eventList = eventList;
        this.calendarFragment = calendarFragment;
        this.alertDialog = alertDialog;

        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_event_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Event event = eventList.get(position);

        holder.eventColorImageView.setBackgroundColor(event.getColor());
        holder.eventTitleTextView.setText(event.getTitle());
        holder.eventTimeTextView.setText(event.getTime());
        holder.eventNoteTextView.setText(event.getNote());
        holder.isDoneCheckBox.setChecked(event.isDone());

        holder.eventCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditEventActivity.class);
                intent.putExtra("eventId", event.getId());
                intent.putExtra("eventDate", event.getDate());
                calendarFragment.startActivityForResult(intent, EDIT_EVENT_ACTIVITY_REQUEST_CODE);
            }
        });

        holder.optionsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.optionsImageButton, position);
            }
        });

        if (!event.isNotify()) {
            holder.notificationImageButton.setVisibility(View.GONE);
        }

        if (!event.isBirthday()) {
            holder.birthdayImageView.setVisibility(View.GONE);
        }

        if (event.isAllDay()) {
            holder.eventTimeLinearLayout.setVisibility(View.GONE);
        }
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView eventCardView;
        private ImageView eventColorImageView;
        private TextView eventTitleTextView;
        private TextView eventTimeTextView;
        private TextView eventNoteTextView;
        private ImageButton optionsImageButton;
        private ImageButton notificationImageButton;
        private ImageView birthdayImageView;
        private LinearLayout eventTimeLinearLayout;
        private CheckBox isDoneCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eventCardView = (CardView) itemView.findViewById(R.id.LayoutCell_CardView);
            eventColorImageView = (ImageView) itemView.findViewById(R.id.LayoutCell_ImageView_EventColor);
            eventTitleTextView = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventTitle);
            eventTimeTextView = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventTime);
            eventNoteTextView = (TextView) itemView.findViewById(R.id.LayoutCell_TextView_EventNote);
            optionsImageButton = (ImageButton) itemView.findViewById(R.id.LayoutCell_ImageButton_Options);
            notificationImageButton = (ImageButton) itemView.findViewById(R.id.LayoutCell_ImageButton_Notification);
            birthdayImageView = (ImageView) itemView.findViewById(R.id.LayoutCell_ImageView_Birthday);
            eventTimeLinearLayout = (LinearLayout) itemView.findViewById(R.id.LayoutCell_LinearLayout_EventTime);
            isDoneCheckBox = (CheckBox) itemView.findViewById(R.id.LayoutCell_CheckBox_isDone);
        }
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;
        private Event mEvent;

        public MyMenuItemClickListener(int position) {
            this.position = position;
            this.mEvent = eventList.get(position);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent intent = null;
            switch (menuItem.getItemId()) {
                case R.id.Popup_Item_Edit:
                    intent = new Intent(context, EditEventActivity.class);
                    intent.putExtra("eventId", mEvent.getId());
                    intent.putExtra("eventDate", mEvent.getDate());
                    calendarFragment.startActivityForResult(intent, EDIT_EVENT_ACTIVITY_REQUEST_CODE);
                    return true;
                case R.id.Popup_Item_Delete:
                    if (mEvent.isRecurring()) {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.delete_warning_title)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.delete_warning_text)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new DeleteAsyncTask().execute(mEvent.getId());
                                        eventList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, eventList.size());
                                        notifyDataSetChanged();
                                        calendarFragment.setUpCalendar();
                                        Toast.makeText(context, R.string.event_deleted, Toast.LENGTH_SHORT).show();
                                        if (eventList.isEmpty()) {
                                            alertDialog.dismiss();
                                        }
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    } else {
                        new DeleteAsyncTask().execute(mEvent.getId());
                        eventList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, eventList.size());
                        notifyDataSetChanged();
                        calendarFragment.setUpCalendar();
                        Toast.makeText(context, R.string.event_deleted, Toast.LENGTH_SHORT).show();
                        if (eventList.isEmpty()) {
                            alertDialog.dismiss();
                        }
                    }

                    calendarFragment.getFragmentManager().beginTransaction().detach(calendarFragment).commit();
                    calendarFragment.getFragmentManager().beginTransaction().attach(calendarFragment).commit();
                    return true;
                case R.id.Popup_Item_Share:
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, mEvent.toString());
                    intent.setType("text/plain");
                    calendarFragment.startActivity(Intent.createChooser(intent, null));
                    return true;
            }

            return false;
        }
    }

    private class DeleteAsyncTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            cancelAllNotifications(integers[0]);
            deleteEvent(integers[0]);
            return null;
        }
    }

    private void cancelAllNotifications(Integer integer) {
        cancelAlarms(readNotifications(integer));
    }

    private void deleteEvent(int eventId) {
        dbHelper.deleteEvent(dbHelper.getWritableDatabase(), eventId);
        dbHelper.deleteRecurringPattern(dbHelper.getWritableDatabase(), eventId);
        dbHelper.deleteEventInstanceException(dbHelper.getWritableDatabase(), eventId);
        dbHelper.deleteNotificationsByEventId(dbHelper.getWritableDatabase(), eventId);
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

    private void cancelAlarms(List<Notification> notifications) {
        for (Notification notification : notifications) {
            cancelAlarm(notification.getId());
            dbHelper.deleteNotificationById(dbHelper.getWritableDatabase(), notification.getId());
        }
    }

    private void cancelAlarm(int requestCode) {
        Log.d(TAG, "cancelAlarm: " + requestCode);
        Intent intent = new Intent(context.getApplicationContext(), ServiceAutoLauncher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), requestCode, intent, 0);
        pendingIntent.cancel();
    }
}
