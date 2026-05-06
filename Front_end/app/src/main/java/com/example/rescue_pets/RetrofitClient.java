package com.example.rescue_pets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rescue_pets.Volunteer.Volunteer_login_activity;
import com.example.rescue_pets.user.User_login_activity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    private static final String BASE_URL = MyIP.IP_ADDRESS;



    public static Retrofit getClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static class ResetPasswordActivity extends AppCompatActivity {

        EditText etNewPassword, etConfirmPassword;
        Button btnSavePassword;
        ProgressBar progressBar;
        String email, role;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reset_password);

            etNewPassword = findViewById(R.id.etNewPassword);
            etConfirmPassword = findViewById(R.id.etConfirmPassword);
            btnSavePassword = findViewById(R.id.btnResetPassword);
            progressBar = findViewById(R.id.progress_bar);

            email = getIntent().getStringExtra("email");
            role = getIntent().getStringExtra("role");

            btnSavePassword.setOnClickListener(v -> {
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                resetPassword(email, newPassword);
            });
        }

        private void resetPassword(String email, String newPassword) {
            String url;

            if ("vol".equals(role)) {
                url = MyIP.IP_ADDRESS +"vol/reset-password";
            } else {
                url = MyIP.IP_ADDRESS +"user/reset-password";
            }

            progressBar.setVisibility(View.VISIBLE);
            btnSavePassword.setEnabled(false);

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        progressBar.setVisibility(View.GONE);
                        btnSavePassword.setEnabled(true);

                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_LONG).show();

                        Intent intent;
                        if ("vol".equals(role)) {
                            intent = new Intent(ResetPasswordActivity.this, Volunteer_login_activity.class);
                        } else {
                            intent = new Intent(ResetPasswordActivity.this, User_login_activity.class);
                        }

                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        progressBar.setVisibility(View.GONE);
                        btnSavePassword.setEnabled(true);
                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_LONG).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();
                    data.put("email", email);
                    data.put("newPassword", newPassword);
                    return data;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }
}