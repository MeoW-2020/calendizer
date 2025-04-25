package com.meowstudio.calendar_organizer.database;

public class DBTables {

    public static final String EVENT_TABLE_NAME = "EVENT";
    public static final String EVENT_ID = "id";
    public static final String EVENT_TITLE = "title";
    public static final String EVENT_IS_ALL_DAY = "is_allDay";
    public static final String EVENT_IS_DONE = "is_done";
    public static final String EVENT_DATE = "date";
    public static final String EVENT_MONTH = "month";
    public static final String EVENT_YEAR = "year";
    public static final String EVENT_TIME = "time";
    public static final String EVENT_IS_NOTIFY = "is_notify";
    public static final String EVENT_IS_BIRTHDAY = "is_birthday";
    public static final String EVENT_IS_RECURRING = "is_recurring";
    public static final String EVENT_NOTE = "note";
    public static final String EVENT_COLOR = "color";
    public static final String EVENT_ADDRESS = "address";
    public static final String EVENT_PHONE_NUMBER = "number";
    public static final String EVENT_EMAIL = "email";
    public static final String EVENT_PARENT_ID = "parent_id";

    public static final String RECURRING_PATTERN_TABLE_NAME = "RECURRING_PATTERN";
    public static final String RECURRING_PATTERN_EVENT_ID = "event_id";
    public static final String RECURRING_PATTERN_TYPE = "type";
    public static final String RECURRING_PATTERN_MONTH_OF_YEAR = "month_of_year";
    public static final String RECURRING_PATTERN_DAY_OF_MONTH = "day_of_month";
    public static final String RECURRING_PATTERN_DAY_OF_WEEK = "day_of_week";

    public static final String EVENT_INSTANCE_EXCEPTION_TABLE_NAME = "EVENT_INSTANCE_EXCEPTION";
    public static final String EVENT_INSTANCE_EXCEPTION_ID = "id";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_ID = "event_id";
    public static final String EVENT_INSTANCE_EXCEPTION_IS_CHANGED = "is_changed";
    public static final String EVENT_INSTANCE_EXCEPTION_IS_CANCELED = "is_canceled";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_TITLE = "title";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_IS_ALL_DAY = "is_allDay";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_IS_DONE = "is_done";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_DATE = "date";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_MONTH = "month";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_YEAR = "year";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_TIME = "time";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_IS_NOTIFY = "is_notify";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_IS_BIRTHDAY = "is_birthday";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_IS_RECURRING = "is_recurring";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_NOTE = "note";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_COLOR = "color";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_ADDRESS = "address";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_PHONE_NUMBER = "number";
    public static final String EVENT_INSTANCE_EXCEPTION_EVENT_EVENT_EMAIL = "email";

    public static final String NOTIFICATION_TABLE_NAME = "NOTIFICATION";
    public static final String NOTIFICATION_ID = "id";
    public static final String NOTIFICATION_EVENT_ID = "event_id";
    public static final String NOTIFICATION_TIME = "time";
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
}
