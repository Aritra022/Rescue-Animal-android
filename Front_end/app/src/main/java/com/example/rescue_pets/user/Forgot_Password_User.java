package com.example.rescue_pets.user;

import android.app.ProgressDialog;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rescue_pets.R;

import java.util.HashMap;
import java.util.Map;

public class Forgot_Password_User extends AppCompatActivity {

    EditText etEmail;
    Button btnResetPassword;
    ProgressBar progressBar;
    LinearLayout signup; // The "Login" link

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_user); // Make sure this matches your XML file name

        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progress_bar);
        signup = findViewById(R.id.signup);

        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
            } else {
                sendResetRequest(email);
            }
        });

        // Go back to login screen
        signup.setOnClickListener(v -> {
            startActivity(new Intent(Forgot_Password_User.this, User_login_activity.class));
            finish();
        });
    }

    private void sendResetRequest(String email) {
        String url = "http://192.168.31.170:4000/user/forgot-password"; // Replace with your actual backend API

        progressBar.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(this, "✔️ Reset link sent to email", Toast.LENGTH_LONG).show();
                    finish(); // Go back to login
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(this, "❌ Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("email", email);
                return data;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
