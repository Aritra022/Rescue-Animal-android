package com.example.rescue_pets.Volunteer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.rescue_pets.MyIP;
import com.example.rescue_pets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Volunteer_login_activity extends AppCompatActivity {

    EditText login_email, login_password;
    Button btn_login_user;
    TextView link_signup_user, link_forgot_password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_login);

        login_email = findViewById(R.id.user_name);
        login_password = findViewById(R.id.user_password);
        btn_login_user = findViewById(R.id.user_button);
        link_signup_user = findViewById(R.id.vol_signup);
        link_forgot_password = findViewById(R.id.vol_forgot_password);

        // Login button logic
        btn_login_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_email.getText().toString().trim();
                String password = login_password.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Volunteer_login_activity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = MyIP.IP_ADDRESS +"vol/login"; // Replace with your actual server URL
               

                loginUser(url, email, password);
            }
        });

        // Sign-up link logic
        link_signup_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(Volunteer_login_activity.this, Volunteer_registration_activity.class);
                startActivity(signupIntent);
            }
        });

        // Forgot password logic
        link_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotIntent = new Intent(Volunteer_login_activity.this, Volunteer_Forgot_Password_activity.class);
                startActivity(forgotIntent);
            }
        });
    }

    private void loginUser(String url, String email, String password) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
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
                                    Toast.makeText(getApplicationContext(), "✅ Login Success", Toast.LENGTH_LONG).show();
                                    SharedPreferences prefs = getSharedPreferences("VolunteerPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("_id", user.optString("_id"));
                                    editor.putString("volName", user.optString("name"));
                                    editor.putString("volEmail", user.optString("email"));
                                    editor.putString("volPhone", user.optString("contact"));
                                    editor.putString("volLocation", user.optString("location"));
                                    editor.apply();

                                    Intent intent = new Intent(Volunteer_login_activity.this, Volunteer_Dashboard_activity.class);
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
                            Toast.makeText(getApplicationContext(), "❌ Error parsing server response", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Volunteer_login_activity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
