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
import com.example.rescue_pets.MyIP;
import com.example.rescue_pets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class User_Registration_Activity extends AppCompatActivity {

    EditText reg_username, reg_email, reg_contact, reg_password, reg_confirm_password, reg_location;
    Button btn_register_user;
    TextView link_login_user, btn_signin;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        // Link UI elements
        reg_username = findViewById(R.id.reg_username);
        reg_email = findViewById(R.id.reg_email);
        reg_contact = findViewById(R.id.reg_contact);
        reg_password = findViewById(R.id.reg_password);
        reg_confirm_password = findViewById(R.id.reg_confirm_password);
        reg_location = findViewById(R.id.reg_location);
        btn_register_user = findViewById(R.id.btn_register_user);
        link_login_user = findViewById(R.id.link_login_user);
        btn_signin = findViewById(R.id.btn_signin); // "Sign in" link

        // Register button click listener
        btn_register_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = reg_username.getText().toString().trim();
                String email = reg_email.getText().toString().trim();
                String contact = reg_contact.getText().toString().trim();
                String password = reg_password.getText().toString().trim();
                String confirmPassword = reg_confirm_password.getText().toString().trim();
                String location = reg_location.getText().toString().trim();

                // Validation
                if (name.isEmpty() || email.isEmpty() || contact.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty() || location.isEmpty()) {
                    Toast.makeText(User_Registration_Activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(User_Registration_Activity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Replace with your actual server IP
               // String url = "http://192.168.31.130:4000/user/register";
                String url = MyIP.IP_ADDRESS +"user/register";

                insertData(url, name, email, contact, password, location);
            }
        });

        // Sign in TextView click listener
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_Registration_Activity.this, User_login_activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void insertData(String url, String name, String email, String contact, String password, String location) {
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
                                    Toast.makeText(getApplicationContext(), "✅ Registration Success", Toast.LENGTH_LONG).show();
                                    reg_username.setText("");
                                    reg_email.setText("");
                                    reg_contact.setText("");
                                    reg_password.setText("");
                                    reg_confirm_password.setText("");
                                    reg_location.setText("");
                                } else {
                                    Toast.makeText(getApplicationContext(), "❌ Registration Failed", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(User_Registration_Activity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
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
