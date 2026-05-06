package com.example.rescue_pets.Volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class Volunteer_Forgot_Password_activity extends AppCompatActivity {

    EditText etEmail;
    Button btnResetPassword;
    ProgressBar progressBar;
    LinearLayout signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer__forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progress_bar);
        signup = findViewById(R.id.signup);

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
            } else {
                sendOtp(email);
            }
        });

        signup.setOnClickListener(v -> {
            startActivity(new Intent(this, Volunteer_login_activity.class));
            finish();
        });
    }

    private void sendOtp(String email) {
        progressBar.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);

        String url = MyIP.IP_ADDRESS +"vol/forgot-password";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);

                    Toast.makeText(this, "OTP sent to your email", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Volunteer_Forgot_Password_activity.this, EnterOtpActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("role", "vol");
                    startActivity(intent);
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}