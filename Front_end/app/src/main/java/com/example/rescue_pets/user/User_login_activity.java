package com.example.rescue_pets.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rescue_pets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class User_login_activity extends AppCompatActivity {

    EditText login_email, login_password;
    Button btn_login_user;
    TextView link_signup_user, link_forgot_password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login2);

        // Bind views
        login_email = findViewById(R.id.user_name);
        login_password = findViewById(R.id.user_password);
        btn_login_user = findViewById(R.id.user_button);
        link_signup_user = findViewById(R.id.user_signup);
        link_forgot_password = findViewById(R.id.user_forgot_password);

        // Login button click
        btn_login_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_email.getText().toString().trim();
                String password = login_password.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(User_login_activity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "http://192.168.31.170:4000/user/login"; // replace with live URL or local IP
                loginUser(url, email, password);
            }
        });

        // Forgot Password click
        link_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(User_login_activity.this, Forgot_Password_User.class));
            }
        });

        // Sign Up click → goes to registration page
        link_signup_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(User_login_activity.this, User_Registration_Activity.class));
            }
        });
    }

    private void loginUser(String url, String email, String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("user")) {
                                JSONObject user = jsonObject.getJSONObject("user");
                                String userId = user.optString("_id");

                                if (!userId.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "✅ Login Successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(User_login_activity.this, User_Dashboard_Activity.class);
                                    startActivity(intent);
                                    finish(); // Optional: Finish login activity so it doesn't come back on back press
                                } else {
                                    Toast.makeText(getApplicationContext(), "❌ Login Failed", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "❌ Invalid response from server", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "❌ Error parsing response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(User_login_activity.this, "❌ Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
