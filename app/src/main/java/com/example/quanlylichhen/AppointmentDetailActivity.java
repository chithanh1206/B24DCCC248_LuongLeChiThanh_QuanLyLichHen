
package com.example.quanlylichhen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentDetailActivity extends AppCompatActivity {

    private TextView tvDetailTitle;
    private TextView tvDetailLocation;
    private TextView tvDetailDate;
    private TextView tvDetailTime;

    private Switch switchDetailReminder;

    private Button btnDelete;
    private Button btnBack;

    private DatabaseHelper databaseHelper;

    private int appointmentId;
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        // Anh xa giao dien
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailTime = findViewById(R.id.tvDetailTime);

        switchDetailReminder = findViewById(R.id.switchDetailReminder);

        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        // Khoi tao database
        databaseHelper = new DatabaseHelper(this);

        // Nhan ID lich hen tu MainActivity
        appointmentId = getIntent().getIntExtra(
                "appointment_id",
                -1
        );

        // Hien thi chi tiet lich hen
        loadAppointmentDetail();

        // Bat/tat nhac nho
        switchDetailReminder.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (isLoading || appointmentId == -1) {
                        return;
                    }

                    boolean result = databaseHelper.updateReminder(
                            appointmentId,
                            isChecked
                    );

                    if (result) {
                        String message = isChecked
                                ? "Da bat nhac nho"
                                : "Da tat nhac nho";

                        Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {
                        Toast.makeText(
                                this,
                                "Cap nhat nhac nho that bai",
                                Toast.LENGTH_SHORT
                        ).show();

                        // Dua switch ve trang thai cu neu cap nhat that bai
                        isLoading = true;
                        switchDetailReminder.setChecked(!isChecked);
                        isLoading = false;
                    }
                }
        );

        // Xoa lich hen
        btnDelete.setOnClickListener(v -> confirmDelete());

        // Quay lai
        btnBack.setOnClickListener(v -> finish());
    }

    // Dinh dang ngay tu yyyy-MM-dd sang dd/MM/yyyy
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date date = inputFormat.parse(dateString);

            if (date != null) {
                return outputFormat.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    // Tai thong tin lich hen
    private void loadAppointmentDetail() {

        if (appointmentId == -1) {
            Toast.makeText(
                    this,
                    "Khong tim thay lich hen",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        Appointment appointment =
                databaseHelper.getAppointmentById(appointmentId);

        if (appointment == null) {
            Toast.makeText(
                    this,
                    "Lich hen khong ton tai",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        tvDetailTitle.setText(appointment.getTitle());

        tvDetailLocation.setText(appointment.getLocation());

        tvDetailDate.setText(
                formatDate(appointment.getDate())
        );

        tvDetailTime.setText(appointment.getTime());

        // Hien thi trang thai nhac nho
        isLoading = true;

        switchDetailReminder.setChecked(
                appointment.isReminder()
        );

        isLoading = false;
    }

    // Xac nhan truoc khi xoa
    private void confirmDelete() {

        new AlertDialog.Builder(this)
                .setTitle("Xoa lich hen")
                .setMessage("Ban co chac chan muon xoa lich hen nay khong?")
                .setPositiveButton("Xoa", (dialog, which) -> deleteAppointment())
                .setNegativeButton("Huy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Xoa lich hen
    private void deleteAppointment() {

        boolean result = databaseHelper.deleteAppointment(
                appointmentId
        );

        if (result) {

            Toast.makeText(
                    this,
                    "Da xoa lich hen",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Xoa lich hen that bai",
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
