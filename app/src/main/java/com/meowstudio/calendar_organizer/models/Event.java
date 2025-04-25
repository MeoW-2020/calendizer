package com.meowstudio.calendar_organizer.models;

public class Event {

    private int id;
    private String title;
    private boolean isAllDay;
    private String date;
    private String time;
    private String month;
    private String year;
    private boolean isNotify;
    private boolean isBirthday;
    private boolean isDone;
    private boolean isRecurring;
    private String recurringPeriod;
    private String note;
    private int color;
    private String address;
    private String phoneNumber;
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public boolean isBirthday() {
        return isBirthday;
    }

    public void setBirthday(boolean birthday) {
        isBirthday = birthday;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurringPeriod() {
        return recurringPeriod;
    }

    public void setRecurringPeriod(String recurringPeriod) {
        this.recurringPeriod = recurringPeriod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String location) {
        this.address = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    @Override
    public String toString() {
        return title + '\n' +
                "Дата: " + date + '\n' +
                "Время: " + time + '\n' +
                "Заметка: " + note + '\n';
    }
}
