package com.example.rescue_pets;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class EnterOtpActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    Button btnVerifyOtp;
    ProgressBar progressBar;

    String email, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        progressBar = findViewById(R.id.progress_bar);

        email = getIntent().getStringExtra("email");
        role = getIntent().getStringExtra("role");

        setupOtpInputs();

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = getOtp();

            if (otp.length() != 6) {
                Toast.makeText(this, "Please enter complete OTP", Toast.LENGTH_SHORT).show();
            } else {
                verifyOtp(email, otp, role);
            }
        });
    }

    private void setupOtpInputs() {
        moveToNext(otp1, otp2);
        moveToNext(otp2, otp3);
        moveToNext(otp3, otp4);
        moveToNext(otp4, otp5);
        moveToNext(otp5, otp6);

        moveToPrevious(otp2, otp1);
        moveToPrevious(otp3, otp2);
        moveToPrevious(otp4, otp3);
        moveToPrevious(otp5, otp4);
        moveToPrevious(otp6, otp5);
    }

    private void moveToNext(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void moveToPrevious(EditText current, EditText previous) {
        current.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL &&
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                    current.getText().toString().isEmpty()) {

                previous.requestFocus();
                previous.setSelection(previous.getText().length());
                return true;
            }
            return false;
        });
    }

    private String getOtp() {
        return otp1.getText().toString().trim()
                + otp2.getText().toString().trim()
                + otp3.getText().toString().trim()
                + otp4.getText().toString().trim()
                + otp5.getText().toString().trim()
                + otp6.getText().toString().trim();
    }

    private void verifyOtp(String email, String otp, String role) {
        progressBar.setVisibility(android.view.View.VISIBLE);
        btnVerifyOtp.setEnabled(false);

        String url;

        if ("admin".equals(role)) {
            url = MyIP.IP_ADDRESS + "admin/verify-otp";
        } else if ("vol".equals(role)) {
            url = MyIP.IP_ADDRESS + "vol/verify-otp";
        } else {
            url = MyIP.IP_ADDRESS + "user/verify-otp";
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnVerifyOtp.setEnabled(true);

                    Toast.makeText(this, "OTP verified successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EnterOtpActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("role", role);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    btnVerifyOtp.setEnabled(true);
                    Toast.makeText(this, "Invalid OTP or verification failed", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("otp", otp);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}