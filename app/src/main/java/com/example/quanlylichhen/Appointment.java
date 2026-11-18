
package com.example.quanlylichhen;

public class Appointment {

    private int id;
    private String title;
    private String location;
    private String date;
    private String time;
    private boolean reminder;

    // Constructor đầy đủ
    public Appointment(int id, String title, String location,
                       String date, String time, boolean reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    // Constructor khi thêm lịch hẹn mới
    public Appointment(String title, String location,
                       String date, String time, boolean reminder) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }
}
