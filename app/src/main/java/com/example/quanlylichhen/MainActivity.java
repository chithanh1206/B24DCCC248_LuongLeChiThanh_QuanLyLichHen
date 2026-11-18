
package com.example.quanlylichhen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotal, tvReminder;
    private ListView lvAppointments;
    private Button btnAdd;

    private DatabaseHelper databaseHelper;

    private ArrayList<Appointment> appointmentList;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ giao diện
        tvTotal = findViewById(R.id.tvTotal);
        tvReminder = findViewById(R.id.tvReminder);
        lvAppointments = findViewById(R.id.lvAppointments);
        btnAdd = findViewById(R.id.btnAdd);

        // Khởi tạo SQLite
        databaseHelper = new DatabaseHelper(this);

        // Khởi tạo danh sách
        appointmentList = new ArrayList<>();

        adapter = new AppointmentAdapter(appointmentList);
        lvAppointments.setAdapter(adapter);

        // Nút thêm lịch hẹn
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddAppointmentActivity.class
            );
            startActivity(intent);
        });

        // Nhấn vào lịch hẹn để xem chi tiết
        lvAppointments.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Appointment appointment =
                            appointmentList.get(position);

                    Intent intent = new Intent(
                            MainActivity.this,
                            AppointmentDetailActivity.class
                    );

                    // Truyền ID lịch hẹn
                    intent.putExtra(
                            "appointment_id",
                            appointment.getId()
                    );

                    startActivity(intent);
                }
        );
    }

    // Tải danh sách lịch hẹn từ SQLite
    private void loadAppointments() {

        appointmentList.clear();

        // Dữ liệu được truy vấn và sắp xếp từ SQLite
        appointmentList.addAll(
                databaseHelper.getAllAppointments()
        );

        adapter.notifyDataSetChanged();

        // Cập nhật thống kê
        updateStatistics();
    }

    // Cập nhật tổng số lịch và số lịch bật nhắc nhở
    private void updateStatistics() {

        int total = databaseHelper.countAll();
        int reminder = databaseHelper.countReminders();

        tvTotal.setText("Tổng số lịch hẹn: " + total);

        tvReminder.setText(
                "Đang bật nhắc nhở: " + reminder
        );
    }

    // Định dạng ngày từ yyyy-MM-dd sang dd/MM/yyyy
    private String formatDate(String date) {

        try {
            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            Date parsedDate = inputFormat.parse(date);

            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    // Tạo giao diện riêng cho từng lịch hẹn
    private class AppointmentAdapter
            extends ArrayAdapter<Appointment> {

        public AppointmentAdapter(
                ArrayList<Appointment> appointments) {

            super(MainActivity.this, 0, appointments);
        }

        @Override
        public View getView(
                int position,
                View convertView,
                ViewGroup parent) {

            Appointment appointment =
                    getItem(position);

            if (appointment == null) {
                return new View(MainActivity.this);
            }

            // Khung chứa thông tin lịch hẹn
            LinearLayout card = new LinearLayout(
                    MainActivity.this
            );

            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(18, 16, 18, 16);

            GradientDrawable background =
                    new GradientDrawable();

            background.setColor(Color.WHITE);
            background.setCornerRadius(18);

            background.setStroke(
                    1,
                    Color.rgb(226, 232, 240)
            );

            card.setBackground(background);
            card.setElevation(3);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(4, 6, 4, 6);
            card.setLayoutParams(cardParams);

            // Tên lịch hẹn
            TextView tvTitle = new TextView(
                    MainActivity.this
            );

            tvTitle.setText(appointment.getTitle());
            tvTitle.setTextSize(17);
            tvTitle.setTextColor(
                    Color.rgb(21, 101, 192)
            );
            tvTitle.setTypeface(
                    null,
                    Typeface.BOLD
            );

            // Địa điểm
            TextView tvLocation = new TextView(
                    MainActivity.this
            );

            tvLocation.setText(
                    "Địa điểm: " + appointment.getLocation()
            );

            tvLocation.setTextSize(14);
            tvLocation.setTextColor(
                    Color.rgb(69, 90, 100)
            );

            // Ngày và giờ
            TextView tvDateTime = new TextView(
                    MainActivity.this
            );

            tvDateTime.setText(
                    "Thời gian: " +
                            formatDate(appointment.getDate()) +
                            " · " + appointment.getTime()
            );

            tvDateTime.setTextSize(14);
            tvDateTime.setTextColor(
                    Color.rgb(69, 90, 100)
            );

            // Trạng thái nhắc nhở
            TextView tvReminderStatus = new TextView(
                    MainActivity.this
            );

            if (appointment.isReminder()) {

                tvReminderStatus.setText(
                        "● Có nhắc nhở"
                );

                tvReminderStatus.setTextColor(
                        Color.rgb(46, 125, 50)
                );

            } else {

                tvReminderStatus.setText(
                        "● Không nhắc nhở"
                );

                tvReminderStatus.setTextColor(
                        Color.rgb(117, 117, 117)
                );
            }

            tvReminderStatus.setTextSize(13);
            tvReminderStatus.setTypeface(
                    null,
                    Typeface.BOLD
            );

            // Khoảng cách giữa các dòng
            LinearLayout.LayoutParams textParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            textParams.setMargins(0, 4, 0, 4);

            card.addView(tvTitle, textParams);
            card.addView(tvLocation, textParams);
            card.addView(tvDateTime, textParams);
            card.addView(tvReminderStatus, textParams);

            return card;
        }
    }

    // Tự động tải lại khi quay về màn hình danh sách
    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadAppointments();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
