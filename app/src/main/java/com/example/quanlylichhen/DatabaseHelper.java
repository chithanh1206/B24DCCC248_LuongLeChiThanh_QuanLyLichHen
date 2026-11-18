
package com.example.quanlylichhen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_APPOINTMENTS = "appointments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Tao database
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)";

        db.execSQL(createUsers);

        String createAppointments = "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "reminder INTEGER NOT NULL DEFAULT 0)";

        db.execSQL(createAppointments);

        // Tai khoan mac dinh
        ContentValues user = new ContentValues();
        user.put("username", "admin");
        user.put("password", "123456");

        db.insert(TABLE_USERS, null, user);

        // Du lieu mau
        insertSample(db,
                "Họp nhóm đồ án",
                "Phòng B203",
                "2026-11-20",
                "09:00",
                1);

        insertSample(db,
                "Khám răng",
                "Nha khoa Minh Tâm",
                "2026-11-22",
                "14:30",
                1);

        insertSample(db,
                "Đi cà phê với bạn",
                "Highland Coffee",
                "2026-11-25",
                "19:00",
                0);
    }

    // Them lich hen mau
    private void insertSample(
            SQLiteDatabase db,
            String title,
            String location,
            String date,
            String time,
            int reminder) {

        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("location", location);
        values.put("date", date);
        values.put("time", time);
        values.put("reminder", reminder);

        db.insert(TABLE_APPOINTMENTS, null, values);
    }

    // Nang cap database
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Chua can thay doi cau truc database
    }

    // 1. Kiem tra dang nhap
    public boolean checkLogin(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USERS +
                        " WHERE username = ? AND password = ?",
                new String[]{username, password}
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    // 2. Them tai khoan
    public long insertUser(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("username", username);
        values.put("password", password);

        return db.insert(TABLE_USERS, null, values);
    }

    // 3. Them lich hen
    public long insertAppointment(Appointment appointment) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("title", appointment.getTitle());
        values.put("location", appointment.getLocation());
        values.put("date", appointment.getDate());
        values.put("time", appointment.getTime());
        values.put("reminder", appointment.isReminder() ? 1 : 0);

        return db.insert(TABLE_APPOINTMENTS, null, values);
    }

    // 4. Lay danh sach lich hen
    public ArrayList<Appointment> getAllAppointments() {

        ArrayList<Appointment> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_APPOINTMENTS +
                        " ORDER BY date ASC, time ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow("title"));

                String location = cursor.getString(
                        cursor.getColumnIndexOrThrow("location"));

                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow("date"));

                String time = cursor.getString(
                        cursor.getColumnIndexOrThrow("time"));

                boolean reminder = cursor.getInt(
                        cursor.getColumnIndexOrThrow("reminder")) == 1;

                Appointment appointment = new Appointment(
                        id,
                        title,
                        location,
                        date,
                        time,
                        reminder
                );

                list.add(appointment);
            }

        } finally {
            cursor.close();
        }

        return list;
    }

    // 5. Lay lich hen theo ID
    public Appointment getAppointmentById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_APPOINTMENTS +
                        " WHERE id = ?",
                new String[]{String.valueOf(id)}
        );

        try {
            if (cursor.moveToFirst()) {

                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow("title"));

                String location = cursor.getString(
                        cursor.getColumnIndexOrThrow("location"));

                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow("date"));

                String time = cursor.getString(
                        cursor.getColumnIndexOrThrow("time"));

                boolean reminder = cursor.getInt(
                        cursor.getColumnIndexOrThrow("reminder")) == 1;

                return new Appointment(
                        id,
                        title,
                        location,
                        date,
                        time,
                        reminder
                );
            }

        } finally {
            cursor.close();
        }

        return null;
    }

    // 6. Cap nhat trang thai nhac nho
    public boolean updateReminder(int id, boolean on) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("reminder", on ? 1 : 0);

        int result = db.update(
                TABLE_APPOINTMENTS,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // 7. Xoa lich hen
    public boolean deleteAppointment(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_APPOINTMENTS,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // 8. Dem tong so lich hen
    public int countAll() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }

        } finally {
            cursor.close();
        }

        return 0;
    }

    // 9. Dem lich hen bat nhac nho
    public int countReminders() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS +
                        " WHERE reminder = 1",
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }

        } finally {
            cursor.close();
        }

        return 0;
    }
}
