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

import com.example.rescue_pets.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Admin_Login_Activity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginBtn;
    TextView forgotPassword;
    ProgressBar progressBar;

    // <-- set this to your backend admin login endpoint
    private static final String LOGIN_URL = "http://192.168.31.170:4000/admin/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        emailInput = findViewById(R.id.admin_username); // keep id; should contain email
        passwordInput = findViewById(R.id.admin_password);
        loginBtn = findViewById(R.id.btn_admin);
        forgotPassword = findViewById(R.id.user_forgot_password);
        progressBar = findViewById(R.id.progress_bar);

        forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(Admin_Login_Activity.this, Admin_Forgot_Password_Activity.class));
        });

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Admin_Login_Activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start network call off the UI thread
            progressBar.setVisibility(View.VISIBLE);
            new Thread(() -> loginAdmin(email, password)).start();
        });
    }

    private void loginAdmin(String email, String password) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(LOGIN_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setDoOutput(true);

            JSONObject bodyJson = new JSONObject();
            bodyJson.put("email", email);
            bodyJson.put("password", password);

            byte[] out = bodyJson.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(out);
            }

            int responseCode = conn.getResponseCode();

            InputStream is = (responseCode >= 200 && responseCode < 400) ? conn.getInputStream() : conn.getErrorStream();
            String responseBody = "";
            if (is != null) {
                try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
                    responseBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                }
            }

            final String respBodyFinal = responseBody;
            runOnUiThread(() -> progressBar.setVisibility(View.GONE));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // parse JSON and check for token/admin
                JSONObject respJson = new JSONObject(respBodyFinal);
                boolean hasToken = respJson.has("token");
                boolean hasAdmin = respJson.has("admin");
                if (hasToken || hasAdmin) {
                    // success
                    runOnUiThread(() -> {
                        Toast.makeText(Admin_Login_Activity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // optionally save token: respJson.optString("token")
                        startActivity(new Intent(Admin_Login_Activity.this, Admin_Dashboard_Activity.class));
                        finish();
                    });
                } else {
                    // backend returned 200 but no token/admin -> treat as failure (unexpected)
                    String message = respJson.optString("message", "Login failed");
                    runOnUiThread(() -> Toast.makeText(Admin_Login_Activity.this, message, Toast.LENGTH_SHORT).show());
                }
            } else {
                // handle 4xx/5xx - backend usually returns JSON with message
                String message = "Login failed";
                try {
                    if (!respBodyFinal.isEmpty()) {
                        JSONObject err = new JSONObject(respBodyFinal);
                        message = err.optString("message", message);
                    }
                } catch (Exception ignored) {}
                final String showMsg = message;
                runOnUiThread(() -> Toast.makeText(Admin_Login_Activity.this, showMsg, Toast.LENGTH_SHORT).show());
            }

        } catch (Exception e) {
            final String errMsg = e.getMessage() != null ? e.getMessage() : "Network error";
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Admin_Login_Activity.this, "Network Error: " + errMsg, Toast.LENGTH_LONG).show();
            });
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
