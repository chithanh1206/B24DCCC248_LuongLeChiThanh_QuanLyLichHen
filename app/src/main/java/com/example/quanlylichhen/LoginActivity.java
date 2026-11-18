
package com.example.quanlylichhen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private TextView tvError;
    private Button btnLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ giao diện
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);

        // Khởi tạo SQLite
        databaseHelper = new DatabaseHelper(this);

        // Ẩn thông báo lỗi ban đầu
        tvError.setVisibility(View.GONE);

        // Xóa thông báo lỗi khi người dùng nhập lại
        edtUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                clearError();
            }
        });

        edtPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                clearError();
            }
        });

        // Xử lý nút đăng nhập
        btnLogin.setOnClickListener(v -> checkLogin());
    }

    // Xóa thông báo lỗi
    private void clearError() {
        tvError.setText("");
        tvError.setVisibility(View.GONE);
        edtUsername.setError(null);
        edtPassword.setError(null);
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

    // Xử lý đăng nhập
    private void checkLogin() {

        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        clearError();

        // Kiểm tra tên đăng nhập
        if (username.isEmpty()) {
            edtUsername.setError("Vui lòng nhập tên đăng nhập");
            edtUsername.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu
        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        hideKeyboard();

        // Kiểm tra tài khoản trong SQLite
        boolean isValid = databaseHelper.checkLogin(
                username, password
        );

        if (isValid) {

            // Chuyển sang màn hình danh sách
            Intent intent = new Intent(
                    LoginActivity.this,
                    MainActivity.class
            );

            startActivity(intent);

            // Không quay lại màn hình đăng nhập
            finish();

        } else {

            // Hiển thị thông báo khi đăng nhập sai
            tvError.setText(
                    "Sai tên đăng nhập hoặc mật khẩu"
            );

            tvError.setVisibility(View.VISIBLE);

            edtPassword.setText("");
            edtPassword.requestFocus();
        }
    }
}
