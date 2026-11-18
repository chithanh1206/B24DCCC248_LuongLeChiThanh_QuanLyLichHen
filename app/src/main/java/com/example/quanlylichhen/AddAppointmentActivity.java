package com.example.quanlylichhen;



import android.app.AlertDialog;

import android.app.DatePickerDialog;

import android.os.Bundle;

import android.view.Gravity;

import android.view.View;

import android.view.inputmethod.InputMethodManager;

import android.content.Context;

import android.widget.Button;

import android.widget.EditText;

import android.widget.LinearLayout;

import android.widget.NumberPicker;

import android.widget.Switch;

import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;



import java.util.Calendar;

import java.util.Locale;



public class AddAppointmentActivity extends AppCompatActivity {



    private EditText edtTitle, edtLocation;

    private Button btnDate, btnTime, btnSave, btnCancel;

    private Switch switchReminder;



    private DatabaseHelper databaseHelper;



    private String selectedDate;

    private String selectedTime;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_appointment);



// Ánh xạ giao diện

        edtTitle = findViewById(R.id.edtTitle);

        edtLocation = findViewById(R.id.edtLocation);



        btnDate = findViewById(R.id.btnDate);

        btnTime = findViewById(R.id.btnTime);

        btnSave = findViewById(R.id.btnSave);

        btnCancel = findViewById(R.id.btnCancel);



        switchReminder = findViewById(R.id.switchReminder);



        databaseHelper = new DatabaseHelper(this);



// Thiết lập nhập văn bản, không bật tự động sửa từ

        edtTitle.setInputType(

                android.text.InputType.TYPE_CLASS_TEXT

                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        );



        edtLocation.setInputType(

                android.text.InputType.TYPE_CLASS_TEXT

                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        );



// Lấy ngày giờ hiện tại

        Calendar calendar = Calendar.getInstance();



        selectedDate = String.format(

                Locale.US,

                "%04d-%02d-%02d",

                calendar.get(Calendar.YEAR),

                calendar.get(Calendar.MONTH) + 1,

                calendar.get(Calendar.DAY_OF_MONTH)

        );



        selectedTime = String.format(

                Locale.US,

                "%02d:%02d",

                calendar.get(Calendar.HOUR_OF_DAY),

                calendar.get(Calendar.MINUTE)

        );



        btnDate.setText(selectedDate);

        btnTime.setText(selectedTime);



// Sự kiện các nút

        btnDate.setOnClickListener(v -> showDatePicker());

        btnTime.setOnClickListener(v -> showTimePicker());



        btnSave.setOnClickListener(v -> saveAppointment());

        btnCancel.setOnClickListener(v -> finish());

    }



// Chọn ngày

    private void showDatePicker() {



        Calendar calendar = Calendar.getInstance();



        DatePickerDialog dialog = new DatePickerDialog(

                this,

                (view, year, month, day) -> {



                    selectedDate = String.format(

                            Locale.US,

                            "%04d-%02d-%02d",

                            year,

                            month + 1,

                            day

                    );



                    btnDate.setText(selectedDate);

                },

                calendar.get(Calendar.YEAR),

                calendar.get(Calendar.MONTH),

                calendar.get(Calendar.DAY_OF_MONTH)

        );



        dialog.show();

    }



// Chọn giờ bằng hai cột Spinner

    private void showTimePicker() {



        Calendar calendar = Calendar.getInstance();



        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        int currentMinute = calendar.get(Calendar.MINUTE);



// Tạo giao diện chứa hai NumberPicker

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.HORIZONTAL);

        layout.setGravity(Gravity.CENTER);

        layout.setPadding(30, 20, 30, 20);



        NumberPicker hourPicker = new NumberPicker(this);

        hourPicker.setMinValue(0);

        hourPicker.setMaxValue(23);

        hourPicker.setValue(currentHour);

        hourPicker.setFormatter(value ->

                String.format(Locale.US, "%02d", value)

        );



        NumberPicker minutePicker = new NumberPicker(this);

        minutePicker.setMinValue(0);

        minutePicker.setMaxValue(59);

        minutePicker.setValue(currentMinute);

        minutePicker.setFormatter(value ->

                String.format(Locale.US, "%02d", value)

        );



// Không cho nhập giá trị tùy ý

        hourPicker.setWrapSelectorWheel(true);

        minutePicker.setWrapSelectorWheel(true);



// Thêm hai Spinner vào giao diện

        LinearLayout.LayoutParams params =

                new LinearLayout.LayoutParams(0, 180, 1);



        layout.addView(hourPicker, params);

        layout.addView(minutePicker, params);



// Hiển thị hộp thoại chọn giờ

        AlertDialog dialog = new AlertDialog.Builder(this)

                .setTitle("Chọn giờ và phút")

                .setView(layout)

                .setPositiveButton("Xác nhận", (d, which) -> {



                    selectedTime = String.format(

                            Locale.US,

                            "%02d:%02d",

                            hourPicker.getValue(),

                            minutePicker.getValue()

                    );



                    btnTime.setText(selectedTime);

                })

                .setNegativeButton("Hủy", null)

                .create();



        dialog.show();

    }



// Kiểm tra lịch hẹn có nằm trong quá khứ không

    private boolean isAppointmentInPast() {



        try {



            String[] dateParts = selectedDate.split("-");

            String[] timeParts = selectedTime.split(":");



            Calendar appointmentCalendar = Calendar.getInstance();



            appointmentCalendar.set(

                    Integer.parseInt(dateParts[0]),

                    Integer.parseInt(dateParts[1]) - 1,

                    Integer.parseInt(dateParts[2]),

                    Integer.parseInt(timeParts[0]),

                    Integer.parseInt(timeParts[1]),

                    0

            );



            appointmentCalendar.set(Calendar.MILLISECOND, 0);



// So sánh đến phút, bỏ qua giây

            Calendar currentCalendar = Calendar.getInstance();



            currentCalendar.set(Calendar.SECOND, 0);

            currentCalendar.set(Calendar.MILLISECOND, 0);



            return appointmentCalendar.before(currentCalendar);



        } catch (Exception e) {

            return true;

        }

    }



// Ẩn bàn phím

    private void hideKeyboard() {



        InputMethodManager imm = (InputMethodManager)

                getSystemService(Context.INPUT_METHOD_SERVICE);



        if (imm != null && getCurrentFocus() != null) {

            imm.hideSoftInputFromWindow(

                    getCurrentFocus().getWindowToken(), 0

            );

        }

    }



// Lưu lịch hẹn

    private void saveAppointment() {



        String title = edtTitle.getText().toString().trim();

        String location = edtLocation.getText().toString().trim();



        boolean reminder = switchReminder.isChecked();



// Kiểm tra tên lịch hẹn

        if (title.isEmpty()) {



            edtTitle.setError("Vui lòng nhập tên lịch hẹn");

            edtTitle.requestFocus();



            return;

        }



// Kiểm tra địa điểm

        if (location.isEmpty()) {



            edtLocation.setError("Vui lòng nhập địa điểm");

            edtLocation.requestFocus();



            return;

        }



        hideKeyboard();



// Kiểm tra ngày giờ

        if (isAppointmentInPast()) {



            Toast.makeText(

                    this,

                    "Ngày giờ hẹn không được nằm trong quá khứ!",

                    Toast.LENGTH_LONG

            ).show();



            return;

        }



// Tạo đối tượng lịch hẹn

        Appointment appointment = new Appointment(

                0,

                title,

                location,

                selectedDate,

                selectedTime,

                reminder

        );



// Lưu vào SQLite

        long result = databaseHelper.insertAppointment(appointment);



        if (result != -1) {



            Toast.makeText(

                    this,

                    "Thêm lịch hẹn thành công!",

                    Toast.LENGTH_SHORT

            ).show();



            finish();



        } else {



            Toast.makeText(

                    this,

                    "Thêm lịch hẹn thất bại!",

                    Toast.LENGTH_SHORT

            ).show();

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