package com.example.rescue_pets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rescue_pets.R;
import com.example.rescue_pets.Volunteer.Volunteer_login_activity;
import com.example.rescue_pets.admin.Admin_Login_Activity;
import com.example.rescue_pets.user.User_login_activity;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText etNewPassword, etConfirmPassword;
    Button btnResetPassword;
    ProgressBar progressBar;

    String email, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progress_bar);

        email = getIntent().getStringExtra("email");
        role = getIntent().getStringExtra("role");

        btnResetPassword.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(email, newPassword, role);
            }
        });
    }

    private void resetPassword(String email, String newPassword, String role) {
        progressBar.setVisibility(android.view.View.VISIBLE);
        btnResetPassword.setEnabled(false);

        String url;

        if ("admin".equals(role)) {
            url = MyIP.IP_ADDRESS +"admin/reset-password";
        } else if ("vol".equals(role)) {
            url = MyIP.IP_ADDRESS +"admin/reset-password";
        } else {
            url =MyIP.IP_ADDRESS +"admin/reset-password";
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnResetPassword.setEnabled(true);

                    Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show();

                    Intent intent;
                    if ("admin".equals(role)) {
                        intent = new Intent(ResetPasswordActivity.this, Admin_Login_Activity.class);
                    } else if ("vol".equals(role)) {
                        intent = new Intent(ResetPasswordActivity.this, Volunteer_login_activity.class);
                    } else {
                        intent = new Intent(ResetPasswordActivity.this, User_login_activity.class);
                    }

                    startActivity(intent);
                    finish();
                },
                error -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("newPassword", newPassword);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}