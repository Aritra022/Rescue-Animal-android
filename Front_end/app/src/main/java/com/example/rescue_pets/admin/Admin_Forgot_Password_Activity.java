package com.example.rescue_pets.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rescue_pets.EnterOtpActivity;
import com.example.rescue_pets.MyIP;
import com.example.rescue_pets.R;

import java.util.HashMap;
import java.util.Map;

public class Admin_Forgot_Password_Activity extends AppCompatActivity {

    EditText etEmail;
    Button btnResetPassword;
    ProgressBar progressBar;
    TextView textSignin;

    private boolean isSendingOtp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_forgot_password);

        etEmail = findViewById(R.id.admin_email);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progress_bar);
        textSignin = findViewById(R.id.text_signin);

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                sendOtp(email);
            }
        });

        textSignin.setOnClickListener(v -> {
            Intent intent = new Intent(Admin_Forgot_Password_Activity.this, Admin_Login_Activity.class);
            startActivity(intent);
            finish();
        });
    }

    private void sendOtp(String email) {
        if (isSendingOtp) return;

        isSendingOtp = true;
        progressBar.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);

        String url = MyIP.IP_ADDRESS + "admin/forgot-password";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    isSendingOtp = false;
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    Toast.makeText(this, "OTP sent to your email", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Admin_Forgot_Password_Activity.this, EnterOtpActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("role", "admin");
                    startActivity(intent);
                },
                error -> {
                    isSendingOtp = false;
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    String message = "Failed to send OTP";
                    if (error.networkResponse != null) {
                        int code = error.networkResponse.statusCode;
                        if (code == 404) {
                            message = "Email not found";
                        } else if (code == 500) {
                            message = "Server error";
                        }
                    }

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }
}