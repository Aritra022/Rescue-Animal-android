package com.example.rescue_pets.Volunteer;

import android.app.ProgressDialog;
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

public class Volunteer_registration_activity extends AppCompatActivity {

    EditText vol_username, vol_email, vol_password, vol_confirm_password, vol_contact, vol_location;
    Button btn_register_volunteer;
    ProgressBar progressBar;
    TextView link_login_volunteer, btn_signin_volunteer;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_registration);

        // UI references
        vol_username = findViewById(R.id.vol_username);
        vol_email = findViewById(R.id.vol_email);
        vol_password = findViewById(R.id.vol_password);
        vol_confirm_password = findViewById(R.id.vol_confirm_password);
        vol_contact = findViewById(R.id.vol_contact);
        vol_location = findViewById(R.id.vol_location);

        btn_register_volunteer = findViewById(R.id.btn_register_volunteer);
        progressBar = findViewById(R.id.progress_bar_volunteer);
        link_login_volunteer = findViewById(R.id.link_login_volunteer);
        btn_signin_volunteer = findViewById(R.id.btn_signin_volunteer);

        btn_register_volunteer.setOnClickListener(v -> {
            String name = vol_username.getText().toString().trim();
            String email = vol_email.getText().toString().trim();
            String contact = vol_contact.getText().toString().trim();
            String password = vol_password.getText().toString().trim();
            String confirmPassword = vol_confirm_password.getText().toString().trim();
            String location = vol_location.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || location.isEmpty()) {
                Toast.makeText(Volunteer_registration_activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(Volunteer_registration_activity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = MyIP.IP_ADDRESS +"vol/register"; // replace this with your actual backend route
            registerVolunteer(url, name, email, contact, password, location);
        });

        btn_signin_volunteer.setOnClickListener(v -> {
            Intent intent = new Intent(Volunteer_registration_activity.this, Volunteer_login_activity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerVolunteer(String url, String name, String email, String contact, String password, String location) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("user")) {
                            JSONObject volunteer = jsonObject.getJSONObject("user");
                            String id = volunteer.optString("_id");

                            if (!id.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "✅ Volunteer Registered Successfully", Toast.LENGTH_LONG).show();
                                vol_username.setText("");
                                vol_email.setText("");
                                vol_contact.setText("");
                                vol_password.setText("");
                                vol_confirm_password.setText("");
                                vol_location.setText("");
                            } else {
                                Toast.makeText(getApplicationContext(), "❌ Registration Failed", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "❌ Invalid server response", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "❌ JSON Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(Volunteer_registration_activity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("contact", contact);
                params.put("password", password);
                params.put("location", location);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
