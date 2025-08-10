package com.example.rescue_pets.Volunteer;

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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(Volunteer_Forgot_Password_activity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    sendResetLink(email);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Volunteer_Forgot_Password_activity.this, Volunteer_login_activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendResetLink(String email) {
        progressBar.setVisibility(View.VISIBLE);
        btnResetPassword.setEnabled(false);

        String url = "http://192.168.0.119:4000/vol/forgot-password"; // Update this to your real backend endpoint

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "📩 Reset link sent to your email", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        btnResetPassword.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "❌ Failed to send reset link", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
