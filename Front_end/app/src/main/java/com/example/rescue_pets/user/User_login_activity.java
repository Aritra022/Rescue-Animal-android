package com.example.rescue_pets.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rescue_pets.MyIP;
import com.example.rescue_pets.R;
import com.example.rescue_pets.TokenManager;

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

        login_email = findViewById(R.id.user_name);
        login_password = findViewById(R.id.user_password);
        btn_login_user = findViewById(R.id.user_button);
        link_signup_user = findViewById(R.id.user_signup);
        link_forgot_password = findViewById(R.id.user_forgot_password);

        btn_login_user.setOnClickListener(v -> {
            String email = login_email.getText().toString().trim();
            String password = login_password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(User_login_activity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = MyIP.IP_ADDRESS + "user/login";

            loginUser(url, email, password);
        });

        link_forgot_password.setOnClickListener(view ->
                startActivity(new Intent(User_login_activity.this, Forgot_Password_User.class)));

        link_signup_user.setOnClickListener(view ->
                startActivity(new Intent(User_login_activity.this, User_Registration_Activity.class)));
    }

    private void loginUser(String url, String email, String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    android.util.Log.d("LOGIN_DEBUG", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("user")) {
                            JSONObject user = jsonObject.getJSONObject("user");
                            String userId = user.getString("_id");
                            String username = user.getString("name");
                            String userLocation = user.getString("location");
                            String userPhone = user.getString("contact");

                            if (!userId.isEmpty()) {

                                getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                        .edit()
                                        .putString("_id", userId)
                                        .putString("userEmail", email)
                                        .putString("userPhone", userPhone)
                                        .putString("userLocation", userLocation)
                                        .putString("username", username)

                                        .apply();
                                android.util.Log.d("FCM_TOKEN", "login success reached");

                                TokenManager.fetchAndSendToken(User_login_activity.this);

                                Toast.makeText(getApplicationContext(), "✅ Login Successful", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(User_login_activity.this, User_Dashboard_Activity.class);
                                startActivity(intent);
                                finish();

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
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(User_login_activity.this, "❌ Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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