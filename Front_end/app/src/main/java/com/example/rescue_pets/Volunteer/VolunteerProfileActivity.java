package com.example.rescue_pets.Volunteer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.ApiService;
import com.example.rescue_pets.R;
import com.example.rescue_pets.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerProfileActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etLocation;
    Button btnUpdateProfile;
    ApiService apiService;
    ProgressDialog progressDialog;

    String id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etLocation = findViewById(R.id.etLocation);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        loadSavedVolunteerData();

        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void loadSavedVolunteerData() {
        Context context = getApplicationContext();

        id = context.getSharedPreferences("VolunteerPrefs", MODE_PRIVATE).getString("_id", "");
        String name = context.getSharedPreferences("VolunteerPrefs", MODE_PRIVATE).getString("volName", "");
        String email = context.getSharedPreferences("VolunteerPrefs", MODE_PRIVATE).getString("volEmail", "");
        String phone = context.getSharedPreferences("VolunteerPrefs", MODE_PRIVATE).getString("volPhone", "");
        String location = context.getSharedPreferences("VolunteerPrefs", MODE_PRIVATE).getString("volLocation", "");

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);
        etLocation.setText(location);
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        VolunteerUpdateProfileRequest request =
                new VolunteerUpdateProfileRequest(name, email, phone, location);

        apiService.updateVolunteerProfile(id, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    getSharedPreferences("VolunteerPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("_id", id)
                            .putString("volName", name)
                            .putString("volEmail", email)
                            .putString("volPhone", phone)
                            .putString("volLocation", location)
                            .apply();

                    Toast.makeText(VolunteerProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VolunteerProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(VolunteerProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}